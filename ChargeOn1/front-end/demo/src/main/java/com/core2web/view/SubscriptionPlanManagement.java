package com.core2web.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import com.core2web.controller.SubscriptionPlanController;
import com.core2web.model.SubscriptionPlan;

import static com.core2web.view.AdminLayout.*;

public class SubscriptionPlanManagement {

    private static final SubscriptionPlanController planController =
            new SubscriptionPlanController();

    private static final Map<String, SubscriptionPlan> plansByName =
            new HashMap<>();

    public static void show() {
        AdminDashboard.openSubscriptionPlanManagement();
    }

    static ScrollPane buildMainContent() {

        VBox content = new VBox(16);
        content.setPadding(new Insets(18));

        Button backButton =
                new Button("←  Back to Revenue");

        backButton.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #94a3b8;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 6 0 6 0;" +
                "-fx-cursor: hand;"
        );

        backButton.setOnAction(
                e -> AdminDashboard.goTo("Revenue")
        );

        VBox pageHeader = new VBox(3);

        Label title =
                new Label("Subscription plan management");

        title.getStyleClass().add(
                "section-title"
        );

        Label subtitle =
                new Label(
                        "Configure plan details, pricing and subscriber information"
                );

        subtitle.getStyleClass().add(
                "card-sub"
        );

        pageHeader.getChildren().addAll(
                title,
                subtitle
        );

        HBox columns = new HBox(16);

        VBox leftColumn =
                buildPlanDetailsCard();

        HBox.setHgrow(
                leftColumn,
                Priority.ALWAYS
        );

        VBox rightColumn =
                new VBox(
                        16,
                        buildPlanSummaryCard(),
                        buildSubscribersCard()
                );

        HBox.setHgrow(
                rightColumn,
                Priority.ALWAYS
        );

        columns.getChildren().addAll(
                leftColumn,
                rightColumn
        );

        content.getChildren().addAll(
                backButton,
                pageHeader,
                columns
        );

        ScrollPane scrollPane =
                new ScrollPane(content);

        scrollPane.setFitToWidth(true);

        scrollPane.getStyleClass().add(
                "scroll-pane"
        );

        return scrollPane;
    }

    private static VBox buildPlanDetailsCard() {

        List<SubscriptionPlan> plans =
                planController.getAllPlans();

        VBox card =
                new VBox(14);

        card.getStyleClass().add("card");

        card.setPadding(
                new Insets(18)
        );

        Label title =
                new Label("Plan details");

        title.setStyle(
                "-fx-text-fill:#f8fafc;" +
                "-fx-font-size:16px;" +
                "-fx-font-weight:bold;"
        );

        VBox nameBox =
                new VBox(4);

        Label nameLabel =
                new Label("Plan name");

        nameLabel.setStyle(
                "-fx-text-fill:#64748b;" +
                "-fx-font-size:11px;"
        );

        ComboBox<String> planNameField =
                new ComboBox<>();

        planNameField.setEditable(true);

        planNameField.setMaxWidth(
                Double.MAX_VALUE
        );

        planNameField.setStyle(
                "-fx-background-color:#0b1420;" +
                "-fx-text-fill:#f8fafc;" +
                "-fx-font-size:13px;" +
                "-fx-border-color:#1e293b;" +
                "-fx-border-radius:8;" +
                "-fx-background-radius:8;"
        );

        nameBox.getChildren().addAll(
                nameLabel,
                planNameField
        );

        HBox priceRow =
                new HBox(14);

        VBox priceBox =
                new VBox(4);

        Label priceLabel =
                new Label("Price (₹)");

        priceLabel.setStyle(
                "-fx-text-fill:#64748b;" +
                "-fx-font-size:11px;"
        );

        ComboBox<String> priceField =
                new ComboBox<>();

        priceField.getItems().addAll(
                "4,999",
                "9,999",
                "14,999",
                "19,999",
                "24,999",
                "29,999"
        );

        priceField.setEditable(true);

        priceField.setMaxWidth(
                Double.MAX_VALUE
        );

        priceField.setStyle(
                "-fx-background-color:#0b1420;" +
                "-fx-text-fill:#f8fafc;" +
                "-fx-font-size:13px;" +
                "-fx-border-color:#1e293b;" +
                "-fx-border-radius:8;" +
                "-fx-background-radius:8;"
        );

        priceBox.getChildren().addAll(
                priceLabel,
                priceField
        );

        HBox.setHgrow(
                priceBox,
                Priority.ALWAYS
        );

        VBox cycleBox =
                new VBox(4);

        Label cycleLabel =
                new Label("Billing cycle");

        cycleLabel.setStyle(
                "-fx-text-fill:#64748b;" +
                "-fx-font-size:11px;"
        );

        ComboBox<String> billingCycle =
                new ComboBox<>();

        billingCycle.getItems().addAll(
                "Monthly",
                "Quarterly",
                "Yearly"
        );

        billingCycle.setValue(
                "Monthly"
        );

        billingCycle.setMaxWidth(
                Double.MAX_VALUE
        );

        billingCycle.setStyle(
                "-fx-background-color:#0b1420;" +
                "-fx-text-fill:#f8fafc;" +
                "-fx-font-size:13px;" +
                "-fx-border-color:#1e293b;" +
                "-fx-border-radius:8;" +
                "-fx-background-radius:8;"
        );

        cycleBox.getChildren().addAll(
                cycleLabel,
                billingCycle
        );

        HBox.setHgrow(
                cycleBox,
                Priority.ALWAYS
        );

        priceRow.getChildren().addAll(
                priceBox,
                cycleBox
        );

        VBox benefitsBox =
                new VBox(8);

        Label benefitsLabel =
                new Label("Benefits");

        benefitsLabel.setStyle(
                "-fx-text-fill:#64748b;" +
                "-fx-font-size:11px;"
        );

        VBox benefitsListBox =
                new VBox(8);

        benefitsBox.getChildren().addAll(
                benefitsLabel,
                benefitsListBox
        );

        Button addBenefitButton =
                new Button("+ Add benefit");

        addBenefitButton.setStyle(
                "-fx-background-color:transparent;" +
                "-fx-border-color:#10b981;" +
                "-fx-border-radius:8;" +
                "-fx-background-radius:8;" +
                "-fx-text-fill:#10b981;" +
                "-fx-font-size:12px;" +
                "-fx-font-weight:bold;" +
                "-fx-padding:7 16;" +
                "-fx-cursor:hand;"
        );

        addBenefitButton.setOnAction(
                e -> benefitsListBox.getChildren().add(
                        benefitInputItem(benefitsListBox)
                )
        );

        VBox statusBox =
                new VBox(6);

        Label statusTitle =
                new Label("Status");

        statusTitle.setStyle(
                "-fx-text-fill:#64748b;" +
                "-fx-font-size:11px;"
        );

        HBox statusRow =
                new HBox(10);

        statusRow.setAlignment(
                Pos.CENTER_LEFT
        );

        StackPane switchTrack =
                new StackPane();

        switchTrack.setPrefSize(
                36,
                20
        );

        switchTrack.setMaxSize(
                36,
                20
        );

        Circle thumb =
                new Circle(
                        8,
                        Color.WHITE
                );

        switchTrack.getChildren().add(
                thumb
        );

        Label statusText =
                new Label();

        final boolean[] active =
                { true };

        setSwitchState(
                switchTrack,
                thumb,
                statusText,
                true
        );

        switchTrack.setOnMouseClicked(
                e -> {
                    active[0] =
                            !active[0];

                    setSwitchState(
                            switchTrack,
                            thumb,
                            statusText,
                            active[0]
                    );
                }
        );

        statusRow.getChildren().addAll(
                switchTrack,
                statusText
        );

        statusBox.getChildren().addAll(
                statusTitle,
                statusRow
        );

        Label saveMessage =
                new Label();

        saveMessage.setStyle(
                "-fx-text-fill:#ef4444;" +
                "-fx-font-size:11px;" +
                "-fx-font-weight:bold;"
        );

        saveMessage.setVisible(false);

        saveMessage.setManaged(false);

        Button saveButton =
                new Button("Save plan");

        saveButton.getStyleClass().add(
                "primary-btn"
        );

        Button cancelButton =
                new Button("Cancel");

        cancelButton.getStyleClass().add(
                "secondary-btn"
        );

        cancelButton.setOnAction(e -> {

            String selectedName =
                    planNameField.getValue();

            SubscriptionPlan selectedPlan =
                    selectedName == null
                            ? null
                            : plansByName.get(selectedName);

            if (selectedPlan == null) {
                AdminDashboard.goTo("Revenue");
                return;
            }

            if (selectedPlan.getId() == null ||
                    selectedPlan.getId().isBlank()) {

                AdminDashboard.goTo("Revenue");
                return;
            }

            cancelButton.setDisable(true);

            Thread deleter =
                    new Thread(() -> {

                        boolean deleted =
                                planController.deletePlan(
                                        selectedPlan.getId()
                                );

                        Platform.runLater(() -> {

                            cancelButton.setDisable(false);

                            if (deleted) {

                                plansByName.remove(
                                        selectedPlan.getName()
                                );

                                AdminDashboard.goTo(
                                        "Revenue"
                                );

                            } else {

                                showSaveMessage(
                                        saveMessage,
                                        "Failed to delete plan. Please try again.",
                                        false
                                );
                            }
                        });
                    });

            deleter.setDaemon(true);

            deleter.start();
        });

        HBox actionButtons =
                new HBox(
                        10,
                        cancelButton,
                        saveButton
                );

        actionButtons.setAlignment(
                Pos.CENTER_RIGHT
        );

        planNameField.valueProperty().addListener(
                (obs, oldV, newV) -> {

                    if (newV == null) {
                        return;
                    }

                    SubscriptionPlan plan =
                            plansByName.get(newV);

                    if (plan == null) {
                        return;
                    }

                    priceField.setValue(
                            String.valueOf(
                                    (int) plan.getPriceInr()
                            )
                    );

                    billingCycle.setValue(
                            plan.getBillingCycle()
                    );

                    active[0] =
                            plan.isActive();

                    setSwitchState(
                            switchTrack,
                            thumb,
                            statusText,
                            plan.isActive()
                    );

                    benefitsListBox.getChildren().clear();

                    for (String benefit :
                            plan.getBenefits()) {

                        benefitsListBox.getChildren().add(
                                benefitItem(
                                        benefit,
                                        benefitsListBox
                                )
                        );
                    }
                }
        );

        saveButton.setOnAction(e -> {

            String name =
                    planNameField.getEditor().getText();

            if (name == null ||
                    name.isBlank()) {

                showSaveMessage(
                        saveMessage,
                        "Plan name is required.",
                        false
                );

                return;
            }

            String finalName =
                    name.trim();

            double price;

            try {

                String priceText =
                        priceField
                                .getEditor()
                                .getText();

                price =
                        Double.parseDouble(
                                priceText
                                        .replace(",", "")
                                        .trim()
                        );

            } catch (Exception ex) {

                showSaveMessage(
                        saveMessage,
                        "Enter a valid price.",
                        false
                );

                return;
            }

            String cycle =
                    billingCycle.getValue();

            List<String> benefits =
                    readBenefits(
                            benefitsListBox
                    );

            boolean isActive =
                    active[0];

            SubscriptionPlan existing =
                    plansByName.get(
                            finalName
                    );

            saveButton.setDisable(true);

            Thread saver =
                    new Thread(() -> {

                        boolean ok;

                        if (existing != null) {

                            existing.setPriceInr(
                                    price
                            );

                            existing.setBillingCycle(
                                    cycle
                            );

                            existing.setBenefits(
                                    benefits
                            );

                            existing.setActive(
                                    isActive
                            );

                            ok =
                                    planController.updatePlan(
                                            existing
                                    );

                        } else {

                            ok =
                                    planController.createPlan(
                                            finalName,
                                            price,
                                            cycle,
                                            benefits,
                                            isActive
                                    ) != null;
                        }

                        List<SubscriptionPlan> refreshed =
                                planController.getAllPlans();

                        Platform.runLater(() -> {

                            saveButton.setDisable(false);

                            if (ok) {

                                refreshPlanList(
                                        planNameField,
                                        refreshed,
                                        finalName
                                );

                                showSaveMessage(
                                        saveMessage,
                                        "Plan saved.",
                                        true
                                );

                            } else {

                                showSaveMessage(
                                        saveMessage,
                                        "Failed to save plan. Please try again.",
                                        false
                                );
                            }
                        });

                    });

            saver.setDaemon(true);

            saver.start();
        });

        card.getChildren().addAll(
                title,
                nameBox,
                priceRow,
                benefitsBox,
                addBenefitButton,
                statusBox,
                saveMessage,
                actionButtons
        );

        refreshPlanList(
                planNameField,
                plans,
                plans.isEmpty()
                        ? null
                        : plans.get(0).getName()
        );

        benefitsListBox.getChildren().clear();

        if (!plans.isEmpty()) {

            SubscriptionPlan first =
                    plans.get(0);

            priceField.setValue(
                    String.valueOf(
                            (int) first.getPriceInr()
                    )
            );

            billingCycle.setValue(
                    first.getBillingCycle()
            );

            active[0] =
                    first.isActive();

            setSwitchState(
                    switchTrack,
                    thumb,
                    statusText,
                    first.isActive()
            );

            for (String benefit :
                    first.getBenefits()) {

                benefitsListBox.getChildren().add(
                        benefitItem(
                                benefit,
                                benefitsListBox
                        )
                );
            }

        } else {

            setSwitchState(
                    switchTrack,
                    thumb,
                    statusText,
                    true
            );
        }

        return card;
    }

    private static void refreshPlanList(
            ComboBox<String> planNameField,
            List<SubscriptionPlan> plans,
            String selectName
    ) {

        plansByName.clear();

        planNameField.getItems().clear();

        for (SubscriptionPlan plan :
                plans) {

            plansByName.put(
                    plan.getName(),
                    plan
            );

            planNameField.getItems().add(
                    plan.getName()
            );
        }

        if (selectName != null) {

            planNameField.setValue(
                    selectName
            );
        }
    }

    private static void setSwitchState(
            StackPane switchTrack,
            Circle thumb,
            Label statusText,
            boolean isActive
    ) {

        if (isActive) {

            switchTrack.setStyle(
                    "-fx-background-color:#10b981;" +
                    "-fx-background-radius:12;" +
                    "-fx-cursor:hand;"
            );

            thumb.setTranslateX(8);

            statusText.setStyle(
                    "-fx-text-fill:#f8fafc;" +
                    "-fx-font-size:13px;"
            );

            statusText.setText(
                    "Active"
            );

        } else {

            switchTrack.setStyle(
                    "-fx-background-color:#475569;" +
                    "-fx-background-radius:12;" +
                    "-fx-cursor:hand;"
            );

            thumb.setTranslateX(-8);

            statusText.setStyle(
                    "-fx-text-fill:#f8fafc;" +
                    "-fx-font-size:13px;"
            );

            statusText.setText(
                    "Inactive"
            );
        }
    }

    private static void showSaveMessage(
            Label saveMessage,
            String text,
            boolean success
    ) {

        saveMessage.setStyle(
                success
                        ? "-fx-text-fill:#10b981;" +
                          "-fx-font-size:11px;" +
                          "-fx-font-weight:bold;"
                        : "-fx-text-fill:#ef4444;" +
                          "-fx-font-size:11px;" +
                          "-fx-font-weight:bold;"
        );

        saveMessage.setText(
                text
        );

        saveMessage.setVisible(
                true
        );

        saveMessage.setManaged(
                true
        );
    }

    private static List<String> readBenefits(
            VBox benefitsListBox
    ) {

        List<String> result =
                new ArrayList<>();

        for (javafx.scene.Node node :
                benefitsListBox.getChildren()) {

            if (!(node instanceof HBox row)) {
                continue;
            }

            for (javafx.scene.Node child :
                    row.getChildren()) {

                if (child instanceof ComboBox<?> cb) {

                    String v =
                            cb.getEditor().getText();

                    if (v != null &&
                            !v.isBlank()) {

                        result.add(
                                v.trim()
                        );
                    }

                    break;
                }

                if (child instanceof Label lbl &&
                        !"⋮⋮".equals(lbl.getText())) {

                    result.add(
                            lbl.getText()
                    );

                    break;
                }
            }
        }

        return result;
    }

    private static HBox benefitItem(
            String text,
            VBox benefitsBox
    ) {

        HBox box =
                new HBox(10);

        box.setAlignment(
                Pos.CENTER_LEFT
        );

        box.setPadding(
                new Insets(
                        8,
                        12,
                        8,
                        12
                )
        );

        box.setStyle(
                "-fx-background-color:#0b1420;" +
                "-fx-background-radius:8;" +
                "-fx-border-color:#1e293b;" +
                "-fx-border-radius:8;"
        );

        Label dragHandle =
                new Label("⋮⋮");

        dragHandle.setStyle(
                "-fx-text-fill:#475569;" +
                "-fx-font-size:14px;"
        );

        Label name =
                new Label(text);

        name.setStyle(
                "-fx-text-fill:#f8fafc;" +
                "-fx-font-size:13px;"
        );

        HBox.setHgrow(
                name,
                Priority.ALWAYS
        );

        Button close =
                new Button("✕");

        close.setStyle(
                "-fx-background-color:transparent;" +
                "-fx-text-fill:#64748b;" +
                "-fx-font-size:12px;" +
                "-fx-cursor:hand;"
        );

        close.setOnAction(
                e -> benefitsBox.getChildren().remove(
                        box
                )
        );

        box.getChildren().addAll(
                dragHandle,
                name,
                close
        );

        return box;
    }

    private static HBox benefitInputItem(
            VBox benefitsBox
    ) {

        HBox box =
                new HBox(10);

        box.setAlignment(
                Pos.CENTER_LEFT
        );

        box.setPadding(
                new Insets(
                        8,
                        12,
                        8,
                        12
                )
        );

        box.setStyle(
                "-fx-background-color:#0b1420;" +
                "-fx-background-radius:8;" +
                "-fx-border-color:#10b981;" +
                "-fx-border-radius:8;"
        );

        ComboBox<String> benefitField =
                new ComboBox<>();

        benefitField.getItems().addAll(
                "Unlimited bookings",
                "Priority support",
                "Advanced analytics",
                "Multi-user access",
                "Dedicated account manager",
                "Real-time fleet reports",
                "24/7 charging support",
                "Fleet performance reports"
        );

        benefitField.setEditable(
                true
        );

        benefitField.setPromptText(
                "Type or select a benefit"
        );

        benefitField.setMaxWidth(
                Double.MAX_VALUE
        );

        benefitField.setStyle(
                "-fx-background-color:#0b1420;" +
                "-fx-text-fill:#f8fafc;" +
                "-fx-font-size:13px;" +
                "-fx-border-color:#1e293b;" +
                "-fx-border-radius:8;" +
                "-fx-background-radius:8;"
        );

        HBox.setHgrow(
                benefitField,
                Priority.ALWAYS
        );

        Button close =
                new Button("✕");

        close.setStyle(
                "-fx-background-color:transparent;" +
                "-fx-text-fill:#64748b;" +
                "-fx-font-size:12px;" +
                "-fx-cursor:hand;"
        );

        close.setOnAction(
                e -> benefitsBox.getChildren().remove(
                        box
                )
        );

        box.getChildren().addAll(
                benefitField,
                close
        );

        return box;
    }

    private static VBox buildPlanSummaryCard() {

        VBox card =
                new VBox(12);

        card.getStyleClass().add(
                "card"
        );

        card.setPadding(
                new Insets(18)
        );

        Label title =
                new Label("Plan summary");

        title.setStyle(
                "-fx-text-fill:#f8fafc;" +
                "-fx-font-size:15px;" +
                "-fx-font-weight:bold;"
        );

        HBox stats =
                new HBox(16);

        VBox stat1 =
                summaryStat(
                        "₹0",
                        "#10b981",
                        "MRR contribution",
                        "No subscriptions tracked yet"
                );

        VBox stat2 =
                summaryStat(
                        "0",
                        "#f8fafc",
                        "Total subscribers",
                        "Plans aren't linked to accounts yet"
                );

        VBox stat3 =
                summaryStat(
                        "₹0",
                        "#f8fafc",
                        "Avg. revenue / user",
                        "Per month"
                );

        HBox.setHgrow(
                stat1,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                stat2,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                stat3,
                Priority.ALWAYS
        );

        stats.getChildren().addAll(
                stat1,
                stat2,
                stat3
        );

        card.getChildren().addAll(
                title,
                stats
        );

        return card;
    }

    private static VBox summaryStat(
            String value,
            String valueColor,
            String title,
            String subtitle
    ) {

        VBox box =
                new VBox(4);

        Label valueLabel =
                new Label(value);

        valueLabel.setStyle(
                "-fx-text-fill:" +
                valueColor +
                ";" +
                "-fx-font-size:24px;" +
                "-fx-font-weight:bold;"
        );

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-text-fill:#64748b;" +
                "-fx-font-size:11px;"
        );

        Label subtitleLabel =
                new Label(subtitle);

        subtitleLabel.setStyle(
                "-fx-text-fill:#94a3b8;" +
                "-fx-font-size:11px;"
        );

        box.getChildren().addAll(
                valueLabel,
                titleLabel,
                subtitleLabel
        );

        return box;
    }

    private static VBox buildSubscribersCard() {

        VBox card =
                new VBox(12);

        card.getStyleClass().add(
                "card"
        );

        card.setPadding(
                new Insets(18)
        );

        Label title =
                new Label("Subscribers");

        title.setStyle(
                "-fx-text-fill:#f8fafc;" +
                "-fx-font-size:15px;" +
                "-fx-font-weight:bold;"
        );

        Label empty =
                new Label(
                        "No subscription assignments exist yet — plans aren't linked to customer accounts."
                );

        empty.setStyle(
                "-fx-text-fill:#64748b;" +
                "-fx-font-size:12px;"
        );

        empty.setWrapText(
                true
        );

        card.getChildren().addAll(
                title,
                empty
        );

        return card;
    }
}