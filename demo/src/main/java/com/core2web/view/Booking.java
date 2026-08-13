package com.core2web.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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

public class Booking {
    private Scene bookingscene;

    Scene getBookingScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0f1720;");
        root.setLeft(buildSidebar());

        // Build the center content area (top bar + main content)
        VBox centerArea = new VBox();
        centerArea.getChildren().addAll(buildTopBar(), buildMainContent());
        VBox.setVgrow(centerArea.getChildren().get(1), Priority.ALWAYS);
        root.setCenter(centerArea);

        Scene sc = new Scene(root, DriverDashboard.homestage.getWidth(), DriverDashboard.homestage.getHeight());
        bookingscene = sc;
        sc.getStylesheets().add(getClass().getResource("/styles/booking.css").toExternalForm());
        // arg0.setScene(sc);
        // arg0.setTitle("ChargeOn · My Bookings");
        // arg0.show();
        return bookingscene;
    }

    // ── SIDEBAR ──

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

        // Left: Page title
        VBox left = new VBox(2);
        left.getChildren().addAll(
                label("My bookings", "heading"),
                label("Assigned, queued and completed charging bookings", "small-muted"));

        // Search
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

        // User info
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
        HBox main = new HBox(16);
        main.setPadding(new Insets(16));

        // Left column: emergency banner + bookings table
        VBox leftCol = new VBox(14);
        HBox.setHgrow(leftCol, Priority.ALWAYS);
        leftCol.getChildren().addAll(buildEmergencyBanner(), buildBookingsSection());

        // Right column: new assignment card + session log
        VBox rightCol = new VBox(16);
        rightCol.setPrefWidth(320);
        rightCol.getChildren().addAll(buildNewAssignmentCard(), buildSessionLog());

        main.getChildren().addAll(leftCol, rightCol);

        ScrollPane scrollPane = new ScrollPane(main);
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

    // ── BOOKINGS TABLE SECTION ──

    private VBox buildBookingsSection() {
        VBox section = new VBox(14);
        section.getStyleClass().add("bookings-section");
        section.setPadding(new Insets(18));

        // Header row: title + filter tabs
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        Label sectionTitle = new Label("Assigned bookings — today");
        sectionTitle.getStyleClass().add("booking-stage");

        Label frRef = label("FR-DRV-02 · 03", "muted-tag");

        Region headerSp = new Region();
        HBox.setHgrow(headerSp, Priority.ALWAYS);

        // Filter tabs
        Button allTab = new Button("All");
        allTab.getStyleClass().add("filter-tab-active");
        Button activeTab = new Button("Active");
        activeTab.getStyleClass().add("filter-tab");
        Button completedTab = new Button("Completed");
        completedTab.getStyleClass().add("filter-tab");

        HBox tabs = new HBox(2, allTab, activeTab, completedTab);
        tabs.setAlignment(Pos.CENTER);
        tabs.getStyleClass().add("filter-tabs-container");

        header.getChildren().addAll(sectionTitle, frRef, headerSp, tabs);

        // Column headers
        HBox colHeaders = new HBox();
        colHeaders.setPadding(new Insets(10, 12, 10, 12));
        colHeaders.setAlignment(Pos.CENTER_LEFT);
        colHeaders.getChildren().addAll(
                colLabel("BOOKING /\nCUSTOMER", 160),
                colLabel("DESTINATION", 150),
                colLabel("ETA", 80),
                colLabel("ENERGY", 70),
                colLabel("STATUS", 100));

        // Booking rows
        VBox rows = new VBox(2);
        rows.getChildren().addAll(
                bookingRow("Anuj Mehta", "BKG-4468", "Ganga Trueno, Baner", "04:30 PM", "25 kWh", "EN_ROUTE",
                        "#3b82f6"),
                bookingRow("Emergency · Priya D.", "BKG-4471", "Hinjewadi Ph-1", "04:55 PM", "15 kWh", "PRIORITY",
                        "#ef4444"),
                bookingRow("Sneha Patil", "BKG-4473", "Pashan Sus Road", "05:20 PM", "30 kWh", "ASSIGNED", "#10b981"),
                bookingRow("Rohit Sharma", "BKG-4475", "Westend Mall, Aundh", "06:05 PM", "18 kWh", "QUEUED",
                        "#f59e0b"),
                bookingRow("Meera Joshi", "BKG-4462", "Kothrud Depot", "02:10 PM", "20 kWh", "COMPLETED", "#6b7280"),
                bookingRow("Vikram Rao", "BKG-4459", "Warje Bridge", "12:40 PM", "28 kWh", "COMPLETED", "#6b7280"));

        section.getChildren().addAll(header, colHeaders, rows);
        return section;
    }

    private HBox bookingRow(String name, String bookingId, String dest, String eta, String energy, String status,
            String statusColor) {
        HBox row = new HBox();
        row.setPadding(new Insets(12, 12, 12, 12));
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("booking-row");

        // Name + booking ID
        VBox nameBox = new VBox(2);
        nameBox.setPrefWidth(160);
        nameBox.setMinWidth(160);
        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:13px;-fx-font-weight:bold;");
        nameLabel.setWrapText(true);
        Label idLabel = new Label(bookingId);
        idLabel.setStyle("-fx-text-fill:#64748b;-fx-font-size:11px;");
        nameBox.getChildren().addAll(nameLabel, idLabel);

        // Destination
        Label destLabel = new Label(dest);
        destLabel.setStyle("-fx-text-fill:#cbd5e1;-fx-font-size:13px;");
        destLabel.setPrefWidth(150);
        destLabel.setMinWidth(150);

        // ETA
        Label etaLabel = new Label(eta);
        etaLabel.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:13px;-fx-font-weight:bold;");
        etaLabel.setPrefWidth(80);
        etaLabel.setMinWidth(80);

        // Energy
        Label energyLabel = new Label(energy);
        energyLabel.setStyle("-fx-text-fill:#cbd5e1;-fx-font-size:13px;");
        energyLabel.setPrefWidth(70);
        energyLabel.setMinWidth(70);

        // Status badge
        Label statusBadge = new Label(status);
        statusBadge.setStyle(
                "-fx-background-color: " + statusColor + "22;" +
                        "-fx-text-fill: " + statusColor + ";" +
                        "-fx-font-size: 10px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 4 10 4 10;" +
                        "-fx-background-radius: 6;");
        HBox statusBox = new HBox(statusBadge);
        statusBox.setPrefWidth(100);
        statusBox.setMinWidth(100);
        statusBox.setAlignment(Pos.CENTER_LEFT);

        row.getChildren().addAll(nameBox, destLabel, etaLabel, energyLabel, statusBox);
        return row;
    }

    private Label colLabel(String text, double width) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill:#64748b;-fx-font-size:10px;-fx-font-weight:bold;");
        l.setPrefWidth(width);
        l.setMinWidth(width);
        return l;
    }

    // ── NEW ASSIGNMENT CARD ──

    private VBox buildNewAssignmentCard() {
        VBox card = new VBox(10);
        card.getStyleClass().add("assignment-card");
        card.setPadding(new Insets(18));

        // Header row
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label newTag = new Label("NEW ASSIGNMENT");
        newTag.getStyleClass().add("new-assignment-tag");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Label timeLabel = new Label("01:42");
        timeLabel.setStyle("-fx-text-fill:#64748b;-fx-font-size:12px;");
        header.getChildren().addAll(newTag, sp, timeLabel);

        // Customer name
        Label custName = new Label("Sneha Patil");
        custName.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:20px;-fx-font-weight:bold;");

        // Details
        Label details = new Label(
                "Pashan Sus Road · MG ZS EV · 30 kWh · CCS2\nETA 05:20 PM · 6.4 km from current stop");
        details.setStyle("-fx-text-fill:#94a3b8;-fx-font-size:12px;");
        details.setWrapText(true);

        // Action buttons
        HBox buttons = new HBox(12);
        buttons.setPadding(new Insets(8, 0, 0, 0));
        Button declineBtn = new Button("Decline");
        declineBtn.getStyleClass().add("decline-btn");
        declineBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(declineBtn, Priority.ALWAYS);
        Button acceptBtn = new Button("Accept");
        acceptBtn.getStyleClass().add("accept-btn");
        acceptBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(acceptBtn, Priority.ALWAYS);
        buttons.getChildren().addAll(declineBtn, acceptBtn);

        // Footer note
        Label footer = new Label("Accepting sets the booking to EN_ROUTE and adds a stop to your route.");
        footer.setStyle("-fx-text-fill:#64748b;-fx-font-size:10px;");
        footer.setWrapText(true);

        card.getChildren().addAll(header, custName, details, buttons, footer);
        return card;
    }

    // ── SESSION LOG ──

    private VBox buildSessionLog() {
        VBox card = new VBox(14);
        card.getStyleClass().add("session-log-card");
        card.setPadding(new Insets(18));

        Label sectionTitle = new Label("Session log");
        sectionTitle.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:16px;-fx-font-weight:bold;");

        VBox logEntries = new VBox(12);
        logEntries.getChildren().addAll(
                logEntry("#10b981", "Session completed · BKG-4462 · 20.0 kWh · \u20B9500", "02:34 PM"),
                logEntry("#f59e0b", "Arrival confirmed manually at Kothrud Depot", "02:06 PM"),
                logEntry("#ef4444", "Port 2 handshake dropped — reported to ops", "01:12 PM"),
                logEntry("#3b82f6", "Clocked in at Baner depot · Bus #07 assigned", "06:54 AM"));

        card.getChildren().addAll(sectionTitle, logEntries);
        return card;
    }

    private HBox logEntry(String dotColor, String text, String time) {
        HBox entry = new HBox(10);
        entry.setAlignment(Pos.TOP_LEFT);

        Circle dot = new Circle(5, Color.web(dotColor));
        dot.setTranslateY(5);

        VBox textBox = new VBox(2);
        HBox.setHgrow(textBox, Priority.ALWAYS);
        Label textLabel = new Label(text);
        textLabel.setStyle("-fx-text-fill:#cbd5e1;-fx-font-size:12px;");
        textLabel.setWrapText(true);
        Label timeLabel = new Label(time);
        timeLabel.setStyle("-fx-text-fill:#64748b;-fx-font-size:10px;");
        textBox.getChildren().addAll(textLabel, timeLabel);

        entry.getChildren().addAll(dot, textBox);
        return entry;
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
