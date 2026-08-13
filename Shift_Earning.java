package com.core2web.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
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
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Shift_Earning {

    Scene getShiftScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0f1720;");

        root.setLeft(buildSidebar());

        VBox centerArea = new VBox();
        centerArea.getChildren().addAll(buildTopBar(), buildMainContent());
        VBox.setVgrow(centerArea.getChildren().get(1), Priority.ALWAYS);
        root.setCenter(centerArea);

        Scene sc = new Scene(root, DriverDashboard.homestage.getWidth(), DriverDashboard.homestage.getHeight());
        sc.getStylesheets().add(getClass().getResource("/styles/shiftearning.css").toExternalForm());
        // stage.setScene(sc);
        // stage.setTitle("ChargeOn · Shift & Earnings");
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
        Region logoutSpacer = new Region();
        HBox.setHgrow(logoutSpacer, Priority.ALWAYS);
        logout.getChildren().addAll(logoutBtn, logoutSpacer);
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
        item.getChildren().addAll(lbl);

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

    // ── TOP BAR (copied from DriverDashboard) ──

    private HBox buildTopBar() {
        HBox bar = new HBox(14);
        bar.getStyleClass().add("topbar");
        bar.setPadding(new Insets(14, 20, 14, 20));
        bar.setAlignment(Pos.CENTER_LEFT);

        VBox left = new VBox(2);
        left.getChildren().addAll(
                label("Shift & earnings", "heading"),
                label("Schedule, clock and daily summary", "small-muted"));

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

        content.getChildren().addAll(
                buildEmergencyBanner(),
                buildStatCards(),
                buildBottomSection());

        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.getStyleClass().add("scroll-pane");
        sp.setStyle("-fx-background-color: transparent;");
        return sp;
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
        HBox titleRow = new HBox(12);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        Label emergTitle = label("Emergency booking · BKG-4471 diverted to your route", "emergency-title");
        Label frTag = new Label("FR-DRV-09");
        frTag.setStyle("-fx-text-fill:#64748b;-fx-font-size:10px;");
        titleRow.getChildren().addAll(emergTitle, frTag);
        text.getChildren().addAll(titleRow,
                label("Stranded EV at Hinjewadi Ph-1, Ganga Trueno · 4.2 km · reroute adds 9 min",
                        "emergency-subtitle"));

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Button dismiss = new Button("Dismiss");
        dismiss.getStyleClass().add("secondary-btn");
        Button accept = new Button("Accept diversion");
        accept.getStyleClass().add("danger-btn");

        banner.getChildren().addAll(icon, text, sp, dismiss, accept);
        return banner;
    }

    // ── STAT CARDS ROW ──

    private HBox buildStatCards() {
        HBox row = new HBox(12);

        // Clocked In
        VBox clockedIn = statCard("Clocked In");
        Label clockTime = new Label("06:54 AM");
        clockTime.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:28px;-fx-font-weight:bold;");
        Label elapsed = label("07h 32m elapsed", "card-sub");
        clockedIn.getChildren().addAll(clockTime, elapsed);
        HBox.setHgrow(clockedIn, Priority.ALWAYS);

        // Sessions today
        VBox sessions = statCard("Sessions today");
        Label sessNum = new Label("6");
        sessNum.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:28px;-fx-font-weight:bold;");
        Label sessDetail = label("142 kWh dispensed", "card-sub");
        sessions.getChildren().addAll(sessNum, sessDetail);
        HBox.setHgrow(sessions, Priority.ALWAYS);

        // Billed today
        VBox billed = statCard("Billed today");
        Label billAmt = new Label("\u20B94,180");
        billAmt.setStyle("-fx-text-fill:#10b981;-fx-font-size:28px;-fx-font-weight:bold;");
        Label billSub = label("Avg \u20B9697 / session", "card-sub");
        billed.getChildren().addAll(billAmt, billSub);
        HBox.setHgrow(billed, Priority.ALWAYS);

        // Driver payout (est.)
        VBox payout = statCard("Driver payout (est.)");
        Label payAmt = new Label("\u20B91,240");
        payAmt.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:28px;-fx-font-weight:bold;");
        Label paySub = label("Base \u20B9900 + \u20B9340 incentives", "card-sub");
        payout.getChildren().addAll(payAmt, paySub);
        HBox.setHgrow(payout, Priority.ALWAYS);

        row.getChildren().addAll(clockedIn, sessions, billed, payout);
        return row;
    }

    // ── BOTTOM SECTION (Schedule + Clock/Earnings) ──

    private HBox buildBottomSection() {
        HBox bottom = new HBox(16);

        // Left: Shift schedule table
        VBox schedule = buildShiftSchedule();
        HBox.setHgrow(schedule, Priority.ALWAYS);

        // Right column: Clock + Earnings
        VBox rightCol = new VBox(16);
        rightCol.setPrefWidth(320);
        rightCol.getChildren().addAll(buildClockWidget(), buildEarningsChart());

        bottom.getChildren().addAll(schedule, rightCol);
        return bottom;
    }

    // ── SHIFT SCHEDULE TABLE ──

    private VBox buildShiftSchedule() {
        VBox section = new VBox(0);
        section.getStyleClass().add("schedule-card");
        section.setPadding(new Insets(18));

        // Header
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 14, 0));
        Label schedTitle = new Label("Shift schedule — this week");
        schedTitle.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:16px;-fx-font-weight:bold;");
        Label schedTag = new Label("FR-DRV-11 · 12");
        schedTag.setStyle("-fx-text-fill:#64748b;-fx-font-size:10px;");
        header.getChildren().addAll(schedTitle, schedTag);

        // Table header
        HBox tableHeader = buildTableRow("DAY", "SHIFT", "BUS / DEPOT", "SESSIONS", "STATUS", true);
        tableHeader.setPadding(new Insets(8, 0, 8, 0));
        tableHeader
                .setStyle("-fx-border-color: transparent transparent #1e293b transparent; -fx-border-width: 0 0 1 0;");

        // Table rows
        VBox rows = new VBox(0);
        rows.getChildren().addAll(
                buildScheduleRow("Mon", "07:00 — 15:00", "Bus #07 · Baner", "8", "DONE"),
                buildScheduleRow("Tue", "07:00 — 15:00", "Bus #07 · Baner", "7", "DONE"),
                buildScheduleRow("Wed", "07:00 — 15:00", "Bus #07 · Baner", "6", "ACTIVE"),
                buildScheduleRow("Thu", "12:00 — 20:00", "Bus #12 · Hinjewadi", "—", "UPCOMING"),
                buildScheduleRow("Fri", "Rest day", "—", "—", "OFF"));

        section.getChildren().addAll(header, tableHeader, rows);
        return section;
    }

    private HBox buildTableRow(String day, String shift, String bus, String sessions, String status, boolean isHeader) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 0, 10, 0));

        Label dayLbl = new Label(day);
        dayLbl.setPrefWidth(70);
        Label shiftLbl = new Label(shift);
        shiftLbl.setPrefWidth(130);
        Label busLbl = new Label(bus);
        busLbl.setPrefWidth(140);
        Label sessLbl = new Label(sessions);
        sessLbl.setPrefWidth(80);
        sessLbl.setAlignment(Pos.CENTER);
        Label statusLbl = new Label(status);
        statusLbl.setPrefWidth(100);

        if (isHeader) {
            String headerStyle = "-fx-text-fill:#64748b;-fx-font-size:10px;-fx-font-weight:bold;";
            dayLbl.setStyle(headerStyle);
            shiftLbl.setStyle(headerStyle);
            busLbl.setStyle(headerStyle);
            sessLbl.setStyle(headerStyle);
            statusLbl.setStyle(headerStyle);
        }

        row.getChildren().addAll(dayLbl, shiftLbl, busLbl, sessLbl, statusLbl);
        return row;
    }

    private HBox buildScheduleRow(String day, String shift, String bus, String sessions, String status) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 0, 12, 0));
        row.setStyle("-fx-border-color: transparent transparent #1e293b transparent; -fx-border-width: 0 0 1 0;");

        Label dayLbl = new Label(day);
        dayLbl.setPrefWidth(70);
        dayLbl.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:13px;-fx-font-weight:bold;");

        Label shiftLbl = new Label(shift);
        shiftLbl.setPrefWidth(130);
        shiftLbl.setStyle("-fx-text-fill:#94a3b8;-fx-font-size:12px;");

        Label busLbl = new Label(bus);
        busLbl.setPrefWidth(140);
        busLbl.setStyle("-fx-text-fill:#94a3b8;-fx-font-size:12px;");

        Label sessLbl = new Label(sessions);
        sessLbl.setPrefWidth(80);
        sessLbl.setAlignment(Pos.CENTER);
        sessLbl.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:13px;-fx-font-weight:bold;");

        Label statusLbl = new Label(status);
        statusLbl.setPrefWidth(100);

        switch (status) {
            case "DONE":
                statusLbl.setStyle("-fx-text-fill:#10b981;-fx-font-size:11px;-fx-font-weight:bold;");
                statusLbl.getStyleClass().add("status-done");
                break;
            case "ACTIVE":
                statusLbl.setStyle("-fx-text-fill:#10b981;-fx-font-size:11px;-fx-font-weight:bold;");
                statusLbl.getStyleClass().add("status-active");
                break;
            case "UPCOMING":
                statusLbl.setStyle("-fx-text-fill:#94a3b8;-fx-font-size:11px;");
                break;
            case "OFF":
                statusLbl.setStyle("-fx-text-fill:#64748b;-fx-font-size:11px;");
                break;
        }

        row.getChildren().addAll(dayLbl, shiftLbl, busLbl, sessLbl, statusLbl);
        return row;
    }

    // ── CLOCK WIDGET ──

    private VBox buildClockWidget() {
        VBox card = new VBox(10);
        card.getStyleClass().add("clock-card");
        card.setPadding(new Insets(18));

        Label clockTitle = new Label("Clock");
        clockTitle.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:16px;-fx-font-weight:bold;");

        // Clock display area
        VBox clockDisplay = new VBox(6);
        clockDisplay.setAlignment(Pos.CENTER);
        clockDisplay.setPadding(new Insets(18));
        clockDisplay.setStyle("-fx-background-color:#0f1720;-fx-background-radius:12;");

        Label onDutyLabel = new Label("ON DUTY");
        onDutyLabel.setStyle("-fx-text-fill:#64748b;-fx-font-size:10px;-fx-font-weight:bold;");

        Label clockTime = new Label("07h 32m");
        clockTime.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:36px;-fx-font-weight:bold;");

        Label sinceLabel = new Label("since 06:54 AM · depot Baner");
        sinceLabel.setStyle("-fx-text-fill:#64748b;-fx-font-size:11px;");

        Button clockOutBtn = new Button("Clock out");
        clockOutBtn.getStyleClass().add("clock-out-btn");
        clockOutBtn.setMaxWidth(Double.MAX_VALUE);
        VBox.setMargin(clockOutBtn, new Insets(8, 0, 0, 0));

        clockDisplay.getChildren().addAll(onDutyLabel, clockTime, sinceLabel, clockOutBtn);

        card.getChildren().addAll(clockTitle, clockDisplay);
        return card;
    }

    // ── EARNINGS BAR CHART ──

    private VBox buildEarningsChart() {
        VBox card = new VBox(10);
        card.getStyleClass().add("earnings-card");
        card.setPadding(new Insets(18));

        Label chartTitle = new Label("Earnings, last 7 days");
        chartTitle.setStyle("-fx-text-fill:#94a3b8;-fx-font-size:12px;");

        // Bar chart using Canvas
        Canvas canvas = new Canvas(280, 120);
        drawBarChart(canvas.getGraphicsContext2D(), 280, 120);

        // Day labels
        HBox dayLabels = new HBox();
        dayLabels.setAlignment(Pos.CENTER);
        String[] days = { "Th", "Fr", "Sa", "Su", "Mo", "Tu", "We" };
        for (String d : days) {
            Label dl = new Label(d);
            dl.setStyle("-fx-text-fill:#64748b;-fx-font-size:10px;");
            dl.setPrefWidth(280.0 / 7);
            dl.setAlignment(Pos.CENTER);
            dayLabels.getChildren().add(dl);
        }

        card.getChildren().addAll(chartTitle, canvas, dayLabels);
        return card;
    }

    private void drawBarChart(GraphicsContext gc, double width, double height) {
        double[] values = { 0.5, 0.45, 0.55, 0.4, 0.5, 0.6, 0.9 };
        int bars = values.length;
        double barWidth = (width / bars) * 0.5;
        double gap = (width / bars);
        double maxBarHeight = height - 10;

        for (int i = 0; i < bars; i++) {
            double barHeight = values[i] * maxBarHeight;
            double x = (i * gap) + (gap - barWidth) / 2;
            double y = height - barHeight;

            // Use green for the last bar (today), dark gray for others
            if (i == bars - 1) {
                gc.setFill(Color.web("#10b981"));
            } else {
                gc.setFill(Color.web("#1e293b"));
            }

            // Draw rounded bars
            double radius = 4;
            gc.fillRoundRect(x, y, barWidth, barHeight, radius, radius);
        }
    }

    // ── HELPERS ──

    private VBox statCard(String title) {
        VBox c = new VBox(6);
        c.getStyleClass().add("card");
        c.setPadding(new Insets(14));
        c.getChildren().add(label(title, "card-title"));
        return c;
    }

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
