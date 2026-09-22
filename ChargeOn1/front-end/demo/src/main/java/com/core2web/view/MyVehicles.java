package com.core2web.view;

import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import com.core2web.controller.VehicleController;
import com.core2web.model.Vehicle;

public class MyVehicles {

    private static final VehicleController vehicleController = new VehicleController();

    Scene getMyVehiclesScene() {
        OwnerDashboard.goTo("My Vehicles");
        return OwnerDashboard.scene;
    }

    static ScrollPane buildMainContent() {
        return new MyVehicles().buildContent();
    }

    private ScrollPane buildContent() {
        VBox content = new VBox(18);
        content.setPadding(new Insets(20));

        List<Vehicle> vehicles = vehicleController.getMyVehicles();

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label sectionTitle = new Label("My vehicles");
        sectionTitle.setStyle("-fx-text-fill:#F8FAFC;-fx-font-size:18px;-fx-font-weight:bold;");
        Region hsp = new Region();
        HBox.setHgrow(hsp, Priority.ALWAYS);
        Button registerBtn = new Button("+ Register vehicle");
        registerBtn.getStyleClass().add("dashboard-active-session");
        registerBtn.setOnAction(e -> {
            OwnerDashboard.heading.setText("Add vehicle");
            OwnerDashboard.subheading.setText("Enter your vehicle details");
            OwnerDashboard.middleBox.getChildren().setAll(RegisterVehicle.buildMainContent());
        });
        header.getChildren().addAll(sectionTitle, hsp, registerBtn);

        HBox vehicleGrid = new HBox(18);
        vehicleGrid.setPadding(new Insets(0, 0, 0, 0));

        if (vehicles.isEmpty()) {
            Label empty = new Label("No vehicles registered yet. Click \"+ Register vehicle\" to add one.");
            empty.setStyle("-fx-text-fill:#64748b;-fx-font-size:13px;");
            vehicleGrid.getChildren().add(empty);
        } else {
            for (Vehicle v : vehicles) {
                VBox card = buildVehicleCard(v);
                HBox.setHgrow(card, Priority.ALWAYS);
                vehicleGrid.getChildren().add(card);
            }
        }

        content.getChildren().addAll(header, vehicleGrid);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("scroll-pane");
        scrollPane.setStyle("-fx-background-color:transparent;");

        return scrollPane;
    }

    private VBox buildVehicleCard(Vehicle v) {
        VBox card = new VBox(10);
        card.getStyleClass().addAll("card", "vehicle-card");
        card.setPadding(new Insets(18));

        StackPane iconArea = new StackPane();
        iconArea.setPrefHeight(100);
        iconArea.setStyle("-fx-background-color:#1e293b;-fx-background-radius:10;");

        Label iconLbl = new Label("🚗");
        iconLbl.setStyle("-fx-font-size:36px;");
        iconArea.getChildren().add(iconLbl);

        if (v.getPhotoUrl() != null && !v.getPhotoUrl().isEmpty()) {
            try {
                Image image = new Image(v.getPhotoUrl(), 0, 100, true, true, true);
                if (!image.isError()) {
                    ImageView imageView = new ImageView(image);
                    imageView.setFitHeight(100);
                    imageView.setPreserveRatio(true);
                    iconArea.getChildren().setAll(imageView);
                }
            } catch (Exception ignored) {
            }
        }

        HBox nameRow = new HBox(8);
        nameRow.setAlignment(Pos.CENTER_LEFT);
        Label nameLbl = new Label(v.displayName());
        nameLbl.setStyle("-fx-text-fill:#F8FAFC;-fx-font-size:16px;-fx-font-weight:bold;");
        nameRow.getChildren().add(nameLbl);
        if (v.isPrimary()) {
            Label tagLbl = new Label("PRIMARY");
            tagLbl.setStyle(
                    "-fx-text-fill:#10b981;-fx-font-size:10px;-fx-font-weight:bold;-fx-background-color:rgba(34,197,94,0.15);-fx-padding:2 8 2 8;-fx-background-radius:4;");
            nameRow.getChildren().add(tagLbl);
        }

        Label detLbl = new Label(v.detailLine());
        detLbl.setStyle("-fx-text-fill:#64748b;-fx-font-size:11px;");
        detLbl.setWrapText(true);

        HBox btns = new HBox(10);
        Button manageBtn = new Button("Manage");
        manageBtn.getStyleClass().add("secondary-btn");
        manageBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(manageBtn, Priority.ALWAYS);
        manageBtn.setOnAction(e -> {
            OwnerDashboard.heading.setText("Edit vehicle");
            OwnerDashboard.subheading.setText("Update your vehicle details");
            OwnerDashboard.middleBox.getChildren().setAll(RegisterVehicle.buildMainContent(v));
        });

        Button chargeBtn = new Button("Charge");
        chargeBtn.getStyleClass().add("primary-btn");
        chargeBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(chargeBtn, Priority.ALWAYS);
        BookCharging bk = new BookCharging();
        chargeBtn.setOnAction(e -> OwnerDashboard.goTo("Book Charging"));

        btns.getChildren().addAll(manageBtn, chargeBtn);

        card.getChildren().addAll(iconArea, nameRow, detLbl, btns);
        return card;
    }
}
