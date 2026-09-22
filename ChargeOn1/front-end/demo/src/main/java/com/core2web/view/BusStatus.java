// package com.core2web.view;

// import javafx.geometry.Insets;
// import javafx.geometry.Pos;
// import javafx.scene.Scene;
// import javafx.scene.control.Label;
// import javafx.scene.control.ScrollPane;
// import javafx.scene.layout.HBox;
// import javafx.scene.layout.Priority;
// import javafx.scene.layout.Region;
// import javafx.scene.layout.StackPane;
// import javafx.scene.layout.VBox;
// import javafx.scene.paint.Color;
// import javafx.scene.shape.Arc;
// import javafx.scene.shape.ArcType;
// import javafx.scene.shape.StrokeLineCap;

// import com.core2web.controller.BookingController;
// import com.core2web.controller.BusController;
// import com.core2web.model.Bus;

// public class BusStatus {

//     private static final BusController busController = new BusController();
//     private static final BookingController bookingController = new BookingController();

//     Scene getBusStatusScene() {
//         DriverDashboard.goTo("Bus Status");
//         return DriverDashboard.scene;
//     }

//     static ScrollPane buildMainContent() {
//         return new BusStatus().buildContent();
//     }

//     private ScrollPane buildContent() {
//         VBox content = new VBox(16);
//         content.setPadding(new Insets(16));

//         // Only added when a real pending emergency exists — no placeholder banner.
//         HBox banner = buildEmergencyBanner();

//         Bus bus = busController.getBusForCurrentDriver();
//         VBox batteryHolder = new VBox();
//         batteryHolder.getChildren().add(
//                 bus == null ? buildNoBusCard() : buildBatteryReserveCard(bus));

//         HBox topRow = new HBox(16);
//         HBox.setHgrow(batteryHolder, Priority.ALWAYS);
//         VBox portsCard = buildChargingPortsCard();
//         HBox.setHgrow(portsCard, Priority.ALWAYS);
//         topRow.getChildren().addAll(batteryHolder, portsCard);

//         HBox bottomRow = new HBox(16);
//         VBox locationCard = buildLocationCard();
//         HBox.setHgrow(locationCard, Priority.ALWAYS);

//         bottomRow.getChildren().addAll(locationCard);

//         if (banner != null) {
//             content.getChildren().add(banner);
//         }

//         content.getChildren().addAll(topRow, bottomRow);

//         ScrollPane scrollPane = new ScrollPane(content);
//         scrollPane.setFitToWidth(true);
//         scrollPane.setFitToHeight(true);
//         scrollPane.getStyleClass().add("scroll-pane");
//         scrollPane.setStyle("-fx-background-color: transparent;");
//         return scrollPane;
//     }

//     private VBox buildNoBusCard() {
//         VBox card = new VBox(12);
//         card.getStyleClass().add("card");
//         card.setPadding(new Insets(20));
//         Label titleLbl = new Label("Battery reserve");
//         titleLbl.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:18px;-fx-font-weight:bold;");
//         Label empty = new Label("No bus assigned to you yet.");
//         empty.setStyle("-fx-text-fill:#64748b;-fx-font-size:12px;");
//         card.getChildren().addAll(titleLbl, empty);
//         return card;
//     }

//     /**
//      * The emergency diversion banner, or null when this driver has no pending
//      * emergency — callers must skip adding it entirely in that case.
//      */
//     private HBox buildEmergencyBanner() {
//         com.core2web.model.Booking pending =
//                 bookingController.getPendingEmergencyBookingForCurrentDriver();

//         if (pending == null) {
//             return null;
//         }

//         return EmergencyBanner.build(pending, () -> DriverDashboard.goTo("Bus Status"));
//     }

//     private VBox buildBatteryReserveCard(Bus bus) {
//         VBox card = new VBox(12);
//         card.getStyleClass().add("card");
//         card.setPadding(new Insets(20));

//         HBox header = new HBox();
//         header.setAlignment(Pos.CENTER_LEFT);
//         Label titleLbl = new Label("Battery reserve");
//         titleLbl.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:18px;-fx-font-weight:bold;");
//         Region hSp = new Region();
//         HBox.setHgrow(hSp, Priority.ALWAYS);
//         Label busLbl = new Label(bus.getBusCode());
//         busLbl.setStyle("-fx-text-fill:#64748b;-fx-font-size:12px;-fx-font-weight:bold;");

//         header.getChildren().addAll(titleLbl, hSp, busLbl);

//         String displayStatus = busController.getDisplayStatus(bus);
//         StackPane gauge = buildBatteryGauge(bus.getBatteryLevel() / 100.0, displayStatus);

//         HBox bottomRow1 = new HBox();
//         bottomRow1.setAlignment(Pos.CENTER_LEFT);
//         Label reserveLabel = new Label("Reserve floor");
//         reserveLabel.setStyle("-fx-text-fill:#94a3b8;-fx-font-size:12px;");
//         Region bSp1 = new Region();
//         HBox.setHgrow(bSp1, Priority.ALWAYS);
//         Label reserveVal = new Label("20% · locked");
//         reserveVal.setStyle("-fx-text-fill:#f59e0b;-fx-font-size:12px;-fx-font-weight:bold;");
//         bottomRow1.getChildren().addAll(reserveLabel, bSp1, reserveVal);

//         HBox bottomRow2 = new HBox();
//         bottomRow2.setAlignment(Pos.CENTER_LEFT);
//         Label dispLabel = new Label("Depot");
//         dispLabel.setStyle("-fx-text-fill:#94a3b8;-fx-font-size:12px;");
//         Region bSp2 = new Region();
//         HBox.setHgrow(bSp2, Priority.ALWAYS);
//         Label dispVal = new Label(bus.getDepot());
//         dispVal.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:14px;-fx-font-weight:bold;");
//         bottomRow2.getChildren().addAll(dispLabel, bSp2, dispVal);

//         card.getChildren().addAll(header, gauge, bottomRow1, bottomRow2);
//         return card;
//     }

//     private StackPane buildBatteryGauge(double percentage, String statusLabel) {
//         StackPane gaugePane = new StackPane();
//         gaugePane.setPrefSize(200, 200);
//         gaugePane.setMaxSize(200, 200);

//         double radius = 80;
//         double strokeWidth = 14;

//         Arc bgArc = new Arc(0, 0, radius, radius, 90, -360);
//         bgArc.setType(ArcType.OPEN);
//         bgArc.setFill(Color.TRANSPARENT);
//         bgArc.setStroke(Color.web("#1e293b"));
//         bgArc.setStrokeWidth(strokeWidth);
//         bgArc.setStrokeLineCap(StrokeLineCap.ROUND);

//         double angle = -360 * percentage;
//         Arc fgArc = new Arc(0, 0, radius, radius, 90, angle);
//         fgArc.setType(ArcType.OPEN);
//         fgArc.setFill(Color.TRANSPARENT);
//         fgArc.setStroke(Color.web("#10b981"));
//         fgArc.setStrokeWidth(strokeWidth);
//         fgArc.setStrokeLineCap(StrokeLineCap.ROUND);

//         VBox centerText = new VBox(2);
//         centerText.setAlignment(Pos.CENTER);
//         Label pctLabel = new Label(String.valueOf((int) (percentage * 100)) + "%");
//         pctLabel.setStyle("-fx-text-fill:#10b981;-fx-font-size:36px;-fx-font-weight:bold;");
//         Label statusLbl = new Label(statusLabel);
//         statusLbl.setStyle("-fx-text-fill:#64748b;-fx-font-size:11px;");
//         centerText.getChildren().addAll(pctLabel, statusLbl);

//         gaugePane.getChildren().addAll(bgArc, fgArc, centerText);
//         return gaugePane;
//     }

//     private VBox buildChargingPortsCard() {
//         VBox card = new VBox(12);
//         card.getStyleClass().add("card");
//         card.setPadding(new Insets(20));

//         Label titleLbl = new Label("Charging ports");
//         titleLbl.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:18px;-fx-font-weight:bold;");

//         HBox row1 = new HBox(12);
//         VBox port1 = buildPortCard("Port 1", "CCS2 · 60 kW\n",
//                 "IN USE", "#3b82f6");
//         VBox port2 = buildPortCard("Port 2", "CCS2 · 60 kW\n",
//                 "FREE", "#6b7280");
//         HBox.setHgrow(port1, Priority.ALWAYS);
//         HBox.setHgrow(port2, Priority.ALWAYS);
//         row1.getChildren().addAll(port1, port2);

//         HBox row2 = new HBox(12);
//         VBox port3 = buildPortCard("Port 3", "CHAdeMO · 50 kW\n",
//                 "FREE", "#6b7280");
//         VBox port4 = buildPortCard("Port 4", "Type 2 AC · 22 kW\n",
//                 "FREE", "#6b7280");
//         HBox.setHgrow(port3, Priority.ALWAYS);
//         HBox.setHgrow(port4, Priority.ALWAYS);
//         row2.getChildren().addAll(port3, port4);

//         card.getChildren().addAll(titleLbl, row1, row2);
//         return card;
//     }

//     private VBox buildPortCard(String portName, String details, String status,
//             String statusColor) {
//         VBox portCard = new VBox(8);
//         portCard.getStyleClass().add("port-card");
//         portCard.setPadding(new Insets(14));

//         HBox header = new HBox();
//         header.setAlignment(Pos.CENTER_LEFT);
//         Label nameLabel = new Label(portName);
//         nameLabel.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:14px;-fx-font-weight:bold;");
//         Region hSp = new Region();
//         HBox.setHgrow(hSp, Priority.ALWAYS);
//         Label statusLabel = new Label(status);
//         statusLabel.setStyle(
//                 "-fx-text-fill:" + statusColor + ";" +
//                         "-fx-font-size:10px;-fx-font-weight:bold;");
//         header.getChildren().addAll(nameLabel, hSp, statusLabel);

//         Label detailsLabel = new Label(details);
//         detailsLabel.setStyle("-fx-text-fill:#94a3b8;-fx-font-size:11px;");
//         detailsLabel.setWrapText(true);

//         portCard.getChildren().addAll(header, detailsLabel);

//         return portCard;
//     }

//     private VBox buildLocationCard() {
//         VBox card = new VBox(8);
//         card.getStyleClass().add("card");
//         card.setPadding(new Insets(18));

//         Label header = label("Current location", "card-header-muted");

//         Label location = new Label("Baner Road, near Ganga Trueno");
//         location.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:16px;-fx-font-weight:bold;");
//         location.setWrapText(true);

//         Label coords = new Label("18.5601° N, 73.7769° E · updated 4s ago");
//         coords.setStyle("-fx-text-fill:#64748b;-fx-font-size:11px;");

//         Label geofence = new Label("Inside geofence of stop #3");
//         geofence.setStyle("-fx-text-fill:#10b981;-fx-font-size:12px;-fx-font-weight:bold;");

//         card.getChildren().addAll(header, location, coords, geofence);
//         return card;
//     }

//     private Label label(String text, String styleClass) {
//         Label l = new Label(text);
//         l.getStyleClass().add(styleClass);
//         return l;
//     }

// }

package com.core2web.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.StrokeLineCap;

import com.core2web.controller.BookingController;
import com.core2web.controller.BusController;
import com.core2web.model.Bus;

public class BusStatus {

    private static final BusController busController = new BusController();
    private static final BookingController bookingController = new BookingController();

    /*
     * Existing port information.
     *
     * This is kept here as the single source for the port display.
     * No Firebase data is added or written.
     */
    private static final String[][] PORT_DATA = {
            { "Port 1", "CCS2 · 60 kW\n", "IN USE", "#3b82f6" },
            { "Port 2", "CCS2 · 60 kW\n", "FREE", "#6b7280" },
            { "Port 3", "CHAdeMO · 50 kW\n", "FREE", "#6b7280" },
            { "Port 4", "Type 2 AC · 22 kW\n", "FREE", "#6b7280" }
    };

    Scene getBusStatusScene() {
        DriverDashboard.goTo("Bus Status");
        return DriverDashboard.scene;
    }

    static ScrollPane buildMainContent() {
        return new BusStatus().buildContent();
    }

    /**
     * Provides the same existing port information to the Driver Dashboard.
     *
     * No Firebase read/write is performed here.
     */
    static String getDashboardPortSummary() {
        int totalPorts = PORT_DATA.length;
        int inUse = 0;
        int free = 0;

        for (String[] port : PORT_DATA) {
            if ("IN USE".equalsIgnoreCase(port[2])) {
                inUse++;
            } else if ("FREE".equalsIgnoreCase(port[2])) {
                free++;
            }
        }

        if (totalPorts == 0) {
            return "N/A";
        }

        return inUse + " in use · " + free + " free";
    }

    private ScrollPane buildContent() {
        VBox content = new VBox(16);
        content.setPadding(new Insets(16));

        // Only added when a real pending emergency exists — no placeholder banner.
        HBox banner = buildEmergencyBanner();

        Bus bus = busController.getBusForCurrentDriver();
        VBox batteryHolder = new VBox();
        batteryHolder.getChildren().add(
                bus == null ? buildNoBusCard() : buildBatteryReserveCard(bus));

        HBox topRow = new HBox(16);
        HBox.setHgrow(batteryHolder, Priority.ALWAYS);
        VBox portsCard = buildChargingPortsCard();
        HBox.setHgrow(portsCard, Priority.ALWAYS);
        topRow.getChildren().addAll(batteryHolder, portsCard);

        HBox bottomRow = new HBox(16);
        VBox locationCard = buildLocationCard();
        HBox.setHgrow(locationCard, Priority.ALWAYS);

        bottomRow.getChildren().addAll(locationCard);

        if (banner != null) {
            content.getChildren().add(banner);
        }

        content.getChildren().addAll(topRow, bottomRow);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.getStyleClass().add("scroll-pane");
        scrollPane.setStyle("-fx-background-color: transparent;");
        return scrollPane;
    }

    private VBox buildNoBusCard() {
        VBox card = new VBox(12);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(20));
        Label titleLbl = new Label("Battery reserve");
        titleLbl.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:18px;-fx-font-weight:bold;");
        Label empty = new Label("No bus assigned to you yet.");
        empty.setStyle("-fx-text-fill:#64748b;-fx-font-size:12px;");
        card.getChildren().addAll(titleLbl, empty);
        return card;
    }

    /**
     * The emergency diversion banner, or null when this driver has no pending
     * emergency — callers must skip adding it entirely in that case.
     */
    private HBox buildEmergencyBanner() {
        com.core2web.model.Booking pending =
                bookingController.getPendingEmergencyBookingForCurrentDriver();

        if (pending == null) {
            return null;
        }

        return EmergencyBanner.build(pending, () -> DriverDashboard.goTo("Bus Status"));
    }

    private VBox buildBatteryReserveCard(Bus bus) {
        VBox card = new VBox(12);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(20));

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label titleLbl = new Label("Battery reserve");
        titleLbl.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:18px;-fx-font-weight:bold;");
        Region hSp = new Region();
        HBox.setHgrow(hSp, Priority.ALWAYS);
        Label busLbl = new Label(bus.getBusCode());
        busLbl.setStyle("-fx-text-fill:#64748b;-fx-font-size:12px;-fx-font-weight:bold;");

        header.getChildren().addAll(titleLbl, hSp, busLbl);

        String displayStatus = busController.getDisplayStatus(bus);
        StackPane gauge = buildBatteryGauge(bus.getBatteryLevel() / 100.0, displayStatus);

        HBox bottomRow1 = new HBox();
        bottomRow1.setAlignment(Pos.CENTER_LEFT);
        Label reserveLabel = new Label("Reserve floor");
        reserveLabel.setStyle("-fx-text-fill:#94a3b8;-fx-font-size:12px;");
        Region bSp1 = new Region();
        HBox.setHgrow(bSp1, Priority.ALWAYS);
        Label reserveVal = new Label("20% · locked");
        reserveVal.setStyle("-fx-text-fill:#f59e0b;-fx-font-size:12px;-fx-font-weight:bold;");
        bottomRow1.getChildren().addAll(reserveLabel, bSp1, reserveVal);

        HBox bottomRow2 = new HBox();
        bottomRow2.setAlignment(Pos.CENTER_LEFT);
        Label dispLabel = new Label("Depot");
        dispLabel.setStyle("-fx-text-fill:#94a3b8;-fx-font-size:12px;");
        Region bSp2 = new Region();
        HBox.setHgrow(bSp2, Priority.ALWAYS);
        Label dispVal = new Label(bus.getDepot());
        dispVal.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:14px;-fx-font-weight:bold;");
        bottomRow2.getChildren().addAll(dispLabel, bSp2, dispVal);

        card.getChildren().addAll(header, gauge, bottomRow1, bottomRow2);
        return card;
    }

    private StackPane buildBatteryGauge(double percentage, String statusLabel) {
        StackPane gaugePane = new StackPane();
        gaugePane.setPrefSize(200, 200);
        gaugePane.setMaxSize(200, 200);

        double radius = 80;
        double strokeWidth = 14;

        Arc bgArc = new Arc(0, 0, radius, radius, 90, -360);
        bgArc.setType(ArcType.OPEN);
        bgArc.setFill(Color.TRANSPARENT);
        bgArc.setStroke(Color.web("#1e293b"));
        bgArc.setStrokeWidth(strokeWidth);
        bgArc.setStrokeLineCap(StrokeLineCap.ROUND);

        double angle = -360 * percentage;
        Arc fgArc = new Arc(0, 0, radius, radius, 90, angle);
        fgArc.setType(ArcType.OPEN);
        fgArc.setFill(Color.TRANSPARENT);
        fgArc.setStroke(Color.web("#10b981"));
        fgArc.setStrokeWidth(strokeWidth);
        fgArc.setStrokeLineCap(StrokeLineCap.ROUND);

        VBox centerText = new VBox(2);
        centerText.setAlignment(Pos.CENTER);
        Label pctLabel = new Label(String.valueOf((int) (percentage * 100)) + "%");
        pctLabel.setStyle("-fx-text-fill:#10b981;-fx-font-size:36px;-fx-font-weight:bold;");
        Label statusLbl = new Label(statusLabel);
        statusLbl.setStyle("-fx-text-fill:#64748b;-fx-font-size:11px;");
        centerText.getChildren().addAll(pctLabel, statusLbl);

        gaugePane.getChildren().addAll(bgArc, fgArc, centerText);
        return gaugePane;
    }

    private VBox buildChargingPortsCard() {
        VBox card = new VBox(12);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(20));

        Label titleLbl = new Label("Charging ports");
        titleLbl.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:18px;-fx-font-weight:bold;");

        HBox row1 = new HBox(12);
        VBox port1 = buildPortCard(
                PORT_DATA[0][0],
                PORT_DATA[0][1],
                PORT_DATA[0][2],
                PORT_DATA[0][3]);

        VBox port2 = buildPortCard(
                PORT_DATA[1][0],
                PORT_DATA[1][1],
                PORT_DATA[1][2],
                PORT_DATA[1][3]);

        HBox.setHgrow(port1, Priority.ALWAYS);
        HBox.setHgrow(port2, Priority.ALWAYS);
        row1.getChildren().addAll(port1, port2);

        HBox row2 = new HBox(12);
        VBox port3 = buildPortCard(
                PORT_DATA[2][0],
                PORT_DATA[2][1],
                PORT_DATA[2][2],
                PORT_DATA[2][3]);

        VBox port4 = buildPortCard(
                PORT_DATA[3][0],
                PORT_DATA[3][1],
                PORT_DATA[3][2],
                PORT_DATA[3][3]);

        HBox.setHgrow(port3, Priority.ALWAYS);
        HBox.setHgrow(port4, Priority.ALWAYS);
        row2.getChildren().addAll(port3, port4);

        card.getChildren().addAll(titleLbl, row1, row2);
        return card;
    }

    private VBox buildPortCard(String portName, String details, String status,
            String statusColor) {
        VBox portCard = new VBox(8);
        portCard.getStyleClass().add("port-card");
        portCard.setPadding(new Insets(14));

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label nameLabel = new Label(portName);
        nameLabel.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:14px;-fx-font-weight:bold;");
        Region hSp = new Region();
        HBox.setHgrow(hSp, Priority.ALWAYS);
        Label statusLabel = new Label(status);
        statusLabel.setStyle(
                "-fx-text-fill:" + statusColor + ";" +
                        "-fx-font-size:10px;-fx-font-weight:bold;");
        header.getChildren().addAll(nameLabel, hSp, statusLabel);

        Label detailsLabel = new Label(details);
        detailsLabel.setStyle("-fx-text-fill:#94a3b8;-fx-font-size:11px;");
        detailsLabel.setWrapText(true);

        portCard.getChildren().addAll(header, detailsLabel);

        return portCard;
    }

    private VBox buildLocationCard() {
        VBox card = new VBox(8);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(18));

        Label header = label("Current location", "card-header-muted");

        Label location = new Label("Baner Road, near Ganga Trueno");
        location.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:16px;-fx-font-weight:bold;");
        location.setWrapText(true);

        Label coords = new Label("18.5601° N, 73.7769° E · updated 4s ago");
        coords.setStyle("-fx-text-fill:#64748b;-fx-font-size:11px;");

        Label geofence = new Label("Inside geofence of stop #3");
        geofence.setStyle("-fx-text-fill:#10b981;-fx-font-size:12px;-fx-font-weight:bold;");

        card.getChildren().addAll(header, location, coords, geofence);
        return card;
    }

    private Label label(String text, String styleClass) {
        Label l = new Label(text);
        l.getStyleClass().add(styleClass);
        return l;
    }
}
