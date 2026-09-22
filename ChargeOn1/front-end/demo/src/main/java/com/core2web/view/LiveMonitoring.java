package com.core2web.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import com.core2web.controller.BookingController;
import com.core2web.controller.BusController;
import com.core2web.controller.BusLocationController;
import com.core2web.controller.DriverController;
import com.core2web.model.Bus;
import com.core2web.model.BusLocation;
import com.core2web.model.Driver;

import static com.core2web.view.AdminLayout.*;

public class LiveMonitoring {

    private static final BusController busController = new BusController();
    private static final BookingController bookingController = new BookingController();
    private static final DriverController driverController = new DriverController();
    private static final BusLocationController busLocationController = new BusLocationController();

    private static String selectedCity = "All cities";

    public static void show(Stage stage) {
        AdminDashboard.goTo("Live Monitoring");
    }


    static ScrollPane buildMainContent() {

        VBox page = new VBox(16);

        page.setPadding(
                new Insets(16)
        );


        VBox header = new VBox(4);

        Label title =
                new Label("Live Monitoring");

        title.getStyleClass().add(
                "section-title"
        );

        Label subtitle =
                new Label(
                        "Monitor live fleet activity and charging status across cities"
                );

        subtitle.getStyleClass().add(
                "card-sub"
        );

        header.getChildren().addAll(
                title,
                subtitle
        );


        HBox cityFilters =
                buildCityFilters();


        HBox main =
                new HBox(16);

        VBox center =
                new VBox(14);

        HBox.setHgrow(
                center,
                Priority.ALWAYS
        );

        List<Bus> buses = busController.getAllBuses();
List<com.core2web.model.Booking> queued =
        bookingController.getDispatchQueueBookings();

List<com.core2web.model.Booking> allBookings =
        bookingController.getAllBookings();

List<com.core2web.model.Booking> dispatched =
        new ArrayList<>();

for (com.core2web.model.Booking b : allBookings) {

    if (hasDriver(b)
            && !b.isCompleted()
            && !com.core2web.model.Booking.STATUS_CANCELLED.equals(
                    b.getStatus())) {

        dispatched.add(b);
    }
}

dispatched.sort(
        (a, b) ->
                a.getCreatedAt().compareTo(
                        b.getCreatedAt()
                )
);
        // ONE round-trip for every position, joined against the fleet in memory.
        // Calling getLocationForBus per bus would be N REST calls.
        Map<String, BusLocation> positions = busLocationController.getAllBusLocations();

        // "LIVE" now means "reporting a fresh position", not "not faulted" — the
        // previous count was really just the non-fault bus count.
        int live = 0;
        for (Bus bus : buses) {
            BusLocation loc = positions.get(bus.getId());
            if (loc != null && loc.hasCoordinates() && !loc.isStale()) {
                live++;
            }
        }

        Label liveCountLabel = new Label("LIVE");

        VBox assignHolder = new VBox();
        populateAssignCard(assignHolder, queued, buses);

        VBox dispatchRows = new VBox(2);
        populateDispatchList(dispatchRows, dispatched, buses);

        center.getChildren().addAll(
                buildMapCard(liveCountLabel, buses, positions),
                buildAssignCard(assignHolder),
                buildDispatchCard(dispatchRows, dispatched.size())
        );

        VBox rightCol =
                new VBox(14);

        rightCol.setPrefWidth(
                300
        );

        Label fleetCountLabel = new Label(buses.size() + " buses");
        fleetCountLabel.getStyleClass().add("small-muted");
        VBox listRows = new VBox(2);
        populateFleetList(listRows, buses, positions);

        rightCol.getChildren().add(
                buildBusListCard(fleetCountLabel, listRows)
        );

        main.getChildren().addAll(
                center,
                rightCol
        );

        page.getChildren().addAll(
                header,
                cityFilters,
                main
        );

        ScrollPane sp =
                new ScrollPane(page);

        sp.setFitToWidth(
                true
        );

        sp.getStyleClass().add(
                "scroll-pane"
        );

        return sp;
    }

    private static void populateFleetList(VBox listRows, List<Bus> buses,
            Map<String, BusLocation> positions) {
        listRows.getChildren().clear();
        if (buses.isEmpty()) {
            Label empty = new Label("No buses in the fleet yet.");
            empty.setStyle("-fx-text-fill:#64748b;-fx-font-size:12px;");
            listRows.getChildren().add(empty);
            return;
        }
        for (Bus bus : buses) {
            String status = busController.getDisplayStatus(bus);
            String color = busController.getDisplayColor(status);

            // Subtitle carries position freshness so an admin can see at a glance
            // which buses are actually reporting.
            BusLocation loc = positions.get(bus.getId());
            String depot = bus.getDepot() == null ? "" : bus.getDepot();
            String subtitle = loc == null || !loc.hasCoordinates()
                    ? depot + " · no position reported"
                    : depot + " · " + loc.freshnessLabel();

            listRows.getChildren().add(busRow(bus.getBusCode(), subtitle, status, color));
        }
    }


    private static HBox buildCityFilters() {

        HBox tabs =
                new HBox(2);

        tabs.getStyleClass().add(
                "filter-tabs-container"
        );

        tabs.getChildren().addAll(
                filterTab("All cities"),
                filterTab("Pune"),
                filterTab("Mumbai"),
                filterTab("Nagpur")
        );

        return tabs;
    }


    private static Label filterTab(
            String city
    ) {

        Label tab =
                new Label(city);

        updateFilterStyle(
                tab,
                city.equals(selectedCity)
        );


        tab.setOnMouseClicked(e -> {

            selectedCity = city;


            AdminDashboard.heading.setText(
                    "Live Monitoring"
            );

            AdminDashboard.subheading.setText(
                    city.equals("All cities")
                            ? "Monitor live fleet activity across all cities"
                            : "Live fleet monitoring · " + city
            );

            AdminDashboard.middleBox.getChildren().setAll(
                    buildMainContent()
            );

            AdminDashboard.window.setTitle(
                    "ChargeOn · Live Monitoring"
            );
        });

        tab.setOnMouseEntered(e -> {

            if (!city.equals(selectedCity)) {

                tab.setStyle(
                        "-fx-background-color:#1e293b;" +
                        "-fx-text-fill:#f8fafc;" +
                        "-fx-padding:8 16 8 16;" +
                        "-fx-font-size:12px;" +
                        "-fx-cursor:hand;"
                );
            }
        });

        tab.setOnMouseExited(e -> {

            updateFilterStyle(
                    tab,
                    city.equals(selectedCity)
            );
        });

        return tab;
    }


    private static void updateFilterStyle(
            Label tab,
            boolean active
    ) {

        if (active) {

            tab.getStyleClass().remove(
                    "filter-tab"
            );

            if (!tab.getStyleClass().contains(
                    "filter-tab-active"
            )) {

                tab.getStyleClass().add(
                        "filter-tab-active"
                );
            }

        } else {

            tab.getStyleClass().remove(
                    "filter-tab-active"
            );

            if (!tab.getStyleClass().contains(
                    "filter-tab"
            )) {

                tab.getStyleClass().add(
                        "filter-tab"
                );
            }
        }
    }


    /**
     * Centre point for the fleet map when this city is selected. "All cities"
     * frames Maharashtra as a whole; each named city uses its own centre so the
     * toggle actually recentres the map instead of just relabeling it.
     */
    private static double[] cityCenter(String city) {
        switch (city) {
            case "Pune":   return new double[] {18.5204, 73.8567};
            case "Mumbai": return new double[] {19.0760, 72.8777};
            case "Nagpur": return new double[] {21.1458, 79.0882};
            default:       return new double[] {19.7515, 75.7139}; // Maharashtra centre
        }
    }

    /** Wide enough to show the whole state for "All cities"; street-level for a named city. */
    private static double cityZoom(String city) {
        return "All cities".equals(city) ? 6.3 : 12.0;
    }


    private static VBox buildMapCard(Label liveCountLabel, List<Bus> buses,
            Map<String, BusLocation> positions) {

        VBox card =
                new VBox(12);

        card.getStyleClass().add(
                "card"
        );

        card.setPadding(
                new Insets(18)
        );


        HBox header =
                new HBox();

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        liveCountLabel.setStyle(
                "-fx-background-color:#10b98122;" +
                "-fx-text-fill:#10b981;" +
                "-fx-font-size:10px;" +
                "-fx-font-weight:bold;" +
                "-fx-padding:4 10 4 10;" +
                "-fx-background-radius:6;"
        );

        header.getChildren().addAll(

                label(
                        "Fleet map · " + selectedCity,
                        "section-title"
                ),

                spacer,

                new HBox(liveCountLabel)
        );


        // A marker per bus that has reported a position, coloured by the same
        // display status the fleet list uses. Buses with no BusLocation document
        // are simply absent — never plotted at 0,0.
        List<GluonMapPane.Marker> markers = new ArrayList<>();

        for (Bus bus : buses) {
            BusLocation loc = positions.get(bus.getId());
            if (loc == null || !loc.hasCoordinates()) {
                continue;
            }
            String status = busController.getDisplayStatus(bus);
            markers.add(new GluonMapPane.Marker(
                    loc.getLatitude(),
                    loc.getLongitude(),
                    loc.isStale() ? "#f59e0b" : busController.getDisplayColor(status)));
        }

        int reporting = markers.size();

        // The city toggle drives where the map looks, not where the buses happen to
        // be: "All cities" frames the whole state, and picking a named city
        // recentres and zooms the map on that city specifically. A highlighted
        // marker pins the city's own centre point, so the toggle visibly does
        // something even before any bus in that city has reported a position.
        double[] center = cityCenter(selectedCity);
        List<GluonMapPane.Marker> mapMarkers = new ArrayList<>(markers);
        mapMarkers.add(new GluonMapPane.Marker(center[0], center[1], "#facc15", true));

        javafx.scene.layout.Region mapArea;
        if (buses.isEmpty()) {
            mapArea = MapPlaceholder.of("No buses in the fleet yet.");
        } else {
            mapArea = new GluonMapPane(
                    center[0], center[1],
                    cityZoom(selectedCity),
                    mapMarkers).node();
        }
        mapArea.setPrefHeight(340);


        HBox legend =
                new HBox(18,

                        legendItem(
                                "#10b981",
                                "Available"
                        ),

                        legendItem(
                                "#3b82f6",
                                "En route"
                        ),

                        legendItem(
                                "#f59e0b",
                                "Charging / stale position"
                        ),

                        legendItem(
                                "#ef4444",
                                "Fault"
                        )
                );

        legend.setAlignment(
                Pos.CENTER_LEFT
        );

        Label plottedText = new Label(reporting + " of " + buses.size()
                + " buses reporting a position");
        plottedText.setStyle("-fx-text-fill:#64748b;-fx-font-size:11px;");

        card.getChildren().addAll(
                header,
                mapArea,
                legend,
                plottedText
        );

        return card;
    }


    private static HBox legendItem(
            String color,
            String text
    ) {

        HBox item =
                new HBox(
                        6,
                        new Circle(
                                5,
                                Color.web(color)
                        ),
                        label(
                                text,
                                "small-muted"
                        )
                );

        item.setAlignment(
                Pos.CENTER_LEFT
        );

        return item;
    }


    private static VBox buildAssignCard(VBox holder) {

        VBox card = new VBox(10);
        card.getStyleClass().add("assignment-card");
        card.setPadding(new Insets(18));

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        Label tag = new Label("PENDING ASSIGNMENTS");
        tag.getStyleClass().add("new-assignment-tag");
        header.getChildren().add(tag);

        card.getChildren().addAll(header, holder);
        return card;
    }

    private static void populateAssignCard(VBox holder, List<com.core2web.model.Booking> queued,
            List<Bus> buses) {
        holder.getChildren().clear();

        if (queued.isEmpty()) {
            Label empty = new Label("No bookings waiting for a bus right now.");
            empty.setStyle("-fx-text-fill:#94a3b8;-fx-font-size:12px;");
            empty.setWrapText(true);
            holder.getChildren().add(empty);
            return;
        }

        Label title = new Label(
                queued.size() + (queued.size() == 1 ? " booking" : " bookings") + " waiting for a bus");
        title.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:18px;-fx-font-weight:bold;");
        holder.getChildren().add(title);

        // Resolved once per render rather than per row — same pattern as
        // populateDispatchList, just reused here for the driver names shown in the
        // assign dialog's picker.
        Map<String, String> driverNameById = new HashMap<>();
        for (Driver driver : driverController.getAllDrivers()) {
            driverNameById.put(driver.getUid(), driver.getName());
        }

        // Listed individually rather than summarised as "next", so an admin can act
        // on any stuck booking instead of only the oldest one.
        for (com.core2web.model.Booking b : queued) {
            HBox row = new HBox(8);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(8, 0, 8, 0));

            VBox info = new VBox(2);
            HBox.setHgrow(info, Priority.ALWAYS);

            Label ref = new Label(b.shortRef());
            ref.setStyle("-fx-text-fill:#f8fafc;-fx-font-size:12px;-fx-font-weight:bold;");
            ref.setTooltip(new Tooltip("Booking ID: " + b.getId()));

            Label detail = new Label(b.getLocation() + " · scheduled " + b.getScheduledTime());
            detail.getStyleClass().add("small-muted");
            detail.setWrapText(true);

            info.getChildren().addAll(ref, detail);
            row.getChildren().addAll(
                    info,
                    buildAssignButton(b, buses, driverNameById),
                    buildCancelButton(b));
            holder.getChildren().add(row);
        }
    }


    /**
     * Dispatches a QUEUED booking to a driver, picked by their bus — bookings carry
     * a busId, not a driverId directly, so "assign to driver" means "assign to the
     * bus that driver is on" (same model {@code UsersAndDrivers}' reassign dialog
     * uses). Only buses that currently have a driver are offered: assigning a
     * driverless bus would flip the booking to ASSIGNED with an empty driverId,
     * which would then show up in neither the queued nor the dispatched list.
     */
    private static Button buildAssignButton(com.core2web.model.Booking b, List<Bus> buses,
            Map<String, String> driverNameById) {

        Button assign = new Button("Assign");
        assign.getStyleClass().add("primary-btn");

        assign.setOnAction(e -> {
           List<Bus> manned = new ArrayList<>();

              for (Bus bus : buses) {

         if (BusController.hasDriver(bus)
            && "AVAILABLE".equals(bus.getStatus())) {

           manned.add(bus);
       }
}

            if (manned.isEmpty()) {
                Alert none = new Alert(Alert.AlertType.WARNING);
                none.setHeaderText(null);
                none.setContentText("No buses currently have a driver assigned. "
                        + "Assign a driver to a bus first, from Users & Drivers.");
                none.showAndWait();
                return;
            }

            ComboBox<Bus> driverCombo = new ComboBox<>();
            driverCombo.setMaxWidth(Double.MAX_VALUE);
            driverCombo.setPromptText("Select driver");
            driverCombo.setConverter(new StringConverter<Bus>() {
                @Override
                public String toString(Bus bus) {
                    if (bus == null) {
                        return "";
                    }
                    String driverId = bus.getAssignedDriverId();
                    String driverName = driverNameById.get(driverId);
                    return (driverName == null || driverName.isEmpty() ? driverId : driverName)
                            + " · " + bus.getBusCode() + " · " + bus.getDepot();
                }

                @Override
                public Bus fromString(String s) {
                    return null;
                }
            });
            driverCombo.getItems().setAll(manned);

            VBox form = new VBox(10, new Label("Driver"), driverCombo);
            form.setPadding(new Insets(12));

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Assign booking");
            dialog.setHeaderText("Dispatch " + b.shortRef() + " (" + b.getLocation() + ")");
            dialog.getDialogPane().setContent(form);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

            if (dialog.showAndWait().filter(t -> t == ButtonType.OK).isEmpty()) {
                return;
            }

            Bus bus = driverCombo.getValue();
            if (bus == null) {
                Alert incomplete = new Alert(Alert.AlertType.WARNING);
                incomplete.setHeaderText(null);
                incomplete.setContentText("Pick a driver.");
                incomplete.showAndWait();
                return;
            }

            assign.setDisable(true);
            assign.setText("Assigning…");

            Thread worker = new Thread(() -> {
                boolean ok = bookingController.assignBus(b.getId(), bus.getId());
                Platform.runLater(() -> {
                    if (ok) {
                        AdminDashboard.goTo("Live Monitoring");
                    } else {
                        assign.setDisable(false);
                        assign.setText("Assign");
                        Alert failed = new Alert(Alert.AlertType.ERROR);
                        failed.setHeaderText(null);
                        failed.setContentText("Could not assign " + b.shortRef()
                                + ". Check the app output for the reason.");
                        failed.showAndWait();
                    }
                });
            });
            worker.setDaemon(true);
            worker.start();
        });

        return assign;
    }


    /**
     * Per-booking cancel for admins — the ops escape hatch for a booking that is
     * stuck, e.g. an emergency request no driver ever answered. Disabled once the
     * booking has finished or already been cancelled.
     */
    private static Button buildCancelButton(com.core2web.model.Booking b) {

        Button cancel = new Button("Cancel");
        cancel.getStyleClass().add("secondary-btn");

        if (!b.isCancellableByAdmin()) {
            cancel.setDisable(true);
            cancel.setTooltip(new Tooltip("Already " + b.getStatus().toLowerCase() + "."));
            return cancel;
        }

        cancel.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Cancel booking");
            confirm.setHeaderText("Cancel " + b.shortRef() + "?");
            confirm.setContentText(b.getLocation() + " · " + ((int) b.getKwh()) + " kWh"
                    + "\n\nThis sets the booking to CANCELLED. It cannot be undone from the app.");

            if (confirm.showAndWait().filter(t -> t == ButtonType.OK).isEmpty()) {
                return;
            }

            cancel.setDisable(true);
            cancel.setText("Cancelling…");

            Thread worker = new Thread(() -> {
                boolean ok = bookingController.cancelBooking(b.getId());
                Platform.runLater(() -> {
                    if (ok) {
                        AdminDashboard.goTo("Live Monitoring");
                    } else {
                        cancel.setDisable(false);
                        cancel.setText("Cancel");
                        Alert failed = new Alert(Alert.AlertType.ERROR);
                        failed.setHeaderText(null);
                        failed.setContentText("Could not cancel " + b.shortRef()
                                + ". Check the app output for the reason.");
                        failed.showAndWait();
                    }
                });
            });
            worker.setDaemon(true);
            worker.start();
        });

        return cancel;
    }


    private static boolean hasDriver(com.core2web.model.Booking booking) {
        String driverId = booking.getDriverId();
        return driverId != null && !driverId.isEmpty() && !"null".equals(driverId);
    }


    private static VBox buildDispatchCard(VBox rows, int count) {

        VBox card = new VBox(10);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(18));

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label countLabel = new Label(count + (count == 1 ? " booking" : " bookings"));
        countLabel.getStyleClass().add("small-muted");

        header.getChildren().addAll(
                label("Dispatched bookings · driver & bus", "section-title"),
                spacer,
                countLabel);

        card.getChildren().addAll(header, rows);
        return card;
    }


    /**
     * Shows who is actually on each dispatched booking. Booking.driverId and
     * Booking.busId are raw ids, so they are resolved to the driver's name and the
     * bus code; the id is only shown when the lookup finds nothing.
     */
    private static void populateDispatchList(VBox rows,
            List<com.core2web.model.Booking> dispatched, List<Bus> buses) {

        rows.getChildren().clear();

        if (dispatched.isEmpty()) {
            Label empty = new Label("No bookings are assigned to a driver right now.");
            empty.setStyle("-fx-text-fill:#64748b;-fx-font-size:12px;");
            empty.setWrapText(true);
            rows.getChildren().add(empty);
            return;
        }

        Map<String, String> busCodeById = new HashMap<>();
        for (Bus bus : buses) {
            busCodeById.put(bus.getId(), bus.getBusCode());
        }

        Map<String, String> driverNameById = new HashMap<>();
        for (Driver driver : driverController.getAllDrivers()) {
            driverNameById.put(driver.getUid(), driver.getName());
        }

        for (com.core2web.model.Booking b : dispatched) {
            rows.getChildren().add(dispatchRow(b,
                    displayName(driverNameById, b.getDriverId(), "Unknown driver"),
                    displayName(busCodeById, b.getBusId(), "Unknown bus")));
        }
    }

    /** Resolved label for an id, falling back to the raw id, then to a placeholder. */
    private static String displayName(Map<String, String> lookup, String id, String unknown) {
        if (id == null || id.isEmpty() || "null".equals(id)) {
            return unknown;
        }
        String name = lookup.get(id);
        return name == null || name.isEmpty() ? id : name;
    }


    private static HBox dispatchRow(com.core2web.model.Booking b, String driverName, String busCode) {

        HBox row = new HBox(8);
        row.setPadding(new Insets(9, 6, 9, 6));
        row.getStyleClass().add("booking-row");
        row.setAlignment(Pos.CENTER_LEFT);

        VBox info = new VBox(2);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label idLabel = new Label(b.shortRef() + (b.isEmergency() ? "  ·  EMERGENCY" : ""));
        idLabel.setStyle(
                "-fx-text-fill:" + (b.isEmergency() ? "#f87171" : "#f8fafc") + ";" +
                "-fx-font-size:12px;" +
                "-fx-font-weight:bold;");
        idLabel.setTooltip(new Tooltip("Booking ID: " + b.getId()));

        Label whoLabel = new Label(driverName + " · " + busCode
                + (b.getLocation() == null || b.getLocation().isEmpty() ? "" : " · " + b.getLocation()));
        whoLabel.getStyleClass().add("small-muted");
        whoLabel.setWrapText(true);

        info.getChildren().addAll(idLabel, whoLabel);

        String color = b.statusColor();

        row.getChildren().addAll(
                new Circle(4, Color.web(color)),
                info,
                statusBadge(b.getStatus(), color, 0),
                buildCancelButton(b));

        return row;
    }


    private static VBox buildBusListCard(Label fleetCountLabel, VBox rows) {

        VBox card =
                new VBox(10);

        card.getStyleClass().add(
                "card"
        );

        card.setPadding(
                new Insets(16)
        );

        HBox header =
                new HBox();

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        header.getChildren().addAll(

                label(
                        "Fleet list",
                        "section-title"
                ),

                spacer,

                fleetCountLabel
        );

        card.getChildren().addAll(
                header,
                rows
        );

        return card;
    }


    private static HBox busRow(
            String id,
            String location,
            String status,
            String color
    ) {

        HBox row =
                new HBox(8);

        row.setPadding(
                new Insets(
                        9,
                        6,
                        9,
                        6
                )
        );

        row.getStyleClass().add(
                "booking-row"
        );

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox info =
                new VBox(2);

        HBox.setHgrow(
                info,
                Priority.ALWAYS
        );

        Label idLabel =
                new Label(id);

        idLabel.setStyle(
                "-fx-text-fill:#f8fafc;" +
                "-fx-font-size:12px;" +
                "-fx-font-weight:bold;"
        );

        info.getChildren().addAll(
                idLabel,
                label(
                        location,
                        "small-muted"
                )
        );

        row.getChildren().addAll(

                new Circle(
                        4,
                        Color.web(color)
                ),

                info,

                statusBadge(
                        status,
                        color,
                        0
                )
        );

        return row;
    }


}

