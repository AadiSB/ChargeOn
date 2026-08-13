package com.core2web.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class Navigation {
    private Scene navigationscene;

    Scene getNavigationScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0f1720;");

        root.setLeft(buildSidebar());

        VBox centerArea = new VBox();
        centerArea.getChildren().addAll(buildTopBar(), buildMainContent());
        VBox.setVgrow(centerArea.getChildren().get(1), Priority.ALWAYS);
        root.setCenter(centerArea);

        Scene sc = new Scene(root, DriverDashboard.homestage.getWidth(), DriverDashboard.homestage.getHeight());
        navigationscene = sc;
        sc.getStylesheets().add(getClass().getResource("/styles/navigation.css").toExternalForm());

        // arg1.setScene(sc);
        // arg1.setTitle("ChargeOn · Live Navigation");
        // arg1.show();
        return navigationscene;
    }

    // ── SIDEBAR (matching DriverDashboard) ──

    private VBox buildSidebar() {
        VBox sb = new VBox(4);
        sb.getStyleClass().add("sidebar");
        sb.setPrefWidth(210);
        sb.setPadding(new Insets(16, 12, 16, 12));

        HBox logobox = new HBox(6);
        logobox.setAlignment(Pos.CENTER_LEFT);

        try {
            Image logo = new Image("assets/logo/new_logo.png");
            ImageView logoview = new ImageView(logo);
            logoview.setFitHeight(30);
            logoview.setPreserveRatio(true);
            logobox.getChildren().add(logoview);
        } catch (Exception e) {
            // fallback: use lightning emoji
        }

        Label title = new Label("\u26A1 ChargeOn");
        title.getStyleClass().add("app-title");
        logobox.getChildren().add(title);

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
        Label logoutLbl = new Label("\u23FB  Log out");
        logoutLbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 13px;");
        logout.getChildren().addAll(logoutLbl);
        sb.getChildren().add(logout);

        return sb;
    }

    private HBox navItem(String text) {
        HBox item = new HBox(8);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(10, 12, 10, 12));
        item.getStyleClass().add("nav-item");

        Label lbl = new Label(text);
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

    // ── TOP BAR (matching DriverDashboard) ──

    private HBox buildTopBar() {
        HBox bar = new HBox(14);
        bar.getStyleClass().add("topbar");
        bar.setPadding(new Insets(14, 20, 14, 20));
        bar.setAlignment(Pos.CENTER_LEFT);

        VBox left = new VBox(2);
        left.getChildren().addAll(
                label("Live navigation", "heading"),
                label("Turn-by-turn guidance to your next stop", "small-muted"));

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

        Label uName = new Label("Rajesh K.");
        uName.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:13px;-fx-font-weight:bold;");

        HBox right = new HBox(14, gps, bell, avatar, uName);
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

        // Main body: Route guidance (left) + Stops on this leg (right)
        HBox body = new HBox(16);
        VBox routeGuidance = buildRouteGuidance();
        HBox.setHgrow(routeGuidance, Priority.ALWAYS);
        VBox stopsPanel = buildStopsPanel();
        stopsPanel.setPrefWidth(280);
        stopsPanel.setMinWidth(260);
        body.getChildren().addAll(routeGuidance, stopsPanel);

        content.getChildren().addAll(banner, body);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(false);
        scrollPane.setFitToHeight(false);
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

    // ── ROUTE GUIDANCE CARD ──

    private VBox buildRouteGuidance() {
        VBox card = new VBox(0);
        card.getStyleClass().add("route-card");
        card.setPadding(new Insets(18));

        // Header row
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label titleLabel = new Label("Route guidance");
        titleLabel.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:18px;-fx-font-weight:bold;");
        Label frRef = label("FR-DRV-06", "muted-tag");
        frRef.setPadding(new Insets(0, 0, 0, 10));
        Region hSp1 = new Region();
        HBox.setHgrow(hSp1, Priority.ALWAYS);
        Label nextStop = new Label("Next stop · Ganga Trueno, Baner");
        nextStop.setStyle("-fx-text-fill:#94a3b8;-fx-font-size:12px;");
        header.getChildren().addAll(titleLabel, frRef, hSp1, nextStop);
        header.setPadding(new Insets(0, 0, 14, 0));

        // Map area with Canvas
        StackPane mapArea = buildMapCanvas();
        VBox.setVgrow(mapArea, Priority.ALWAYS);

        // Turn instruction overlay (bottom-left of map)
        VBox turnInstruction = new VBox(2);
        turnInstruction.getStyleClass().add("turn-instruction");
        turnInstruction.setPadding(new Insets(12, 16, 12, 16));
        Label inLabel = new Label("IN 400 M");
        inLabel.setStyle("-fx-text-fill:#64748b;-fx-font-size:10px;-fx-font-weight:bold;");
        Label turnLabel = new Label("Turn right onto Baner Road");
        turnLabel.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:15px;-fx-font-weight:bold;");
        Label continueLabel = new Label("Then continue 1.2 km · moderate traffic");
        continueLabel.setStyle("-fx-text-fill:#10b981;-fx-font-size:11px;");
        turnInstruction.getChildren().addAll(inLabel, turnLabel, continueLabel);

        // Map buttons overlay (top-right)
        HBox mapButtons = new HBox(8);
        mapButtons.setAlignment(Pos.CENTER);
        Button recenterBtn = new Button("Re-center");
        recenterBtn.getStyleClass().add("map-btn");
        Button trafficBtn = new Button("Traffic");
        trafficBtn.getStyleClass().add("map-btn");
        mapButtons.getChildren().addAll(recenterBtn, trafficBtn);

        // Stack the map, turn instruction, and buttons
        StackPane mapStack = new StackPane();
        mapStack.getChildren().addAll(mapArea, turnInstruction, mapButtons);
        StackPane.setAlignment(turnInstruction, Pos.BOTTOM_LEFT);
        StackPane.setMargin(turnInstruction, new Insets(0, 0, 10, 10));
        StackPane.setAlignment(mapButtons, Pos.TOP_RIGHT);
        StackPane.setMargin(mapButtons, new Insets(10, 10, 0, 0));
        VBox.setVgrow(mapStack, Priority.ALWAYS);
        mapStack.setMinHeight(280);

        // Bottom info bar
        HBox bottomBar = new HBox(24);
        bottomBar.setAlignment(Pos.CENTER_LEFT);
        bottomBar.setPadding(new Insets(16, 0, 0, 0));

        // ETA
        VBox etaBox = new VBox(2);
        Label etaLabel = new Label("ETA");
        etaLabel.setStyle("-fx-text-fill:#64748b;-fx-font-size:10px;-fx-font-weight:bold;");
        Label etaVal = new Label("04:30 PM");
        etaVal.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:20px;-fx-font-weight:bold;");
        etaBox.getChildren().addAll(etaLabel, etaVal);

        // Distance
        VBox distBox = new VBox(2);
        Label distLabel = new Label("DISTANCE");
        distLabel.setStyle("-fx-text-fill:#64748b;-fx-font-size:10px;-fx-font-weight:bold;");
        Label distVal = new Label("4.8 km");
        distVal.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:20px;-fx-font-weight:bold;");
        distBox.getChildren().addAll(distLabel, distVal);

        // Drive time
        VBox driveBox = new VBox(2);
        Label driveLabel = new Label("DRIVE TIME");
        driveLabel.setStyle("-fx-text-fill:#64748b;-fx-font-size:10px;-fx-font-weight:bold;");
        Label driveVal = new Label("12 min");
        driveVal.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:20px;-fx-font-weight:bold;");
        driveBox.getChildren().addAll(driveLabel, driveVal);

        Region bottomSpacer = new Region();
        HBox.setHgrow(bottomSpacer, Priority.ALWAYS);

        Button skipBtn = new Button("Skip stop");
        skipBtn.getStyleClass().add("secondary-btn");
        Button confirmBtn = new Button("Confirm arrival");
        confirmBtn.getStyleClass().add("primary-btn");

        bottomBar.getChildren().addAll(etaBox, distBox, driveBox, bottomSpacer, skipBtn, confirmBtn);

        card.getChildren().addAll(header, mapStack, bottomBar);
        return card;
    }

    // ── MAP (Shape-based, avoids D3D12 Canvas crash) ──

    private StackPane buildMapCanvas() {
        Pane mapPane = new Pane();
        mapPane.setStyle("-fx-background-color: #111827;");
        mapPane.setPrefSize(550, 300);
        mapPane.setMinHeight(280);

        // Draw map content on layout
        mapPane.widthProperty().addListener((obs, old, nv) -> drawMapShapes(mapPane));
        mapPane.heightProperty().addListener((obs, old, nv) -> drawMapShapes(mapPane));

        StackPane wrapper = new StackPane(mapPane);
        wrapper.getStyleClass().add("map-container");
        wrapper.setMinHeight(280);
        return wrapper;
    }

    private void drawMapShapes(Pane pane) {
        pane.getChildren().clear();
        double w = pane.getWidth();
        double h = pane.getHeight();
        if (w <= 0 || h <= 0)
            return;

        // Background rectangle
        Rectangle bg = new Rectangle(w, h, Color.web("#111827"));
        pane.getChildren().add(bg);

        // Grid lines (subtle)
        for (double x = 0; x < w; x += 40) {
            Line gl = new Line(x, 0, x, h);
            gl.setStroke(Color.web("#1a2332"));
            gl.setStrokeWidth(0.5);
            pane.getChildren().add(gl);
        }
        for (double y = 0; y < h; y += 40) {
            Line gl = new Line(0, y, w, y);
            gl.setStroke(Color.web("#1a2332"));
            gl.setStrokeWidth(0.5);
            pane.getChildren().add(gl);
        }

        // Road network (subtle grey roads)
        addRoad(pane, w * 0.1, h * 0.6, w, h * 0.6);
        addRoad(pane, 0, h * 0.33, w * 0.73, h * 0.33);
        addRoad(pane, w * 0.18, h * 0.8, w * 0.91, h * 0.8);
        addRoad(pane, w * 0.36, 0, w * 0.36, h);
        addRoad(pane, w * 0.64, h * 0.17, w * 0.64, h);
        addRoad(pane, w * 0.87, h * 0.27, w * 0.87, h * 0.83);

        // Route path coordinates
        double rx1 = w * 0.24, ry1 = h * 0.73;
        double rx2 = w * 0.51, ry2 = h * 0.6;
        double rx3 = w * 0.69, ry3 = h * 0.6;
        double rx4 = w * 0.69, ry4 = h * 0.37;
        double rx5 = w * 0.87, ry5 = h * 0.37;

        // Route path (dashed green lines)
        addRouteLine(pane, rx1, ry1, rx2, ry2);
        addRouteLine(pane, rx2, ry2, rx3, ry3);
        addRouteLine(pane, rx3, ry3, rx4, ry4);
        addRouteLine(pane, rx4, ry4, rx5, ry5);

        // Start point (blue dot)
        Circle startDot = new Circle(rx1, ry1, 7, Color.web("#3b82f6"));
        pane.getChildren().add(startDot);

        // Current position (orange dot)
        Circle currentDot = new Circle(rx3, ry3, 8, Color.web("#f59e0b"));
        pane.getChildren().add(currentDot);

        // Waypoint (green dot)
        Circle wpDot = new Circle(rx4, ry4, 7, Color.web("#10b981"));
        pane.getChildren().add(wpDot);

        // Destination (green dot)
        Circle destDot = new Circle(rx5, ry5, 7, Color.web("#10b981"));
        pane.getChildren().add(destDot);
    }

    private void addRoad(Pane pane, double x1, double y1, double x2, double y2) {
        Line road = new Line(x1, y1, x2, y2);
        road.setStroke(Color.web("#2a3544"));
        road.setStrokeWidth(3);
        pane.getChildren().add(road);
    }

    private void addRouteLine(Pane pane, double x1, double y1, double x2, double y2) {
        Line route = new Line(x1, y1, x2, y2);
        route.setStroke(Color.web("#10b981"));
        route.setStrokeWidth(3);
        route.getStrokeDashArray().addAll(10.0, 8.0);
        pane.getChildren().add(route);
    }

    // ── STOPS ON THIS LEG ──

    private VBox buildStopsPanel() {
        VBox panel = new VBox(12);
        panel.getStyleClass().add("stops-panel");
        panel.setPadding(new Insets(18));

        Label titleLbl = new Label("Stops on this leg");
        titleLbl.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:16px;-fx-font-weight:bold;");

        VBox stopsList = new VBox(0);
        stopsList.getChildren().addAll(
                buildStopItem("Ganga Trueno, Baner · BKG-4468",
                        "Next · ETA 04:30 PM · 25 kWh",
                        "#10b981", true),
                buildStopItem("Hinjewadi Ph-1 · BKG-4471",
                        "Emergency insert · ETA 04:55 PM",
                        "#ef4444", false),
                buildStopItem("Pashan Sus Road · BKG-4473",
                        "ETA 05:20 PM · 30 kWh",
                        "#6b7280", false),
                buildStopItem("Baner depot · handover",
                        "Shift end 15:00 · charge to 90%",
                        "#6b7280", false));

        panel.getChildren().addAll(titleLbl, stopsList);
        return panel;
    }

    private HBox buildStopItem(String name, String details, String dotColor, boolean isNext) {
        HBox item = new HBox(12);
        item.setPadding(new Insets(12, 14, 12, 14));
        item.setAlignment(Pos.TOP_LEFT);
        item.getStyleClass().add("stop-item");

        // Status dot
        Circle dot = new Circle(6, Color.web(dotColor));
        if (dotColor.equals("#ef4444")) {
            // Red ring for emergency
            dot.setFill(Color.TRANSPARENT);
            dot.setStroke(Color.web("#ef4444"));
            dot.setStrokeWidth(2);
        }
        VBox dotContainer = new VBox();
        dotContainer.setAlignment(Pos.TOP_CENTER);
        dotContainer.setPadding(new Insets(4, 0, 0, 0));
        dotContainer.getChildren().add(dot);

        // Text
        VBox textBox = new VBox(3);
        HBox.setHgrow(textBox, Priority.ALWAYS);
        Label nameLabel = new Label(name);
        nameLabel.setWrapText(true);
        if (isNext) {
            nameLabel.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:13px;-fx-font-weight:bold;");
        } else {
            nameLabel.setStyle("-fx-text-fill:#cbd5e1;-fx-font-size:13px;-fx-font-weight:bold;");
        }
        Label detailLabel = new Label(details);
        detailLabel.setWrapText(true);
        detailLabel.setStyle("-fx-text-fill:#64748b;-fx-font-size:11px;");
        textBox.getChildren().addAll(nameLabel, detailLabel);

        item.getChildren().addAll(dotContainer, textBox);
        return item;
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
