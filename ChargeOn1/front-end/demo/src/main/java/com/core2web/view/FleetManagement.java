package com.core2web.view;

import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.core2web.controller.BusController;
import com.core2web.controller.RouteController;
import com.core2web.model.Bus;
import com.core2web.model.Route;

import static com.core2web.view.AdminLayout.*;

public class FleetManagement {

    private static final BusController busController =
            new BusController();

    private static final RouteController routeController =
            new RouteController();


    public static void show(Stage stage) {

        AdminDashboard.goTo(
                "Fleet Management"
        );
    }


    static ScrollPane buildMainContent() {

        VBox content =
                new VBox(16);

        content.setPadding(
                new Insets(16)
        );


        List<Route> routes =
                routeController.getAllRoutes();

        VBox routesHolder =
                new VBox();

        populateRoutes(
                routesHolder,
                routes
        );

        VBox routesCard =
                buildRoutesCard(
                        routesHolder
                );


        List<Bus> buses =
                busController.getAllBuses();

        VBox inventoryHolder =
                new VBox();

        populateInventory(
                inventoryHolder,
                buses
        );

        VBox inventoryCard =
                buildInventoryCard(
                        inventoryHolder
                );


        content.getChildren().addAll(
                routesCard,
                inventoryCard
        );


        ScrollPane sp =
                new ScrollPane(
                        content
                );

        sp.setFitToWidth(true);

        sp.getStyleClass().add(
                "scroll-pane"
        );

        return sp;
    }


    private static void populateRoutes(
            VBox routesHolder,
            List<Route> routes) {

        routesHolder.getChildren().clear();


        if (routes.isEmpty()) {

            routesHolder.getChildren().add(
                    loadingLabel(
                            "No routes created yet."
                    )
            );

            return;
        }


        for (Route route : routes) {

            String busLabel;


            if (route.getAssignedBusId() == null ||
                    route.getAssignedBusId().isEmpty()) {

                busLabel =
                        "— unassigned";

            } else {

                busLabel =
                        route.getAssignedBusId();
            }


            routesHolder.getChildren().add(
                    routeRow(
                            route,
                            route.getRouteCode(),
                            route.stopsSummary(),
                            route.scheduleSummary(),
                            busLabel,
                            route.getStatus(),
                            route.statusColor()
                    )
            );
        }
    }


    private static void populateInventory(
            VBox inventoryHolder,
            List<Bus> buses) {

        inventoryHolder.getChildren().clear();


        if (buses.isEmpty()) {

            Label empty =
                    new Label(
                            "No buses in the fleet yet."
                    );

            empty.setStyle(
                    "-fx-text-fill:#64748b;" +
                    "-fx-font-size:12px;"
            );

            inventoryHolder.getChildren().add(
                    empty
            );

            return;
        }


        GridPane grid =
                new GridPane();

        grid.setHgap(12);
        grid.setVgap(12);


        ColumnConstraints col =
                new ColumnConstraints();

        col.setPercentWidth(50);

        grid.getColumnConstraints().addAll(
                col,
                col
        );


        int row = 0;
        int col2 = 0;


        for (Bus bus : buses) {

            VBox card =
                    inventoryBusCard(
                            bus
                    );

            grid.add(
                    card,
                    col2,
                    row
            );

            GridPane.setHgrow(
                    card,
                    Priority.ALWAYS
            );

            card.setMaxWidth(
                    Double.MAX_VALUE
            );


            col2++;


            if (col2 > 1) {

                col2 = 0;
                row++;
            }
        }


        inventoryHolder.getChildren().add(
                grid
        );
    }


    private static VBox buildRoutesCard(
            VBox routesHolder) {

        VBox card =
                new VBox(12);

        card.getStyleClass().add(
                "bookings-section"
        );

        card.setPadding(
                new Insets(18)
        );


        HBox header =
                new HBox(10);

        header.setAlignment(
                Pos.CENTER_LEFT
        );


        Label title =
                new Label(
                        "Routes & schedules"
                );

        title.getStyleClass().add(
                "booking-stage"
        );


        Region sp =
                new Region();

        HBox.setHgrow(
                sp,
                Priority.ALWAYS
        );


        Button addRoute =
                new Button(
                        "+ New route"
                );

        addRoute.getStyleClass().add(
                "primary-btn"
        );


        addRoute.setOnAction(e -> {

            AdminDashboard.openNewRoute();

        });


        header.getChildren().addAll(
                title,
                sp,
                addRoute
        );


        HBox colHeaders =
                new HBox();

        colHeaders.setPadding(
                new Insets(
                        10,
                        12,
                        10,
                        12
                )
        );


        colHeaders.getChildren().addAll(

                colLabel(
                        "ROUTE",
                        130
                ),

                colLabel(
                        "STOPS",
                        160
                ),

                colLabel(
                        "SCHEDULE",
                        140
                ),

                colLabel(
                        "ASSIGNED BUSES",
                        130
                ),

                colLabel(
                        "STATUS",
                        100
                ),

                colLabel(
                        "ACTION",
                        90
                )
        );


        card.getChildren().addAll(
                header,
                colHeaders,
                routesHolder
        );


        return card;
    }


    private static HBox routeRow(
            Route route,
            String code,
            String stops,
            String schedule,
            String buses,
            String status,
            String color) {

        HBox row =
                new HBox();

        row.setPadding(
                new Insets(12)
        );

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        row.getStyleClass().add(
                "booking-row"
        );


        Label codeLbl =
                new Label(code);

        codeLbl.setStyle(
                "-fx-text-fill:#f8fafc;" +
                "-fx-font-size:13px;" +
                "-fx-font-weight:bold;"
        );

        codeLbl.setPrefWidth(130);
        codeLbl.setMinWidth(130);


        Label stopsLbl =
                new Label(stops);

        stopsLbl.setStyle(
                "-fx-text-fill:#cbd5e1;" +
                "-fx-font-size:12px;"
        );

        stopsLbl.setWrapText(true);

        stopsLbl.setPrefWidth(160);
        stopsLbl.setMinWidth(160);


        Label schedLbl =
                new Label(schedule);

        schedLbl.setStyle(
                "-fx-text-fill:#94a3b8;" +
                "-fx-font-size:12px;"
        );

        schedLbl.setWrapText(true);

        schedLbl.setPrefWidth(140);
        schedLbl.setMinWidth(140);


        Label busesLbl =
                new Label(buses);

        busesLbl.setStyle(
                "-fx-text-fill:#cbd5e1;" +
                "-fx-font-size:12px;"
        );

        busesLbl.setPrefWidth(130);
        busesLbl.setMinWidth(130);


        Region actionSpacer =
                new Region();

        HBox.setHgrow(
                actionSpacer,
                Priority.ALWAYS
        );


        Button removeButton =
                new Button("Remove");

        removeButton.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #ef4444;" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 4 8 4 8;"
        );


        removeButton.setOnAction(e -> {

            boolean deleted =
                    routeController.deleteRoute(
                            route.getId()
                    );


            if (deleted) {

                AdminDashboard.goTo(
                        "Fleet Management"
                );

            } else {

                System.out.println(
                        "Failed to remove route: "
                                + route.getId()
                );
            }
        });


        /*
         * Do NOT assign statusBadge()
         * to a Label.
         *
         * Your existing statusBadge()
         * is added directly to the row,
         * exactly like your original code.
         */
        row.getChildren().addAll(

                codeLbl,

                stopsLbl,

                schedLbl,

                busesLbl,

                statusBadge(
                        status,
                        color,
                        100
                ),

                actionSpacer,

                removeButton
        );


        return row;
    }


    private static VBox buildInventoryCard(
            VBox inventoryHolder) {

        VBox card =
                new VBox(12);

        card.getStyleClass().add(
                "card"
        );

        card.setPadding(
                new Insets(18)
        );


        Label title =
                label(
                        "Battery inventory",
                        "section-title"
                );


        card.getChildren().addAll(
                title,
                inventoryHolder
        );


        return card;
    }


    private static Label loadingLabel(
            String text) {

        Label l =
                new Label(text);

        l.setStyle(
                "-fx-text-fill:#64748b;" +
                "-fx-font-size:12px;"
        );

        return l;
    }


    private static VBox inventoryBusCard(
            Bus bus) {

        String statusLabel =
                busController.getDisplayStatus(
                        bus
                );

        String statusColor =
                busController.getDisplayColor(
                        statusLabel
                );

        String subtitle =
                (int) bus.getBatteryLevel()
                        + "% reserve · "
                        + bus.getDepot();


        VBox c =
                new VBox(8);

        c.getStyleClass().add(
                "port-card"
        );

        c.setPadding(
                new Insets(14)
        );

        c.setPrefHeight(96);
        c.setMinHeight(96);


        HBox header =
                new HBox();

        header.setAlignment(
                Pos.CENTER_LEFT
        );


        Label id =
                new Label(
                        bus.getBusCode()
                );

        id.setStyle(
                "-fx-text-fill:#f8fafc;" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;"
        );


        Region sp =
                new Region();

        HBox.setHgrow(
                sp,
                Priority.ALWAYS
        );


        header.getChildren().addAll(
                id,
                sp,
                statusBadge(
                        statusLabel,
                        statusColor,
                        100
                )
        );


        Label battLbl =
                new Label(
                        subtitle
                );

        battLbl.setStyle(
                "-fx-text-fill:#94a3b8;" +
                "-fx-font-size:11px;"
        );

        battLbl.setWrapText(true);


        Region vsp =
                new Region();

        VBox.setVgrow(
                vsp,
                Priority.ALWAYS
        );


        ProgressBar pb =
                new ProgressBar(
                        bus.getBatteryLevel()
                                / 100.0
                );

        pb.getStyleClass().add(
                "battery-bar"
        );

        pb.setMaxWidth(
                Double.MAX_VALUE
        );


        c.getChildren().addAll(
                header,
                battLbl,
                vsp,
                pb
        );


        return c;
    }
}