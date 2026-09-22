package com.core2web.view;

import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.StringConverter;

import com.core2web.controller.BookingController;
import com.core2web.controller.BusController;
import com.core2web.controller.BusLocationController;
import com.core2web.controller.VehicleController;
import com.core2web.controller.WalletController;
import com.core2web.model.Bus;
import com.core2web.model.BusLocation;
import com.core2web.model.Pricing;
import com.core2web.model.Vehicle;
import com.core2web.service.GeocodingService;
import com.core2web.util.DateTimeUtil;

import static com.core2web.view.OwnerLayout.*;

public class BookCharging {

    private static final VehicleController vehicleController =
            new VehicleController();

    private static final BookingController bookingController =
            new BookingController();

    private static final BusController busController =
            new BusController();

    private static final BusLocationController busLocationController =
            new BusLocationController();

    private static final WalletController walletController =
            new WalletController();

    // Rates live in model/Pricing so the quote here and the receipt at completion
    // can never disagree.

    static final String MODE_INSTANT = "instant";
    static final String MODE_RESERVE = "reserve";
    static final String MODE_EMERGENCY = "emergency";

    /**
     * Tab to open on the next render, set by whoever navigates here.
     *
     * <p>One-shot: buildMainContent() consumes it and resets to Instant, so
     * arriving via the sidebar afterwards doesn't inherit a previous tile's choice.
     */
    private static String pendingMode = MODE_INSTANT;

    /** The fleet map, held so its pulse animation can be stopped on teardown. */
    private GluonMapPane mapPane;

    /** Opens Book Charging with a specific tab pre-selected. */
    static void openWith(String mode) {

        pendingMode =
                mode == null || mode.isEmpty()
                        ? MODE_INSTANT
                        : mode;

        OwnerDashboard.goTo("Book Charging");
    }

    Scene getBookChargingScene() {

        OwnerDashboard.goTo("Book Charging");

        return OwnerDashboard.scene;
    }

    static ScrollPane buildMainContent() {

        String mode = pendingMode;

        pendingMode = MODE_INSTANT;

        return new BookCharging().buildContent(mode);
    }

    private ScrollPane buildContent(String initialMode) {

        HBox content = new HBox(18);

        content.setPadding(
                new Insets(20));

        VBox leftCol = new VBox(14);

        HBox.setHgrow(
                leftCol,
                Priority.ALWAYS);

        String mode =
                MODE_RESERVE.equals(initialMode)
                        || MODE_EMERGENCY.equals(initialMode)
                        ? initialMode
                        : MODE_INSTANT;

        HBox tabs = new HBox(0);

        tabs.getStyleClass().add(
                "filter-tabs-container");

        Button tab1 =
                new Button("Instant");

        Button tab2 =
                new Button("Reserve slot");

        Button tab3 =
                new Button("Emergency");

        tabs.getChildren().addAll(
                tab1,
                tab2,
                tab3);

        javafx.scene.layout.Region mapPane =
                buildMapArea();

        VBox mapSection =
                new VBox(0);

        mapSection.getStyleClass().add(
                "card");

        mapSection.setPadding(
                new Insets(16));

        mapSection.getChildren().addAll(
                buildLegend(),
                mapPane);

        leftCol.getChildren().addAll(
                tabs,
                mapSection);

        // Opens on whichever tab the caller asked for, so the dashboard's
        // "Emergency charge" tile lands on the emergency form rather than Instant.
        VBox rightCol =
                buildBookingForm(mode);

        rightCol.setPrefWidth(340);
        rightCol.setMinWidth(320);

        highlightTab(
                mode,
                tab1,
                tab2,
                tab3);

        tab1.setOnMouseClicked(e -> {

            highlightTab(
                    MODE_INSTANT,
                    tab1,
                    tab2,
                    tab3);

            populateBookingForm(
                    rightCol,
                    MODE_INSTANT);
        });

        tab2.setOnMouseClicked(e -> {

            highlightTab(
                    MODE_RESERVE,
                    tab1,
                    tab2,
                    tab3);

            populateBookingForm(
                    rightCol,
                    MODE_RESERVE);
        });

        tab3.setOnMouseClicked(e -> {

            highlightTab(
                    MODE_EMERGENCY,
                    tab1,
                    tab2,
                    tab3);

            populateBookingForm(
                    rightCol,
                    MODE_EMERGENCY);
        });

        content.getChildren().addAll(
                leftCol,
                rightCol);

        ScrollPane scrollPane =
                new ScrollPane(content);

        scrollPane.setFitToWidth(true);

        scrollPane.getStyleClass().add(
                "scroll-pane");

        scrollPane.setStyle(
                "-fx-background-color:transparent;");

        return scrollPane;
    }

    /** Marks one tab active; Emergency uses the danger variant. */
    private static void highlightTab(
            String mode,
            Button instant,
            Button reserve,
            Button emergency) {

        instant.getStyleClass().setAll(
                MODE_INSTANT.equals(mode)
                        ? "filter-tab-active"
                        : "filter-tab");

        reserve.getStyleClass().setAll(
                MODE_RESERVE.equals(mode)
                        ? "filter-tab-active"
                        : "filter-tab");

        emergency.getStyleClass().setAll(
                MODE_EMERGENCY.equals(mode)
                        ? "filter-tab-active-danger"
                        : "filter-tab");
    }

    private HBox buildLegend() {

        HBox legend =
                new HBox(16);

        legend.setPadding(
                new Insets(0, 0, 10, 0));

        legend.getChildren().addAll(

                legendItem(
                        "#10b981",
                        "Available"),

                legendItem(
                        "#F59E0B",
                        "On route"),

                legendItem(
                        "#EF4444",
                        "Busy"));

        return legend;
    }

    private HBox legendItem(
            String color,
            String text) {

        HBox item =
                new HBox(4);

        item.setAlignment(
                Pos.CENTER_LEFT);

        Circle dot =
                new Circle(
                        4,
                        Color.web(color));

        Label lbl =
                new Label(text);

        lbl.setStyle(
                "-fx-text-fill:#94a3b8;-fx-font-size:11px;");

        item.getChildren().addAll(
                dot,
                lbl);

        return item;
    }

    /**
     * Real map of the buses the owner can actually book.
     *
     * <p>The owner pickup map is also created even when there is currently
     * no bus location data. This is important because "Find on map" must
     * work independently of bus-location data.
     */
    private javafx.scene.layout.Region buildMapArea() {

        Map<String, BusLocation> positions =
                busLocationController.getAllBusLocations();

        List<Bus> available =
                busController.getAvailableBuses();

        List<GluonMapPane.Marker> markers =
                new java.util.ArrayList<>();

        double sumLat = 0;
        double sumLng = 0;

        for (Bus bus : available) {

            BusLocation loc =
                    positions.get(bus.getId());

            if (loc == null
                    || !loc.hasCoordinates()) {
                continue;
            }

            markers.add(
                    new GluonMapPane.Marker(
                            loc.getLatitude(),
                            loc.getLongitude(),
                            loc.isStale()
                                    ? "#F59E0B"
                                    : "#10b981"));

            sumLat +=
                    loc.getLatitude();

            sumLng +=
                    loc.getLongitude();
        }

        /*
         * If bus positions exist, keep the existing behavior and initially
         * center the map around them.
         *
         * If there are no bus positions, we still create the map so that
         * the owner's "Find on map" functionality works.
         *
         * 0,0 here is only a neutral initial map coordinate. It is NOT
         * a hardcoded city or pickup location. The moment the owner searches,
         * the map is moved to the actual geocoded coordinates.
         */
        double initialLat;
        double initialLng;
        double initialZoom;

        if (!markers.isEmpty()) {

            initialLat =
                    sumLat / markers.size();

            initialLng =
                    sumLng / markers.size();

            initialZoom = 13.0;

        } else {

            initialLat = 0.0;
            initialLng = 0.0;
            initialZoom = 3.0;
        }

        mapPane =
                new GluonMapPane(
                        initialLat,
                        initialLng,
                        initialZoom,
                        markers);

        return mapPane.node();
    }

    private VBox buildBookingForm(
            String mode) {

        VBox form =
                new VBox(14);

        form.getStyleClass().add(
                "card");

        form.setPadding(
                new Insets(22));

        populateBookingForm(
                form,
                mode);

        return form;
    }

    private void populateBookingForm(
            VBox form,
            String mode) {

        form.getChildren().clear();

        String titleText =
                mode.equals("reserve")
                        ? "Reserve a route slot"
                        : mode.equals("emergency")
                                ? "Emergency request"
                                : "Instant charging request";

        Label formTitle =
                new Label(titleText);

        formTitle.setStyle(
                "-fx-text-fill:#F8FAFC;"
                        + "-fx-font-size:18px;"
                        + "-fx-font-weight:bold;");

        Label pickupLbl =
                label(
                        "Pickup location",
                        "form-label");

        HBox pickupField =
                new HBox();

        pickupField.getStyleClass().add(
                "form-field");

        pickupField.setPadding(
                new Insets(
                        10,
                        14,
                        10,
                        14));

        pickupField.setAlignment(
                Pos.CENTER_LEFT);

        TextField pickupVal =
                new TextField();

        pickupVal.setPromptText(
                "Enter pickup location");

        pickupVal.setStyle(
                "-fx-text-fill:#F8FAFC;"
                        + "-fx-font-size:13px;"
                        + "-fx-background-color:transparent;"
                        + "-fx-padding:0;");

        pickupVal.setPromptText(
                "e.g. Baner, Pune");

        HBox.setHgrow(
                pickupVal,
                Priority.ALWAYS);

        Region psp =
                new Region();

        Label pinIcon =
                new Label("\u2295");

        pinIcon.setStyle(
                "-fx-text-fill:#10b981;"
                        + "-fx-font-size:16px;");

        pickupField.getChildren().addAll(
                pickupVal,
                psp,
                pinIcon);

        // Explicit search rather than per-keystroke lookup: Nominatim allows about
        // one request a second, so autocomplete would breach its usage policy.
        Button findBtn =
                new Button("Find on map");

        findBtn.getStyleClass().add(
                "secondary-btn");

        Label pickupStatus =
                new Label();

        pickupStatus.setStyle(
                "-fx-text-fill:#64748b;"
                        + "-fx-font-size:11px;");

        pickupStatus.setWrapText(true);

        pickupStatus.setVisible(false);
        pickupStatus.setManaged(false);

        // Resolved coordinates for the typed pickup, or null for "unknown". A null
        // never blocks the booking — the text location is still submitted.
        final GeocodingService.Result[] pickupPoint =
                { null };

        // Any edit invalidates a previous resolution, so we never submit
        // coordinates belonging to an address the owner has since changed.
        pickupVal.textProperty().addListener(
                (obs, old, text) -> {

                    if (pickupPoint[0] != null) {

                        pickupPoint[0] = null;

                        showHint(
                                pickupStatus,
                                "Location changed — press "
                                        + "\"Find on map\" to place it, "
                                        + "or book without a map position.");
                    }
                });

        findBtn.setOnAction(
                e -> resolvePickup(
                        pickupVal.getText(),
                        findBtn,
                        pickupStatus,
                        pickupPoint));

        Label vehLbl =
                label(
                        "Vehicle",
                        "form-label");

        ComboBox<Vehicle> vehCombo =
                new ComboBox<>();

        vehCombo.setMaxWidth(
                Double.MAX_VALUE);

        vehCombo.getStyleClass().add(
                "form-combo");

        vehCombo.setConverter(
                new StringConverter<Vehicle>() {

                    @Override
                    public String toString(
                            Vehicle v) {

                        return v == null
                                ? ""
                                : v.displayName()
                                        + " · "
                                        + v.getPlateNumber();
                    }

                    @Override
                    public Vehicle fromString(
                            String s) {

                        return null;
                    }
                });

        List<Vehicle> vehicles =
                vehicleController.getMyVehicles();

        vehCombo.getItems().setAll(
                vehicles);

        vehCombo.setPromptText(
                "Select Vehicle");

        for (Vehicle v : vehicles) {

            if (v.isPrimary()) {

                vehCombo.setValue(v);

                break;
            }
        }

        HBox pickupRow =
                new HBox(
                        8,
                        pickupField,
                        findBtn);

        pickupRow.setAlignment(
                Pos.CENTER_LEFT);

        HBox.setHgrow(
                pickupField,
                Priority.ALWAYS);

        form.getChildren().addAll(
                formTitle,
                pickupLbl,
                pickupRow,
                pickupStatus,
                vehLbl,
                vehCombo);

        String[] scheduledTimeHolder =
                { "As soon as possible" };

        if (mode.equals("reserve")) {

            scheduledTimeHolder[0] =
                    "Awaiting slot confirmation";

            Label slotsLbl =
                    label(
                            "Available slots · today, Baner route",
                            "small-muted");

            GridPane slotGrid =
                    new GridPane();

            slotGrid.setHgap(10);
            slotGrid.setVgap(10);

            ColumnConstraints col1 =
                    new ColumnConstraints();

            col1.setPercentWidth(50);

            ColumnConstraints col2 =
                    new ColumnConstraints();

            col2.setPercentWidth(50);

            slotGrid.getColumnConstraints().addAll(
                    col1,
                    col2);

            String reserve30 = reservationSlot(30);
            String reserve60 = reservationSlot(60);
            String reserve90 = reservationSlot(90);
            String reserve120 = reservationSlot(120);

            slotGrid.add(
                    slotTile(
                            DateTimeUtil.time(reserve30),
                            "Availability is confirmed at dispatch",
                            () -> scheduledTimeHolder[0] =
                                    reserve30),
                    0,
                    0);

            slotGrid.add(
                    slotTile(
                            DateTimeUtil.time(reserve60),
                            "Availability is confirmed at dispatch",
                            () -> scheduledTimeHolder[0] =
                                    reserve60),
                    1,
                    0);

            slotGrid.add(
                    slotTile(
                            DateTimeUtil.time(reserve90),
                            "Availability is confirmed at dispatch",
                            () -> scheduledTimeHolder[0] =
                                    reserve90),
                    0,
                    1);

            // This slot is full, so it remains non-selectable.
            slotGrid.add(
                    slotTile(
                            DateTimeUtil.time(reserve120),
                            "Availability is confirmed at dispatch"),
                    1,
                    1);

            form.getChildren().addAll(
                    slotsLbl,
                    slotGrid);

        } else if (mode.equals("emergency")) {

            scheduledTimeHolder[0] =
                    "ASAP · emergency dispatch";

            Label timeLbl =
                    label(
                            "Preferred time",
                            "form-label");

            HBox timeField =
                    new HBox();

            timeField.getStyleClass().add(
                    "form-field");

            timeField.setPadding(
                    new Insets(
                            10,
                            14,
                            10,
                            14));

            timeField.setAlignment(
                    Pos.CENTER_LEFT);

            Label timeVal =
                    new Label(
                            "As soon as possible · dispatch now");

            timeVal.setStyle(
                    "-fx-text-fill:#F8FAFC;"
                            + "-fx-font-size:13px;");

            timeField.getChildren().add(
                    timeVal);

            form.getChildren().addAll(
                    timeLbl,
                    timeField);

        } else {

            Label timeLbl =
                    label(
                            "Preferred time",
                            "form-label");

            ComboBox<String> timeCombo =
                    new ComboBox<>();

            String instant30 = reservationSlot(30);
            String instant60 = reservationSlot(60);
            String instant90 = reservationSlot(90);
            timeCombo.getItems().addAll(
                    DateTimeUtil.time(instant30),
                    DateTimeUtil.time(instant60),
                    DateTimeUtil.time(instant90));

            timeCombo.setPromptText(
                    "Select Time");

            timeCombo.setMaxWidth(
                    Double.MAX_VALUE);

            timeCombo.getStyleClass().add(
                    "form-combo");

            timeCombo.valueProperty().addListener((obs, oldV, newV) -> {
                if (newV == null) return;
                int index = timeCombo.getItems().indexOf(newV);
                scheduledTimeHolder[0] = index == 0 ? instant30 : index == 1 ? instant60 : instant90;
            });

            form.getChildren().addAll(
                    timeLbl,
                    timeCombo);
        }

        Label energyLbl =
                label(
                        "Energy required",
                        "form-label");

        HBox energyBtns =
                new HBox(0);

        energyBtns.getStyleClass().add(
                "energy-toggle");

        Button e15 =
                new Button("15 kWh");

        e15.getStyleClass().add(
                "energy-btn");

        Button e25 =
                new Button("25 kWh");

        e25.getStyleClass().add(
                "energy-btn-active");

        Button e40 =
                new Button("40 kWh");

        e40.getStyleClass().add(
                "energy-btn");

        Button e80 =
                new Button("To 80%");

        e80.getStyleClass().add(
                "energy-btn");

        Button[] energyButtons =
                {
                        e15,
                        e25,
                        e40,
                        e80
                };

        energyBtns.getChildren().addAll(
                e15,
                e25,
                e40,
                e80);

        form.getChildren().addAll(
                energyLbl,
                energyBtns);

        VBox costBox =
                new VBox(6);

        costBox.setPadding(
                new Insets(
                        10,
                        0,
                        0,
                        0));

        Label chargingTimeVal =
                new Label();

        Label energyCostLbl =
                new Label();

        Label energyCostVal =
                new Label();

        Label feeVal =
                new Label();

        costBox.getChildren().addAll(

                costRow(
                        label(
                                "Est. charging time",
                                "small-muted"),
                        chargingTimeVal,
                        true),

                costRow(
                        energyCostLbl,
                        energyCostVal,
                        false),

                costRow(
                        label(
                                mode.equals("emergency")
                                        ? "Priority dispatch fee"
                                        : "Service & travel fee",
                                "small-muted"),
                        feeVal,
                        false));

        form.getChildren().add(
                costBox);

        HBox totalRow =
                new HBox();

        totalRow.setAlignment(
                Pos.CENTER_LEFT);

        totalRow.setPadding(
                new Insets(
                        8,
                        0,
                        0,
                        0));

        Label totalLbl =
                new Label("Total");

        totalLbl.setStyle(
                "-fx-text-fill:#F8FAFC;"
                        + "-fx-font-size:14px;"
                        + "-fx-font-weight:bold;");

        Region tsp =
                new Region();

        HBox.setHgrow(
                tsp,
                Priority.ALWAYS);

        Label totalVal =
                new Label();

        totalVal.setStyle(
                "-fx-text-fill:#10b981;"
                        + "-fx-font-size:16px;"
                        + "-fx-font-weight:bold;");

        totalRow.getChildren().addAll(
                totalLbl,
                tsp,
                totalVal);

        form.getChildren().add(
                totalRow);

        boolean emergencyMode =
                mode.equals(MODE_EMERGENCY);

        double serviceFee =
                Pricing.serviceFee(
                        emergencyMode);

        double[] kwhHolder =
                { 25 };

        Runnable refreshCost =
                () -> {

                    double kwh =
                            kwhHolder[0];

                    int minutes =
                            (int) Math.round(
                                    kwh / 25.0 * 45);

                    double energyCost =
                            Pricing.energyCost(kwh);

                    chargingTimeVal.setText(
                            minutes + " mins");

                    energyCostLbl.setText(
                            "Energy · "
                                    + formatKwh(kwh)
                                    + " @ ₹"
                                    + (int) Pricing.RATE_PER_KWH);

                    energyCostVal.setText(
                            "₹"
                                    + Math.round(
                                            energyCost));

                    feeVal.setText(
                            "₹"
                                    + Math.round(
                                            serviceFee));

                    totalVal.setText(
                            "₹"
                                    + Math.round(
                                            Pricing.fare(
                                                    kwh,
                                                    emergencyMode)));
                };

        refreshCost.run();

        // Prepaid wallet: the customer must be able to cover the fare before a bus
        // is dispatched. Checked here, not at completion, because the driver has no
        // read access to the customer's wallet.
        Label balanceLbl =
                new Label();

        balanceLbl.setStyle(
                "-fx-text-fill:#64748b;"
                        + "-fx-font-size:11px;");

        balanceLbl.setWrapText(true);

        double walletBalance =
                walletController.getMyBalance();

        balanceLbl.setText(
                "Wallet balance ₹"
                        + (int) walletBalance);

        form.getChildren().add(
                balanceLbl);

        for (Button b : energyButtons) {

            b.setOnAction(
                    e -> {

                        for (Button other :
                                energyButtons) {

                            other.getStyleClass().setAll(
                                    "energy-btn");
                        }

                        b.getStyleClass().setAll(
                                "energy-btn-active");

                        if (b == e80) {

                            Vehicle selected =
                                    vehCombo.getValue();

                            kwhHolder[0] =
                                    selected != null
                                            ? selected.getBatteryCapacityKwh()
                                                    * 0.5
                                            : 25;

                        } else {

                            kwhHolder[0] =
                                    Double.parseDouble(
                                            b.getText()
                                                    .replaceAll(
                                                            "[^0-9.]",
                                                            ""));
                        }

                        refreshCost.run();
                    });
        }

        if (mode.equals("emergency")) {

            VBox notice =
                    new VBox(4);

            notice.getStyleClass().add(
                    "emergency-notice");

            Label noticeTitle =
                    new Label(
                            "Emergency request · priority dispatch");

            noticeTitle.setStyle(
                    "-fx-text-fill:#EF4444;"
                            + "-fx-font-size:12px;"
                            + "-fx-font-weight:bold;");

            Label noticeBody =
                    new Label(
                            "Nearest bus is diverted to you and other stops are re-sequenced. A ₹150 priority fee applies.");

            noticeBody.setStyle(
                    "-fx-text-fill:#94a3b8;"
                            + "-fx-font-size:11px;");

            noticeBody.setWrapText(true);

            notice.getChildren().addAll(
                    noticeTitle,
                    noticeBody);

            form.getChildren().add(
                    notice);
        }

        Label errorLbl =
                new Label();

        errorLbl.setStyle(
                "-fx-text-fill:#ef4444;"
                        + "-fx-font-size:12px;");

        errorLbl.setWrapText(true);

        errorLbl.setVisible(false);
        errorLbl.setManaged(false);

        form.getChildren().add(
                errorLbl);

        String btnText =
                mode.equals("reserve")
                        ? "Reserve slot"
                        : mode.equals("emergency")
                                ? "Send emergency request"
                                : "Confirm booking";

        Button confirmBtn =
                new Button(btnText);

        confirmBtn.getStyleClass().add(
                mode.equals("emergency")
                        ? "danger-btn"
                        : "primary-btn");

        confirmBtn.setMaxWidth(
                Double.MAX_VALUE);

        confirmBtn.setPrefHeight(44);

        confirmBtn.setOnAction(
                e -> {

                    Vehicle selectedVehicle =
                            vehCombo.getValue();

                    String pickup =
                            pickupVal.getText() == null
                                    ? ""
                                    : pickupVal.getText().trim();

                    if (selectedVehicle == null
                            || pickup.isEmpty()) {

                        errorLbl.setText(
                                "Please select a vehicle and enter a pickup location.");

                        errorLbl.setVisible(true);
                        errorLbl.setManaged(true);

                        return;
                    }

                    // Reserve bookings must have an actual slot selected.
                    if (MODE_RESERVE.equals(mode)
                            && "Awaiting slot confirmation"
                                    .equals(scheduledTimeHolder[0])) {

                        errorLbl.setText(
                                "Please select an available reserve slot.");

                        errorLbl.setVisible(true);
                        errorLbl.setManaged(true);

                        return;
                    }

                    double kwh =
                            kwhHolder[0];

                    boolean isEmergency =
                            mode.equals(MODE_EMERGENCY);

                    double fare =
                            Pricing.fare(
                                    kwh,
                                    isEmergency);

                    // Hard block: don't dispatch a bus the customer can't pay for.
                    if (walletBalance < fare) {

                        errorLbl.setText(
                                "Wallet balance ₹"
                                        + (int) walletBalance
                                        + " won't cover this booking (₹"
                                        + (int) fare
                                        + "). Top up ₹"
                                        + (int) Math.ceil(
                                                fare - walletBalance)
                                        + " in Wallet & Payments first.");

                        errorLbl.setVisible(true);
                        errorLbl.setManaged(true);

                        return;
                    }

                    confirmBtn.setDisable(true);

                    String scheduledTime =
                            scheduledTimeHolder[0];

                    // Coordinates only if "Find on map" resolved them.
                    // Otherwise 0/0 means "position unknown".
                    double pickupLat =
                            pickupPoint[0] == null
                                    ? 0
                                    : pickupPoint[0].latitude;

                    double pickupLng =
                            pickupPoint[0] == null
                                    ? 0
                                    : pickupPoint[0].longitude;

                    Thread creator =
                            new Thread(
                                    () -> {

                                        String newId =
                                                bookingController.createBooking(
                                                        selectedVehicle.getId(),
                                                        scheduledTime,
                                                        pickup,
                                                        kwh,
                                                        "",
                                                        isEmergency,
                                                        mode,
                                                        pickupLat,
                                                        pickupLng);

                                        Platform.runLater(
                                                () -> {

                                                    if (newId != null) {

                                                        OwnerDashboard.goTo(
                                                                "Dashboard");

                                                    } else {

                                                        confirmBtn.setDisable(
                                                                false);

                                                        errorLbl.setText(
                                                                "Failed to create booking. Please try again.");

                                                        errorLbl.setVisible(
                                                                true);

                                                        errorLbl.setManaged(
                                                                true);
                                                    }
                                                });
                                    });

                    creator.setDaemon(true);

                    creator.start();
                });

        form.getChildren().add(
                confirmBtn);
    }

    /**
     * Creates a real upcoming reservation time instead of a hardcoded/demo time.
     *
     * <p>The returned value is kept as an ISO local date-time string because the
     * booking flow already passes the selected String through to BookingController.
     * DateTimeUtil.time(...) is used only for the existing UI display.
     */
    private static String reservationSlot(int minutesFromNow) {

        if (minutesFromNow < 0) {
            minutesFromNow = 0;
        }

        LocalDateTime slot =
                LocalDateTime.now()
                        .plusMinutes(minutesFromNow)
                        .withSecond(0)
                        .withNano(0);

        return slot.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    private static String formatKwh(
            double kwh) {

        return (kwh == Math.floor(kwh)
                ? String.valueOf((int) kwh)
                : String.valueOf(kwh))
                + " kWh";
    }

    /**
     * Resolves the typed pickup text to coordinates via GeocodingService.
     *
     * <p>Geocoding is network I/O, so it runs off the FX thread.
     */
    private void resolvePickup(
            String text,
            Button findBtn,
            Label status,
            GeocodingService.Result[] pickupPoint) {

        String query =
                text == null
                        ? ""
                        : text.trim();

        if (query.isEmpty()) {

            showHint(
                    status,
                    "Type a pickup location first.");

            return;
        }

        findBtn.setDisable(true);

        findBtn.setText(
                "Searching…");

        showHint(
                status,
                "Looking up \"" + query + "\"…");

        Thread worker =
                new Thread(
                        () -> {

                            GeocodingService.Result result =
                                    GeocodingService.geocode(
                                            query);

                            Platform.runLater(
                                    () -> {

                                        findBtn.setDisable(
                                                false);

                                        findBtn.setText(
                                                "Find on map");

                                        pickupPoint[0] =
                                                result;

                                        if (result == null) {

                                            showHint(
                                                    status,
                                                    "Couldn't find that place on the map. You can still book — "
                                                            + "the pickup will be sent as text, with no map position.");

                                        } else {

                                            /*
                                             * THIS IS THE IMPORTANT CHANGE.
                                             *
                                             * The coordinates come directly from
                                             * GeocodingService.
                                             *
                                             * Nothing is hardcoded.
                                             */
                                            if (mapPane != null) {

                                                mapPane.setPickupLocation(
                                                        result.latitude,
                                                        result.longitude);
                                            }

                                            showHint(
                                                    status,
                                                    "📍 "
                                                            + result.displayName);
                                        }
                                    });
                        });

        worker.setDaemon(true);

        worker.start();
    }

    private static void showHint(
            Label hint,
            String message) {

        hint.setText(message);

        hint.setVisible(true);

        hint.setManaged(true);
    }

    private VBox slotTile(
            String time,
            String detail) {

        return slotTile(
                time,
                detail,
                null);
    }

    private VBox slotTile(
            String time,
            String detail,
            Runnable onSelect) {

        VBox tile =
                new VBox(2);

        tile.getStyleClass().add(
                "slot-tile");

        tile.setPadding(
                new Insets(
                        8,
                        12,
                        8,
                        12));

        Label timeLbl =
                new Label(time);

        timeLbl.setStyle(
                "-fx-text-fill:#F8FAFC;"
                        + "-fx-font-size:13px;"
                        + "-fx-font-weight:bold;");

        Label detailLbl =
                new Label(detail);

        detailLbl.setStyle(
                "-fx-text-fill:#94a3b8;"
                        + "-fx-font-size:11px;");

        tile.getChildren().addAll(
                timeLbl,
                detailLbl);

        tile.setMaxWidth(
                Double.MAX_VALUE);

        if (onSelect != null) {

            tile.setOnMouseClicked(
                    e -> onSelect.run());

            tile.setCursor(
                    Cursor.HAND);
        }

        return tile;
    }

    private HBox costRow(
            Label keyLabel,
            Label valueLabel,
            boolean bold) {

        HBox row =
                new HBox();

        row.setAlignment(
                Pos.CENTER_LEFT);

        Region sp =
                new Region();

        HBox.setHgrow(
                sp,
                Priority.ALWAYS);

        valueLabel.setStyle(
                "-fx-text-fill:#F8FAFC;"
                        + "-fx-font-size:12px;"
                        + (bold
                                ? "-fx-font-weight:bold;"
                                : ""));

        row.getChildren().addAll(
                keyLabel,
                sp,
                valueLabel);

        return row;
    }
}
