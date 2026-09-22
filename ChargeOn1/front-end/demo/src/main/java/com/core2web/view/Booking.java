package com.core2web.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import com.core2web.controller.BookingController;
import com.core2web.controller.OwnerController;
import com.core2web.util.DateTimeUtil;

public class Booking {

    private static final BookingController bookingController = new BookingController();
    private static final OwnerController ownerController = new OwnerController();

    Scene getBookingScene() {
        DriverDashboard.goTo("My Bookings");
        return DriverDashboard.scene;
    }

    static ScrollPane buildMainContent() {
        return new Booking().buildContent();
    }

    private static final class BookingState {
        List<com.core2web.model.Booking> bookings = new ArrayList<>();
        Map<String, String> ownerNames = new HashMap<>();
        String filter = "All";
    }

    private static Map<String, String> resolveOwnerNames(
            List<com.core2web.model.Booking> bookings) {
        List<String> ids = new ArrayList<>();
        for (com.core2web.model.Booking b : bookings) {
            ids.add(b.getOwnerId());
        }
        return ownerController.getOwnerNames(ids);
    }

    private ScrollPane buildContent() {
        HBox main = new HBox(16);
        main.setPadding(new Insets(16));

        BookingState state = new BookingState();
        state.bookings = bookingController.getBookingsForCurrentDriver();
        state.ownerNames = resolveOwnerNames(state.bookings);

        VBox rows = new VBox(2);
        VBox assignmentHolder = new VBox();
        refresh(state, rows, assignmentHolder);

        VBox leftCol = new VBox(14);
        HBox.setHgrow(leftCol, Priority.ALWAYS);

        HBox emergencyBanner = buildEmergencyBanner();
        if (emergencyBanner != null) {
            leftCol.getChildren().add(emergencyBanner);
        }

        leftCol.getChildren().add(buildBookingsSection(rows, state, assignmentHolder));

        VBox rightCol = new VBox(16);
        rightCol.setPrefWidth(320);
        rightCol.getChildren().addAll(assignmentHolder, buildSessionLog());

        main.getChildren().addAll(leftCol, rightCol);

        ScrollPane scrollPane = new ScrollPane(main);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.getStyleClass().add("scroll-pane");
        scrollPane.setStyle("-fx-background-color: transparent;");
        return scrollPane;
    }

    private static void refresh(BookingState state, VBox rows, VBox assignmentHolder) {
        populateBookingRow(rows, state);

        com.core2web.model.Booking newAssignment = null;
        for (com.core2web.model.Booking b : state.bookings) {
            if ("ASSIGNED".equals(b.getStatus())) {
                newAssignment = b;
                break;
            }
        }

        assignmentHolder.getChildren().setAll(
                newAssignment != null
                        ? buildNewAssignmentCard(newAssignment, state, rows, assignmentHolder)
                        : buildNewAssignmentPlaceholder("No new assignment right now."));
    }

    private HBox buildEmergencyBanner() {
        com.core2web.model.Booking pending =
                bookingController.getPendingEmergencyBookingForCurrentDriver();

        if (pending == null) {
            return null;
        }

        return EmergencyBanner.build(pending, () -> DriverDashboard.goTo("My Bookings"));
    }

    private VBox buildBookingsSection(VBox rows, BookingState state, VBox assignmentHolder) {
        VBox section = new VBox(14);
        section.getStyleClass().add("bookings-section");
        section.setPadding(new Insets(18));

        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        Label sectionTitle = new Label("Assigned bookings");
        sectionTitle.getStyleClass().add("booking-stage");

        Region headerSp = new Region();
        HBox.setHgrow(headerSp, Priority.ALWAYS);

        Button allTab = new Button("All");
        allTab.getStyleClass().add("filter-tab-active");
        Button activeTab = new Button("Active");
        activeTab.getStyleClass().add("filter-tab");
        Button completedTab = new Button("Completed");
        completedTab.getStyleClass().add("filter-tab");

        List<Button> tabButtons = List.of(allTab, activeTab, completedTab);
        allTab.setOnAction(e -> selectFilter(allTab, tabButtons, rows, "All", state));
        activeTab.setOnAction(e -> selectFilter(activeTab, tabButtons, rows, "Active", state));
        completedTab.setOnAction(e -> selectFilter(completedTab, tabButtons, rows, "Completed", state));

        HBox tabs = new HBox(2, allTab, activeTab, completedTab);
        tabs.setAlignment(Pos.CENTER);
        tabs.getStyleClass().add("filter-tabs-container");

        header.getChildren().addAll(sectionTitle, headerSp, tabs);

        HBox colHeaders = new HBox();
        colHeaders.setPadding(new Insets(10, 12, 10, 12));
        colHeaders.setAlignment(Pos.CENTER_LEFT);
        colHeaders.getChildren().addAll(
                colLabel("BOOKING", 120),
                colLabel("REQUESTER", 130),
                colLabel("LOCATION", 170),
                colLabel("TIME", 90),
                colLabel("ENERGY", 70),
                colLabel("STATUS", 100));

        section.getChildren().addAll(header, colHeaders, rows);
        return section;
    }

    private static HBox bookingRow(com.core2web.model.Booking b, Map<String, String> ownerNames) {
        HBox row = new HBox();
        row.setPadding(new Insets(12, 12, 12, 12));
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("booking-row");

        VBox idBox = new VBox(2);
        idBox.setPrefWidth(120);
        idBox.setMinWidth(120);
        Label idLabel = new Label(b.shortRef());
        idLabel.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:12px;-fx-font-weight:bold;");
        idLabel.setTooltip(new Tooltip("Booking ID: " + b.getId()));
        idBox.getChildren().add(idLabel);

        Label requesterLabel = new Label(
                OwnerController.nameOr(ownerNames, b.getOwnerId(), "unknown requester"));
        requesterLabel.setStyle("-fx-text-fill:#94a3b8;-fx-font-size:11px;");
        requesterLabel.setWrapText(true);
        requesterLabel.setPrefWidth(130);
        requesterLabel.setMinWidth(130);

        Label destLabel = new Label(b.getLocation());
        destLabel.setStyle("-fx-text-fill:#cbd5e1;-fx-font-size:13px;");
        destLabel.setWrapText(true);
        destLabel.setPrefWidth(170);
        destLabel.setMinWidth(170);

        Label timeLabel = new Label(displayBookingTime(b.getScheduledTime()));
        timeLabel.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:13px;-fx-font-weight:bold;");
        timeLabel.setPrefWidth(90);
        timeLabel.setMinWidth(90);

        Label energyLabel = new Label(((int) b.getKwh()) + " kWh");
        energyLabel.setStyle("-fx-text-fill:#cbd5e1;-fx-font-size:13px;");
        energyLabel.setPrefWidth(70);
        energyLabel.setMinWidth(70);

        Label statusBadge = new Label(
                b.isPendingDriverAccept() ? "🔒 AWAITING YOU" : b.getStatus());
        String statusColor = b.statusColor();
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

        row.getChildren().addAll(idBox, requesterLabel, destLabel, timeLabel, energyLabel, statusBox);
        return row;
    }

    private static void selectFilter(Button current, List<Button> allTabs, VBox rows, String statusKey,
            BookingState state) {
        for (Button tab : allTabs) {
            tab.getStyleClass().removeAll("filter-tab", "filter-tab-active");
            tab.getStyleClass().add("filter-tab");
        }
        current.getStyleClass().removeAll("filter-tab");
        current.getStyleClass().add("filter-tab-active");
        state.filter = statusKey;
        populateBookingRow(rows, state);
    }

    private static void populateBookingRow(VBox rows, BookingState state) {
        rows.getChildren().clear();
        for (com.core2web.model.Booking b : state.bookings) {
            boolean activeWork = !b.isCompleted()
                    && !"CANCELLED".equals(b.getStatus())
                    && !b.isPendingDriverAccept();

            boolean matches = state.filter.equals("All")
                    || (state.filter.equals("Active") && activeWork)
                    || (state.filter.equals("Completed") && b.isCompleted());
            if (matches) {
                rows.getChildren().add(bookingRow(b, state.ownerNames));
            }
        }
        if (rows.getChildren().isEmpty()) {
            Label empty = new Label("No bookings in this view.");
            empty.setStyle("-fx-text-fill:#64748b;-fx-font-size:12px;-fx-padding:16 0 16 0;");
            rows.getChildren().add(empty);
        }
    }

    private static Label colLabel(String text, double width) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill:#64748b;-fx-font-size:10px;-fx-font-weight:bold;");
        l.setPrefWidth(width);
        l.setMinWidth(width);
        return l;
    }

    private static VBox buildNewAssignmentPlaceholder(String message) {
        VBox card = new VBox(10);
        card.getStyleClass().add("assignment-card");
        card.setPadding(new Insets(18));
        Label newTag = new Label("NEW ASSIGNMENT");
        newTag.getStyleClass().add("new-assignment-tag");
        Label msg = new Label(message);
        msg.setStyle("-fx-text-fill:#94a3b8;-fx-font-size:12px;");
        msg.setWrapText(true);
        card.getChildren().addAll(newTag, msg);
        return card;
    }

    private static VBox buildNewAssignmentCard(com.core2web.model.Booking b, BookingState state,
            VBox rows, VBox assignmentHolder) {
        VBox card = new VBox(10);
        card.getStyleClass().add("assignment-card");
        card.setPadding(new Insets(18));

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label newTag = new Label("NEW ASSIGNMENT");
        newTag.getStyleClass().add("new-assignment-tag");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Label timeLabel = new Label(displayBookingTime(b.getScheduledTime()));
        timeLabel.setStyle("-fx-text-fill:#64748b;-fx-font-size:12px;");
        header.getChildren().addAll(newTag, sp, timeLabel);

        Label bookingLabel = new Label(b.shortRef());
        bookingLabel.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:20px;-fx-font-weight:bold;");
        bookingLabel.setTooltip(new Tooltip("Booking ID: " + b.getId()));

        Label details = new Label(
                b.getLocation() + " · " + ((int) b.getKwh()) + " kWh"
                        + "\nRequested by " + OwnerController.nameOr(
                                state.ownerNames, b.getOwnerId(), "unknown requester")
                        + "\nETA " + displayBookingTime(b.getScheduledTime()));
        details.setStyle("-fx-text-fill:#94a3b8;-fx-font-size:12px;");
        details.setWrapText(true);

        Button startBtn = new Button("Start Trip");
        startBtn.getStyleClass().add("accept-btn");
        startBtn.setMaxWidth(Double.MAX_VALUE);

        Label footer = new Label("Accepting sets the booking to EN_ROUTE and adds a stop to your route.");
        footer.setStyle("-fx-text-fill:#64748b;-fx-font-size:10px;");
        footer.setWrapText(true);

        startBtn.setOnAction(e -> {
            startBtn.setDisable(true);
            Thread updater = new Thread(() -> {
                boolean ok = bookingController.startTrip(b.getId());
                Platform.runLater(() -> {
                    if (ok) {
                        b.setStatus("EN_ROUTE");
                        refresh(state, rows, assignmentHolder);
                    } else {
                        startBtn.setDisable(false);
                        footer.setText("Failed to start trip. Please try again.");
                        footer.setStyle("-fx-text-fill:#ef4444;-fx-font-size:10px;");
                    }
                });
            });
            updater.setDaemon(true);
            updater.start();
        });

        card.getChildren().addAll(header, bookingLabel, details, startBtn, footer);
        return card;
    }

    private VBox buildSessionLog() {
        VBox card = new VBox(14);
        card.getStyleClass().add("session-log-card");
        card.setPadding(new Insets(18));

        Label sectionTitle = new Label("Session log");
        sectionTitle.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:16px;-fx-font-weight:bold;");

        VBox logEntries = new VBox(12);
        logEntries.getChildren().add(new Label("No event log is stored for this booking yet."));

        card.getChildren().addAll(sectionTitle, logEntries);
        return card;
    }

    private static String displayBookingTime(String scheduledTime) {
        if (scheduledTime == null || scheduledTime.isBlank()) return "N/A";
        java.time.Instant instant = DateTimeUtil.parse(scheduledTime);
        return instant == null ? scheduledTime : DateTimeUtil.display(scheduledTime);
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

}
