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

public class Alerts_Issues {

    Scene getAlertsScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0f1720;");

        root.setLeft(buildSidebar());

        VBox centerArea = new VBox();
        centerArea.getChildren().addAll(buildTopBar(), buildMainContent());
        VBox.setVgrow(centerArea.getChildren().get(1), Priority.ALWAYS);
        root.setCenter(centerArea);

        Scene scene = new Scene(root, DriverDashboard.homestage.getWidth(), DriverDashboard.homestage.getHeight());
        scene.getStylesheets().add(getClass().getResource("/styles/alertsissues.css").toExternalForm());

        // stage.setTitle("ChargeOn · Alerts & Issues");
        // stage.setScene(scene);
        // stage.show();
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
        Label shiftInfo = label("07:00 \u2014 15:00 \u00B7 Depot\nBaner", "shift-details");
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
        left.getChildren().addAll(label("Alerts & issues", "heading"),
                label("Report malfunctions and read operations notices", "small-muted"));

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
                label("Emergency booking \u00B7 BKG-4471 diverted to your route", "emergency-title"),
                label("Stranded EV at Hinjewadi Ph-1, Ganga Trueno \u00B7 4.2 km \u00B7 reroute adds 9 min",
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

    // ── MAIN CONTENT ──

    private ScrollPane buildMainContent() {
        VBox content = new VBox(16);
        content.setPadding(new Insets(16));

        HBox columns = new HBox(16);

        // Left column: Report form
        VBox leftCol = new VBox(16);
        HBox.setHgrow(leftCol, Priority.ALWAYS);
        leftCol.getChildren().add(buildReportForm());

        // Right column: Recent reports + Alerts from ops
        VBox rightCol = new VBox(16);
        rightCol.setPrefWidth(380);
        rightCol.getChildren().addAll(buildRecentReports(), buildAlertsFromOps());

        columns.getChildren().addAll(leftCol, rightCol);

        content.getChildren().addAll(buildEmergencyBanner(), columns);

        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.getStyleClass().add("scroll-pane");
        sp.setStyle("-fx-background-color: transparent;");
        return sp;
    }

    // ── REPORT A BUS ISSUE FORM ──

    private VBox buildReportForm() {
        VBox form = new VBox(14);
        form.getStyleClass().add("report-card");
        form.setPadding(new Insets(22));

        // Header row
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        Label formTitle = new Label("Report a bus issue");
        formTitle.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:18px;-fx-font-weight:bold;");
        Label formTag = new Label("FR-DRV-08");
        formTag.setStyle("-fx-text-fill:#64748b;-fx-font-size:10px;");
        header.getChildren().addAll(formTitle, formTag);

        Label formSubtitle = new Label("Goes straight to operations with bus ID, location and telemetry attached.");
        formSubtitle.setStyle("-fx-text-fill:#94a3b8;-fx-font-size:12px;");
        formSubtitle.setWrapText(true);

        // What is wrong? label
        Label whatLabel = new Label("What is wrong?");
        whatLabel.setStyle("-fx-text-fill:#94a3b8;-fx-font-size:12px;-fx-font-weight:bold;");
        VBox.setMargin(whatLabel, new Insets(6, 0, 0, 0));

        // Dropdown (ComboBox)
        ComboBox<String> issueType = new ComboBox<>();
        issueType.getItems().addAll("Charging port", "Display issue", "Battery fault", "Coolant system",
                "Tyre pressure", "Other");
        issueType.setValue("Charging port");
        issueType.setMaxWidth(Double.MAX_VALUE);
        issueType.getStyleClass().add("issue-combo");

        // Description label
        Label descLabel = new Label("Description");
        descLabel.setStyle("-fx-text-fill:#94a3b8;-fx-font-size:12px;-fx-font-weight:bold;");
        VBox.setMargin(descLabel, new Insets(4, 0, 0, 0));

        // Description text area
        TextArea descArea = new TextArea();
        descArea.setPromptText(
                "Port 2 drops the handshake about 30 seconds into a session; customer had to re-plug twice...");
        descArea.setPrefRowCount(6);
        descArea.setWrapText(true);
        descArea.getStyleClass().add("desc-textarea");

        // Auto-attached info
        HBox attachedInfo = new HBox(8);
        attachedInfo.setAlignment(Pos.CENTER_LEFT);
        attachedInfo.setPadding(new Insets(6, 0, 0, 0));
        Label checkMark = new Label("\u2713");
        checkMark.setStyle("-fx-text-fill:#10b981;-fx-font-size:13px;-fx-font-weight:bold;");
        Label attachedText = new Label("Bus #07 \u00B7 Baner Road \u00B7 04:22 PM attached automatically");
        attachedText.setStyle("-fx-text-fill:#94a3b8;-fx-font-size:11px;");
        attachedInfo.getChildren().addAll(checkMark, attachedText);

        // Buttons row
        HBox buttons = new HBox(12);
        buttons.setAlignment(Pos.CENTER_LEFT);
        buttons.setPadding(new Insets(4, 0, 0, 0));
        Button attachPhoto = new Button("\uD83D\uDCCE  Attach photo");
        attachPhoto.getStyleClass().add("secondary-btn");
        Button submitReport = new Button("Submit report");
        submitReport.getStyleClass().add("primary-btn");
        submitReport.setPrefWidth(200);
        buttons.getChildren().addAll(attachPhoto, submitReport);

        form.getChildren().addAll(header, formSubtitle, whatLabel, issueType, descLabel, descArea, attachedInfo,
                buttons);
        return form;
    }

    // ── RECENT REPORTS ──

    private VBox buildRecentReports() {
        VBox section = new VBox(10);

        Label sectionTitle = new Label("Recent reports");
        sectionTitle.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:16px;-fx-font-weight:bold;");
        sectionTitle.setPadding(new Insets(0, 0, 4, 0));

        section.getChildren().addAll(sectionTitle,
                reportItem("Port 2 handshake drop", "RPT-318 \u00B7 today 01:12 PM \u00B7 medium", "IN REVIEW",
                        "#eab308"),
                reportItem("Cabin display flicker", "RPT-311 \u00B7 04 Aug \u00B7 low", "SCHEDULED", "#10b981"),
                reportItem("Coolant warning at 62 \u00B0C", "RPT-298 \u00B7 29 Jul \u00B7 critical", "RESOLVED",
                        "#64748b"));

        return section;
    }

    private HBox reportItem(String title, String detail, String status, String statusColor) {
        HBox item = new HBox(10);
        item.setPadding(new Insets(14, 16, 14, 16));
        item.getStyleClass().add("report-item");
        item.setAlignment(Pos.CENTER_LEFT);

        VBox textBox = new VBox(4);
        HBox.setHgrow(textBox, Priority.ALWAYS);
        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:13px;-fx-font-weight:bold;");
        Label detailLbl = new Label(detail);
        detailLbl.setStyle("-fx-text-fill:#64748b;-fx-font-size:11px;");
        textBox.getChildren().addAll(titleLbl, detailLbl);

        Label statusLbl = new Label(status);
        statusLbl.setStyle("-fx-text-fill:" + statusColor + ";-fx-font-size:10px;-fx-font-weight:bold;"
                + "-fx-background-color:rgba(" + hexToRgba(statusColor, 0.12) + ");"
                + "-fx-padding:4 10 4 10;-fx-background-radius:6;");

        item.getChildren().addAll(textBox, statusLbl);
        return item;
    }

    // ── ALERTS FROM OPERATIONS ──

    private VBox buildAlertsFromOps() {
        VBox section = new VBox(10);

        Label sectionTitle = new Label("Alerts from operations");
        sectionTitle.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:16px;-fx-font-weight:bold;");
        sectionTitle.setPadding(new Insets(4, 0, 4, 0));

        section.getChildren().addAll(sectionTitle,
                opsAlertItem("Emergency booking BKG-4471 \u00B7 Hinjewadi Ph-1",
                        "Route diversion requested \u00B7 04:18 PM", "#ef4444"),
                opsAlertItem("Depot notice \u00B7 charge to 90% before 15:00 handover",
                        "Operations \u00B7 01:05 PM", "#10b981"));

        return section;
    }

    private VBox opsAlertItem(String title, String detail, String borderColor) {
        VBox item = new VBox(4);
        item.setPadding(new Insets(14, 16, 14, 16));
        item.getStyleClass().add("ops-alert-item");
        item.setStyle("-fx-border-color:" + borderColor + " transparent transparent transparent;"
                + "-fx-border-width:0 0 0 3;"
                + "-fx-background-color:#111827;"
                + "-fx-background-radius:10;"
                + "-fx-border-radius:0 10 10 0;");

        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:13px;-fx-font-weight:bold;");
        titleLbl.setWrapText(true);
        Label detailLbl = new Label(detail);
        detailLbl.setStyle("-fx-text-fill:#64748b;-fx-font-size:11px;");

        item.getChildren().addAll(titleLbl, detailLbl);
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

    /** Convert hex color to r,g,b,a string for use in rgba(). */
    private String hexToRgba(String hex, double alpha) {
        Color c = Color.web(hex);
        int r = (int) (c.getRed() * 255);
        int g = (int) (c.getGreen() * 255);
        int b = (int) (c.getBlue() * 255);
        return r + "," + g + "," + b + "," + alpha;
    }
}
