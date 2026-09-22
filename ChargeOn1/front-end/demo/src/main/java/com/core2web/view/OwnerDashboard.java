package com.core2web.view;

import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeLineCap;
import javafx.stage.Stage;

import static com.core2web.view.OwnerLayout.*;

import com.core2web.controller.OwnerDashboardController;
import com.core2web.controller.OwnerDashboardController.ActivityItem;
import com.core2web.controller.OwnerDashboardController.DashboardData;
import com.core2web.dao.OwnerDao;
import com.core2web.model.Booking;
import com.core2web.model.ChargingSession;
import com.core2web.model.Owner;

public class OwnerDashboard {

    public static Stage homestage;

    static Stage window;
    static Scene scene;
    static ListView<String> sidebar;
    static Label heading;
    static Label subheading;
    static StackPane middleBox;

    public static void show(Stage stage) {

        window = stage;
        homestage = stage;

        sidebar = new ListView<>();
        sidebar.getItems().addAll(
                "Dashboard",
                "Book Charging",
                "Live Tracking",
                "My Vehicles",
                "Wallet & Payments",
                "History & Impact",
                "AI Assistant"
        );

        sidebar.getStyleClass().add("nav-list");
        sidebar.setFocusTraversable(false);

        sidebar.getSelectionModel().selectedItemProperty()
                .addListener((item, oldPage, newPage) ->
                        showPage(newPage));

        heading = new Label();
        heading.getStyleClass().add("heading");

        subheading = new Label();
        subheading.getStyleClass().add("small-muted");

        middleBox = new StackPane();

        VBox center = new VBox();
        center.getChildren().add(
                buildTopBar(heading, subheading)
        );

        center.getChildren().add(middleBox);

        VBox.setVgrow(
                middleBox,
                Priority.ALWAYS
        );

        BorderPane root = new BorderPane();

        root.setStyle(
                "-fx-background-color: #0f1720;"
        );

        root.setLeft(
                buildSidebar(sidebar, stage)
        );

        root.setCenter(center);

        double width = AppWindowSize.width();
        double height = AppWindowSize.height();

        scene = new Scene(
                root,
                width,
                height
        );

        scene.getStylesheets().add(
                OwnerDashboard.class
                        .getResource(
                                "/styles/owner.css"
                        )
                        .toExternalForm()
        );

        scene.getStylesheets().add(
                OwnerDashboard.class
                        .getResource(
                                "/styles/ownerdashboard.css"
                        )
                        .toExternalForm()
        );

        scene.getStylesheets().add(
                OwnerDashboard.class
                        .getResource(
                                "/styles/bookcharging.css"
                        )
                        .toExternalForm()
        );

        scene.getStylesheets().add(
                OwnerDashboard.class
                        .getResource(
                                "/styles/livetracking.css"
                        )
                        .toExternalForm()
        );

        scene.getStylesheets().add(
                OwnerDashboard.class
                        .getResource(
                                "/styles/myvehicles.css"
                        )
                        .toExternalForm()
        );

        scene.getStylesheets().add(
                OwnerDashboard.class
                        .getResource(
                                "/styles/walletpayments.css"
                        )
                        .toExternalForm()
        );

        scene.getStylesheets().add(
                OwnerDashboard.class
                        .getResource(
                                "/styles/historyimpact.css"
                        )
                        .toExternalForm()
        );

        scene.getStylesheets().add(
                OwnerDashboard.class
                        .getResource(
                                "/styles/sidebar.css"
                        )
                        .toExternalForm()
        );

        goTo("Dashboard");

        window.setMaximized(false);
        window.setScene(scene);
        window.show();
    }

    public static void goTo(String page) {

        if (sidebar == null) {
            return;
        }

        if (page.equals(
                sidebar.getSelectionModel().getSelectedItem())) {

            showPage(page);

        } else {

            sidebar.getSelectionModel().select(page);
        }
    }

    public static void openNotifications() {

        if (middleBox == null) {
            return;
        }

        heading.setText("Notifications");

        subheading.setText(
                "Recent alerts and important updates on your account"
        );

        middleBox.getStyleClass().clear();

        middleBox.getChildren().setAll(
                OwnerNotifications.buildMainContent()
        );

        window.setTitle(
                "ChargeOn · Notifications"
        );
    }

    public static void openProfile() {

        if (middleBox == null) {
            return;
        }

        heading.setText("Profile");

        subheading.setText(
                "Manage your owner profile and contact information"
        );

        middleBox.getStyleClass().clear();

        middleBox.getChildren().setAll(
                OwnerProfile.buildMainContent()
        );

        window.setTitle(
                "ChargeOn · Profile"
        );
    }

    public static void openReportIssue(Booking booking) {

        if (middleBox == null) {
            return;
        }

        heading.setText("Report an issue");

        subheading.setText(
                "Tell us what went wrong — the support team will receive it immediately"
        );

        middleBox.getStyleClass().clear();

        middleBox.getChildren().setAll(
                ReportIssue.buildMainContent(booking)
        );

        window.setTitle(
                "ChargeOn · Report an issue"
        );
    }

    public static void openSubscriptionPlans() {

        if (middleBox == null) {
            return;
        }

        heading.setText("Subscription plans");

        subheading.setText(
                "Choose a premium plan to unlock added benefits"
        );

        middleBox.getStyleClass().clear();

        middleBox.getChildren().setAll(
                OwnerSubscription.buildMainContent()
        );

        window.setTitle(
                "ChargeOn · Subscription plans"
        );
    }

    private static void showPage(String page) {

        if (page == null) {
            return;
        }

        switch (page) {

            case "Dashboard":

                Owner owner = dashboardController.getOwnerProfile();
                String ownerName = "Owner";

                if (owner != null
                        && owner.getName() != null
                        && !owner.getName().trim().isEmpty()) {

                    ownerName =
                            owner.getName().trim();
                }

                heading.setText(
                        "Hello, " + ownerName
                );

                subheading.setText(
                        "Here’s your charging overview"
                );

                middleBox.getStyleClass().setAll(
                        "page-dashboard"
                );

                middleBox.getChildren().setAll(
                        buildMainContent()
                );

                break;

            case "Book Charging":

                heading.setText(
                        "Book charging"
                );

                subheading.setText(
                        "Instant, scheduled or emergency — a bus comes to you"
                );

                middleBox.getStyleClass().setAll(
                        "page-book-charging"
                );

                middleBox.getChildren().setAll(
                        BookCharging.buildMainContent()
                );

                break;

            case "Live Tracking":

                heading.setText(
                        "Live tracking"
                );

                subheading.setText(
                        "Follow the assigned bus to your pickup point"
                );

                middleBox.getStyleClass().setAll(
                        "page-live-tracking"
                );

                middleBox.getChildren().setAll(
                        LiveTracking.buildMainContent()
                );

                break;

            case "My Vehicles":

                heading.setText(
                        "My vehicles"
                );

                subheading.setText(
                        "Registered EVs under this account"
                );

                middleBox.getStyleClass().setAll(
                        "page-my-vehicles"
                );

                middleBox.getChildren().setAll(
                        MyVehicles.buildMainContent()
                );

                break;

            case "Wallet & Payments":

                heading.setText(
                        "Wallet & payments"
                );

                subheading.setText(
                        "Balance, methods and transaction history"
                );

                middleBox.getStyleClass().setAll(
                        "page-wallet-payments"
                );

                middleBox.getChildren().setAll(
                        WalletPayments.buildMainContent()
                );

                break;

            case "History & Impact":

                heading.setText(
                        "History & impact"
                );

                subheading.setText(
                        "Your charging history and environmental impact"
                );

                middleBox.getStyleClass().setAll(
                        "page-history-impact"
                );

                middleBox.getChildren().setAll(
                        HistoryImpact.buildMainContent()
                );

                break;

            case "AI Assistant":

                heading.setText(
                        "ChargeOn AI Assistant"
                );

                subheading.setText(
                        "Your intelligent EV charging assistant"
                );

                middleBox.getStyleClass().setAll(
                        "page-ai-copilot"
                );

                middleBox.getChildren().setAll(
                        CopilotChatUIOwner.buildMainContent()
                );

                break;

            default:
                return;
        }

        window.setTitle(
                "ChargeOn · " + page
        );
    }

    Scene getOwnerScene() {

        show(homestage);

        return scene;
    }

    private static final OwnerDashboardController dashboardController =
            new OwnerDashboardController();

    static ScrollPane buildMainContent() {

        return new OwnerDashboard().buildContent();
    }

    private ScrollPane buildContent() {

        VBox content = new VBox(18);
        content.setPadding(new Insets(20));

        DashboardData data = dashboardController.load();

        Label upcomingTimeLbl;
        Label upcomingSubLbl;
        if (data.upcomingBooking != null) {
            upcomingTimeLbl = bigLabel(data.upcomingBooking.getScheduledTime());
            upcomingSubLbl  = label(data.upcomingBooking.summaryLine(data.upcomingBusCode), "card-sub");
        } else {
            upcomingTimeLbl = bigLabel("None");
            upcomingSubLbl  = label("No upcoming booking scheduled", "card-sub");
        }

        Label bookingsCountLbl = bigLabel(String.valueOf(data.bookingsThisMonth));
        Label bookingsSubLbl   = label(data.bookingBreakdown(), "card-sub");
        Label spentLbl         = bigLabel(data.spentThisMonthFormatted());
        Label co2Lbl           = new Label("—");
        co2Lbl.setStyle("-fx-text-fill:#10b981;-fx-font-size:12px;-fx-font-weight:bold;");

        HBox summaryRow = buildSummaryCards(
                upcomingTimeLbl, upcomingSubLbl,
                bookingsCountLbl, bookingsSubLbl,
                spentLbl, co2Lbl
        );

        StackPane sessionHolder = new StackPane();
        sessionHolder.getChildren().add(data.activeSession != null
                ? buildActiveSession(data.activeSession, data.upcomingBooking)
                : data.upcomingBooking != null
                        ? buildBookedSession(data.upcomingBooking)
                        : buildSessionPlaceholder());

        HBox.setHgrow(sessionHolder, Priority.ALWAYS);

        VBox activityListBox = new VBox(0);
        if (data.recentActivity.isEmpty()) {
            Label empty = new Label("No recent activity");
            empty.setStyle("-fx-text-fill:#64748b;-fx-font-size:12px;");
            empty.setPadding(new Insets(10, 0, 0, 0));
            activityListBox.getChildren().add(empty);
        } else {
            for (ActivityItem item : data.recentActivity) {
                activityListBox.getChildren().add(
                        activityItem(item.dotColor, item.text, item.time)
                );
            }
        }
        VBox recentActivityCard = buildRecentActivityCard(activityListBox);

        VBox rightCol = new VBox(18);
        rightCol.setPrefWidth(320);
        rightCol.setMinWidth(300);
        rightCol.getChildren().addAll(buildBookChargingCard(), recentActivityCard);

        HBox mainRow = new HBox(18);
        mainRow.getChildren().addAll(sessionHolder, rightCol);

        content.getChildren().addAll(summaryRow, mainRow);

        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.getStyleClass().add("scroll-pane");
        sp.setStyle("-fx-background-color: transparent;");

        return sp;
    }

    private HBox buildSummaryCards(
            Label upcomingTimeLbl,
            Label upcomingSubLbl,
            Label bookingsCountLbl,
            Label bookingsSubLbl,
            Label spentLbl,
            Label co2Lbl) {

        HBox row = new HBox(14);

        VBox card1 = new VBox(6);
        card1.getStyleClass().add("card");
        card1.setPadding(new Insets(18));
        HBox.setHgrow(card1, Priority.ALWAYS);

        Button trackBtn = new Button("Track bus  →");
        trackBtn.getStyleClass().add("track-bus-btn");
        trackBtn.setGraphic(busIcon());
        trackBtn.setContentDisplay(ContentDisplay.LEFT);
        trackBtn.setGraphicTextGap(10);
        trackBtn.setMaxWidth(Double.MAX_VALUE);
        trackBtn.setOnAction(e -> goTo("Live Tracking"));

        card1.getChildren().addAll(
                label("Upcoming booking", "card-title"),
                upcomingTimeLbl,
                upcomingSubLbl,
                trackBtn
        );

        VBox card2 = new VBox(6);
        card2.getStyleClass().add("card");
        card2.setPadding(new Insets(18));
        HBox.setHgrow(card2, Priority.ALWAYS);

        card2.getChildren().addAll(
                label("Bookings this month", "card-title"),
                bookingsCountLbl,
                bookingsSubLbl
        );

        VBox card3 = new VBox(6);
        card3.getStyleClass().add("card");
        card3.setPadding(new Insets(18));
        HBox.setHgrow(card3, Priority.ALWAYS);

        card3.getChildren().addAll(
                label("Spent this month", "card-title"),
                spentLbl,
                co2Lbl
        );

        row.getChildren().addAll(card1, card2, card3);
        return row;
    }

    private SVGPath busIcon() {
        SVGPath icon = new SVGPath();
        icon.setContent("M4 3h12c1.1 0 2 .9 2 2v10c0 1.1-.9 2-2 2v1a1 1 0 0 1-2 0v-1H6v1a1 1 0 0 1-2 0v-1c-1.1 0-2-.9-2-2V5c0-1.1.9-2 2-2zm0 3v5h12V6H4zm2 7a1.5 1.5 0 1 0 0 3 1.5 1.5 0 0 0 0-3zm8 0a1.5 1.5 0 1 0 0 3 1.5 1.5 0 0 0 0-3z");
        icon.setFill(Color.WHITE);
        icon.setScaleX(0.9);
        icon.setScaleY(0.9);
        return icon;
    }

    private VBox buildActiveSession(ChargingSession session, Booking booking) {

        VBox card = new VBox(14);
        card.getStyleClass().add("active-session-card");
        card.setPadding(new Insets(20));

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        Label statusBadge = new Label("CHARGING");
        statusBadge.getStyleClass().add("status-badge-green");
        header.getChildren().addAll(
                label("Active session", "section-title"),
                statusBadge
        );

        HBox contentRow = new HBox(24);
        contentRow.setAlignment(Pos.CENTER_LEFT);

        VBox statsCol = new VBox(14);
        HBox.setHgrow(statsCol, Priority.ALWAYS);

        boolean metered = session.hasLiveMeter();

        if (metered) {
            contentRow.getChildren().add(buildBatteryGauge(
                    session.getBatteryPct(),
                    (int) session.getCurrentKwh(),
                    (int) session.getTotalKwh()));

            int mins = session.estimatedMinutesLeft();
            statsCol.getChildren().addAll(
                    new HBox(24,
                            statItem("TIME LEFT", mins > 0 ? mins + " min" : "—"),
                            statItem("POWER", (int) session.getPowerKw() + " kW")),
                    new HBox(24,
                            statItem("RUNNING COST", "₹" + (int) session.getRunningCost()),
                            statItem("DELIVERED",
                                    (int) session.getCurrentKwh() + " / "
                                            + (int) session.getTotalKwh() + " kWh")));
        } else {
            statsCol.getChildren().addAll(
                    new HBox(24,
                            statItem("RUNNING FOR", session.elapsedLabel()),
                            statItem("REQUESTED", (int) session.getTotalKwh() + " kWh")),
                    new HBox(24,
                            statItem("DELIVERED", "not measured"),
                            statItem("RUNNING COST", "billed on completion")));
        }

        contentRow.getChildren().add(statsCol);

        HBox bottomRow = new HBox(12);
        bottomRow.setAlignment(Pos.CENTER_LEFT);

        Button reportBtn = new Button("Report Issue");
        reportBtn.getStyleClass().add("dashboard-active-session");
        reportBtn.setOnAction(e -> openReportIssue(booking));

        Region bsp = new Region();
        HBox.setHgrow(bsp, Priority.ALWAYS);

        bottomRow.getChildren().addAll(reportBtn, bsp);
        card.getChildren().addAll(header, contentRow, bottomRow);

        if (!metered) {
            Label note = new Label(
                    "Verified with your code at pickup. Live charging progress isn't "
                            + "metered yet — the driver closes the session when it's done.");
            note.setStyle("-fx-text-fill:#64748b;-fx-font-size:11px;");
            note.setWrapText(true);
            card.getChildren().add(note);
        }

        return card;
    }

    private VBox buildBookedSession(Booking booking) {
        VBox card = new VBox(14);
        card.getStyleClass().add("active-session-card");
        card.setPadding(new Insets(20));

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        Label statusBadge = new Label(booking.getStatus().replace('_', ' '));
        statusBadge.getStyleClass().add("status-badge-green");
        header.getChildren().addAll(label("Active session", "section-title"), statusBadge);

        Label bookingRef = new Label(booking.shortRef() + " - " + booking.getLocation());
        bookingRef.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:14px;-fx-font-weight:bold;");
        bookingRef.setWrapText(true);

        HBox stats = new HBox(24,
                statItem("SCHEDULED", booking.getScheduledTime()),
                statItem("REQUESTED", (int) booking.getKwh() + " kWh"));

        Label note = new Label("Your booking is active. You can report a problem to the support team at any time.");
        note.setStyle("-fx-text-fill:#64748b;-fx-font-size:11px;");
        note.setWrapText(true);

        card.getChildren().addAll(header, bookingRef, stats, buildReportIssueButton(booking), note);
        return card;
    }

    private Button buildReportIssueButton(Booking booking) {
        Button reportBtn = new Button("Report Issue");
        reportBtn.getStyleClass().add("dashboard-active-session");
        reportBtn.setOnAction(e -> openReportIssue(booking));
        return reportBtn;
    }

    private VBox buildSessionPlaceholder() {

        VBox card = new VBox(14);
        card.getStyleClass().add("active-session-card");
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.CENTER);

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        Label statusBadge = new Label("IDLE");
        statusBadge.getStyleClass().add("status-badge-grey");
        statusBadge.setStyle("-fx-background-color:#334155;-fx-text-fill:#94a3b8;" +
                "-fx-font-size:10px;-fx-font-weight:bold;" +
                "-fx-padding:3 8 3 8;-fx-background-radius:4;");
        header.getChildren().addAll(
                label("Active session", "section-title"),
                statusBadge
        );

        Label noSession = new Label("No active charging session");
        noSession.setStyle("-fx-text-fill:#64748b;-fx-font-size:14px;");

        Label hint = new Label("Book a session from 'Book Charging' to start charging.");
        hint.setStyle("-fx-text-fill:#475569;-fx-font-size:12px;");
        hint.setWrapText(true);

        card.getChildren().addAll(header, noSession, hint);
        return card;
    }

    private StackPane buildBatteryGauge(
            double pct,
            int current,
            int total) {

        StackPane gaugePane =
                new StackPane();

        gaugePane.setPrefSize(
                160,
                160
        );

        gaugePane.setMaxSize(
                160,
                160
        );

        double radius = 65;
        double strokeWidth = 12;

        Arc bgArc =
                new Arc(
                        0,
                        0,
                        radius,
                        radius,
                        90,
                        -360
                );

        bgArc.setType(
                ArcType.OPEN
        );

        bgArc.setFill(
                Color.TRANSPARENT
        );

        bgArc.setStroke(
                Color.web("#111827")
        );

        bgArc.setStrokeWidth(
                strokeWidth
        );

        bgArc.setStrokeLineCap(
                StrokeLineCap.ROUND
        );

        Arc fgArc =
                new Arc(
                        0,
                        0,
                        radius,
                        radius,
                        90,
                        -360 * pct
                );

        fgArc.setType(
                ArcType.OPEN
        );

        fgArc.setFill(
                Color.TRANSPARENT
        );

        fgArc.setStroke(
                Color.web("#10b981")
        );

        fgArc.setStrokeWidth(
                strokeWidth
        );

        fgArc.setStrokeLineCap(
                StrokeLineCap.ROUND
        );

        VBox centerText =
                new VBox(2);

        centerText.setAlignment(
                Pos.CENTER
        );

        Label pctLabel =
                new Label(
                        (int) (pct * 100) + "%"
                );

        pctLabel.setStyle(
                "-fx-text-fill:#10b981;" +
                "-fx-font-size:32px;" +
                "-fx-font-weight:bold;"
        );

        Label kwhLabel =
                new Label(
                        current + ".2 / " +
                        total +
                        " kWh"
                );

        kwhLabel.setStyle(
                "-fx-text-fill:#64748b;" +
                "-fx-font-size:10px;"
        );

        centerText.getChildren().addAll(
                pctLabel,
                kwhLabel
        );

        gaugePane.getChildren().addAll(
                bgArc,
                fgArc,
                centerText
        );

        return gaugePane;
    }

    private VBox statItem(
            String title,
            String value) {

        VBox item =
                new VBox(4);

        item.getStyleClass().add(
                "stat-item"
        );

        item.setPadding(
                new Insets(10)
        );

        Label t =
                new Label(title);

        t.setStyle(
                "-fx-text-fill:#64748b;" +
                "-fx-font-size:10px;" +
                "-fx-font-weight:bold;"
        );

        Label v =
                new Label(value);

        v.setStyle(
                "-fx-text-fill:#F8FAFC;" +
                "-fx-font-size:20px;" +
                "-fx-font-weight:bold;"
        );

        item.getChildren().addAll(
                t,
                v
        );

        return item;
    }

    private VBox buildBookChargingCard() {

        VBox card =
                new VBox(10);

        card.getStyleClass().add(
                "card"
        );

        card.setPadding(
                new Insets(18)
        );

        Label t =
                new Label("Book charging");

        t.setStyle(
                "-fx-text-fill:#F8FAFC;" +
                "-fx-font-size:16px;" +
                "-fx-font-weight:bold;"
        );

        Label sub =
                label(
                        "Three ways to get charged.",
                        "small-muted"
                );

        VBox option1 =
                buildChargingOption(
                        "⚡  Instant charge",
                        "Nearest available bus comes to you",
                        "~12 min away",
                        "#10b981",
                        true
                );

        option1.setOnMouseClicked(e ->
                BookCharging.openWith(BookCharging.MODE_INSTANT)
        );

        VBox option2 =
                buildChargingOption(
                        "ℹ  Reserve a route slot",
                        "Pre-scheduled modular bus route",
                        "4 slots today",
                        "#F8FAFC",
                        false
                );

        option2.setOnMouseClicked(e ->
                BookCharging.openWith(BookCharging.MODE_RESERVE)
        );

        VBox option3 =
                buildChargingOption(
                        "🚨  Emergency charge",
                        "Stranded or under 10% — pushed to front of queue",
                        "Priority",
                        "#EF4444",
                        false
                );

        option3.setOnMouseClicked(e ->
                BookCharging.openWith(BookCharging.MODE_EMERGENCY)
        );

        card.getChildren().addAll(
                t,
                sub,
                option1,
                option2,
                option3
        );

        return card;
    }

    private VBox buildChargingOption(
            String title,
            String desc,
            String badge,
            String color,
            boolean highlight) {

        VBox option =
                new VBox(4);

        option.setPadding(
                new Insets(14)
        );

        option.getStyleClass().add(
                highlight
                        ? "charge-option-highlight"
                        : "charge-option"
        );

        HBox row =
                new HBox();

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        Label titleLbl =
                new Label(title);

        titleLbl.setStyle(
                "-fx-text-fill:" + color + ";" +
                "-fx-font-size:13px;" +
                "-fx-font-weight:bold;"
        );

        Region sp =
                new Region();

        HBox.setHgrow(
                sp,
                Priority.ALWAYS
        );

        Label badgeLbl =
                new Label(badge);

        badgeLbl.setStyle(
                "-fx-text-fill:" + color + ";" +
                "-fx-font-size:11px;"
        );

        row.getChildren().addAll(
                titleLbl,
                sp,
                badgeLbl
        );

        Label descLbl =
                new Label(desc);

        descLbl.setStyle(
                "-fx-text-fill:#94a3b8;" +
                "-fx-font-size:11px;"
        );

        option.getChildren().addAll(
                row,
                descLbl
        );

        return option;
    }

    private VBox buildRecentActivityCard(VBox activityListBox) {

        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(18));

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        Label t = new Label("Recent activity");
        t.setStyle("-fx-text-fill:#F8FAFC;-fx-font-size:16px;-fx-font-weight:bold;");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Label viewAll = new Label("View all");
        viewAll.setStyle("-fx-text-fill:#10b981;-fx-font-size:12px;-fx-cursor:hand;");
        viewAll.setOnMouseClicked(e -> goTo("History & Impact"));

        header.getChildren().addAll(t, sp, viewAll);

        card.getChildren().addAll(header, activityListBox);
        return card;
    }

    private HBox activityItem(
            String dotColor,
            String text,
            String time) {

        HBox item =
                new HBox(10);

        item.setAlignment(
                Pos.TOP_LEFT
        );

        item.setPadding(
                new Insets(6, 0, 6, 0)
        );

        Circle dot =
                new Circle(
                        5,
                        Color.web(dotColor)
                );

        dot.setTranslateY(5);

        VBox textBox =
                new VBox(2);

        HBox.setHgrow(
                textBox,
                Priority.ALWAYS
        );

        Label textLbl =
                new Label(text);

        textLbl.setStyle(
                "-fx-text-fill:#F8FAFC;" +
                "-fx-font-size:12px;" +
                "-fx-font-weight:bold;"
        );

        textLbl.setWrapText(true);

        Label timeLbl =
                new Label(time);

        timeLbl.setStyle(
                "-fx-text-fill:#64748b;" +
                "-fx-font-size:10px;"
        );

        textBox.getChildren().addAll(
                textLbl,
                timeLbl
        );

        item.getChildren().addAll(
                dot,
                textBox
        );

        return item;
    }

    private Label bigLabel(String text) {

        Label l =
                new Label(text);

        l.setStyle(
                "-fx-text-fill:#f8fafc;" +
                "-fx-font-size:28px;" +
                "-fx-font-weight:bold;"
        );

        return l;
    }
}
