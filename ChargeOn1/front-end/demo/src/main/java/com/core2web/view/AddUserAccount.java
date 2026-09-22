package com.core2web.view;

import java.util.List;

import com.core2web.controller.BusController;
import com.core2web.controller.DriverController;
import com.core2web.model.Bus;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import static com.core2web.view.AdminLayout.*;

public class AddUserAccount {

    private final Stage stage;

    private final BusController busController = new BusController();

    public AddUserAccount(Stage stage, boolean driverMode) {
        this.stage = stage;
    }


    public ScrollPane getMainContent() {

        VBox content = new VBox(16);

        content.setPadding(
                new Insets(16)
        );


        Button backButton =
                new Button("←  Back to Users & Drivers");

        backButton.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #94a3b8;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 6 0 6 0;" +
                "-fx-cursor: hand;"
        );

        backButton.setOnAction(
                e -> goBack()
        );


        VBox pageHeader =
                new VBox(4);

        String pageTitle =
                "Add Driver";

        String pageSubtitle =
                "Register a new driver account and assign operational details.";

        Label title =
                new Label(pageTitle);

        title.getStyleClass().add(
                "section-title"
        );

        Label subtitle =
                new Label(pageSubtitle);

        subtitle.getStyleClass().add(
                "card-sub"
        );

        pageHeader.getChildren().addAll(
                title,
                subtitle
        );


        VBox formCard =
                new VBox(14);

        formCard.getStyleClass().add(
                "card"
        );

        formCard.setPadding(
                new Insets(20)
        );


        Label basicTitle =
                new Label("Basic information");

        basicTitle.getStyleClass().add(
                "section-title"
        );

        Label basicSubtitle =
                new Label(
                        "Enter the information required to create the account."
                );

        basicSubtitle.getStyleClass().add(
                "card-sub"
        );

        TextField nameField =
                createField(
                        "Driver name"
                );

        TextField phoneField =
                createField(
                        "Phone number"
                );

        TextField emailField =
                createField(
                        "Email address"
                );


        VBox driverFields =
                new VBox(14);

        Label operationalTitle =
                new Label(
                        "Operational details"
                );

        operationalTitle.getStyleClass().add(
                "section-title"
        );

        TextField depotField =
                createField(
                        "Depot / location"
                );

        ComboBox<Bus> busCombo =
                createBusCombo();

        TextField shiftField =
                createField(
                        "Shift"
                );

        TextField passwordField =
                createField(
                        "Password"
                );


        Label successLabel =
                new Label(
                        ""
                );

        successLabel.setStyle(
                "-fx-text-fill:#22c55e;" +
                "-fx-font-size:11px;"
        );

        successLabel.setVisible(false);

        driverFields.getChildren().addAll(
                operationalTitle,
                depotField,
                busCombo,
                shiftField,
                passwordField,
                successLabel
        );


        formCard.setUserData(
                new TextField[]{
                        nameField,
                        phoneField,
                        emailField,
                        depotField,
                        shiftField,
                        passwordField
                }
        );


        HBox actions =
                new HBox(10);

        actions.setAlignment(
                Pos.CENTER_RIGHT
        );

        Button saveButton =
                new Button(
                        "Add Driver"
                );

        saveButton.getStyleClass().add(
                "primary-btn"
        );

        saveButton.setOnAction(
                e -> saveAccount(
                        nameField,
                        phoneField,
                        emailField,
                        passwordField,
                        depotField,
                        busCombo,
                        shiftField,
                        formCard,
                        successLabel
                )
        );

        actions.getChildren().add(
                saveButton
        );


        formCard.getChildren().addAll(
                basicTitle,
                basicSubtitle,
                nameField,
                phoneField,
                emailField,
                driverFields,
                actions
        );


        VBox infoCard =
                new VBox(8);

        infoCard.getStyleClass().add(
                "card"
        );

        infoCard.setPadding(
                new Insets(18)
        );

        Label infoTitle =
                new Label(
                        "Admin note"
                );

        infoTitle.getStyleClass().add(
                "section-title"
        );

        Label infoText =
                new Label(
                        "New drivers can be reviewed and assigned to a depot or bus after onboarding."
                );

        infoText.setWrapText(
                true
        );

        infoText.setStyle(
                "-fx-text-fill:#94a3b8;" +
                "-fx-font-size:12px;"
        );

        infoCard.getChildren().addAll(
                infoTitle,
                infoText
        );


        content.getChildren().addAll(
                backButton,
                pageHeader,
                formCard,
                infoCard
        );

        ScrollPane scrollPane =
                new ScrollPane(content);

        scrollPane.setFitToWidth(
                true
        );

        scrollPane.getStyleClass().add(
                "scroll-pane"
        );

        return scrollPane;
    }


    private TextField createField(
            String prompt
    ) {

        TextField field =
                new TextField();

        field.setPromptText(
                prompt
        );

        field.setPrefHeight(
                40
        );

        field.setMaxWidth(
                Double.MAX_VALUE
        );

        field.setStyle(
                "-fx-background-color:#111827;" +
                "-fx-text-fill:#f8fafc;" +
                "-fx-prompt-text-fill:#64748b;" +
                "-fx-border-color:#334155;" +
                "-fx-border-radius:6;" +
                "-fx-background-radius:6;" +
                "-fx-padding:0 12 0 12;"
        );

        return field;
    }


    /**
     * Only buses with no driver on them are offered, so an already-assigned bus
     * cannot be picked at all — we filter the list instead of validating after the
     * fact.
     */
    private ComboBox<Bus> createBusCombo() {

        ComboBox<Bus> combo =
                new ComboBox<>();

        combo.setPrefHeight(
                40
        );

        combo.setMaxWidth(
                Double.MAX_VALUE
        );

        combo.setConverter(
                new StringConverter<Bus>() {

                    @Override
                    public String toString(Bus bus) {

                        if (bus == null) {
                            return "";
                        }

                        String depot =
                                bus.getDepot() == null || bus.getDepot().isEmpty()
                                        ? "no depot"
                                        : bus.getDepot();
                            return bus.getBusCode()
                                  + " · "
                                  + bus.getId()
                                  + " · "
                                  + depot;
                    }

                    @Override
                    public Bus fromString(String text) {
                        return null;
                    }
                }
        );

        List<Bus> unassigned =
                busController.getUnassignedBuses();

        combo.getItems().setAll(
                unassigned
        );

        combo.setPromptText(
                unassigned.isEmpty()
                        ? "No unassigned buses available"
                        : "Assigned bus"
        );

        return combo;
    }


    private void saveAccount(
            TextField nameField,
            TextField phoneField,
            TextField emailField,
            TextField passwordField,
            TextField depotField,
            ComboBox<Bus> busCombo,
            TextField shiftField,
            VBox formCard,
            Label successLabel
    ) {


        successLabel.setVisible(false);
        successLabel.setText("");


        String name =
                nameField.getText().trim();

        String phone =
                phoneField.getText().trim();

        String email =
                emailField.getText().trim();

        String password =
                passwordField.getText().trim();

        String depot =
                depotField.getText().trim();

        Bus selectedBus =
                busCombo.getValue();

        String shift =
                shiftField.getText().trim();




        if (name.isEmpty()
                || phone.isEmpty()
                || email.isEmpty()
                || password.isEmpty()
                || depot.isEmpty()) {

            showError(
                    successLabel,
                    "Please complete all required fields."
            );

            return;
        }


        if (selectedBus == null) {

            showError(
                    successLabel,
                    busCombo.getItems().isEmpty()
                            ? "No unassigned buses are available. Add a bus or free one up first."
                            : "Please select a bus to assign to this driver."
            );

            return;
        }


        if (!phone.matches("\\d{10}")) {

            showError(
                    successLabel,
                    "Phone number must contain exactly 10 digits."
            );

            return;
        }


        if (!email.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
        )) {

            showError(
                    successLabel,
                    "Please enter a valid email address."
            );

            return;
        }


        try {

            DriverController driverController =
                    new DriverController();

            boolean success =
                    driverController.addDriverWithBus(
                            name,
                            email,
                            phone,
                            selectedBus.getId(),
                            depot,
                            shift,
                            password
                    );


            if (success) {


                showSuccess(
                        successLabel,
                        "Driver added successfully and assigned to "
                                + selectedBus.getBusCode() + "."
                );


                nameField.clear();
                phoneField.clear();
                emailField.clear();
                passwordField.clear();
                depotField.clear();
                shiftField.clear();

                // The chosen bus now has a driver, so drop it from the pool.
                busCombo.getItems().remove(selectedBus);
                busCombo.setValue(null);
                busCombo.setPromptText(
                        busCombo.getItems().isEmpty()
                                ? "No unassigned buses available"
                                : "Assigned bus"
                );
            }


            else {

                showError(
                        successLabel,
                        "Failed to add driver. Please check Firebase and try again."
                );
            }

        } catch (Exception e) {

            e.printStackTrace();

            showError(
                    successLabel,
                    "An error occurred while adding the driver."
            );
        }
    }


    private void showSuccess(
            Label label,
            String message
    ) {

        label.setText(
                message
        );

        label.setStyle(
                "-fx-text-fill:#22c55e;" +
                "-fx-font-size:11px;"
        );

        label.setVisible(true);
    }


    private void showError(
            Label label,
            String message
    ) {

        label.setText(
                message
        );

        label.setStyle(
                "-fx-text-fill:#ef4444;" +
                "-fx-font-size:11px;"
        );

        label.setVisible(true);
    }


    private void goBack() {

        AdminDashboard.heading.setText(
                "Users & Drivers"
        );

        AdminDashboard.subheading.setText(
                "Manage drivers, customers and account activity"
        );

        AdminDashboard.middleBox.getChildren().setAll(
                UsersAndDrivers.buildMainContent()
        );

        AdminDashboard.window.setTitle(
                "ChargeOn · Users & Drivers"
        );
    }
}
