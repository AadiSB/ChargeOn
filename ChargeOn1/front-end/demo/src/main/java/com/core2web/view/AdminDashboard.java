
package com.core2web.view;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import com.core2web.controller.AdminDashboardController;
import com.core2web.controller.AdminDashboardController.DashboardData;
import com.core2web.controller.NotificationController;
import com.core2web.controller.BusController;
import com.core2web.model.Bus;
import com.core2web.model.Notification;

import com.gluonhq.maps.MapView;

import static com.core2web.view.AdminLayout.*;

public class AdminDashboard {

    static Stage window;
    static Scene scene;
    static ListView<String> sidebar;
    static Label heading;
    static Label subheading;
    static StackPane middleBox;

    /*
     * Fleet status refresh interval.
     *
     * 60 seconds is deliberately used instead of a very short interval
     * so that the dashboard does not continuously consume Firestore reads.
     *
     * The refresh runs ONLY while the Dashboard page is active.
     */
    private static final long FLEET_REFRESH_SECONDS = 60;

    /*
     * Background scheduler used only for refreshing fleet status.
     *
     * It is a daemon thread, so it will not prevent the application
     * from closing.
     */
    private static ScheduledExecutorService fleetRefreshExecutor;

    /*
     * These references point to the EXISTING dashboard controls.
     * We update them instead of rebuilding the dashboard.
     */
    private static Label enRouteCount;
    private static Label chargingCount;
    private static Label idleCount;
    private static Label faultCount;

    private static ProgressBar enRoutePb;
    private static ProgressBar chargingPb;
    private static ProgressBar idlePb;
    private static ProgressBar faultPb;

    /*
     * Prevents an old background refresh from updating the UI after
     * the user has navigated away from Dashboard.
     */
    private static volatile boolean dashboardPageActive = false;


    public static void show(Stage stage) {

        window = stage;

        sidebar = new ListView<>();

        sidebar.getItems().addAll(
                "Dashboard",
                "Live Monitoring",
                "Fleet Management",
                "Revenue",
                "Support",
                "Users & Drivers",
                "AI Assistant"
        );

        sidebar.getStyleClass().add("nav-list");
        sidebar.setFocusTraversable(false);

        sidebar.getSelectionModel().selectedItemProperty()
                .addListener((item, oldPage, newPage) -> showPage(newPage));

        heading = new Label();
        heading.getStyleClass().add("heading");

        subheading = new Label();
        subheading.getStyleClass().add("small-muted");

        middleBox = new StackPane();

        VBox center = new VBox();

        center.getChildren().add(
                buildTopBar(heading, subheading, 5)
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
                AdminDashboard.class
                        .getResource(
                                "/styles/admin.css"
                        )
                        .toExternalForm()
        );

        scene.getStylesheets().add(
                AdminDashboard.class
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


    private static void setMiddleContent(javafx.scene.Node content) {

        if (middleBox == null) {
            return;
        }

        boolean wasMaximized =
                window != null && window.isMaximized();

        middleBox.getChildren().setAll(content);

        if (window != null && wasMaximized) {
            window.setMaximized(true);
        }
    }


    public static void goTo(String page) {

        if (sidebar == null) {
            return;
        }

        if (page.equals(
                sidebar.getSelectionModel().getSelectedItem()
        )) {

            showPage(page);

        } else {

            sidebar.getSelectionModel().select(page);
        }
    }


    public static void openNotifications() {

        stopFleetStatusRefresh();

        if (middleBox == null) {
            return;
        }

        heading.setText(
                "Notifications"
        );

        subheading.setText(
                "Recent alerts and important updates"
        );

        setMiddleContent(
                AdminNotifications.buildMainContent()
        );

        window.setTitle(
                "ChargeOn · Notifications"
        );
    }


    public static void openNewRoute() {

        stopFleetStatusRefresh();

        heading.setText(
                "New route"
        );

        subheading.setText(
                "Create a new fleet route and schedule"
        );

        setMiddleContent(
                NewRoute.buildMainContent()
        );

        window.setTitle(
                "ChargeOn · New Route"
        );
    }


    public static void openProfile() {

        stopFleetStatusRefresh();

        heading.setText(
                "Profile"
        );

        subheading.setText(
                "Manage your administrator profile and contact information"
        );

        setMiddleContent(
                AdminProfile.buildMainContent()
        );

        window.setTitle(
                "ChargeOn · Profile"
        );
    }


    public static void openSubscriptionPlanManagement() {

        stopFleetStatusRefresh();

        if (middleBox == null) {
            return;
        }

        heading.setText(
                "Subscription Plan Management"
        );

        subheading.setText(
                "Revenue > Subscription Plan Management"
        );

        setMiddleContent(
                SubscriptionPlanManagement.buildMainContent()
        );

        window.setTitle(
                "ChargeOn · Subscription Plan Management"
        );
    }


    private static void showPage(String page) {

        if (page == null) {
            return;
        }

        /*
         * Only the Dashboard page should continuously refresh
         * fleet status.
         */
        if ("Dashboard".equals(page)) {

            dashboardPageActive = true;

        } else {

            stopFleetStatusRefresh();
        }


        switch (page) {

            case "Dashboard":

                heading.setText(
                        "Admin dashboard"
                );

                subheading.setText(
                        "Fleet-wide snapshot across all cities"
                );

                setMiddleContent(
                        buildMainContent()
                );

                /*
                 * Start the low-frequency fleet refresh AFTER the
                 * dashboard controls have been created.
                 */
                startFleetStatusRefresh();

                break;


            case "Live Monitoring":

                heading.setText(
                        "Live monitoring"
                );

                subheading.setText(
                        "Map-based tracking of every bus, across every city"
                );

                setMiddleContent(
                        LiveMonitoring.buildMainContent()
                );

                break;


            case "Fleet Management":

                heading.setText(
                        "Fleet management"
                );

                subheading.setText(
                        "Routes, schedules and charging inventory"
                );

                setMiddleContent(
                        FleetManagement.buildMainContent()
                );

                break;


            case "Revenue":

                heading.setText(
                        "Revenue"
                );

                subheading.setText(
                        "Charging revenue and subscription reporting"
                );

                setMiddleContent(
                        Revenue.buildMainContent()
                );

                break;


            case "Support":

                heading.setText(
                        "Support console"
                );

                subheading.setText(
                        "Customer and driver issue resolution"
                );

                setMiddleContent(
                        Support.buildMainContent()
                );

                break;


            case "Users & Drivers":

                heading.setText(
                        "Users & drivers"
                );

                subheading.setText(
                        "Driver roster and registered EV owner accounts"
                );

                setMiddleContent(
                        UsersAndDrivers.buildMainContent()
                );

                break;


            case "AI Assistant":

                heading.setText(
                        "AI Assistant"
                );

                subheading.setText(
                        "Your intelligent EV charging assistant"
                );

                setMiddleContent(
                        CopilotChatUIAdmin.buildMainContent()
                );

                break;

            case "About Us":
                setMiddleContent(
                 AboutUs.buildMainContent()
    );
                break;


            default:
                return;
        }

        window.setTitle(
                "ChargeOn · " + page
        );
    }


    private static final AdminDashboardController dashboardController =
            new AdminDashboardController();

    private static final NotificationController notificationController =
            new NotificationController();

    private static final BusController busController =
            new BusController();

    /** Keeps the dashboard feed a summary; the full list lives in Notifications. */
    private static final int ACTIVITY_FEED_LIMIT = 6;


    static ScrollPane buildMainContent() {

        /*
         * Reset old references before building a new dashboard.
         */
        enRouteCount = null;
        chargingCount = null;
        idleCount = null;
        faultCount = null;

        enRoutePb = null;
        chargingPb = null;
        idlePb = null;
        faultPb = null;

        DashboardData data = dashboardController.load();

        VBox content =
                new VBox(16);

        content.setPadding(
                new Insets(16)
        );

        VBox bannerHolder = new VBox();

        if (!data.lowBatteryBuses.isEmpty()) {

            bannerHolder.getChildren().setAll(
                    alertBanner(
                            "!",
                            data.lowBatteryBuses.size() + " bus"
                                    + (data.lowBatteryBuses.size() == 1 ? "" : "es")
                                    + " flagged low battery on active routes",
                            "Auto-assignment is routing new bookings away from "
                                    + lowBatteryBusCodes(data.lowBatteryBuses)
                                    + " until they recharge."
                    )
            );
        }


        Label busesOnlineVal =
                kpiValueLabel(
                        data.busesOnlineLabel(),
                        "#10b981"
                );

        Label busesOnlineSub =
                kpiSubLabel(
                        data.fleetSubLabel()
                );


        Label bookingsVal =
                kpiValueLabel(
                        String.valueOf(data.bookingsToday),
                        "#f8fafc"
                );

        Label bookingsSub =
                kpiSubLabel(
                        data.bookingsBreakdown()
                );


        Label revenueVal =
                kpiValueLabel(
                        data.revenueTodayFormatted(),
                        "#10b981"
                );

        Label revenueSub =
                kpiSubLabel(
                        "Today's charging revenue"
                );


        Label ticketsVal =
                kpiValueLabel(
                        String.valueOf(data.openTicketCount),
                        "#f59e0b"
                );

        Label ticketsSub =
                kpiSubLabel(
                        data.ticketsSubLabel()
                );


        HBox kpiRow = buildKpiRow(
                busesOnlineVal, busesOnlineSub,
                bookingsVal, bookingsSub,
                revenueVal, revenueSub,
                ticketsVal, ticketsSub
        );


        int total =
                Math.max(
                        data.fleetStatus.total,
                        1
                );


        /*
         * Store these existing controls in static references so
         * the refresh mechanism can update them later.
         */

        enRouteCount =
                fleetCountLabel(
                        String.valueOf(
                                data.fleetStatus.enRoute
                        ),
                        "#3b82f6"
                );

        enRoutePb =
                fleetProgressBar(
                        "#3b82f6"
                );

        enRoutePb.setProgress(
                data.fleetStatus.enRoute /
                        (double) total
        );


        chargingCount =
                fleetCountLabel(
                        String.valueOf(
                                data.fleetStatus.charging
                        ),
                        "#f59e0b"
                );

        chargingPb =
                fleetProgressBar(
                        "#f59e0b"
                );

        chargingPb.setProgress(
                data.fleetStatus.charging /
                        (double) total
        );


        idleCount =
                fleetCountLabel(
                        String.valueOf(
                                data.fleetStatus.idle
                        ),
                        "#10b981"
                );

        idlePb =
                fleetProgressBar(
                        "#10b981"
                );

        idlePb.setProgress(
                data.fleetStatus.idle /
                        (double) total
        );


        faultCount =
                fleetCountLabel(
                        String.valueOf(
                                data.fleetStatus.fault
                        ),
                        "#ef4444"
                );

        faultPb =
                fleetProgressBar(
                        "#ef4444"
                );

        faultPb.setProgress(
                data.fleetStatus.fault /
                        (double) total
        );


        HBox middleRow = buildMiddleRow(
                enRouteCount, enRoutePb,
                chargingCount, chargingPb,
                idleCount, idlePb,
                faultCount, faultPb
        );


        content.getChildren().addAll(
                bannerHolder,

                kpiRow,

                middleRow,

                buildActivityFeed()
        );


        ScrollPane sp =
                new ScrollPane(content);

        sp.setFitToWidth(
                true
        );

        sp.getStyleClass().add(
                "scroll-pane"
        );

        return sp;
    }


    /*
     * ============================================================
     * AUTOMATIC FLEET STATUS REFRESH
     * ============================================================
     */


    private static synchronized void startFleetStatusRefresh() {

        /*
         * If a refresh executor already exists, do not create
         * another one.
         */
        if (fleetRefreshExecutor != null
                && !fleetRefreshExecutor.isShutdown()
                && !fleetRefreshExecutor.isTerminated()) {

            return;
        }


        dashboardPageActive = true;


        /*
         * Daemon thread:
         * it will not keep the application alive after the window
         * is closed.
         */
        fleetRefreshExecutor =
                Executors.newSingleThreadScheduledExecutor(
                        runnable -> {

                            Thread thread =
                                    new Thread(
                                            runnable,
                                            "ChargeOn-Fleet-Refresh"
                                    );

                            thread.setDaemon(true);

                            return thread;
                        }
                );


        /*
         * Run the first refresh immediately.
         *
         * The dashboard has already performed its initial load,
         * so this simply confirms the latest Firebase state.
         */
        refreshFleetStatus();


        /*
         * Then refresh once every 60 seconds.
         */
        fleetRefreshExecutor.scheduleAtFixedRate(
                AdminDashboard::refreshFleetStatus,
                FLEET_REFRESH_SECONDS,
                FLEET_REFRESH_SECONDS,
                TimeUnit.SECONDS
        );
    }


    private static synchronized void stopFleetStatusRefresh() {

        dashboardPageActive = false;


        if (fleetRefreshExecutor != null) {

            fleetRefreshExecutor.shutdownNow();

            fleetRefreshExecutor = null;
        }
    }


    private static void refreshFleetStatus() {

        /*
         * Do not perform any Firebase read if the user has already
         * left the Dashboard page.
         */
        if (!dashboardPageActive) {
            return;
        }


        try {

            /*
             * This performs ONLY the fleet-status read.
             *
             * We deliberately do NOT call:
             *
             * dashboardController.load()
             *
             * because that would also read bookings, revenue,
             * tickets and low-battery information.
             */
            BusController.FleetStatusCounts status =
                    busController.getFleetStatusCounts();


            /*
             * JavaFX controls MUST be updated on the JavaFX
             * Application Thread.
             */
            Platform.runLater(() -> {

                /*
                 * The user may have navigated away while Firebase
                 * was being read.
                 */
                if (!dashboardPageActive) {
                    return;
                }


                /*
                 * The dashboard may have been rebuilt while the
                 * background request was running.
                 */
                if (enRouteCount == null
                        || chargingCount == null
                        || idleCount == null
                        || faultCount == null
                        || enRoutePb == null
                        || chargingPb == null
                        || idlePb == null
                        || faultPb == null) {

                    return;
                }


                updateFleetStatusUI(status);
            });


        } catch (Exception e) {

            /*
             * Do not crash the dashboard if one refresh fails.
             *
             * The existing displayed values remain visible and
             * the next scheduled refresh will try again.
             */
            System.err.println(
                    "Fleet status refresh failed: "
                            + e.getMessage()
            );
        }
    }


    private static void updateFleetStatusUI(
            BusController.FleetStatusCounts status) {

        int total =
                Math.max(
                        status.total,
                        1
                );


        /*
         * Update counts.
         */
        enRouteCount.setText(
                String.valueOf(
                        status.enRoute
                )
        );

        chargingCount.setText(
                String.valueOf(
                        status.charging
                )
        );

        idleCount.setText(
                String.valueOf(
                        status.idle
                )
        );

        faultCount.setText(
                String.valueOf(
                        status.fault
                )
        );


        /*
         * Update progress bars.
         */
        enRoutePb.setProgress(
                status.enRoute /
                        (double) total
        );

        chargingPb.setProgress(
                status.charging /
                        (double) total
        );

        idlePb.setProgress(
                status.idle /
                        (double) total
        );

        faultPb.setProgress(
                status.fault /
                        (double) total
        );
    }


    private static String lowBatteryBusCodes(List<Bus> buses) {

        StringBuilder sb =
                new StringBuilder();

        for (Bus bus : buses) {

            if (sb.length() > 0) {
                sb.append(", ");
            }

            sb.append(
                    bus.getBusCode()
            );
        }

        return sb.toString();
    }


    private static Label kpiValueLabel(
            String initialText,
            String color) {

        Label l =
                new Label(
                        initialText
                );

        l.setStyle(
                "-fx-text-fill:" + color
                        + ";-fx-font-size:26px;"
                        + "-fx-font-weight:bold;"
        );

        return l;
    }


    private static Label kpiSubLabel(
            String initialText) {

        Label l =
                new Label(
                        initialText
                );

        l.getStyleClass().add(
                "card-sub"
        );

        return l;
    }


    private static HBox buildKpiRow(
            Label busesOnlineVal,
            Label busesOnlineSub,
            Label bookingsVal,
            Label bookingsSub,
            Label revenueVal,
            Label revenueSub,
            Label ticketsVal,
            Label ticketsSub) {

        HBox row =
                new HBox(16);

        row.getChildren().addAll(
                mutableStatCard(
                        "Buses online",
                        busesOnlineVal,
                        busesOnlineSub
                ),

                mutableStatCard(
                        "Bookings today",
                        bookingsVal,
                        bookingsSub
                ),

                mutableStatCard(
                        "Revenue today",
                        revenueVal,
                        revenueSub
                ),

                mutableStatCard(
                        "Open tickets",
                        ticketsVal,
                        ticketsSub
                )
        );

        return row;
    }


    private static VBox mutableStatCard(
            String title,
            Label valueLabel,
            Label subLabel) {

        VBox c =
                card(title);

        c.getChildren().addAll(
                valueLabel,
                subLabel
        );

        HBox.setHgrow(
                c,
                Priority.ALWAYS
        );

        return c;
    }


    private static HBox buildMiddleRow(
            Label enRouteCount,
            ProgressBar enRoutePb,
            Label chargingCount,
            ProgressBar chargingPb,
            Label idleCount,
            ProgressBar idlePb,
            Label faultCount,
            ProgressBar faultPb) {

        HBox row =
                new HBox(16);


        VBox fleetSplit =
                card(
                        "Fleet status split"
                );


        fleetSplit.getChildren().addAll(

                fleetRow(
                        "En route",
                        "#3b82f6",
                        enRouteCount,
                        enRoutePb
                ),

                fleetRow(
                        "Charging",
                        "#f59e0b",
                        chargingCount,
                        chargingPb
                ),

                fleetRow(
                        "Idle / available",
                        "#10b981",
                        idleCount,
                        idlePb
                ),

                fleetRow(
                        "Fault / attention",
                        "#ef4444",
                        faultCount,
                        faultPb
                )
        );


        HBox.setHgrow(
                fleetSplit,
                Priority.ALWAYS
        );


        VBox mapPreview =
                card(
                        "Live fleet map"
                );

        mapPreview.getStyleClass().add(
                "card-highlight"
        );


        /*
         * ============================================================
         * EXISTING MAP BOX
         * ============================================================
         *
         * The size of this box is intentionally NOT changed.
         *
         * Existing height:
         *     180px
         *
         * Existing map card width:
         *     480px
         *
         * Only the old placeholder text is replaced with the
         * interactive Gluon MapView.
         */
        VBox mapBox =
                new VBox();

        mapBox.getStyleClass().add(
                "map-placeholder"
        );

        mapBox.setAlignment(
                Pos.CENTER
        );

        mapBox.setPrefHeight(
                180
        );

        mapBox.setMaxWidth(
                Double.MAX_VALUE
        );


        /*
         * ============================================================
         * GLUON MAP
         * ============================================================
         *
         * Initial position is Maharashtra.
         *
         * The map is NOT recentered after this.
         *
         * Therefore:
         *
         *  - mouse scroll can zoom
         *  - mouse dragging can move/pan
         *  - zooming out does NOT return to Maharashtra
         *  - moving the map does NOT get overridden by the
         *    Firebase fleet refresh
         *
         * This is deliberately only a map preview. No buttons,
         * markers, routes or additional controls are added here.
         */
        MapView mapView =
                new MapView();


        /*
         * Keep the exact existing map height.
         */
        mapView.setPrefHeight(
                180
        );

        mapView.setMinHeight(
                180
        );

        mapView.setMaxHeight(
                180
        );


        /*
         * Allow the map to occupy the full width of the
         * existing rectangular map box.
         */
        mapView.setMaxWidth(
                Double.MAX_VALUE
        );


        /*
         * Maharashtra-centered initial view.
         *
         * Approximate center:
         *
         * Latitude  : 19.75
         * Longitude : 75.70
         *
         * Zoom 6 gives a useful Maharashtra-level view while
         * still showing surrounding areas.
         */
        mapView.setCenter(
                19.75,
                75.70
        );

        mapView.setZoom(
                6
        );


        /*
         * Let the map fill the existing map box vertically.
         */
        VBox.setVgrow(
                mapView,
                Priority.ALWAYS
        );


        /*
         * Add ONLY the map to the existing map box.
         */
        mapBox.getChildren().add(
                mapView
        );


        /*
         * Keep the existing text below the map.
         *
         * No buttons or map controls are added.
         */
        mapPreview.getChildren().addAll(

                mapBox,

                label(
                        "Open \"Live Monitoring\" in the sidebar for the full interactive map.",
                        "card-sub"
                )
        );


        /*
         * Existing map card width is preserved.
         */
        mapPreview.setPrefWidth(
                480
        );


        HBox.setHgrow(
                mapPreview,
                Priority.ALWAYS
        );


        row.getChildren().addAll(
                fleetSplit,
                mapPreview
        );


        return row;
    }


    private static VBox fleetRow(
            String labelText,
            String color,
            Label countLabel,
            ProgressBar pb) {

        VBox box =
                new VBox(4);

        HBox top =
                new HBox(8);

        top.setAlignment(
                Pos.CENTER_LEFT
        );


        Region sp =
                new Region();

        HBox.setHgrow(
                sp,
                Priority.ALWAYS
        );


        top.getChildren().addAll(

                new Circle(
                        5,
                        Color.web(color)
                ),

                label(
                        labelText,
                        "card-sub"
                ),

                sp,

                countLabel
        );


        box.getChildren().addAll(
                top,
                pb
        );


        return box;
    }


    private static Label fleetCountLabel(
            String initialText,
            String color) {

        return labelBold(
                initialText,
                color
        );
    }


    private static ProgressBar fleetProgressBar(
            String color) {

        ProgressBar pb =
                new ProgressBar(0);

        pb.setStyle(
                "-fx-accent: " + color + ";"
        );

        pb.getStyleClass().add(
                "utilisation-bar"
        );

        pb.setMaxWidth(
                Double.MAX_VALUE
        );

        return pb;
    }


    private static Label labelBold(
            String text,
            String color) {

        Label l =
                new Label(text);

        l.setStyle(
                "-fx-text-fill:" + color
                        + ";-fx-font-size:13px;"
                        + "-fx-font-weight:bold;"
        );

        return l;
    }


    private static VBox buildActivityFeed() {

        VBox section =
                new VBox(14);

        section.getStyleClass().add(
                "session-log-card"
        );

        section.setPadding(
                new Insets(18)
        );


        section.getChildren().add(
                label(
                        "Live activity",
                        "section-title"
                )
        );


        /*
         * The admin broadcast feed is the real activity stream:
         * driver links, emergency dispatches/rejections and
         * critical alerts all notify admins.
         */
        List<Notification> feed =
                notificationController.getAdminFeed();


        if (feed.isEmpty()) {

            Label empty =
                    new Label(
                            "No activity recorded yet."
                    );

            empty.getStyleClass().add(
                    "booking-detail-muted"
            );

            section.getChildren().add(
                    empty
            );

            return section;
        }


        int shown =
                Math.min(
                        feed.size(),
                        ACTIVITY_FEED_LIMIT
                );


        for (int i = 0; i < shown; i++) {

            Notification n =
                    feed.get(i);

            section.getChildren().add(
                    logEntry(
                            n.displayColor(),
                            n.displayTitle()
                                    + " · "
                                    + n.getMessage(),
                            relativeTime(
                                    n.getCreatedAt()
                            )
                    )
            );
        }


        return section;
    }


    /**
     * "Just now" / "6 min ago" / "3 h ago" / "2 d ago",
     * or the raw value if unparseable.
     */
    private static String relativeTime(
            String isoInstant) {

        if (isoInstant == null
                || isoInstant.isEmpty()) {

            return "";
        }


        try {

            long seconds =
                    java.time.Duration.between(
                            java.time.Instant.parse(
                                    isoInstant
                            ),
                            java.time.Instant.now()
                    ).getSeconds();


            if (seconds < 60) {
                return "Just now";
            }

            if (seconds < 3600) {
                return (seconds / 60)
                        + " min ago";
            }

            if (seconds < 86400) {
                return (seconds / 3600)
                        + " h ago";
            }


            return (seconds / 86400)
                    + " d ago";


        } catch (Exception e) {

            return isoInstant;
        }
    }


    private static HBox logEntry(
            String dotColor,
            String text,
            String time) {

        HBox entry =
                new HBox(10);

        entry.setAlignment(
                Pos.TOP_LEFT
        );


        Circle dot =
                new Circle(
                        5,
                        Color.web(dotColor)
                );

        dot.setTranslateY(
                5
        );


        VBox textBox =
                new VBox(2);

        HBox.setHgrow(
                textBox,
                Priority.ALWAYS
        );


        Label textLabel =
                new Label(text);

        textLabel.setStyle(
                "-fx-text-fill:#cbd5e1;"
                        + "-fx-font-size:12px;"
        );

        textLabel.setWrapText(
                true
        );


        textBox.getChildren().addAll(

                textLabel,

                label(
                        time,
                        "booking-detail-muted"
                )
        );


        entry.getChildren().addAll(
                dot,
                textBox
        );


        return entry;
    }
}