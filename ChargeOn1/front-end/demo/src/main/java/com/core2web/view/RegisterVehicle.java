package com.core2web.view;

import java.io.File;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import com.core2web.controller.VehicleController;
import com.core2web.model.Vehicle;
import com.core2web.service.CloudinaryService;

public class RegisterVehicle {

    private static final VehicleController vehicleController = new VehicleController();
    private static final CloudinaryService cloudinaryService = new CloudinaryService();

    static ScrollPane buildMainContent() {
        return buildMainContent(null);
    }

    static ScrollPane buildMainContent(Vehicle existing) {

        boolean editing = existing != null;

        VBox content = new VBox(18);
        content.setPadding(new Insets(20));

        Label backLink = new Label("← Back to My Vehicles");
        backLink.setStyle("-fx-text-fill:#94a3b8;-fx-font-size:12px;-fx-cursor:hand;");
        backLink.setOnMouseClicked(e -> {
            OwnerDashboard.heading.setText("My vehicles");
            OwnerDashboard.subheading.setText("Registered EVs under this account");
            OwnerDashboard.middleBox.getChildren().setAll(MyVehicles.buildMainContent());
        });

        Label pageTitle = new Label(editing ? "Edit vehicle" : "Add vehicle");
        pageTitle.setStyle("-fx-text-fill:#F8FAFC;-fx-font-size:22px;-fx-font-weight:bold;");
        Label pageSub = new Label(editing ? "Update your vehicle details" : "Enter your vehicle details");
        pageSub.setStyle("-fx-text-fill:#64748b;-fx-font-size:13px;");

        HBox mainRow = new HBox(30);

        VBox formCol = new VBox(18);
        HBox.setHgrow(formCol, Priority.ALWAYS);

        HBox makeModelRow = new HBox(18);

        VBox vbleft = new VBox(6);
        Label comp = new Label("Company");
        comp.setStyle("-fx-text-fill:#94a3b8;-fx-font-size:12px;");
        ComboBox<String> makeCombo = new ComboBox<>();
        makeCombo.setPromptText("Select Vehicle");
        makeCombo.getItems().addAll("Tata", "MG", "Hyundai", "Mahindra", "BYD", "Ather");
        makeCombo.setMaxWidth(Double.MAX_VALUE);
        makeCombo.getStyleClass().add("form-combo");
        vbleft.getChildren().addAll(comp, makeCombo);

        VBox vbright = new VBox(6);
        Label modelLbl = new Label("Model");
        modelLbl.setStyle("-fx-text-fill:#94a3b8;-fx-font-size:12px;");
        ComboBox<String> modelCombo = new ComboBox<>();
        modelCombo.setPromptText("Select Vehicle");
        modelCombo.getItems().addAll("Nexon EV", "Tigor EV", "Punch EV", "Curvv EV", "ZS EV", "450X");
        modelCombo.setMaxWidth(Double.MAX_VALUE);
        modelCombo.getStyleClass().add("form-combo");
        vbright.getChildren().addAll(modelLbl, modelCombo);

        HBox.setHgrow(vbleft, Priority.ALWAYS);
        HBox.setHgrow(vbright, Priority.ALWAYS);
        makeModelRow.getChildren().addAll(vbleft, vbright);

        VBox plateBox = new VBox(6);
        Label plateLbl = new Label("Plate number");
        plateLbl.setStyle("-fx-text-fill:#94a3b8;-fx-font-size:12px;");
        TextField plateField = new TextField();
        plateField.setPromptText("MH12 AB 1234");
        plateField.setStyle(
                "-fx-background-color:#1e293b;-fx-text-fill:#F8FAFC;-fx-font-size:14px;-fx-background-radius:8;-fx-border-color:#334155;-fx-border-radius:8;-fx-padding:10 14;");
        plateField.setMaxWidth(Double.MAX_VALUE);
        plateBox.getChildren().addAll(plateLbl, plateField);

        HBox connBattRow = new HBox(18);

        VBox connBox = new VBox(6);
        Label connectorLabel = new Label("Connector type");
        connectorLabel.setStyle("-fx-text-fill:#94a3b8;-fx-font-size:12px;");
        ComboBox<String> connectorCombo = new ComboBox<>();
        connectorCombo.setPromptText("Select connector type");
        connectorCombo.getItems().addAll("CCS2", "Type 2 AC", "CHAdeMO", "GB/T");
        connectorCombo.setMaxWidth(Double.MAX_VALUE);
        connectorCombo.getStyleClass().add("form-combo");
        connBox.getChildren().addAll(connectorLabel, connectorCombo);

        VBox battBox = new VBox(6);
        Label batteryLabel = new Label("Battery capacity");
        batteryLabel.setStyle("-fx-text-fill:#94a3b8;-fx-font-size:12px;");
        ComboBox<String> batteryCombo = new ComboBox<>();
        batteryCombo.setPromptText("Select battery capacity");
        batteryCombo.getItems().addAll("3.7 kWh", "25 kWh", "30 kWh", "40.5 kWh", "50.3 kWh", "72 kWh");
        batteryCombo.setMaxWidth(Double.MAX_VALUE);
        batteryCombo.getStyleClass().add("form-combo");
        battBox.getChildren().addAll(batteryLabel, batteryCombo);

        HBox.setHgrow(connBox, Priority.ALWAYS);
        HBox.setHgrow(battBox, Priority.ALWAYS);
        connBattRow.getChildren().addAll(connBox, battBox);

        VBox primaryBox = new VBox(4);
        primaryBox.setPadding(new Insets(14));
        primaryBox.setStyle(
                "-fx-background-color:#1e293b;-fx-background-radius:10;-fx-border-color:#334155;-fx-border-radius:10;");
        CheckBox primaryCb = new CheckBox("Set as primary vehicle");
        primaryCb.setStyle("-fx-text-fill:#F8FAFC;-fx-font-size:13px;-fx-font-weight:bold;");
        Label primarySub = new Label("This will be your default vehicle for bookings.");
        primarySub.setStyle("-fx-text-fill:#64748b;-fx-font-size:11px;");
        primarySub.setPadding(new Insets(0, 0, 0, 24));
        primaryBox.getChildren().addAll(primaryCb, primarySub);

        Label errorLbl = new Label();
        errorLbl.setStyle("-fx-text-fill:#ef4444;-fx-font-size:12px;");
        errorLbl.setWrapText(true);
        errorLbl.setVisible(false);
        errorLbl.setManaged(false);

        HBox btnRow = new HBox(14);
        btnRow.setPadding(new Insets(10, 0, 0, 0));
        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle(
                "-fx-background-color:#1e293b;-fx-text-fill:#F8FAFC;-fx-font-size:13px;-fx-background-radius:8;-fx-padding:10 30;-fx-cursor:hand;-fx-border-color:#334155;-fx-border-radius:8;");
        cancelBtn.setOnAction(e -> OwnerDashboard.middleBox.getChildren().setAll(MyVehicles.buildMainContent()));

        Button saveBtn = new Button(editing ? "Save changes" : "Save vehicle");
        saveBtn.setStyle(
                "-fx-background-color:#EF4444;-fx-text-fill:white;-fx-font-size:13px;-fx-font-weight:bold;-fx-background-radius:8;-fx-padding:10 30;-fx-cursor:hand;");
        btnRow.getChildren().addAll(cancelBtn, saveBtn);

        formCol.getChildren().addAll(makeModelRow, plateBox, connBattRow, primaryBox, errorLbl, btnRow);

        VBox photoCol = new VBox(14);
        photoCol.setPrefWidth(320);
        photoCol.setMinWidth(280);

        Label photoTitle = new Label("Vehicle photo");
        photoTitle.setStyle("-fx-text-fill:#F8FAFC;-fx-font-size:16px;-fx-font-weight:bold;");
        Label photoSub = new Label("Upload a clear photo of your vehicle");
        photoSub.setStyle("-fx-text-fill:#64748b;-fx-font-size:12px;");

        VBox uploadArea = new VBox(8);
        uploadArea.setAlignment(Pos.CENTER);
        uploadArea.setPrefHeight(160);
        uploadArea.setCursor(Cursor.HAND);
        uploadArea.setStyle(
                "-fx-background-color:#1e293b;-fx-background-radius:12;-fx-border-color:#334155;-fx-border-radius:12;-fx-border-style:dashed;-fx-border-width:2;");
        Label carIcon = new Label("🚗");
        carIcon.setStyle("-fx-font-size:36px;");
        Label clickLbl = new Label("Click to upload");
        clickLbl.setStyle("-fx-text-fill:#F8FAFC;-fx-font-size:13px;-fx-font-weight:bold;");
        Label formatLbl = new Label("PNG, JPG up to 5MB");
        formatLbl.setStyle("-fx-text-fill:#64748b;-fx-font-size:11px;");
        uploadArea.getChildren().addAll(carIcon, clickLbl, formatLbl);

        StackPane previewPane = new StackPane();
        previewPane.setPrefHeight(180);
        previewPane.setStyle("-fx-background-color:#1e293b;-fx-background-radius:12;");
        Label previewCar = new Label("🚗");
        previewCar.setStyle("-fx-font-size:60px;");
        ImageView previewImage = new ImageView();
        previewImage.setFitWidth(280);
        previewImage.setFitHeight(180);
        previewImage.setPreserveRatio(false);
        previewImage.setVisible(false);
        previewPane.getChildren().addAll(previewCar, previewImage);

        String[] photoUrlHolder = { editing && existing.getPhotoUrl() != null ? existing.getPhotoUrl() : "" };

        if (editing && existing.getPhotoUrl() != null && !existing.getPhotoUrl().isEmpty()) {
            loadPreviewImage(existing.getPhotoUrl(), previewImage, previewCar);
        }

        uploadArea.setOnMouseClicked(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Vehicle Photo");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.webp"));
            Window window = uploadArea.getScene() != null ? uploadArea.getScene().getWindow() : null;
            File selected = fileChooser.showOpenDialog(window);
            if (selected == null) return;

            clickLbl.setText("Uploading…");
            Thread uploader = new Thread(() -> {
                String url = cloudinaryService.uploadImage(selected.toPath());
                Platform.runLater(() -> {
                    clickLbl.setText("Click to upload");
                    if (url == null) {
                        showError(errorLbl, "Photo upload failed. Please try again.");
                        return;
                    }
                    photoUrlHolder[0] = url;
                    loadPreviewImage(url, previewImage, previewCar);
                });
            });
            uploader.setDaemon(true);
            uploader.start();
        });

        HBox removeRow = new HBox(6);
        removeRow.setAlignment(Pos.CENTER_RIGHT);
        if (editing) {
            Label trashIcon = new Label("🗑");
            trashIcon.setStyle("-fx-text-fill:#EF4444;-fx-font-size:13px;");
            Label removeLbl = new Label("Remove vehicle");
            removeLbl.setStyle("-fx-text-fill:#EF4444;-fx-font-size:13px;-fx-cursor:hand;-fx-font-weight:bold;");
            removeLbl.setCursor(Cursor.HAND);
            removeLbl.setOnMouseClicked(e -> {
                removeLbl.setDisable(true);
                Thread remover = new Thread(() -> {
                    boolean ok = vehicleController.deleteVehicle(existing.getId());
                    Platform.runLater(() -> {
                        if (ok) {
                            OwnerDashboard.middleBox.getChildren().setAll(MyVehicles.buildMainContent());
                        } else {
                            removeLbl.setDisable(false);
                            showError(errorLbl, "Failed to remove vehicle. Please try again.");
                        }
                    });
                });
                remover.setDaemon(true);
                remover.start();
            });
            removeRow.getChildren().addAll(trashIcon, removeLbl);
        }

        photoCol.getChildren().addAll(photoTitle, photoSub, uploadArea, previewPane, removeRow);

        if (editing) {
            makeCombo.setValue(existing.getMake());
            modelCombo.setValue(existing.getModel());
            plateField.setText(existing.getPlateNumber());
            connectorCombo.setValue(existing.getConnectorType());
            batteryCombo.setValue(formatKwh(existing.getBatteryCapacityKwh()));
            primaryCb.setSelected(existing.isPrimary());
        }

        saveBtn.setOnAction(e -> {
            String make = makeCombo.getValue();
            String model = modelCombo.getValue();
            String plate = plateField.getText() == null ? "" : plateField.getText().trim();
            String connector = connectorCombo.getValue();
            String batteryStr = batteryCombo.getValue();

            if (make == null || model == null || plate.isEmpty() || connector == null || batteryStr == null) {
                showError(errorLbl, "Please fill in all fields before saving.");
                return;
            }

            double batteryKwh = parseKwh(batteryStr);
            boolean isPrimary = primaryCb.isSelected();
            String photoUrl = photoUrlHolder[0];

            saveBtn.setDisable(true);
            cancelBtn.setDisable(true);

            Thread saver = new Thread(() -> {
                boolean ok;
                if (editing) {
                    existing.setMake(make);
                    existing.setModel(model);
                    existing.setPlateNumber(plate);
                    existing.setConnectorType(connector);
                    existing.setBatteryCapacityKwh(batteryKwh);
                    existing.setPrimary(isPrimary);
                    existing.setPhotoUrl(photoUrl);
                    ok = vehicleController.updateVehicle(existing);
                } else {
                    ok = vehicleController.addVehicle(
                            make, model, plate, connector, batteryKwh, isPrimary, photoUrl) != null;
                }

                boolean saved = ok;
                Platform.runLater(() -> {
                    if (saved) {
                        OwnerDashboard.middleBox.getChildren().setAll(MyVehicles.buildMainContent());
                    } else {
                        saveBtn.setDisable(false);
                        cancelBtn.setDisable(false);
                        showError(errorLbl, "Failed to save vehicle. Please try again.");
                    }
                });
            });
            saver.setDaemon(true);
            saver.start();
        });

        mainRow.getChildren().addAll(formCol, photoCol);
        content.getChildren().addAll(backLink, pageTitle, pageSub, mainRow);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("scroll-pane");
        scrollPane.setStyle("-fx-background-color:transparent;");
        return scrollPane;
    }

    private static void showError(Label errorLbl, String message) {
        errorLbl.setText(message);
        errorLbl.setVisible(true);
        errorLbl.setManaged(true);
    }

    private static String formatKwh(double kwh) {
        String num = kwh == Math.floor(kwh) ? String.valueOf((int) kwh) : String.valueOf(kwh);
        return num + " kWh";
    }

    private static double parseKwh(String text) {
        try {
            return Double.parseDouble(text.replaceAll("[^0-9.]", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    private static void loadPreviewImage(String imageUrl, ImageView imageView, Label fallbackIcon) {
        try {
            Image image = new Image(imageUrl, 280, 180, false, true, true);
            if (image.isError()) {
                imageView.setVisible(false);
                fallbackIcon.setVisible(true);
                return;
            }
            imageView.setImage(image);
            imageView.setVisible(true);
            fallbackIcon.setVisible(false);
        } catch (Exception e) {
            imageView.setVisible(false);
            fallbackIcon.setVisible(true);
        }
    }
}
