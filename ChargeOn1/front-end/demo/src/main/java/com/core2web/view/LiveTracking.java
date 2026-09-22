package com.core2web.view;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import com.core2web.controller.BookingController;
import com.core2web.controller.BusController;
import com.core2web.controller.BusLocationController;
import com.core2web.controller.DriverController;
import com.core2web.controller.VehicleController;
import com.core2web.model.Bus;
import com.core2web.model.BusLocation;
import com.core2web.model.Driver;
import com.core2web.model.Vehicle;
import com.core2web.service.GeocodingService;

import static com.core2web.view.OwnerLayout.*;

public class LiveTracking {

    private static final BookingController bookingController =
            new BookingController();

    private static final VehicleController vehicleController =
            new VehicleController();

    private static final BusLocationController busLocationController =
            new BusLocationController();

    private static final DriverController driverController =
            new DriverController();

    private static final BusController busController =
            new BusController();

    private GluonMapPane mapPane;

    Scene getLiveTrackingScene() {
        OwnerDashboard.goTo("Live Tracking");
        return OwnerDashboard.scene;
    }

    static ScrollPane buildMainContent() {
        return new LiveTracking().buildContent();
    }

    private ScrollPane buildContent() {

        VBox content = new VBox(18);

        content.setPadding(
                new Insets(20));

        loadBooking(content);

        ScrollPane scrollPane =
                new ScrollPane(content);

        scrollPane.setFitToWidth(true);

        scrollPane.getStyleClass()
                .add("scroll-pane");

        scrollPane.setStyle(
                "-fx-background-color:transparent;");

        return scrollPane;
    }

    private void loadBooking(VBox content) {

        com.core2web.model.Booking booking =
                bookingController.getMyUpcomingBooking();

        List<Vehicle> vehicles =
                vehicleController.getMyVehicles();

        BusLocation busLocation =
                booking == null
                        ? null
                        : busLocationController.getLocationForBus(
                                booking.getBusId());

        if (!isRideConfirmed(booking)) {

            populateContent(
                    content,
                    booking,
                    vehicles,
                    busLocation,
                    null,
                    null);

            return;
        }

        Driver driver =
                getDriverForBooking(booking);

        if (driver == null) {

            populateContent(
                    content,
                    booking,
                    vehicles,
                    busLocation,
                    null,
                    null);

            return;
        }

        String depot =
                driver.getDepot();

        if (depot == null
                || depot.trim().isEmpty()) {

            populateContent(
                    content,
                    booking,
                    vehicles,
                    busLocation,
                    driver,
                    null);

            return;
        }

        Thread geocoder =
                new Thread(() -> {

                    GeocodingService.Result depotPoint = null;

                    try {

                        depotPoint =
                                GeocodingService.geocode(
                                        depot.trim());

                    } catch (Exception ex) {

                        ex.printStackTrace();
                    }

                    GeocodingService.Result finalDepotPoint =
                            depotPoint;

                    Platform.runLater(() -> {

                        populateContent(
                                content,
                                booking,
                                vehicles,
                                busLocation,
                                driver,
                                finalDepotPoint);
                    });
                });

        geocoder.setDaemon(true);
        geocoder.start();
    }

    private boolean isRideConfirmed(
            com.core2web.model.Booking booking) {

        if (booking == null
                || booking.getStatus() == null) {

            return false;
        }

        String status =
                booking.getStatus()
                        .trim()
                        .toUpperCase();

        return status.equals("CONFIRMED")
                || status.equals("EN_ROUTE")
                || status.equals("ARRIVED")
                || status.equals("CHARGING")
                || status.equals("COMPLETED");
    }

    private void populateContent(
            VBox content,
            com.core2web.model.Booking booking,
            List<Vehicle> vehicles,
            BusLocation busLocation,
            Driver driver,
            GeocodingService.Result depotPoint) {

        content.getChildren().clear();

        if (booking == null) {

            Label empty =
                    new Label(
                            "No active booking to track right now.");

            empty.setStyle(
                    "-fx-text-fill:#64748b;" +
                    "-fx-font-size:13px;");

            content.getChildren().add(empty);

            return;
        }

        Vehicle vehicle = null;

        for (Vehicle v : vehicles) {

            if (v.getId().equals(
                    booking.getVehicleId())) {

                vehicle = v;
                break;
            }
        }

        String vehicleLabel =
                vehicle != null
                        ? vehicle.getMake()
                                + " "
                                + vehicle.getModel()
                                + " · "
                                + vehicle.getPlateNumber()
                        : "—";

        HBox main =
                new HBox(18);

        VBox leftCol =
                new VBox(14);

        HBox.setHgrow(
                leftCol,
                Priority.ALWAYS);

        VBox mapCard =
                new VBox(12);

        mapCard.getStyleClass()
                .add("card");

        mapCard.setPadding(
                new Insets(18));

        HBox mapHeader =
                new HBox(8);

        mapHeader.setAlignment(
                Pos.CENTER_LEFT);

        Label trackTitle =
                new Label(
                        "Live tracking · "
                                + booking.shortRef());

        trackTitle.setStyle(
                "-fx-text-fill:#F8FAFC;" +
                "-fx-font-size:16px;" +
                "-fx-font-weight:bold;");

        trackTitle.setTooltip(
                new Tooltip(
                        "Booking ID: "
                                + booking.getId()));

        Region hsp =
                new Region();

        HBox.setHgrow(
                hsp,
                Priority.ALWAYS);

        boolean hasBusFix =
                busLocation != null
                        && busLocation.hasCoordinates();

        String dotColor =
                !hasBusFix
                        ? "#64748b"
                        : busLocation.isStale()
                                ? "#f59e0b"
                                : "#10b981";

        String dotText =
                !hasBusFix
                        ? "No position reported"
                        : busLocation.freshnessLabel();

        HBox liveDot =
                new HBox(4);

        liveDot.setAlignment(
                Pos.CENTER_LEFT);

        liveDot.getChildren().addAll(
                new Circle(
                        4,
                        Color.web(dotColor)),
                styledLabel(
                        dotText,
                        dotColor,
                        11,
                        false));

        mapHeader.getChildren().addAll(
                trackTitle,
                hsp,
                liveDot);

        javafx.scene.layout.Region mapPaneRegion =
                buildMapArea(
                        booking,
                        driver,
                        depotPoint);

        HBox tracker =
                buildProgressTracker(
                        booking.getStatus());

        mapCard.getChildren().addAll(
                mapHeader,
                mapPaneRegion,
                tracker);

        leftCol.getChildren()
                .add(mapCard);

        VBox rightCol =
                new VBox(18);

        rightCol.setPrefWidth(
                320);

        rightCol.setMinWidth(
                300);

        VBox detailsCard =
                new VBox(8);

        detailsCard.getStyleClass()
                .add("card");

        detailsCard.setPadding(
                new Insets(18));

        Label detTitle =
                new Label(
                        "Booking details");

        detTitle.setStyle(
                "-fx-text-fill:#F8FAFC;" +
                "-fx-font-size:16px;" +
                "-fx-font-weight:bold;");

        detailsCard.getChildren().addAll(
                detTitle,
                buildOtpRow(booking),
                detailRow(
                        "Booking",
                        booking.shortRef()),
                detailRow(
                        "Vehicle",
                        vehicleLabel),
                detailRow(
                        "Pickup",
                        booking.getLocation()),
                detailRow(
                        "Energy",
                        ((int) booking.getKwh())
                                + " kWh"),
                detailRow(
                        "Total",
                        "₹"
                                + (int) booking.fareToBill()));

        HBox btns =
                new HBox(12);

        Button cancelBtn =
                new Button("Cancel");

        cancelBtn.getStyleClass()
                .add("dashboard-active-session");

        cancelBtn.setMaxWidth(
                Double.MAX_VALUE);

        HBox.setHgrow(
                cancelBtn,
                Priority.ALWAYS);

        cancelBtn.setDisable(
                !booking.isCancellableByOwner());

        if (!booking.isCancellableByOwner()) {

            cancelBtn.setTooltip(
                    new Tooltip(
                            booking.isCompleted()
                                    ? "This booking has already finished."
                                    : "The driver is already on the way — "
                                      + "contact support to cancel."));
        }

        cancelBtn.setOnAction(e -> {

            cancelBtn.setDisable(true);

            Thread canceller =
                    new Thread(() -> {

                        bookingController.cancelBooking(
                                booking.getId());

                        Platform.runLater(
                                () -> loadBooking(content));
                    });

            canceller.setDaemon(true);
            canceller.start();
        });

        Button shareBtn =
                new Button("Share ETA");

        shareBtn.getStyleClass()
                .add("primary-btn");

        shareBtn.setMaxWidth(
                Double.MAX_VALUE);

        HBox.setHgrow(
                shareBtn,
                Priority.ALWAYS);

        btns.getChildren().addAll(
                cancelBtn,
                shareBtn);

        detailsCard.getChildren()
                .add(btns);

        VBox timelineCard =
                new VBox(10);

        timelineCard.getStyleClass()
                .add("card");

        timelineCard.setPadding(
                new Insets(18));

        Label tlTitle =
                new Label("Timeline");

        tlTitle.setStyle(
                "-fx-text-fill:#F8FAFC;" +
                "-fx-font-size:16px;" +
                "-fx-font-weight:bold;");

        timelineCard.getChildren().addAll(
                tlTitle,
                timelineItem(
                        "#64748b",
                        "Request created from "
                                + booking.getLocation(),
                        "—"));

        rightCol.getChildren().addAll(
                detailsCard,
                timelineCard);

        main.getChildren().addAll(
                leftCol,
                rightCol);

        content.getChildren()
                .add(main);
    }

    private Driver getDriverForBooking(
            com.core2web.model.Booking booking) {

        if (booking == null
                || booking.getBusId() == null
                || booking.getBusId().trim().isEmpty()) {

            return null;
        }

        Bus bus =
                busController.getBus(
                        booking.getBusId());

        if (bus == null
                || !BusController.hasDriver(bus)) {

            return null;
        }

        return driverController.getDriver(
                bus.getAssignedDriverId());
    }

    private javafx.scene.layout.Region buildMapArea(
            com.core2web.model.Booking booking,
            Driver driver,
            GeocodingService.Result depotPoint) {

        boolean hasPickup =
                booking != null
                        && booking.hasPickupCoordinates();

        boolean hasDriverDepot =
                driver != null
                        && depotPoint != null;

        String missing;

        if (!isRideConfirmed(booking)) {

            missing =
                    "Live tracking will appear after the "
                    + "driver confirms the ride.";

        } else if (driver == null) {

            missing =
                    "Live tracking will appear after a driver "
                    + "has been assigned.";

        } else if (!hasPickup && !hasDriverDepot) {

            missing =
                    "No location data — this booking has no "
                    + "pickup coordinates and the driver's "
                    + "depot could not be located.";

        } else if (!hasPickup) {

            missing =
                    "No location data — this booking has no "
                    + "pickup coordinates.";

        } else if (!hasDriverDepot) {

            if (driver.getDepot() == null
                    || driver.getDepot().trim().isEmpty()) {

                missing =
                        "No location data — the driver's "
                        + "depot is not configured.";

            } else {

                missing =
                        "No location data — unable to locate "
                        + "the driver's depot.";
            }

        } else {

            missing = "";
        }

        if (!hasPickup || !hasDriverDepot) {

            return MapPlaceholder.of(
                    missing);
        }

        double driverLatitude =
                depotPoint.latitude;

        double driverLongitude =
                depotPoint.longitude;

        double ownerLatitude =
                booking.getPickupLatitude();

        double ownerLongitude =
                booking.getPickupLongitude();

        List<GluonMapPane.Marker> markers =
                new ArrayList<>();

        markers.add(
                new GluonMapPane.Marker(
                        ownerLatitude,
                        ownerLongitude,
                        "#10B981"));

        markers.add(
                new GluonMapPane.Marker(
                        driverLatitude,
                        driverLongitude,
                        "#F59E0B",
                        true));

        double centerLat =
                (ownerLatitude
                        + driverLatitude)
                        / 2.0;

        double centerLng =
                (ownerLongitude
                        + driverLongitude)
                        / 2.0;

        mapPane =
                new GluonMapPane(
                        centerLat,
                        centerLng,
                        13.0,
                        markers);

        mapPane.setRoute(
                driverLatitude,
                driverLongitude,
                ownerLatitude,
                ownerLongitude);

        return mapPane.node();
    }

    private HBox buildProgressTracker(
            String status) {

        HBox tracker =
                new HBox();

        tracker.setAlignment(
                Pos.CENTER);

        tracker.setPadding(
                new Insets(
                        14,
                        0,
                        6,
                        0));

        String[] steps = {
                "CONFIRMED",
                "BUS EN ROUTE",
                "CHARGING",
                "COMPLETED"
        };

        int active = 0;

        switch (status == null ? "" : status) {

            case "EN_ROUTE":
            case "ARRIVED":
                active = 1;
                break;

            case "CHARGING":
                active = 2;
                break;

            case "COMPLETED":
                active = 3;
                break;

            default:
                active = 0;
        }

        for (int i = 0; i < steps.length; i++) {

            VBox step =
                    new VBox(6);

            step.setAlignment(
                    Pos.CENTER);

            Circle c;

            if (i < active) {

                c =
                        new Circle(
                                10,
                                Color.web("#10b981"));

            } else if (i == active) {

                c =
                        new Circle(
                                12,
                                Color.web("#10b981"));

            } else {

                c =
                        new Circle(
                                10,
                                Color.TRANSPARENT);

                c.setStroke(
                        Color.web("#334155"));

                c.setStrokeWidth(2);
            }

            Label stepLbl =
                    new Label(
                            steps[i]);

            stepLbl.setStyle(
                    "-fx-font-size:10px;" +
                    "-fx-text-fill:"
                    + (i <= active
                            ? "#F8FAFC"
                            : "#334155")
                    + ";");

            step.getChildren().addAll(
                    c,
                    stepLbl);

            tracker.getChildren()
                    .add(step);

            if (i < steps.length - 1) {

                Line line =
                        new Line(
                                0,
                                0,
                                50,
                                0);

                line.setStroke(
                        Color.web(
                                i < active
                                        ? "#10b981"
                                        : "#334155"));

                line.setStrokeWidth(2);

                StackPane lp =
                        new StackPane(line);

                lp.setPadding(
                        new Insets(
                                0,
                                4,
                                16,
                                4));

                tracker.getChildren()
                        .add(lp);
            }
        }

        return tracker;
    }

    private VBox buildOtpRow(
            com.core2web.model.Booking booking) {

        VBox box =
                new VBox(4);

        box.setPadding(
                new Insets(
                        4,
                        0,
                        10,
                        0));

        if (booking.isOtpVerified()) {

            Label done =
                    new Label(
                            "✓  Pickup verified · "
                            + "charging authorised");

            done.setStyle(
                    "-fx-text-fill:#10b981;" +
                    "-fx-font-size:12px;" +
                    "-fx-font-weight:bold;");

            done.setWrapText(true);

            box.getChildren()
                    .add(done);

            return box;
        }

        String otp =
                bookingController.getOtpForBooking(
                        booking.getId());

        Label caption =
                new Label(
                        "SHARE THIS CODE WITH THE DRIVER");

        caption.setStyle(
                "-fx-text-fill:#64748b;" +
                "-fx-font-size:10px;" +
                "-fx-font-weight:bold;");

        if (otp == null) {

            Label missing =
                    new Label(
                            "No verification code on this booking.");

            missing.setStyle(
                    "-fx-text-fill:#64748b;" +
                    "-fx-font-size:12px;");

            missing.setWrapText(true);

            box.getChildren().addAll(
                    caption,
                    missing);

            return box;
        }

        Label code =
                new Label(
                        otp.replaceAll(
                                "(\\d{3})(\\d{3})",
                                "$1 $2"));

        code.setStyle(
                "-fx-text-fill:#10b981;" +
                "-fx-font-size:26px;" +
                "-fx-font-weight:bold;");

        Label hint =
                new Label(
                        "The driver enters this to start charging. "
                        + "Don't share it until they arrive.");

        hint.setStyle(
                "-fx-text-fill:#64748b;" +
                "-fx-font-size:11px;");

        hint.setWrapText(true);

        box.getChildren().addAll(
                caption,
                code,
                hint);

        return box;
    }

    private HBox detailRow(
            String label,
            String value) {

        HBox row =
                new HBox();

        row.setAlignment(
                Pos.CENTER_LEFT);

        row.setPadding(
                new Insets(
                        4,
                        0,
                        4,
                        0));

        Label l =
                new Label(label);

        l.setStyle(
                "-fx-text-fill:#94a3b8;" +
                "-fx-font-size:12px;");

        l.setPrefWidth(80);

        Region sp =
                new Region();

        HBox.setHgrow(
                sp,
                Priority.ALWAYS);

        Label v =
                new Label(value);

        v.setStyle(
                "-fx-text-fill:#F8FAFC;" +
                "-fx-font-size:12px;" +
                "-fx-font-weight:bold;");

        v.setWrapText(true);

        row.getChildren().addAll(
                l,
                sp,
                v);

        return row;
    }

    private HBox timelineItem(
            String dotColor,
            String text,
            String time) {

        HBox item =
                new HBox(10);

        item.setAlignment(
                Pos.TOP_LEFT);

        item.setPadding(
                new Insets(
                        4,
                        0,
                        4,
                        0));

        Circle dot =
                new Circle(
                        5,
                        Color.web(dotColor));

        dot.setTranslateY(5);

        VBox textBox =
                new VBox(2);

        HBox.setHgrow(
                textBox,
                Priority.ALWAYS);

        Label t =
                new Label(text);

        t.setStyle(
                "-fx-text-fill:#F8FAFC;" +
                "-fx-font-size:12px;" +
                "-fx-font-weight:bold;");

        t.setWrapText(true);

        Label tm =
                new Label(time);

        tm.setStyle(
                "-fx-text-fill:#64748b;" +
                "-fx-font-size:10px;");

        textBox.getChildren().addAll(
                t,
                tm);

        item.getChildren().addAll(
                dot,
                textBox);

        return item;
    }
}