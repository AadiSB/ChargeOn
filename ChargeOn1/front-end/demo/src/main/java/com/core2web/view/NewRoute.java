package com.core2web.view;

import java.util.List;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import com.core2web.controller.BusController;
import com.core2web.controller.RouteController;
import com.core2web.model.Bus;

public class NewRoute {

    private static final BusController busController = new BusController();
    private static final RouteController routeController = new RouteController();

    public static ScrollPane buildMainContent() {

        VBox content = new VBox(16);
        content.setPadding(new Insets(16));

        Button backButton = new Button("←  Back to Fleet Management");

        backButton.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #94a3b8;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 6 0 6 0;" +
                "-fx-cursor: hand;"
        );

        backButton.setOnAction(e -> {

            AdminDashboard.heading.setText("Fleet management");

            AdminDashboard.subheading.setText(
                    "Routes, schedules and charging inventory"
            );

            AdminDashboard.middleBox.getChildren().setAll(
                    FleetManagement.buildMainContent()
            );

            AdminDashboard.window.setTitle(
                    "ChargeOn · Fleet Management"
            );
        });

        VBox pageHeader = new VBox(4);

        Label title = new Label("Create new route");
        title.getStyleClass().add("section-title");

        Label subtitle = new Label(
                "Add a new route, schedule and bus assignment to the fleet"
        );
        subtitle.getStyleClass().add("card-sub");

        pageHeader.getChildren().addAll(title, subtitle);

        VBox routeCard = new VBox(12);
        routeCard.getStyleClass().add("bookings-section");
        routeCard.setPadding(new Insets(18));

        Label routeTitle = new Label("Route details");
        routeTitle.getStyleClass().add("booking-stage");

        Label routeIdLabel = fieldLabel("Route ID");

        TextField routeIdField = new TextField();
        routeIdField.setPromptText("Example: R-16");
        routeIdField.getStyleClass().add("search-field");

        Label routeNameLabel = fieldLabel("Route name");

        TextField routeNameField = new TextField();
        routeNameField.setPromptText("Example: Baner → Hinjewadi");
        routeNameField.getStyleClass().add("search-field");

        Label startLabel = fieldLabel("Starting point");

        TextField startField = new TextField();
        startField.setPromptText("Example: Baner");
        startField.getStyleClass().add("search-field");

        Label destinationLabel = fieldLabel("Destination");

        TextField destinationField = new TextField();
        destinationField.setPromptText("Example: Hinjewadi Phase 3");
        destinationField.getStyleClass().add("search-field");

        Label stopsLabel = fieldLabel("Stops");

        TextField stopsField = new TextField();
        stopsField.setPromptText(
                "Example: Wakad, Hinjewadi Phase 1, Phase 2"
        );
        stopsField.getStyleClass().add("search-field");

        routeCard.getChildren().addAll(
                routeTitle,
                routeIdLabel,
                routeIdField,
                routeNameLabel,
                routeNameField,
                startLabel,
                startField,
                destinationLabel,
                destinationField,
                stopsLabel,
                stopsField
        );

        VBox scheduleCard = new VBox(12);
        scheduleCard.getStyleClass().add("card");
        scheduleCard.setPadding(new Insets(18));

        Label scheduleTitle = new Label("Schedule & assignment");
        scheduleTitle.getStyleClass().add("section-title");

        Label startTimeLabel = fieldLabel("Start time");

        TextField startTimeField = new TextField();
        startTimeField.setPromptText("Example: 06:00");
        startTimeField.getStyleClass().add("search-field");

        Label endTimeLabel = fieldLabel("End time");

        TextField endTimeField = new TextField();
        endTimeField.setPromptText("Example: 22:00");
        endTimeField.getStyleClass().add("search-field");

        Label frequencyLabel = fieldLabel("Frequency");

        ComboBox<String> frequencyBox = new ComboBox<>();

        frequencyBox.getItems().addAll(
                "Every 10 minutes",
                "Every 12 minutes",
                "Every 15 minutes",
                "Every 20 minutes",
                "Every 30 minutes"
        );

        frequencyBox.setPromptText("Select frequency");
        frequencyBox.setMaxWidth(Double.MAX_VALUE);

        Label busLabel = fieldLabel("Assigned bus");

        /*
         * Use Bus objects instead of String.
         *
         * Displayed value:
         * BUS01 · Baner
         *
         * Saved value:
         * Actual Firestore bus document ID
         *
         * Any bus in the fleet can be selected.
         * A bus previously assigned to another route
         * is still allowed.
         */
        ComboBox<Bus> busBox = new ComboBox<>();

        busBox.setPromptText("Select a bus");
        busBox.setMaxWidth(Double.MAX_VALUE);

        busBox.setConverter(new StringConverter<Bus>() {

            @Override
            public String toString(Bus bus) {

                if (bus == null) {
                    return "";
                }

                String busCode = bus.getBusCode();
                String depot = bus.getDepot();

                if (busCode == null || busCode.isEmpty()) {
                    busCode = bus.getId();
                }

                if (depot == null || depot.isEmpty()) {
                    return busCode;
                }

                return busCode + " · " + depot;
            }

            @Override
            public Bus fromString(String string) {
                return null;
            }
        });

        /*
         * Get ALL buses from Firestore.
         *
         * Do NOT filter based on previous route assignments.
         * This allows old/previously assigned buses to be reused
         * when creating another route.
         */
        List<Bus> buses = busController.getAllBuses();

        busBox.getItems().setAll(buses);

        scheduleCard.getChildren().addAll(
                scheduleTitle,
                startTimeLabel,
                startTimeField,
                endTimeLabel,
                endTimeField,
                frequencyLabel,
                frequencyBox,
                busLabel,
                busBox
        );

        Label errorLabel = new Label();

        errorLabel.setStyle(
                "-fx-text-fill:#ef4444;" +
                "-fx-font-size:11px;" +
                "-fx-font-weight:bold;"
        );

        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        errorLabel.setWrapText(true);

        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_RIGHT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button cancelButton = new Button("Cancel");
        cancelButton.getStyleClass().add("secondary-btn");

        cancelButton.setOnAction(e -> {

            AdminDashboard.heading.setText("Fleet management");

            AdminDashboard.subheading.setText(
                    "Routes, schedules and charging inventory"
            );

            AdminDashboard.middleBox.getChildren().setAll(
                    FleetManagement.buildMainContent()
            );

            AdminDashboard.window.setTitle(
                    "ChargeOn · Fleet Management"
            );
        });

        Button saveButton = new Button("Save route");
        saveButton.getStyleClass().add("primary-btn");

        saveButton.setOnAction(e -> {

            String routeCode =
                    routeIdField.getText() == null
                            ? ""
                            : routeIdField.getText().trim();

            String routeName =
                    routeNameField.getText() == null
                            ? ""
                            : routeNameField.getText().trim();

            if (routeCode.isEmpty() || routeName.isEmpty()) {

                errorLabel.setText(
                        "Route ID and route name are required."
                );

                errorLabel.setVisible(true);
                errorLabel.setManaged(true);

                return;
            }

            String start =
                    startField.getText() == null
                            ? ""
                            : startField.getText().trim();

            String destination =
                    destinationField.getText() == null
                            ? ""
                            : destinationField.getText().trim();

            List<String> stops = new java.util.ArrayList<>();

            if (stopsField.getText() != null) {

                for (String stop : stopsField.getText().split(",")) {

                    if (!stop.trim().isEmpty()) {
                        stops.add(stop.trim());
                    }
                }
            }

            String startTime =
                    startTimeField.getText() == null
                            ? ""
                            : startTimeField.getText().trim();

            String endTime =
                    endTimeField.getText() == null
                            ? ""
                            : endTimeField.getText().trim();

            String frequency =
                    frequencyBox.getValue() == null
                            ? ""
                            : frequencyBox.getValue();

            /*
             * Get the selected Bus object.
             */
            Bus selectedBus = busBox.getValue();

            /*
             * Save the Firestore document ID,
             * NOT the visible bus code.
             *
             * final is important because this value
             * is used inside the Thread lambda below.
             */
            final String assignedBusId =
                    selectedBus == null || selectedBus.getId() == null
                            ? ""
                            : selectedBus.getId();

            errorLabel.setVisible(false);
            errorLabel.setManaged(false);

            saveButton.setDisable(true);

            Thread saver = new Thread(() -> {

                String newId =
                        routeController.createRoute(
                                routeCode,
                                routeName,
                                start,
                                destination,
                                stops,
                                startTime,
                                endTime,
                                frequency,
                                assignedBusId
                        );

                Platform.runLater(() -> {

                    saveButton.setDisable(false);

                    if (newId != null) {

                        AdminDashboard.goTo(
                                "Fleet Management"
                        );

                    } else {

                        errorLabel.setText(
                                "Failed to save route. Please try again."
                        );

                        errorLabel.setVisible(true);
                        errorLabel.setManaged(true);
                    }
                });

            });

            saver.setDaemon(true);
            saver.start();
        });

        actions.getChildren().addAll(
                spacer,
                errorLabel,
                cancelButton,
                saveButton
        );

        content.getChildren().addAll(
                backButton,
                pageHeader,
                routeCard,
                scheduleCard,
                actions
        );

        ScrollPane scrollPane =
                new ScrollPane(content);

        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("scroll-pane");

        return scrollPane;
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