package com.core2web.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeLineCap;
import javafx.stage.Stage;

public class BusStatus {

    Scene getBusStatusScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0f1720;");

        root.setLeft(buildSidebar());

        VBox centerArea = new VBox();
        centerArea.getChildren().addAll(buildTopBar(), buildMainContent());
        VBox.setVgrow(centerArea.getChildren().get(1), Priority.ALWAYS);
        root.setCenter(centerArea);

        Scene sc = new Scene(root, DriverDashboard.homestage.getWidth(), DriverDashboard.homestage.getHeight());
        sc.getStylesheets().add(getClass().getResource("/styles/busstatus.css").toExternalForm());
        // stage.setScene(sc);
        // stage.setTitle("ChargeOn · Bus Status");
        // stage.show();
        return sc;
    }

    // ── SIDEBAR (copied from DriverDashboard) ──

    private VBox buildSidebar() {
        VBox sb = new VBox(4);
        sb.getStyleClass().add("sidebar");
        sb.setPrefWidth(210);
        sb.setPadding(new Insets(16, 12, 16, 12));

        HBox logobox = new HBox();

        Image logo = new Image("assets/logo/new_logo.png");
        ImageView logoview = new ImageView(logo);
        logoview.setFitHeight(30);
        logoview.setPreserveRatio(true);

        Label title = new Label("ChargeOn");
        title.getStyleClass().add("app-title");
        title.setStyle("-fx-alignment: center;");

        logobox.getChildren().addAll(logoview, title);

        sb.getChildren().addAll(logobox, gap(12),
                navItem("Dashboard"),
                navItem("My Bookings"),
                navItem("Live Navigation"),
                navItem("Bus Status"),
                navItem("Alerts & Issues"),
                navItem("Shift & Earnings"));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        sb.getChildren().add(spacer);

        // Shift card
        VBox shiftCard = new VBox(6);
        shiftCard.getStyleClass().add("shift-card");
        shiftCard.setPadding(new Insets(14));
        Label shiftHead = label("SHIFT", "shift-label");
        HBox timeRow = new HBox(6);
        timeRow.setAlignment(Pos.BASELINE_LEFT);
        Label time = new Label("07h 32m");
        time.setStyle("-fx-text-fill: #f8fafc; -fx-font-size: 22px; -fx-font-weight: bold;");
        Label onDuty = new Label("on duty");
        onDuty.setStyle("-fx-text-fill: #10b981; -fx-font-size: 13px; -fx-font-weight: bold;");
        timeRow.getChildren().addAll(time, onDuty);
        Label shiftInfo = label("07:00 — 15:00 · Depot\nBaner", "shift-details");
        Button clockOut = new Button("Clock out");
        clockOut.getStyleClass().add("clock-out-btn");
        clockOut.setMaxWidth(Double.MAX_VALUE);
        shiftCard.getChildren().addAll(shiftHead, timeRow, shiftInfo, clockOut);
        sb.getChildren().add(shiftCard);

        // Logout
        HBox logout = new HBox(8);
        logout.setPadding(new Insets(10, 0, 0, 0));
        logout.setAlignment(Pos.CENTER_LEFT);
        Button logoutBtn = new Button("\u23FB  Log out");
        logoutBtn.getStyleClass().add("logout-btn");
        logout.getChildren().add(logoutBtn);
        sb.getChildren().add(logout);

        return sb;
    }

    private HBox navItem(String text) {
        HBox item = new HBox(8);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(10, 12, 10, 12));
        item.getStyleClass().add("nav-item");

        Label lbl = new Label(text);
        lbl.setStyle("-fx-text-fill:white;-fx-font-size:13px;");
        item.getChildren().add(lbl);

        item.setOnMouseClicked(e -> {
            switch (text) {
                case "Dashboard":
                    DriverDashboard driverpage = new DriverDashboard();
                    DriverDashboard.homestage.setScene(driverpage.getDriverScene());
                    System.out.println("Dashboard Clicked");
                    break;

                case "My Bookings":
                    Booking bookingpage = new Booking();
                    DriverDashboard.homestage.setScene(bookingpage.getBookingScene());
                    break;

                case "Live Navigation":
                    Navigation navigationpage = new Navigation();
                    DriverDashboard.homestage.setScene(navigationpage.getNavigationScene());
                    break;

                case "Bus Status":
                    BusStatus busstatuspage = new BusStatus();
                    DriverDashboard.homestage.setScene(busstatuspage.getBusStatusScene());
                    break;

                case "Alerts & Issues":
                    Alerts_Issues alertpage = new Alerts_Issues();
                    DriverDashboard.homestage.setScene(alertpage.getAlertsScene());
                    break;

                case "Shift & Earnings":
                    Shift_Earning shiftpage = new Shift_Earning();
                    DriverDashboard.homestage.setScene(shiftpage.getShiftScene());
                    break;

                default:
                    break;
            }
        });
        return item;
    }

    // ── TOP BAR ──

    private HBox buildTopBar() {
        HBox bar = new HBox(14);
        bar.getStyleClass().add("topbar");
        bar.setPadding(new Insets(14, 20, 14, 20));
        bar.setAlignment(Pos.CENTER_LEFT);

        VBox left = new VBox(2);
        left.getChildren().addAll(
                label("Bus status", "heading"),
                label("Battery reserve, ports and vehicle telemetry", "small-muted"));

        TextField search = new TextField();
        search.setPromptText("\uD83D\uDD0D Search bookings, stops, bus ID...");
        search.getStyleClass().add("search-field");
        search.setPrefWidth(280);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // GPS pill
        HBox gps = new HBox(6, new Circle(5, Color.web("#10b981")), label("GPS live", "small-muted"));
        gps.setAlignment(Pos.CENTER);
        gps.getStyleClass().add("gps-pill");

        // Bell with badge
        StackPane bell = new StackPane();
        Label bellIcon = new Label("\uD83D\uDD14");
        bellIcon.setStyle("-fx-font-size: 18px;");
        StackPane bellBadge = new StackPane(new Circle(8, Color.web("#ef4444")),
                new Label("2") {
                    {
                        setStyle("-fx-text-fill:white;-fx-font-size:9px;-fx-font-weight:bold;");
                    }
                });
        bellBadge.setTranslateX(10);
        bellBadge.setTranslateY(-10);
        bell.getChildren().addAll(bellIcon, bellBadge);

        // Avatar
        StackPane avatar = new StackPane(new Circle(18, Color.web("#2dd4bf")),
                new Label("RK") {
                    {
                        setStyle("-fx-text-fill:#0f1720;-fx-font-weight:bold;-fx-font-size:12px;");
                    }
                });

        VBox userInfo = new VBox(1);
        Label uName = new Label("Rajesh K.");
        uName.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:13px;-fx-font-weight:bold;");
        Label uSub = new Label("DRV-2214 · Bus #07");
        uSub.setStyle("-fx-text-fill:#64748b;-fx-font-size:10px;");
        userInfo.getChildren().addAll(uName, uSub);

        HBox right = new HBox(14, gps, bell, avatar, userInfo);
        right.setAlignment(Pos.CENTER);

        bar.getChildren().addAll(left, search, spacer, right);
        return bar;
    }

    // ── MAIN CONTENT ──

    private ScrollPane buildMainContent() {
        VBox content = new VBox(16);
        content.setPadding(new Insets(16));

        // Emergency banner
        HBox banner = buildEmergencyBanner();

        // Top row: Battery reserve + Charging ports
        HBox topRow = new HBox(16);
        VBox batteryCard = buildBatteryReserveCard();
        HBox.setHgrow(batteryCard, Priority.ALWAYS);
        VBox portsCard = buildChargingPortsCard();
        HBox.setHgrow(portsCard, Priority.ALWAYS);
        topRow.getChildren().addAll(batteryCard, portsCard);

        // Bottom row: Location + Vehicle Health + Shift Utilisation
        HBox bottomRow = new HBox(16);
        VBox locationCard = buildLocationCard();
        HBox.setHgrow(locationCard, Priority.ALWAYS);
        VBox healthCard = buildVehicleHealthCard();
        HBox.setHgrow(healthCard, Priority.ALWAYS);
        VBox utilisationCard = buildShiftUtilisationCard();
        HBox.setHgrow(utilisationCard, Priority.ALWAYS);
        bottomRow.getChildren().addAll(locationCard, healthCard, utilisationCard);

        content.getChildren().addAll(banner, topRow, bottomRow);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.getStyleClass().add("scroll-pane");
        scrollPane.setStyle("-fx-background-color: transparent;");
        return scrollPane;
    }

    // ── EMERGENCY BANNER ──

    private HBox buildEmergencyBanner() {
        HBox banner = new HBox(14);
        banner.setPadding(new Insets(14, 18, 14, 18));
        banner.getStyleClass().add("emergency-banner");
        banner.setAlignment(Pos.CENTER_LEFT);

        StackPane icon = new StackPane(new Circle(14, Color.web("#f87171")),
                new Label("!") {
                    {
                        setStyle("-fx-text-fill:white;-fx-font-weight:bold;-fx-font-size:14px;");
                    }
                });

        VBox text = new VBox(3);
        text.getChildren().addAll(
                label("Emergency booking · BKG-4471 diverted to your route", "emergency-title"),
                label("Stranded EV at Hinjewadi Ph-1, Ganga Trueno · 4.2 km · reroute adds 9 min",
                        "emergency-subtitle"));

        Label frId = label("FR-DRV-09", "muted-tag");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Button dismiss = new Button("Dismiss");
        dismiss.getStyleClass().add("secondary-btn");
        Button accept = new Button("Accept diversion");
        accept.getStyleClass().add("danger-btn");

        banner.getChildren().addAll(icon, text, frId, sp, dismiss, accept);
        return banner;
    }

    // ── BATTERY RESERVE CARD ──

    private VBox buildBatteryReserveCard() {
        VBox card = new VBox(12);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(20));

        // Header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label titleLbl = new Label("Battery reserve");
        titleLbl.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:18px;-fx-font-weight:bold;");
        Region hSp = new Region();
        HBox.setHgrow(hSp, Priority.ALWAYS);
        Label frRef = label("FR-DRV-07", "muted-tag");
        header.getChildren().addAll(titleLbl, hSp, frRef);

        // Circular gauge using Arc shapes
        StackPane gauge = buildBatteryGauge(0.68, 198, 240);

        // Bottom info
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
        Label dispLabel = new Label("Dispensable now");
        dispLabel.setStyle("-fx-text-fill:#94a3b8;-fx-font-size:12px;");
        Region bSp2 = new Region();
        HBox.setHgrow(bSp2, Priority.ALWAYS);
        Label dispVal = new Label("139 kWh");
        dispVal.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:14px;-fx-font-weight:bold;");
        bottomRow2.getChildren().addAll(dispLabel, bSp2, dispVal);

        card.getChildren().addAll(header, gauge, bottomRow1, bottomRow2);
        return card;
    }

    private StackPane buildBatteryGauge(double percentage, int currentKwh, int totalKwh) {
        StackPane gaugePane = new StackPane();
        gaugePane.setPrefSize(200, 200);
        gaugePane.setMaxSize(200, 200);

        double radius = 80;
        double strokeWidth = 14;

        // Background track arc (full circle)
        Arc bgArc = new Arc(0, 0, radius, radius, 90, -360);
        bgArc.setType(ArcType.OPEN);
        bgArc.setFill(Color.TRANSPARENT);
        bgArc.setStroke(Color.web("#1e293b"));
        bgArc.setStrokeWidth(strokeWidth);
        bgArc.setStrokeLineCap(StrokeLineCap.ROUND);

        // Foreground arc (percentage filled)
        double angle = -360 * percentage;
        Arc fgArc = new Arc(0, 0, radius, radius, 90, angle);
        fgArc.setType(ArcType.OPEN);
        fgArc.setFill(Color.TRANSPARENT);
        fgArc.setStroke(Color.web("#10b981"));
        fgArc.setStrokeWidth(strokeWidth);
        fgArc.setStrokeLineCap(StrokeLineCap.ROUND);

        // Center text
        VBox centerText = new VBox(2);
        centerText.setAlignment(Pos.CENTER);
        Label pctLabel = new Label(String.valueOf((int) (percentage * 100)) + "%");
        pctLabel.setStyle("-fx-text-fill:#10b981;-fx-font-size:36px;-fx-font-weight:bold;");
        Label kwhLabel = new Label(currentKwh + " kWh · " + totalKwh + " kWh");
        kwhLabel.setStyle("-fx-text-fill:#64748b;-fx-font-size:11px;");
        centerText.getChildren().addAll(pctLabel, kwhLabel);

        gaugePane.getChildren().addAll(bgArc, fgArc, centerText);
        return gaugePane;
    }

    // ── CHARGING PORTS CARD ──

    private VBox buildChargingPortsCard() {
        VBox card = new VBox(12);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(20));

        Label titleLbl = new Label("Charging ports");
        titleLbl.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:18px;-fx-font-weight:bold;");

        // 2x2 grid of ports
        HBox row1 = new HBox(12);
        VBox port1 = buildPortCard("Port 1", "CCS2 · 60 kW\nBKG-4468 · 18.2 / 25 kWh",
                "IN USE", "#3b82f6", 0.73);
        VBox port2 = buildPortCard("Port 2", "CCS2 · 60 kW\nHandshake intermittent · reported",
                "FAULT", "#ef4444", -1);
        HBox.setHgrow(port1, Priority.ALWAYS);
        HBox.setHgrow(port2, Priority.ALWAYS);
        row1.getChildren().addAll(port1, port2);

        HBox row2 = new HBox(12);
        VBox port3 = buildPortCard("Port 3", "CHAdeMO · 50 kW\nReady · last used 01:40 PM",
                "FREE", "#6b7280", -1);
        VBox port4 = buildPortCard("Port 4", "Type 2 AC · 22 kW\nReady",
                "FREE", "#6b7280", -1);
        HBox.setHgrow(port3, Priority.ALWAYS);
        HBox.setHgrow(port4, Priority.ALWAYS);
        row2.getChildren().addAll(port3, port4);

        card.getChildren().addAll(titleLbl, row1, row2);
        return card;
    }

    private VBox buildPortCard(String portName, String details, String status,
            String statusColor, double progress) {
        VBox portCard = new VBox(8);
        portCard.getStyleClass().add("port-card");
        portCard.setPadding(new Insets(14));

        // Header row
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

        // Details
        Label detailsLabel = new Label(details);
        detailsLabel.setStyle("-fx-text-fill:#94a3b8;-fx-font-size:11px;");
        detailsLabel.setWrapText(true);

        portCard.getChildren().addAll(header, detailsLabel);

        // Progress bar (only for IN USE ports)
        if (progress >= 0) {
            ProgressBar pb = new ProgressBar(progress);
            pb.getStyleClass().add("port-progress");
            pb.setMaxWidth(Double.MAX_VALUE);
            portCard.getChildren().add(pb);
        }

        return portCard;
    }

    // ── CURRENT LOCATION CARD ──

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

    // ── VEHICLE HEALTH CARD ──

    private VBox buildVehicleHealthCard() {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(18));

        Label header = label("Vehicle health", "card-header-muted");

        VBox rows = new VBox(8);
        rows.getChildren().addAll(
                healthRow("Coolant temp", "41°C", "#f8fafc"),
                healthRow("Inverter", "Nominal", "#10b981"),
                healthRow("Port 2 handshake", "Intermittent", "#f59e0b"),
                healthRow("Odometer", "84,120 km", "#f8fafc"));

        card.getChildren().addAll(header, rows);
        return card;
    }

    private HBox healthRow(String label, String value, String valueColor) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill:#94a3b8;-fx-font-size:12px;");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Label val = new Label(value);
        val.setStyle("-fx-text-fill:" + valueColor + ";-fx-font-size:12px;-fx-font-weight:bold;");
        row.getChildren().addAll(lbl, sp, val);
        return row;
    }

    // ── SHIFT UTILISATION CARD ──

    private VBox buildShiftUtilisationCard() {
        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(18));

        Label header = label("Shift utilisation", "card-header-muted");

        HBox countRow = new HBox(4);
        countRow.setAlignment(Pos.BASELINE_LEFT);
        Label countNum = new Label("6");
        countNum.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:28px;-fx-font-weight:bold;");
        Label countSuffix = new Label("of 8 sessions");
        countSuffix.setStyle("-fx-text-fill:#94a3b8;-fx-font-size:13px;");
        countRow.getChildren().addAll(countNum, countSuffix);

        ProgressBar pb = new ProgressBar(0.75);
        pb.getStyleClass().add("utilisation-bar");
        pb.setMaxWidth(Double.MAX_VALUE);

        Label billedLabel = new Label("142 kWh dispensed · \u20B94,180 billed");
        billedLabel.setStyle("-fx-text-fill:#94a3b8;-fx-font-size:11px;");

        card.getChildren().addAll(header, countRow, pb, billedLabel);
        return card;
    }

    // ── HELPERS ──

    private Label label(String text, String styleClass) {
        Label l = new Label(text);
        l.getStyleClass().add(styleClass);
        return l;
    }

    private Region gap(double height) {
        Region r = new Region();
        r.setPrefHeight(height);
        return r;
    }
}
