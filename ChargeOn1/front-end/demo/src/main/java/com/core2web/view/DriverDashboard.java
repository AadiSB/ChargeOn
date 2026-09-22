package com.core2web.view;

import java.util.List;
import java.time.Instant;

import com.core2web.util.DateTimeUtil;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import com.core2web.controller.BookingController;
import com.core2web.controller.BusController;

public class DriverDashboard {

    static Stage window;
    static Scene scene;
    static ListView<String> sidebar;
    static Label heading;
    static Label subheading;
    static StackPane middleBox;

    private static final BookingController bookingController = new BookingController();
    private static final BusController busController = new BusController();

    public static void show(Stage stage) {
        window = stage;

        sidebar = new ListView<>();
        sidebar.getItems().addAll("Dashboard", "My Bookings", "Live Navigation", "Bus Status",
                "Alerts & Issues", "Shift & Earnings", "AI Assistant");
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
        center.getChildren().add(DriverLayout.buildTopBar(heading, subheading));
        center.getChildren().add(middleBox);
        VBox.setVgrow(middleBox, Priority.ALWAYS);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0f1720;");
        root.setLeft(DriverLayout.buildSidebar(sidebar, stage));
        root.setCenter(center);

        double width = AppWindowSize.width();
        double height = AppWindowSize.height();

        scene = new Scene(root, width, height);

        goTo("Dashboard");

        window.setMaximized(false);
        window.setScene(scene);
        window.show();
    }

    public static void goTo(String page) {
        if (sidebar == null) {
            return;
        }

        if (page.equals(sidebar.getSelectionModel().getSelectedItem())) {
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
        subheading.setText("Recent alerts and important updates for your shift");
        useOnlyThisCss("/styles/driverdashboard.css");

        middleBox.getChildren().setAll(
                DriverNotifications.buildMainContent()
        );

        window.setTitle("ChargeOn · Notifications");
    }

    public static void openProfile() {

        if (middleBox == null) {
            return;
        }

        heading.setText("Profile");
        subheading.setText("Manage your driver profile and contact information");
        useOnlyThisCss("/styles/driverdashboard.css");

        middleBox.getChildren().setAll(
                DriverProfile.buildMainContent()
        );

        window.setTitle("ChargeOn · Profile");
    }

    private static void showPage(String page) {
        if (page == null) {
            return;
        }

        LiveNavigation.disposeCurrent();

        switch (page) {
            case "Dashboard":
                heading.setText("Today's run");
                subheading.setText("Current assignment and booking activity");
                useOnlyThisCss("/styles/driverdashboard.css");
                middleBox.getChildren().setAll(buildMainContent());
                break;

            case "My Bookings":
                heading.setText("My bookings");
                subheading.setText("Assigned, queued and completed charging bookings");
                useOnlyThisCss("/styles/booking.css");
                middleBox.getChildren().setAll(Booking.buildMainContent());
                break;

            case "Live Navigation":
                heading.setText("Live navigation");
                subheading.setText("Turn-by-turn guidance to your next stop");
                useOnlyThisCss("/styles/navigation.css");
                middleBox.getChildren().setAll(LiveNavigation.buildMainContent());
                break;

            case "Bus Status":
                heading.setText("Bus status");
                subheading.setText("Battery reserve, ports and vehicle telemetry");
                useOnlyThisCss("/styles/busstatus.css");
                middleBox.getChildren().setAll(BusStatus.buildMainContent());
                break;

            case "Alerts & Issues":
                heading.setText("Alerts & issues");
                subheading.setText("Report malfunctions and read operations notices");
                useOnlyThisCss("/styles/alertsissues.css");
                middleBox.getChildren().setAll(Alerts_Issues.buildMainContent());
                break;

            case "Shift & Earnings":
                heading.setText("Shift & earnings");
                subheading.setText("Schedule, clock and daily summary");
                useOnlyThisCss("/styles/shiftearning.css");
                middleBox.getChildren().setAll(Shift_Earning.buildMainContent());
                break;

            case "AI Assistant":
                heading.setText("AI Assistant");
                subheading.setText("Your intelligent EV charging assistant");
                useOnlyThisCss("/styles/driverdashboard.css");
                middleBox.getChildren().setAll(CopilotChatUIDriver.buildMainContent());
                break;

            default:
                return;
        }

        window.setTitle("ChargeOn · " + page);
    }

    private static void useOnlyThisCss(String file) {
        scene.getStylesheets().clear();
        scene.getStylesheets().add(DriverDashboard.class.getResource(file).toExternalForm());
        scene.getStylesheets().add(DriverDashboard.class.getResource("/styles/sidebar.css").toExternalForm());
    }

    static HBox buildMainContent() {
        return new DriverDashboard().buildContent();
    }

    private HBox buildContent() {
        HBox main = new HBox(16);
        main.setPadding(new Insets(16));

        com.core2web.model.Bus bus = busController.getBusForCurrentDriver();
        List<com.core2web.model.Booking> bookings = bookingController.getBookingsForCurrentDriver();
        if (bus != null) {
            subheading.setText((bus.getBusCode() == null ? "Assigned bus" : bus.getBusCode())
                    + " · " + (bus.getDepot() == null || bus.getDepot().isBlank() ? "Depot unavailable" : bus.getDepot()));
        }

        VBox busCard = card("Assigned bus");
        populateBusCard(busCard, bus);
        HBox.setHgrow(busCard, Priority.ALWAYS);

        VBox batteryCard = card("Battery reserve");
        populateBatteryCard(batteryCard, bus);
        HBox.setHgrow(batteryCard, Priority.ALWAYS);

        VBox portsCard = buildPortsCard();
        HBox.setHgrow(portsCard, Priority.ALWAYS);
        VBox todayCard = buildTodayCard(bookings);
        HBox.setHgrow(todayCard, Priority.ALWAYS);

        HBox infoRow = new HBox(12, busCard, batteryCard, portsCard, todayCard);

        VBox activeBookingHolder = new VBox();
        populateActiveBooking(activeBookingHolder, bookings);

        VBox upNextRows = new VBox(10);
        populateUpNext(upNextRows, bookings);

        VBox center = new VBox(14);
        HBox.setHgrow(center, Priority.ALWAYS);

        HBox emergencyBanner = buildEmergencyBanner();
        if (emergencyBanner != null) {
            center.getChildren().add(emergencyBanner);
        }

        center.getChildren().addAll(infoRow, activeBookingHolder);

        VBox rightCol = new VBox(16);
        rightCol.setPrefWidth(280);
        rightCol.getChildren().add(buildUpNextSection(upNextRows));

        main.getChildren().addAll(center, rightCol);

        return main;
    }

    private void populateBusCard(VBox busCard, com.core2web.model.Bus bus) {
        busCard.getChildren().remove(1, busCard.getChildren().size());
        if (bus == null) {
            busCard.getChildren().add(label("No bus assigned", "card-sub"));
            return;
        }
        Label busNum = new Label(bus.getBusCode());
        busNum.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:22px;-fx-font-weight:bold;");
        busCard.getChildren().addAll(busNum, label(bus.getDepot(), "card-sub"), gap(8));
    }

    private void populateBatteryCard(VBox batteryCard, com.core2web.model.Bus bus) {
        batteryCard.getChildren().remove(1, batteryCard.getChildren().size());
        if (bus == null) {
            batteryCard.getChildren().add(label("No bus assigned", "card-sub"));
            return;
        }
        HBox battRow = new HBox(6);
        battRow.setAlignment(Pos.BASELINE_LEFT);
        Label pct = new Label(((int) bus.getBatteryLevel()) + "%");
        pct.setStyle("-fx-text-fill:#10b981;-fx-font-size:24px;-fx-font-weight:bold;");
        battRow.getChildren().addAll(pct, label(busController.getDisplayStatus(bus), "card-sub"));
        ProgressBar pb = new ProgressBar(bus.getBatteryLevel() / 100.0);
        pb.getStyleClass().add("battery-bar");
        pb.setMaxWidth(Double.MAX_VALUE);
        batteryCard.getChildren().addAll(battRow, pb, gap(8));
    }

    private HBox buildEmergencyBanner() {
        com.core2web.model.Booking pending =
                bookingController.getPendingEmergencyBookingForCurrentDriver();

        if (pending == null) {
            return null;
        }

        return EmergencyBanner.build(pending, () -> goTo("Dashboard"));
    }
         private VBox buildPortsCard() {
                  VBox ports = card("Ports");
                    ports.getStyleClass().add("card-highlight");

                     HBox portsRow = new HBox(4);
                     portsRow.setAlignment(Pos.BASELINE_LEFT);

                     String portSummary = BusStatus.getDashboardPortSummary();

                     Label summary = new Label(portSummary);
                     summary.setStyle(
                     "-fx-text-fill:#94a3b8;" +
                     "-fx-font-size:20px;" +
                    "-fx-font-weight:bold;"
    );

    portsRow.getChildren().add(summary);
    ports.getChildren().add(portsRow);

    return ports;
}

    private VBox buildTodayCard(List<com.core2web.model.Booking> bookings) {
        VBox today = card("Today");
        HBox todayRow = new HBox(4);
        todayRow.setAlignment(Pos.BASELINE_LEFT);
        long completed = bookings.stream().filter(com.core2web.model.Booking::isCompleted)
                .filter(b -> isToday(b.getCreatedAt())).count();
        double billed = bookings.stream().filter(com.core2web.model.Booking::isCompleted)
                .filter(b -> isToday(b.getCreatedAt())).mapToDouble(com.core2web.model.Booking::getAmount).sum();
        long remaining = bookings.stream().filter(b -> !b.isCompleted() && !"CANCELLED".equals(b.getStatus())).count();
        Label num = new Label(String.valueOf(completed));
        num.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:24px;-fx-font-weight:bold;");
        todayRow.getChildren().addAll(num, label("sessions · ₹" + Math.round(billed) + " billed", "card-sub"));
        today.getChildren().addAll(todayRow, label(remaining + " booking" + (remaining == 1 ? "" : "s") + " remaining", "card-sub"),
                gap(8));
        return today;
    }

    private boolean isToday(String timestamp) {
        Instant instant = DateTimeUtil.parse(timestamp);
        return instant != null && instant.atZone(DateTimeUtil.INDIA).toLocalDate()
                .equals(java.time.LocalDate.now(DateTimeUtil.INDIA));
    }

    private com.core2web.model.Booking findActiveBooking(List<com.core2web.model.Booking> bookings) {
        for (com.core2web.model.Booking b : bookings) {
            if (b.isInProgress()) {
                return b;
            }
        }
        return null;
    }

    private void populateActiveBooking(VBox holder, List<com.core2web.model.Booking> bookings) {
        holder.getChildren().clear();
        com.core2web.model.Booking active = findActiveBooking(bookings);
        if (active == null) {
            VBox empty = card("Active booking");
            empty.getChildren().add(label("No active booking right now.", "card-sub"));
            holder.getChildren().add(empty);
            return;
        }
        holder.getChildren().add(buildActiveBookingCard(active));
    }

    private VBox buildActiveBookingCard(com.core2web.model.Booking b) {
        VBox section = new VBox(14);
        section.getStyleClass().add("active-booking-card");
        section.setPadding(new Insets(18));

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        Label statusBadge = new Label(b.getStatus());
        statusBadge.getStyleClass().add("status-badge-green");

        header.getChildren().addAll(
                label("Active booking", "booking-stage"), statusBadge);

        HBox details = new HBox(16);

        VBox bookingBox = new VBox(4);
        bookingBox.getStyleClass().add("inner-card");
        bookingBox.setPadding(new Insets(14));
        HBox.setHgrow(bookingBox, Priority.ALWAYS);
        Label refLabel = label(b.shortRef(), "booking-title");
        refLabel.setTooltip(new Tooltip("Booking ID: " + b.getId()));

        bookingBox.getChildren().addAll(
                label("BOOKING", "detail-label"),
                refLabel,
                label(((int) b.getKwh()) + " kWh requested", "booking-detail"));

        VBox dest = new VBox(4);
        dest.getStyleClass().add("inner-card");
        dest.setPadding(new Insets(14));
        HBox.setHgrow(dest, Priority.ALWAYS);
        dest.getChildren().addAll(
                label("DESTINATION", "detail-label"),
                label(b.getLocation(), "booking-title"),
                label("Scheduled " + b.getScheduledTime(), "booking-detail"));

        details.getChildren().addAll(bookingBox, dest);

        HBox tracker = buildProgressTracker(b.getStatus());

        Label feedback = new Label();
        feedback.setWrapText(true);
        feedback.setVisible(false);
        feedback.setManaged(false);

        HBox btns = new HBox(12);

        if (com.core2web.model.Booking.STATUS_CHARGING.equals(b.getStatus())) {
            btns.getChildren().add(buildCompleteSessionControls(b, feedback));
            show(feedback, "✓  Verified at pickup. Complete the session when charging finishes.",
                    "#10b981");
        } else if (b.isOtpVerified()) {
            Button startBtn = new Button("Charging authorised");
            startBtn.getStyleClass().add("primary-btn");
            startBtn.setDisable(true);
            show(feedback, "✓  Pickup verified with the customer's code.", "#10b981");
            btns.getChildren().add(startBtn);
        } else if (!b.canVerifyOtp()) {
            Button startBtn = new Button("Verify code & start charging");
            startBtn.getStyleClass().add("primary-btn");
            startBtn.setDisable(true);
            startBtn.setTooltip(new Tooltip(
                    "Start the trip and reach the pickup before verifying the customer's code."));
            btns.getChildren().add(startBtn);
        } else {
            btns.getChildren().add(buildOtpVerificationControls(b, feedback));
        }

        section.getChildren().addAll(header, details, tracker, btns, feedback);
        return section;
    }

    private VBox buildCompleteSessionControls(com.core2web.model.Booking b, Label feedback) {

        Button completeBtn = new Button("Complete session");
        completeBtn.getStyleClass().add("primary-btn");

        Label warnLbl = new Label("Finish " + b.shortRef() + "? " + b.getLocation() + " · "
                + ((int) b.getKwh()) + " kWh — this closes the booking and the charging "
                + "session. It can't be reopened.");
        warnLbl.setWrapText(true);
        warnLbl.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:12px;");

        Button confirmBtn = new Button("Confirm completion");
        confirmBtn.getStyleClass().add("danger-btn");

        Button cancelBtn = new Button("Cancel");
        cancelBtn.getStyleClass().add("secondary-btn");

        HBox confirmBtns = new HBox(10, confirmBtn, cancelBtn);

        VBox confirmBlock = new VBox(8, warnLbl, confirmBtns);
        confirmBlock.setVisible(false);
        confirmBlock.setManaged(false);

        completeBtn.setOnAction(e -> {
            completeBtn.setVisible(false);
            completeBtn.setManaged(false);
            confirmBlock.setVisible(true);
            confirmBlock.setManaged(true);
        });

        cancelBtn.setOnAction(e -> {
            confirmBlock.setVisible(false);
            confirmBlock.setManaged(false);
            completeBtn.setVisible(true);
            completeBtn.setManaged(true);
        });

        confirmBtn.setOnAction(e -> {
            confirmBtn.setDisable(true);
            cancelBtn.setDisable(true);
            confirmBtn.setText("Completing…");

            Thread worker = new Thread(() -> {
                boolean ok = bookingController.completeBooking(b.getId());
                javafx.application.Platform.runLater(() -> {
                    if (ok) {
                        goTo("Dashboard");
                    } else {
                        confirmBtn.setDisable(false);
                        cancelBtn.setDisable(false);
                        confirmBtn.setText("Confirm completion");
                        show(feedback, "Couldn't complete the session. Check the app output.",
                                "#ef4444");
                    }
                });
            });
            worker.setDaemon(true);
            worker.start();
        });

        return new VBox(10, completeBtn, confirmBlock);
    }

    private VBox buildOtpVerificationControls(com.core2web.model.Booking b, Label feedback) {

        Button startBtn = new Button("Verify code & start charging");
        startBtn.getStyleClass().add("primary-btn");

        Label subtitle = new Label("Ask the customer for the 6-digit code shown in their booking.");
        subtitle.getStyleClass().add("card-sub");
        subtitle.setWrapText(true);

        Label bookingRef = new Label(b.shortRef() + " - " + b.getLocation());
        bookingRef.getStyleClass().add("otp-booking-ref");
        bookingRef.setWrapText(true);

        Label codeLabel = new Label("6-digit verification code");
        codeLabel.getStyleClass().add("chargeon-form-label");

        TextField codeField = new TextField();
        codeField.setPromptText("0 0 0 0 0 0");
        codeField.setMaxWidth(Double.MAX_VALUE);
        codeField.getStyleClass().add("otp-code-field");
        codeField.setTextFormatter(new TextFormatter<String>(change ->
                change.getControlNewText().matches("\\d{0,6}") ? change : null));

        Button verifyBtn = new Button("Verify & start");
        verifyBtn.getStyleClass().add("primary-btn");
        verifyBtn.setDisable(true);
        codeField.textProperty().addListener((obs, oldV, newV) ->
                verifyBtn.setDisable(!newV.matches("\\d{6}")));

        Button cancelBtn = new Button("Cancel");
        cancelBtn.getStyleClass().add("secondary-btn");

        HBox otpBtns = new HBox(10, verifyBtn, cancelBtn);

        VBox otpBlock = new VBox(8, subtitle, bookingRef, codeLabel, codeField, otpBtns);
        otpBlock.setVisible(false);
        otpBlock.setManaged(false);

        startBtn.setOnAction(e -> {
            startBtn.setVisible(false);
            startBtn.setManaged(false);
            otpBlock.setVisible(true);
            otpBlock.setManaged(true);
        });

        cancelBtn.setOnAction(e -> {
            codeField.clear();
            otpBlock.setVisible(false);
            otpBlock.setManaged(false);
            startBtn.setVisible(true);
            startBtn.setManaged(true);
        });

        verifyBtn.setOnAction(e -> {
            String code = codeField.getText();
            verifyBtn.setDisable(true);
            cancelBtn.setDisable(true);
            verifyBtn.setText("Verifying…");

            Thread worker = new Thread(() -> {
                boolean ok = bookingController.verifyOtpAndStartCharging(b.getId(), code);

                javafx.application.Platform.runLater(() -> {
                    if (ok) {
                        goTo("Dashboard");
                    } else {
                        verifyBtn.setDisable(!codeField.getText().matches("\\d{6}"));
                        cancelBtn.setDisable(false);
                        verifyBtn.setText("Verify & start");
                        show(feedback,
                                "That code didn't match. Check it with the customer and try again.",
                                "#ef4444");
                    }
                });
            });
            worker.setDaemon(true);
            worker.start();
        });

        return new VBox(10, startBtn, otpBlock);
    }

    private void show(Label label, String message, String color) {
        label.setText(message);
        label.setStyle("-fx-text-fill:" + color + ";-fx-font-size:12px;");
        label.setVisible(true);
        label.setManaged(true);
    }

    private HBox buildProgressTracker(String status) {
        HBox tracker = new HBox();
        tracker.setAlignment(Pos.CENTER_LEFT);
        tracker.setPadding(new Insets(14, 0, 6, 0));

        String[] steps = { "ASSIGNED", "EN_ROUTE", "ARRIVED", "CHARGING", "COMPLETED" };
        int active = 0;
        for (int i = 0; i < steps.length; i++) {
            if (steps[i].equals(status)) {
                active = i;
                break;
            }
        }

        for (int i = 0; i < steps.length; i++) {
            VBox step = new VBox(6);
            step.setAlignment(Pos.CENTER);

            StackPane circlePane = new StackPane();
            if (i < active) {
                circlePane.getChildren().addAll(new Circle(10, Color.web("#10b981")),
                        new Label("✓") {
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

    private VBox buildUpNextSection(VBox rows) {
        VBox section = new VBox(10);
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.getChildren().addAll(label("Up next", "section-title"));

        section.getChildren().addAll(header, rows);
        return section;
    }

    private void populateUpNext(VBox rows, List<com.core2web.model.Booking> bookings) {
        rows.getChildren().clear();
        com.core2web.model.Booking active = findActiveBooking(bookings);
        boolean any = false;
        for (com.core2web.model.Booking b : bookings) {
            if (b.isInProgress() && b != active) {
                rows.getChildren().add(upNextItem(b));
                any = true;
            }
        }
        if (!any) {
            rows.getChildren().add(label("No upcoming bookings.", "small-muted"));
        }
    }

    private HBox upNextItem(com.core2web.model.Booking b) {
        HBox item = new HBox(10);
        item.setPadding(new Insets(10, 12, 10, 12));
        item.getStyleClass().add("upnext-item");
        item.setAlignment(Pos.CENTER_LEFT);

        VBox nameBox = new VBox(2);
        HBox.setHgrow(nameBox, Priority.ALWAYS);
        Label n = new Label(b.shortRef());
        n.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:13px;-fx-font-weight:bold;");
        n.setTooltip(new Tooltip("Booking ID: " + b.getId()));
        nameBox.getChildren().addAll(n, label(b.getLocation(), "small-muted"));

        VBox timeBox = new VBox(2);
        timeBox.setAlignment(Pos.CENTER_RIGHT);
        Label t = new Label(b.getScheduledTime());
        t.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:13px;-fx-font-weight:bold;");
        timeBox.getChildren().addAll(t, label(((int) b.getKwh()) + " kWh", "small-muted"));

        item.getChildren().addAll(new Circle(6, Color.web(b.statusColor())), nameBox, timeBox);
        return item;
    }

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
