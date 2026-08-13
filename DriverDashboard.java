package com.core2web.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class DriverDashboard extends Application {
    public static Stage homestage;
    private Scene homescene;

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0f1720;");

        root.setLeft(buildSidebar());

        VBox centerArea = new VBox();
        centerArea.getChildren().addAll(buildTopBar(), buildMainContent());
        VBox.setVgrow(centerArea.getChildren().get(1), Priority.ALWAYS);
        root.setCenter(centerArea);

        Scene scene = new Scene(root, 1200, 600);
        homescene = scene;
        scene.getStylesheets().add(getClass().getResource("/styles/dashboard.css").toExternalForm());

        homestage = stage;
        stage.setTitle("ChargeOn · Driver Portal");
        stage.setMinWidth(1000);
        stage.setMinHeight(600);
        stage.setScene(scene);
        stage.show();
    }

    Scene getDriverScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0f1720;");

        root.setLeft(buildSidebar());

        VBox centerArea = new VBox();
        centerArea.getChildren().addAll(buildTopBar(), buildMainContent());
        VBox.setVgrow(centerArea.getChildren().get(1), Priority.ALWAYS);
        root.setCenter(centerArea);

        Scene scene = new Scene(root, homestage.getWidth(), homestage.getHeight());
        scene.getStylesheets().add(getClass().getResource("/styles/dashboard.css").toExternalForm());
        return scene;
    }

    // ── SIDEBAR ──

    private VBox buildSidebar() {
        VBox sb = new VBox(4);
        sb.getStyleClass().add("sidebar");
        sb.setPrefWidth(210);
        sb.setPadding(new Insets(16, 12, 16, 12));

        Label logo = new Label("\u26A1 ChargeOn");
        logo.getStyleClass().add("app-title");

        sb.getChildren().addAll(logo, gap(12),
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
        Region s = new Region();
        HBox.setHgrow(s, Priority.ALWAYS);
        logout.getChildren().addAll(logoutLbl, s);
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
                    homestage.setScene(homescene);
                    System.out.println("Dashboard Clicked");
                    break;

                case "My Bookings":
                    Booking bookingpage = new Booking();
                    homestage.setScene(bookingpage.getBookingScene());
                    break;

                case "Live Navigation":
                    Navigation navigationpage = new Navigation();
                    homestage.setScene(navigationpage.getNavigationScene());
                    break;

                case "Bus Status":
                    BusStatus busstatuspage = new BusStatus();
                    homestage.setScene(busstatuspage.getBusStatusScene());
                    break;

                case "Alerts & Issues":
                    Alerts_Issues alertpage = new Alerts_Issues();
                    homestage.setScene(alertpage.getAlertsScene());
                    break;

                case "Shift & Earnings":
                    Shift_Earning shiftpage = new Shift_Earning();
                    homestage.setScene(shiftpage.getShiftScene());
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
        left.getChildren().addAll(label("Today's run", "heading"),
                label("Shift 07:00 — 15:00 · Bus #07 · Baner depot", "small-muted"));

        TextField search = new TextField();
        search.setPromptText("Search bookings, stops, bus ID...");
        search.getStyleClass().add("search-field");
        search.setPrefWidth(320);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // GPS pill
        HBox gps = new HBox(6, new Circle(5, Color.web("#10b981")), label("GPS live", "small-muted"));
        gps.setAlignment(Pos.CENTER);
        gps.getStyleClass().add("gps-pill");

        // Bell
        StackPane bell = new StackPane();
        Label bellIcon = new Label("\uD83D\uDD14");
        bellIcon.setStyle("-fx-font-size: 18px;");
        StackPane badge = new StackPane(new Circle(8, Color.web("#ef4444")),
                new Label("2") {
                    {
                        setStyle("-fx-text-fill:white;-fx-font-size:9px;-fx-font-weight:bold;");
                    }
                });
        badge.setTranslateX(10);
        badge.setTranslateY(-10);
        bell.getChildren().addAll(bellIcon, badge);

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

    private HBox buildMainContent() {
        HBox main = new HBox(16);
        main.setPadding(new Insets(16));

        VBox center = new VBox(14);
        HBox.setHgrow(center, Priority.ALWAYS);
        center.getChildren().addAll(buildEmergencyBanner(), buildInfoCards(), buildActiveBooking());

        VBox rightCol = new VBox(16);
        rightCol.setPrefWidth(280);
        rightCol.getChildren().add(buildUpNext());

        main.getChildren().addAll(center, rightCol);
        return main;
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

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Button dismiss = new Button("Dismiss");
        dismiss.getStyleClass().add("secondary-btn");
        Button accept = new Button("Accept diversion");
        accept.getStyleClass().add("danger-btn");

        banner.getChildren().addAll(icon, text, sp, dismiss, accept);
        return banner;
    }

    // ── INFO CARDS ROW ──

    private HBox buildInfoCards() {
        HBox row = new HBox(12);

        // Bus card
        VBox bus = card("Assigned bus");
        Label busNum = new Label("Bus #07");
        busNum.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:22px;-fx-font-weight:bold;");
        bus.getChildren().addAll(busNum, label("MH12 AB 1234 · 60 kW · CCS2", "card-sub"),
                gap(8));
        HBox.setHgrow(bus, Priority.ALWAYS);

        // Battery card
        VBox batt = card("Battery reserve");
        HBox battRow = new HBox(6);
        battRow.setAlignment(Pos.BASELINE_LEFT);
        Label pct = new Label("68%");
        pct.setStyle("-fx-text-fill:#10b981;-fx-font-size:24px;-fx-font-weight:bold;");
        battRow.getChildren().addAll(pct, label("198 kWh left", "card-sub"));
        ProgressBar pb = new ProgressBar(0.68);
        pb.getStyleClass().add("battery-bar");
        pb.setMaxWidth(Double.MAX_VALUE);
        batt.getChildren().addAll(battRow, pb, gap(8));
        HBox.setHgrow(batt, Priority.ALWAYS);

        // Ports card
        VBox ports = card("Ports");
        ports.getStyleClass().add("card-highlight");
        HBox portsRow = new HBox(4);
        portsRow.setAlignment(Pos.BASELINE_LEFT);
        Label p1 = new Label("2");
        p1.setStyle("-fx-text-fill:#10b981;-fx-font-size:24px;-fx-font-weight:bold;");
        Label p2 = new Label("2");
        p2.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:24px;-fx-font-weight:bold;");
        portsRow.getChildren().addAll(p1, label("in use · ", "card-sub"), p2, label("free", "card-sub"));
        ports.getChildren().add(portsRow);
        HBox.setHgrow(ports, Priority.ALWAYS);

        // Today card
        VBox today = card("Today");
        HBox todayRow = new HBox(4);
        todayRow.setAlignment(Pos.BASELINE_LEFT);
        Label num = new Label("6");
        num.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:24px;-fx-font-weight:bold;");
        todayRow.getChildren().addAll(num, label("sessions · \u20B94,180 billed", "card-sub"));
        today.getChildren().addAll(todayRow, label("1 booking remaining on shift", "card-sub"),
                gap(8));
        HBox.setHgrow(today, Priority.ALWAYS);

        row.getChildren().addAll(bus, batt, ports, today);
        return row;
    }

    // ── ACTIVE BOOKING ──

    private VBox buildActiveBooking() {
        VBox section = new VBox(14);
        section.getStyleClass().add("active-booking-card");
        section.setPadding(new Insets(18));

        // Header
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        Label statusBadge = new Label("ARRIVED");
        statusBadge.getStyleClass().add("status-badge-green");

        header.getChildren().addAll(
                label("Active booking", "booking-stage"), statusBadge);

        // Customer + Destination
        HBox details = new HBox(16);

        VBox cust = new VBox(4);
        cust.getStyleClass().add("inner-card");
        cust.setPadding(new Insets(14));
        HBox.setHgrow(cust, Priority.ALWAYS);
        cust.getChildren().addAll(
                label("CUSTOMER", "detail-label"),
                label("Anuj Mehta", "booking-title"),
                label("Tata Nexon EV · MH12 AB 1234", "booking-detail"),
                label("Requested 25 kWh · CCS2", "booking-detail-muted"));

        VBox dest = new VBox(4);
        dest.getStyleClass().add("inner-card");
        dest.setPadding(new Insets(14));
        HBox.setHgrow(dest, Priority.ALWAYS);
        dest.getChildren().addAll(
                label("DESTINATION", "detail-label"),
                label("Baner Road, Pune", "booking-title"),
                label("Ganga Trueno, Tower B · P2 level", "booking-detail"),
                label("ETA 04:30 PM · 12 min · 4.8 km", "booking-detail-muted"));

        details.getChildren().addAll(cust, dest);

        // Progress tracker
        HBox tracker = buildProgressTracker();

        // Buttons
        Button startBtn = new Button("Start charging session");
        startBtn.getStyleClass().add("primary-btn");
        Button portBtn = new Button("Port 3 - assign");
        portBtn.getStyleClass().add("outlined-btn");
        HBox btns = new HBox(12, startBtn, portBtn);

        section.getChildren().addAll(header, details, tracker, btns);
        return section;
    }

    private HBox buildProgressTracker() {
        HBox tracker = new HBox();
        tracker.setAlignment(Pos.CENTER_LEFT);
        tracker.setPadding(new Insets(14, 0, 6, 0));

        String[] steps = { "ASSIGNED", "EN_ROUTE", "ARRIVED", "CHARGING", "COMPLETED" };
        int active = 2;

        for (int i = 0; i < steps.length; i++) {
            VBox step = new VBox(6);
            step.setAlignment(Pos.CENTER);

            StackPane circlePane = new StackPane();
            if (i < active) {
                circlePane.getChildren().addAll(new Circle(10, Color.web("#10b981")),
                        new Label("\u2713") {
                            {
                                setStyle("-fx-text-fill:white;-fx-font-size:10px;");
                            }
                        });
            } else if (i == active) {
                circlePane.getChildren().addAll(new Circle(12, Color.web("#10b981")), new Circle(5, Color.WHITE));
            } else {
                Circle c = new Circle(10, Color.TRANSPARENT);
                c.setStroke(Color.web("#374151"));
                c.setStrokeWidth(2);
                circlePane.getChildren().add(c);
            }

            Label stepLbl = new Label(steps[i]);
            stepLbl.setStyle("-fx-font-size:10px; -fx-text-fill:" + (i <= active ? "#cbd5e1" : "#4b5563") + ";");
            step.getChildren().addAll(circlePane, stepLbl);
            tracker.getChildren().add(step);

            if (i < steps.length - 1) {
                Line line = new Line(0, 0, 40, 0);
                line.setStroke(Color.web(i < active ? "#10b981" : "#374151"));
                line.setStrokeWidth(2);
                StackPane lp = new StackPane(line);
                lp.setPadding(new Insets(0, 4, 16, 4));
                tracker.getChildren().add(lp);
            }
        }
        return tracker;
    }

    // ── UP NEXT ──

    private VBox buildUpNext() {
        VBox section = new VBox(10);
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.getChildren().addAll(label("Up next", "section-title"));

        section.getChildren().addAll(header,
                upNextItem("Sneha Patil", "Pashan Sus Road", "05:20 PM", "30 kWh"),
                upNextItem("Rohit Sharma", "Aundh, Westend Mall", "06:05 PM", "18 kWh"),
                upNextItem("Kavya Iyer", "Balewadi High St.", "06:50 PM", "22 kWh"));
        return section;
    }

    private HBox upNextItem(String name, String location, String time, String energy) {
        HBox item = new HBox(10);
        item.setPadding(new Insets(10, 12, 10, 12));
        item.getStyleClass().add("upnext-item");
        item.setAlignment(Pos.CENTER_LEFT);

        VBox nameBox = new VBox(2);
        HBox.setHgrow(nameBox, Priority.ALWAYS);
        Label n = new Label(name);
        n.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:13px;-fx-font-weight:bold;");
        nameBox.getChildren().addAll(n, label(location, "small-muted"));

        VBox timeBox = new VBox(2);
        timeBox.setAlignment(Pos.CENTER_RIGHT);
        Label t = new Label(time);
        t.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:13px;-fx-font-weight:bold;");
        timeBox.getChildren().addAll(t, label(energy, "small-muted"));

        item.getChildren().addAll(new Circle(6, Color.web("#10b981")), nameBox, timeBox);
        return item;
    }

    // ── HELPERS ──

    private Label label(String text, String styleClass) {
        Label l = new Label(text);
        l.getStyleClass().add(styleClass);
        return l;
    }

    private VBox card(String title) {
        VBox c = new VBox(6);
        c.getStyleClass().add("card");
        c.setPadding(new Insets(14));
        c.getChildren().add(label(title, "card-title"));
        return c;
    }

    private Region gap(double height) {
        Region r = new Region();
        r.setPrefHeight(height);
        return r;
    }
}
