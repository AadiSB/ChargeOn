package com.core2web.view;

import java.util.List;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import com.core2web.controller.OwnerController;
import com.core2web.controller.SubscriptionPlanController;
import com.core2web.model.Owner;
import com.core2web.model.SubscriptionPlan;

public class OwnerSubscription {

    private static final OwnerController ownerController = new OwnerController();
    private static final SubscriptionPlanController planController = new SubscriptionPlanController();

    public static ScrollPane buildMainContent() {

        VBox content = new VBox(16);
        content.setPadding(new Insets(16));

        Button backButton = new Button("←  Back to Profile");
        backButton.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #94a3b8;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 6 0 6 0;" +
                "-fx-cursor: hand;"
        );
        backButton.setOnAction(e -> OwnerDashboard.openProfile());

        VBox pageHeader = new VBox(4);
        Label title = new Label("Subscription plans");
        title.getStyleClass().add("section-title");
        Label subtitle = new Label("Plans created by ChargeOn admin — subscribe to unlock premium benefits");
        subtitle.getStyleClass().add("card-sub");
        pageHeader.getChildren().addAll(title, subtitle);

        Owner owner = ownerController.getCurrentOwner();
        String currentPlanId = owner != null ? owner.getSubscriptionPlanId() : "";
        String currentPlanName = owner != null ? owner.getSubscriptionPlanName() : "";

        VBox currentCard = new VBox(6);
        currentCard.getStyleClass().add("card");
        currentCard.setPadding(new Insets(18));
        Label currentLabel = fieldLabel("Current plan");
        Label currentValue = new Label(
                currentPlanName != null && !currentPlanName.isEmpty() ? currentPlanName : "No active subscription"
        );
        currentValue.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:15px;-fx-font-weight:bold;");
        currentCard.getChildren().addAll(currentLabel, currentValue);

        VBox plansList = new VBox(12);

        List<SubscriptionPlan> plans = planController.getAllPlans();
        boolean hasActivePlan = false;
        for (SubscriptionPlan plan : plans) {
            if (plan.isActive()) {
                hasActivePlan = true;
                plansList.getChildren().add(
                        buildPlanCard(plan, plan.getId().equals(currentPlanId), currentCard, currentValue)
                );
            }
        }

        if (!hasActivePlan) {
            Label empty = new Label("No subscription plans are available right now.");
            empty.setStyle("-fx-text-fill:#64748b;-fx-font-size:12px;");
            plansList.getChildren().add(empty);
        }

        content.getChildren().addAll(backButton, pageHeader, currentCard, plansList);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("scroll-pane");
        return scrollPane;
    }

    private static VBox buildPlanCard(SubscriptionPlan plan, boolean isCurrent, VBox currentCard, Label currentValue) {

        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(18));

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(2);
        Label nameLabel = new Label(plan.getName());
        nameLabel.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:16px;-fx-font-weight:bold;");
        Label cycleLabel = new Label(
                "₹" + (int) plan.getPriceInr() + " / " + plan.getBillingCycle()
        );
        cycleLabel.setStyle("-fx-text-fill:#10b981;-fx-font-size:13px;-fx-font-weight:bold;");
        titleBox.getChildren().addAll(nameLabel, cycleLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button subscribeBtn = new Button(isCurrent ? "Current plan" : "Subscribe");
        subscribeBtn.getStyleClass().add(isCurrent ? "secondary-btn" : "primary-btn");
        subscribeBtn.setDisable(isCurrent);

        header.getChildren().addAll(titleBox, spacer, subscribeBtn);

        VBox benefitsBox = new VBox(4);
        for (String benefit : plan.getBenefits()) {
            Label benefitLabel = new Label("✓  " + benefit);
            benefitLabel.setStyle("-fx-text-fill:#cbd5e1;-fx-font-size:12px;");
            benefitsBox.getChildren().add(benefitLabel);
        }

        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill:#ef4444;-fx-font-size:11px;");
        statusLabel.setVisible(false);
        statusLabel.setManaged(false);

        card.getChildren().addAll(header, benefitsBox, statusLabel);

        subscribeBtn.setOnAction(e -> {
            subscribeBtn.setDisable(true);
            Thread updater = new Thread(() -> {
                boolean ok = ownerController.updateSubscription(plan.getId(), plan.getName());
                Platform.runLater(() -> {
                    if (ok) {
                        currentValue.setText(plan.getName());
                        subscribeBtn.setText("Current plan");
                    } else {
                        subscribeBtn.setDisable(false);
                        statusLabel.setText("Failed to subscribe. Please try again.");
                        statusLabel.setVisible(true);
                        statusLabel.setManaged(true);
                    }
                });
            });
            updater.setDaemon(true);
            updater.start();
        });

        return card;
    }

    private static Label fieldLabel(String text) {
        Label label = new Label(text);
        label.setStyle(
                "-fx-text-fill:#cbd5e1;" +
                "-fx-font-size:11px;" +
                "-fx-font-weight:bold;"
        );
        return label;
    }
}
