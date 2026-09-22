package com.core2web.view; 
 
import java.util.List; 
 
import javafx.application.Platform; 
import javafx.geometry.Insets; 
import javafx.geometry.Point2D; 
import javafx.geometry.Pos; 
import javafx.scene.Scene; 
import javafx.scene.control.Button; 
import javafx.scene.control.Label; 
import javafx.scene.control.ScrollPane; 
import javafx.scene.control.Tooltip; 
import javafx.scene.effect.Glow; 
import javafx.scene.input.ScrollEvent; 
import javafx.scene.layout.HBox; 
import javafx.scene.layout.Priority; 
import javafx.scene.layout.Region; 
import javafx.scene.layout.StackPane; 
import javafx.scene.layout.VBox; 
import javafx.scene.paint.Color; 
import javafx.scene.shape.Circle; 
import javafx.scene.shape.Line; 
 
import com.core2web.controller.BookingController; 
import com.core2web.controller.BusLocationController; 
import com.core2web.model.BusLocation; 
import com.gluonhq.maps.MapLayer; 
import com.gluonhq.maps.MapView; 
 
public class LiveNavigation { 
 
    /* 
     * Gluon Maps was originally designed around older JavaFX versions. 
     * These properties make the desktop environment explicit and 
     * prevent common tile-loading/runtime problems. 
     * 
     * They are set before the first MapView is created. 
     * 
     * Do NOT remove, reorder or "clean up" this block: without it Gluon Maps 
     * silently fails to load tiles and you get a blank map with no error. 
     */ 
    static { 
        System.setProperty("javafx.platform", "desktop"); 
        System.setProperty("http.agent", "Gluon Mobile/1.0.3"); 
    } 
 
    private static final BookingController bookingController = 
            new BookingController(); 
 
    private static final BusLocationController busLocationController = 
            new BusLocationController(); 
 
    /* 
     * Tracks the current on-screen stop order. 
     * 
     * There is no skip-stop action anymore. The list simply contains the 
     * driver's current in-progress bookings, with the first one being the 
     * next stop. 
     */ 
    private final List<com.core2web.model.Booking> currentStops = 
            new java.util.ArrayList<>(); 
 
    /** How often the position is re-read. REST has no listener, so we poll. */ 
    private static final javafx.util.Duration POLL_INTERVAL = 
            javafx.util.Duration.seconds(5); 
 
    /** 
     * How often bookings are re-read. 
     * 
     * This is important because an owner can create a new booking while the 
     * driver is already on the Live Navigation page. 
     */ 
    private static final javafx.util.Duration BOOKING_POLL_INTERVAL = 
            javafx.util.Duration.seconds(5); 
 
    /** Interval between simulated waypoint writes. */ 
    private static final javafx.util.Duration SIMULATION_STEP = 
            javafx.util.Duration.seconds(3); 
 
    /** 
     * DEMO DATA ONLY — a short walk through Pune, west to east along Baner Road. 
     * Written to Firestore during "Simulate movement" so the normal poller reads it 
     * back; nothing here is drawn to the map directly. 
     */ 
    private static final double[][] DEMO_ROUTE = { 
            { 18.5590, 73.7770 }, 
            { 18.5601, 73.7834 }, 
            { 18.5614, 73.7902 }, 
            { 18.5628, 73.7968 }, 
            { 18.5639, 73.8035 }, 
            { 18.5652, 73.8101 }, 
            { 18.5663, 73.8168 }, 
    }; 
 
    /* 
     * The actual Gluon map. 
     */ 
    private MapView mapView; 
 
    /* 
     * DEMO VALUES ONLY — hardcoded for the demo, around Pune/Baner. 
     * 
     * These are used only as a fallback when there is no real driver 
     * position and no valid passenger pickup position available yet. 
     */ 
    private static final double DEMO_LATITUDE = 18.5590; 
    private static final double DEMO_LONGITUDE = 73.7868; 
 
    /* 
     * DEMO VALUES ONLY. 
     * 
     * Kept as constants for compatibility with the existing class, but they 
     * are no longer used as the passenger marker location. 
     * 
     * The passenger marker now comes from the current booking's 
     * pickupLatitude and pickupLongitude. 
     */ 
    private static final double DEMO_DESTINATION_LATITUDE = 18.5645; 
    private static final double DEMO_DESTINATION_LONGITUDE = 73.7805; 
 
    /* 
     * Initial zoom level. 
     */ 
    private static final double DEFAULT_ZOOM = 14.0; 
 
    /* 
     * Bounds used by mouse-wheel zoom. 
     */ 
    private static final double MIN_ZOOM = 3.0; 
    private static final double MAX_ZOOM = 19.0; 
 
    private Button simulateBtn; 
 
    /** Pulse on the current-location marker; stopped on teardown so it can't leak. */ 
    private javafx.animation.Timeline markerPulse; 
 
    /** Last position applied to the map. */ 
    private BusLocation lastPosition; 
 
    private javafx.animation.Timeline poller; 
 
    /** 
     * Poller for booking changes. 
     * 
     * This makes a new owner booking appear automatically on the driver's 
     * screen without requiring the driver to leave and reopen the page. 
     */ 
    private javafx.animation.Timeline bookingPoller; 
 
    private javafx.animation.Timeline simulation; 
    private int simulationStep; 
 
    Scene getNavigationScene() { 
        DriverDashboard.goTo("Live Navigation"); 
        return DriverDashboard.scene; 
    } 
 
    /** 
     * The instance currently on screen, so its timers can be stopped. 
     * 
     * <p>Each visit builds a new LiveNavigation. Without this the previous one's 
     * 5-second poller would keep hitting Firestore forever — a real leak with a 
     * real cost, not just untidiness. 
     */ 
    private static LiveNavigation current; 
 
    /** Stops the on-screen instance's poller and playback. Safe to call any time. */ 
    static void disposeCurrent() { 
        if (current != null) { 
            current.dispose(); 
            current = null; 
        } 
    } 
 
    static ScrollPane buildMainContent() { 
        disposeCurrent(); 
 
        LiveNavigation view = new LiveNavigation(); 
        current = view; 
 
        return view.buildContent(); 
    } 
 
    private ScrollPane buildContent() { 
 
        VBox content = new VBox(16); 
        content.setPadding(new Insets(16)); 
 
        /* 
         * Only added when a real pending emergency exists. 
         */ 
        HBox banner = buildEmergencyBanner(); 
 
        Label nextStop = new Label(); 
        nextStop.setStyle( 
                "-fx-text-fill:#94a3b8;-fx-font-size:12px;" 
        ); 
 
        VBox stopsList = new VBox(0); 
 
        List<com.core2web.model.Booking> bookings = 
                bookingController.getBookingsForCurrentDriver(); 
 
        populateStops( 
                nextStop, 
                stopsList, 
                bookings 
        ); 
 
        HBox body = new HBox(16); 
 
        VBox routeGuidance = 
                buildRouteGuidance( 
                        nextStop, 
                        stopsList 
                ); 
 
        HBox.setHgrow( 
                routeGuidance, 
                Priority.ALWAYS 
        ); 
 
        VBox stopsPanel = 
                buildStopsPanel( 
                        stopsList 
                ); 
 
        stopsPanel.setPrefWidth(280); 
        stopsPanel.setMinWidth(260); 
 
        body.getChildren().addAll( 
                routeGuidance, 
                stopsPanel 
        ); 
 
        if (banner != null) { 
            content.getChildren().add(banner); 
        } 
 
        content.getChildren().add(body); 
 
        ScrollPane scrollPane = 
                new ScrollPane(content); 
 
        scrollPane.setFitToWidth(false); 
        scrollPane.setFitToHeight(false); 
 
        scrollPane.getStyleClass().add( 
                "scroll-pane" 
        ); 
 
        scrollPane.setStyle( 
                "-fx-background-color: transparent;" 
        ); 
 
        return scrollPane; 
    } 
 
    private Label loadingLabel(String text) { 
 
        Label l = 
                new Label(text); 
 
        l.setStyle( 
                "-fx-text-fill:#64748b;-fx-font-size:12px;" 
        ); 
 
        return l; 
    } 
 
    private void loadStops( 
            Label nextStop, 
            VBox stopsList) { 
 
        Thread loader = 
                new Thread(() -> { 
 
                    List<com.core2web.model.Booking> bookings = 
                            bookingController.getBookingsForCurrentDriver(); 
 
                    Platform.runLater(() -> 
                            populateStops( 
                                    nextStop, 
                                    stopsList, 
                                    bookings 
                            ) 
                    ); 
                }); 
 
        loader.setDaemon(true); 
        loader.start(); 
    } 
 
    /** 
     * Refreshes the driver's bookings from Firestore. 
     * 
     * <p>This is separate from the location polling because the owner can 
     * create/change a booking while the driver is already viewing this page. 
     */ 
    private void pollBookings( 
            Label nextStop, 
            VBox stopsList) { 
 
        Thread worker = 
                new Thread(() -> { 
 
                    List<com.core2web.model.Booking> bookings = 
                            bookingController.getBookingsForCurrentDriver(); 
 
                    Platform.runLater(() -> { 
 
                        if (bookings == null) { 
                            return; 
                        } 
 
                        populateStops( 
                                nextStop, 
                                stopsList, 
                                bookings 
                        ); 
                    }); 
                }); 
 
        worker.setDaemon(true); 
        worker.start(); 
    } 
 
    private void startBookingPolling( 
            Label nextStop, 
            VBox stopsList) { 
 
        stopBookingPolling(); 
 
        bookingPoller = 
                new javafx.animation.Timeline( 
                        new javafx.animation.KeyFrame( 
                                BOOKING_POLL_INTERVAL, 
                                e -> pollBookings( 
                                        nextStop, 
                                        stopsList 
                                ) 
                        ) 
                ); 
 
        bookingPoller.setCycleCount( 
                javafx.animation.Animation.INDEFINITE 
        ); 
 
        bookingPoller.play(); 
    } 
 
    private void stopBookingPolling() { 
 
        if (bookingPoller != null) { 
 
            bookingPoller.stop(); 
            bookingPoller = null; 
        } 
    } 
 
    private void populateStops( 
            Label nextStop, 
            VBox stopsList, 
            List<com.core2web.model.Booking> bookings) { 
 
        currentStops.clear(); 
 
        if (bookings != null) { 
 
            for (com.core2web.model.Booking b : bookings) { 
 
                if (b != null && b.isInProgress()) { 
                    currentStops.add(b); 
                } 
            } 
        } 
 
        renderStops( 
                nextStop, 
                stopsList 
        ); 
 
        /* 
         * A booking can change while the map is already visible. 
         * 
         * Therefore the map must immediately recalculate its marker, 
         * route and viewport. 
         */ 
        if (mapView != null) { 
 
            mapView.requestLayout(); 
 
            Platform.runLater(() -> { 
 
                if (mapView != null) { 
                    fitMapToCurrentPoints(); 
                    mapView.requestLayout(); 
                } 
            }); 
        } 
    } 
 
    /** 
     * Redraws the stops panel from currentStops. 
     */ 
    private void renderStops( 
            Label nextStop, 
            VBox stopsList) { 
 
        stopsList.getChildren().clear(); 
 
        if (currentStops.isEmpty()) { 
 
            nextStop.setText( 
                    "No stops on this leg" 
            ); 
 
            stopsList.getChildren().add( 
                    loadingLabel( 
                            "No stops on this leg." 
                    ) 
            ); 
 
            if (mapView != null) { 
                mapView.requestLayout(); 
            } 
 
            return; 
        } 
 
        nextStop.setText( 
                "Next stop · " 
                        + currentStops.get(0).getLocation() 
        ); 
 
        for (int i = 0; i < currentStops.size(); i++) { 
 
            com.core2web.model.Booking b = 
                    currentStops.get(i); 
 
            String details = 
                    "ETA " 
                            + b.getScheduledTime() 
                            + " · " 
                            + ((int) b.getKwh()) 
                            + " kWh"; 
 
            stopsList.getChildren().add( 
                    buildStopItem( 
                            b.getLocation() 
                                    + " · #" 
                                    + b.getId(), 
                            details, 
                            b.statusColor(), 
                            i == 0 
                    ) 
            ); 
        } 
 
        /* 
         * The first stop may have changed, so refresh the passenger marker. 
         */ 
        if (mapView != null) { 
            mapView.requestLayout(); 
        } 
    } 
 
    /** 
     * The emergency diversion banner, or null when this driver has no pending 
     * emergency — callers must skip adding it entirely in that case. 
     */ 
    private HBox buildEmergencyBanner() { 
 
        com.core2web.model.Booking pending = 
                bookingController 
                        .getPendingEmergencyBookingForCurrentDriver(); 
 
        if (pending == null) { 
            return null; 
        } 
 
        return EmergencyBanner.build( 
                pending, 
                () -> DriverDashboard.goTo("Live Navigation") 
        ); 
    } 
 
    private VBox buildRouteGuidance( 
            Label nextStop, 
            VBox stopsList) { 
 
        VBox card = 
                new VBox(0); 
 
        card.getStyleClass().add( 
                "route-card" 
        ); 
 
        card.setPadding( 
                new Insets(18) 
        ); 
 
        HBox header = 
                new HBox(); 
 
        header.setAlignment( 
                Pos.CENTER_LEFT 
        ); 
 
        Label titleLabel = 
                new Label("Route guidance"); 
 
        titleLabel.setStyle( 
                "-fx-text-fill:#f8fafc;" 
                        + "-fx-font-size:18px;" 
                        + "-fx-font-weight:bold;" 
        ); 
 
        Label frRef = 
                label( 
                        "FR-DRV-06", 
                        "muted-tag" 
                ); 
 
        frRef.setPadding( 
                new Insets( 
                        0, 
                        0, 
                        0, 
                        10 
                ) 
        ); 
 
        Region hSp1 = 
                new Region(); 
 
        HBox.setHgrow( 
                hSp1, 
                Priority.ALWAYS 
        ); 
 
        header.getChildren().addAll( 
                titleLabel, 
                frRef, 
                hSp1, 
                nextStop 
        ); 
 
        header.setPadding( 
                new Insets( 
                        0, 
                        0, 
                        14, 
                        0 
                ) 
        ); 
 
        /* 
         * Real position for this driver's own bus. 
         * 
         * Null/no-fix is a first-class state. 
         */ 
        BusLocation myPosition = 
                busLocationController 
                        .getLocationForCurrentDriver(); 
 
        /* 
         * Store the actual Firestore position. 
         */ 
        applyPosition( 
                myPosition 
        ); 
 
        Label mapHint = 
                new Label( 
                        "Scroll over the map to zoom · live driver and booking route" 
                ); 
 
        mapHint.setStyle( 
                "-fx-text-fill:#94a3b8;" 
                        + "-fx-font-size:11px;" 
                        + "-fx-background-color:#0f172ecc;" 
                        + "-fx-padding:4 8 4 8;" 
                        + "-fx-background-radius:4;" 
        ); 
 
        /* 
         * The Gluon map replaces the placeholder panel. 
         */ 
        StackPane mapArea = 
                buildMapCanvas(); 
 
        VBox.setVgrow( 
                mapArea, 
                Priority.ALWAYS 
        ); 
 
        /* 
         * Start polling the real driver location. 
         */ 
        startPolling(); 
 
        /* 
         * Start polling bookings so new owner bookings automatically 
         * appear on the driver's map. 
         */ 
        startBookingPolling( 
                nextStop, 
                stopsList 
        ); 
 
        StackPane mapStack = 
                new StackPane(); 
 
        mapStack.getChildren().addAll( 
                mapArea, 
                mapHint 
        ); 
 
        StackPane.setAlignment( 
                mapHint, 
                Pos.BOTTOM_LEFT 
        ); 
 
        StackPane.setMargin( 
                mapHint, 
                new Insets( 
                        0, 
                        0, 
                        10, 
                        10 
                ) 
        ); 
 
        VBox.setVgrow( 
                mapStack, 
                Priority.ALWAYS 
        ); 
 
        mapStack.setMinHeight( 
                500 
        ); 
 
        /* 
         * IMPORTANT: 
         * 
         * The old bottom action panel containing: 
         * 
         * - YOUR POSITION 
         * - navigation note 
         * - Skip stop 
         * - Confirm arrival 
         * 
         * has intentionally been removed completely. 
         * 
         * The route card now ends directly after the map. 
         */ 
        card.getChildren().addAll( 
                header, 
                mapStack 
        ); 
 
        return card; 
    } 
 
    /** 
     * Builds the Gluon map. 
     * 
     * <p>The initial map position prefers the driver's real Firestore position. 
     * If no driver position exists, it uses the next passenger pickup position. 
     * The demo coordinate is only the final fallback. 
     */ 
    private StackPane buildMapCanvas() { 
 
        mapView = 
                new MapView(); 
 
        /* 
         * Determine initial map center. 
         * 
         * Priority: 
         * 
         * 1. Real driver position 
         * 2. Next passenger pickup 
         * 3. Demo fallback 
         */ 
        double initialLatitude = 
                DEMO_LATITUDE; 
 
        double initialLongitude = 
                DEMO_LONGITUDE; 
 
        if (lastPosition != null 
                && lastPosition.hasCoordinates()) { 
 
            initialLatitude = 
                    lastPosition.getLatitude(); 
 
            initialLongitude = 
                    lastPosition.getLongitude(); 
 
        } else if (!currentStops.isEmpty()) { 
 
            com.core2web.model.Booking nextBooking = 
                    currentStops.get(0); 
 
            double pickupLatitude = 
                    nextBooking.getPickupLatitude(); 
 
            double pickupLongitude = 
                    nextBooking.getPickupLongitude(); 
 
            if (isValidCoordinate( 
                    pickupLatitude, 
                    pickupLongitude)) { 
 
                initialLatitude = 
                        pickupLatitude; 
 
                initialLongitude = 
                        pickupLongitude; 
            } 
        } 
 
        mapView.setCenter( 
                initialLatitude, 
                initialLongitude 
        ); 
 
        /* 
         * Initial zoom. 
         */ 
        mapView.setZoom( 
                DEFAULT_ZOOM 
        ); 
 
        /* 
         * Map should fill available space. 
         */ 
        mapView.setPrefSize( 
                550, 
                500 
        ); 
 
        mapView.setMinHeight( 
                500 
        ); 
 
        mapView.setMaxSize( 
                Double.MAX_VALUE, 
                Double.MAX_VALUE 
        ); 
 
        /* 
         * Add our marker and route layer. 
         */ 
        mapView.addLayer( 
                createNavigationLayer() 
        ); 
 
        /* 
         * Mouse-wheel zoom. 
         * 
         * Because the map is inside the outer ScrollPane, 
         * the ScrollPane can otherwise receive the wheel event. 
         * 
         * Scrolling UP   -> zoom in 
         * Scrolling DOWN -> zoom out 
         */ 
        mapView.addEventFilter( 
                ScrollEvent.SCROLL, 
                event -> { 
 
                    if (event.getDeltaY() > 0) { 
 
                        zoomIn(); 
 
                    } else if (event.getDeltaY() < 0) { 
 
                        zoomOut(); 
                    } 
 
                    /* 
                     * Prevent the outer ScrollPane from scrolling when 
                     * the pointer is over the actual map. 
                     */ 
                    event.consume(); 
                } 
        ); 
 
        /* 
         * Wrapper keeps the existing CSS class from the UI. 
         */ 
        StackPane wrapper = 
                new StackPane(mapView); 
 
        wrapper.getStyleClass().add( 
                "map-container" 
        ); 
 
        wrapper.setMinHeight( 
                500 
        ); 
 
        /* 
         * Once the MapView has its real size, fit the current points. 
         */ 
        Platform.runLater(() -> { 
 
            if (mapView != null) { 
                fitMapToCurrentPoints(); 
                mapView.requestLayout(); 
            } 
        }); 
 
        return wrapper; 
    } 
 
    /** 
     * Creates the map overlay layer holding: 
     * 
     * 1. Blue marker = current driver's real location 
     * 2. Green marker = next passenger/owner pickup location 
     * 3. Route line = driver to current passenger/owner pickup 
     * 
     * The locations are NOT hardcoded. 
     */ 
    private MapLayer createNavigationLayer() { 
 
        return new MapLayer() { 
 
            /* 
             * Current driver's real location. 
             */ 
            private final Circle currentLocation = 
                    new Circle( 
                            8, 
                            Color.web("#3b82f6") 
                    ); 
 
            /* 
             * Next passenger/owner pickup location. 
             */ 
            private final Circle destination = 
                    new Circle( 
                            8, 
                            Color.web("#10b981") 
                    ); 
 
            /* 
             * Route between driver and current pickup. 
             */ 
            private final Line routeLine = 
                    new Line(); 
 
            { 
                /* 
                 * Route line goes behind the markers. 
                 */ 
                routeLine.setStroke( 
                        Color.web("#60a5fa") 
                ); 
 
                routeLine.setStrokeWidth( 
                        4 
                ); 
 
                routeLine.setOpacity( 
                        0.85 
                ); 
 
                routeLine.setMouseTransparent( 
                        true 
                ); 
 
                getChildren().add( 
                        routeLine 
                ); 
 
                /* 
                 * Driver marker. 
                 */ 
                getChildren().add( 
                        currentLocation 
                ); 
 
                /* 
                 * Passenger pickup marker. 
                 */ 
                getChildren().add( 
                        destination 
                ); 
 
                /* 
                 * Subtle glow and pulse on driver's marker. 
                 */ 
                currentLocation.setEffect( 
                        new Glow(0.6) 
                ); 
 
                startMarkerPulse( 
                        currentLocation 
                ); 
            } 
 
            @Override 
            protected void layoutLayer() { 
 
                super.layoutLayer(); 
 
                if (mapView == null) { 
                    return; 
                } 
 
                /* 
                 * ===================================================== 
                 * DRIVER'S REAL LOCATION 
                 * ===================================================== 
                 * 
                 * This comes from: 
                 * 
                 * Firestore 
                 *      ↓ 
                 * BusLocationController 
                 *      ↓ 
                 * lastPosition 
                 *      ↓ 
                 * blue marker 
                 */ 
                boolean driverVisible = 
                        lastPosition != null 
                                && lastPosition.hasCoordinates(); 
 
                Point2D currentPoint = null; 
 
                if (driverVisible) { 
 
                    currentPoint = 
                            getMapPoint( 
                                    lastPosition.getLatitude(), 
                                    lastPosition.getLongitude() 
                            ); 
 
                    currentLocation.setVisible( 
                            true 
                    ); 
 
                    currentLocation.setCenterX( 
                            currentPoint.getX() 
                    ); 
 
                    currentLocation.setCenterY( 
                            currentPoint.getY() 
                    ); 
 
                } else { 
 
                    currentLocation.setVisible( 
                            false 
                    ); 
                } 
 
                /* 
                 * ===================================================== 
                 * NEXT PASSENGER / OWNER PICKUP LOCATION 
                 * ===================================================== 
                 * 
                 * This comes directly from the booking: 
                 * 
                 * booking.getPickupLatitude() 
                 * booking.getPickupLongitude() 
                 * 
                 * No geocoding is performed here. 
                 */ 
                boolean destinationVisible = 
                        false; 
 
                Point2D destinationPoint = null; 
 
                if (!currentStops.isEmpty()) { 
 
                    com.core2web.model.Booking nextBooking = 
                            currentStops.get(0); 
 
                    double latitude = 
                            nextBooking.getPickupLatitude(); 
 
                    double longitude = 
                            nextBooking.getPickupLongitude(); 
 
                    /* 
                     * Validate the coordinates before displaying 
                     * the passenger marker. 
                     */ 
                    if (isValidCoordinate( 
                            latitude, 
                            longitude)) { 
 
                        destinationPoint = 
                                getMapPoint( 
                                        latitude, 
                                        longitude 
                                ); 
 
                        destination.setVisible( 
                                true 
                        ); 
 
                        destination.setCenterX( 
                                destinationPoint.getX() 
                        ); 
 
                        destination.setCenterY( 
                                destinationPoint.getY() 
                        ); 
 
                        destinationVisible = true; 
 
                    } else { 
 
                        destination.setVisible( 
                                false 
                        ); 
                    } 
 
                } else { 
 
                    destination.setVisible( 
                            false 
                    ); 
                } 
 
                /* 
                 * ===================================================== 
                 * ROUTE LINE 
                 * ===================================================== 
                 * 
                 * The route currently represents the active leg: 
                 * 
                 * DRIVER 
                 *    ↓ 
                 * CURRENT BOOKING PICKUP 
                 * 
                 * It is recalculated whenever either point changes. 
                 */ 
                if (driverVisible 
                        && destinationVisible 
                        && currentPoint != null 
                        && destinationPoint != null) { 
 
                    routeLine.setVisible( 
                            true 
                    ); 
 
                    routeLine.setStartX( 
                            currentPoint.getX() 
                    ); 
 
                    routeLine.setStartY( 
                            currentPoint.getY() 
                    ); 
 
                    routeLine.setEndX( 
                            destinationPoint.getX() 
                    ); 
 
                    routeLine.setEndY( 
                            destinationPoint.getY() 
                    ); 
 
                } else { 
 
                    routeLine.setVisible( 
                            false 
                    ); 
                } 
            } 
        }; 
    } 
 
    /** 
     * Checks whether a latitude/longitude pair is usable. 
     */ 
    private static boolean isValidCoordinate( 
            double latitude, 
            double longitude) { 
 
        return latitude >= -90 
                && latitude <= 90 
                && longitude >= -180 
                && longitude <= 180 
                && !(latitude == 0.0 
                        && longitude == 0.0); 
    } 
 
    /** 
     * Automatically adjusts the map so the driver's position and the current 
     * booking pickup are both visible as much as possible. 
     * 
     * <p>This does NOT use any hardcoded owner coordinates. 
     * 
     * <p>When there is only one valid point, the map centers on that point. 
     * 
     * <p>When both points exist, the map centers between them and chooses a 
     * zoom level based on the distance between the two coordinates. 
     */ 
    private void fitMapToCurrentPoints() { 
 
        if (mapView == null) { 
            return; 
        } 
 
        boolean hasDriver = 
                lastPosition != null 
                        && lastPosition.hasCoordinates(); 
 
        boolean hasPickup = false; 
 
        double pickupLatitude = 0; 
        double pickupLongitude = 0; 
 
        if (!currentStops.isEmpty()) { 
 
            com.core2web.model.Booking nextBooking = 
                    currentStops.get(0); 
 
            pickupLatitude = 
                    nextBooking.getPickupLatitude(); 
 
            pickupLongitude = 
                    nextBooking.getPickupLongitude(); 
 
            hasPickup = 
                    isValidCoordinate( 
                            pickupLatitude, 
                            pickupLongitude 
                    ); 
        } 
 
        /* 
         * No usable coordinates. 
         */ 
        if (!hasDriver && !hasPickup) { 
            return; 
        } 
 
        /* 
         * Driver only. 
         */ 
        if (hasDriver && !hasPickup) { 
 
            mapView.setCenter( 
                    lastPosition.getLatitude(), 
                    lastPosition.getLongitude() 
            ); 
 
            mapView.setZoom( 
                    DEFAULT_ZOOM 
            ); 
 
            return; 
        } 
 
        /* 
         * Pickup only. 
         */ 
        if (!hasDriver && hasPickup) { 
 
            mapView.setCenter( 
                    pickupLatitude, 
                    pickupLongitude 
            ); 
 
            mapView.setZoom( 
                    DEFAULT_ZOOM 
            ); 
 
            return; 
        } 
 
        /* 
         * Both driver and pickup exist. 
         */ 
        double driverLatitude = 
                lastPosition.getLatitude(); 
 
        double driverLongitude = 
                lastPosition.getLongitude(); 
 
        /* 
         * Calculate the midpoint. 
         */ 
        double centerLatitude = 
                (driverLatitude + pickupLatitude) / 2.0; 
 
        double centerLongitude = 
                (driverLongitude + pickupLongitude) / 2.0; 
 
        mapView.setCenter( 
                centerLatitude, 
                centerLongitude 
        ); 
 
        /* 
         * Calculate coordinate span. 
         */ 
        double latitudeSpan = 
                Math.abs( 
                        driverLatitude 
                                - pickupLatitude 
                ); 
 
        double longitudeSpan = 
                Math.abs( 
                        driverLongitude 
                                - pickupLongitude 
                ); 
 
        double largestSpan = 
                Math.max( 
                        latitudeSpan, 
                        longitudeSpan 
                ); 
 
        /* 
         * Add visual padding around both points. 
         */ 
        largestSpan *= 1.5; 
 
        /* 
         * Select a suitable zoom level. 
         * 
         * The values are intentionally conservative so that both markers 
         * have some space around them instead of being placed at the edges. 
         */ 
        double fittingZoom; 
 
        if (largestSpan <= 0.002) { 
 
            fittingZoom = 17.0; 
 
        } else if (largestSpan <= 0.005) { 
 
            fittingZoom = 16.0; 
 
        } else if (largestSpan <= 0.01) { 
 
            fittingZoom = 15.0; 
 
        } else if (largestSpan <= 0.02) { 
 
            fittingZoom = 14.0; 
 
        } else if (largestSpan <= 0.05) { 
 
            fittingZoom = 13.0; 
 
        } else if (largestSpan <= 0.10) { 
 
            fittingZoom = 12.0; 
 
        } else if (largestSpan <= 0.25) { 
 
            fittingZoom = 11.0; 
 
        } else if (largestSpan <= 0.50) { 
 
            fittingZoom = 10.0; 
 
        } else if (largestSpan <= 1.0) { 
 
            fittingZoom = 9.0; 
 
        } else { 
 
            fittingZoom = 8.0; 
        } 
 
        fittingZoom = 
                Math.max( 
                        MIN_ZOOM, 
                        Math.min( 
                                fittingZoom, 
                                MAX_ZOOM 
                        ) 
                ); 
 
        mapView.setZoom( 
                fittingZoom 
        ); 
 
        mapView.requestLayout(); 
    } 
 
    /** 
     * Gentle pulse on the current-location marker. 
     * 
     * <p>Radius is left alone and only opacity animates: animating the radius 
     * would fight {@code layoutLayer}, which repositions the circle on every pan 
     * and zoom. 
     */ 
    private void startMarkerPulse( 
            Circle marker) { 
 
        stopMarkerPulse(); 
 
        markerPulse = 
                new javafx.animation.Timeline( 
 
                        new javafx.animation.KeyFrame( 
                                javafx.util.Duration.ZERO, 
                                new javafx.animation.KeyValue( 
                                        marker.opacityProperty(), 
                                        1.0 
                                ) 
                        ), 
 
                        new javafx.animation.KeyFrame( 
                                javafx.util.Duration.seconds(1.2), 
                                new javafx.animation.KeyValue( 
                                        marker.opacityProperty(), 
                                        0.45 
                                ) 
                        ) 
                ); 
 
        markerPulse.setCycleCount( 
                javafx.animation.Animation.INDEFINITE 
        ); 
 
        markerPulse.setAutoReverse( 
                true 
        ); 
 
        markerPulse.play(); 
    } 
 
    private void stopMarkerPulse() { 
 
        if (markerPulse != null) { 
 
            markerPulse.stop(); 
            markerPulse = null; 
        } 
    } 
 
    /** 
     * Zoom in one level. 
     * 
     * This method is still used by the mouse-wheel zoom. 
     */ 
    private void zoomIn() { 
 
        if (mapView == null) { 
            return; 
        } 
 
        double currentZoom = 
                mapView.getZoom(); 
 
        double newZoom = 
                Math.min( 
                        currentZoom + 1.0, 
                        MAX_ZOOM 
                ); 
 
        mapView.setZoom( 
                newZoom 
        ); 
    } 
 
    /** 
     * Zoom out one level. 
     * 
     * This method is still used by the mouse-wheel zoom. 
     */ 
    private void zoomOut() { 
 
        if (mapView == null) { 
            return; 
        } 
 
        double currentZoom = 
                mapView.getZoom(); 
 
        double newZoom = 
                Math.max( 
                        currentZoom - 1.0, 
                        MIN_ZOOM 
                ); 
 
        mapView.setZoom( 
                newZoom 
        ); 
    } 
 
    /** 
     * Re-center map. 
     * 
     * Priority: 
     * 
     * 1. Current driver's real location 
     * 2. Next passenger pickup 
     */ 
    private void recenterMap() { 
 
        if (mapView == null) { 
            return; 
        } 
 
        /* 
         * If both points exist, fit both instead of centering only on 
         * the driver. 
         */ 
        if (lastPosition != null 
                && lastPosition.hasCoordinates() 
                && !currentStops.isEmpty()) { 
 
            com.core2web.model.Booking nextBooking = 
                    currentStops.get(0); 
 
            double latitude = 
                    nextBooking.getPickupLatitude(); 
 
            double longitude = 
                    nextBooking.getPickupLongitude(); 
 
            if (isValidCoordinate( 
                    latitude, 
                    longitude)) { 
 
                fitMapToCurrentPoints(); 
                return; 
            } 
        } 
 
        /* 
         * First preference: 
         * driver's real Firestore position. 
         */ 
        if (lastPosition != null 
                && lastPosition.hasCoordinates()) { 
 
            mapView.setCenter( 
                    lastPosition.getLatitude(), 
                    lastPosition.getLongitude() 
            ); 
 
            mapView.setZoom( 
                    DEFAULT_ZOOM 
            ); 
 
            return; 
        } 
 
        /* 
         * Second preference: 
         * next passenger pickup. 
         */ 
        if (!currentStops.isEmpty()) { 
 
            com.core2web.model.Booking nextBooking = 
                    currentStops.get(0); 
 
            double latitude = 
                    nextBooking.getPickupLatitude(); 
 
            double longitude = 
                    nextBooking.getPickupLongitude(); 
 
            if (isValidCoordinate( 
                    latitude, 
                    longitude)) { 
 
                mapView.setCenter( 
                        latitude, 
                        longitude 
                ); 
 
                mapView.setZoom( 
                        DEFAULT_ZOOM 
                ); 
            } 
        } 
    } 
 
    /** 
     * Stores the latest real driver position. 
     * 
     * <p>The actual marker movement happens in the MapLayer's layoutLayer(). 
     */ 
    private void applyPosition( 
            BusLocation position) { 
 
        lastPosition = 
                position; 
 
        /* 
         * Immediately request a map layout so the blue marker uses 
         * the newly received Firestore position. 
         */ 
        if (mapView != null) { 
            mapView.requestLayout(); 
        } 
    } 
 
    /** 
     * Re-reads the position every few seconds. 
     * 
     * <p>Firestore REST has no snapshot listener — that only exists in the client 
     * SDKs, which this project doesn't use — so polling is the only option. 
     * 
     * <p>The read happens off the FX thread and the update is marshalled back onto it. 
     */ 
    private void startPolling() { 
 
        stopPolling(); 
 
        poller = 
                new javafx.animation.Timeline( 
                        new javafx.animation.KeyFrame( 
                                POLL_INTERVAL, 
                                e -> pollOnce() 
                        ) 
                ); 
 
        poller.setCycleCount( 
                javafx.animation.Animation.INDEFINITE 
        ); 
 
        poller.play(); 
    } 
 
    private void pollOnce() { 
 
        Thread worker = 
                new Thread(() -> { 
 
                    BusLocation fresh = 
                            busLocationController 
                                    .getLocationForCurrentDriver(); 
 
                    Platform.runLater(() -> { 
 
                        if (isUnchanged(fresh)) { 
                            return; 
                        } 
 
                        applyPosition( 
                                fresh 
                        ); 
 
                        /* 
                         * Re-fit the map whenever the driver's actual 
                         * position changes. 
                         */ 
                        fitMapToCurrentPoints(); 
 
                        /* 
                         * Force the Gluon marker layer to recalculate 
                         * the marker's screen position. 
                         */ 
                        if (mapView != null) { 
                            mapView.requestLayout(); 
                        } 
                    }); 
                }); 
 
        worker.setDaemon(true); 
        worker.start(); 
    } 
 
    private boolean isUnchanged( 
            BusLocation fresh) { 
 
        if (fresh == null 
                || lastPosition == null) { 
 
            return false; 
        } 
 
        return Double.compare( 
                fresh.getLatitude(), 
                lastPosition.getLatitude() 
        ) == 0 
                && Double.compare( 
                        fresh.getLongitude(), 
                        lastPosition.getLongitude() 
                ) == 0; 
    } 
 
    /** Stops the poller. */ 
    private void stopPolling() { 
 
        if (poller != null) { 
 
            poller.stop(); 
            poller = null; 
        } 
    } 
 
    /** 
     * Starts or stops demo playback. 
     * 
     * <p>Writes only to Firestore, never directly to the map. 
     * The poller reads the updated position and moves the blue marker. 
     */ 
    private void toggleSimulation() { 
 
        if (simulation != null) { 
 
            stopSimulation(); 
            return; 
        } 
 
        simulationStep = 0; 
 
        simulateBtn.setText( 
                "Stop simulation" 
        ); 
 
        simulation = 
                new javafx.animation.Timeline( 
                        new javafx.animation.KeyFrame( 
                                SIMULATION_STEP, 
                                e -> { 
 
                                    if (simulationStep >= 
                                            DEMO_ROUTE.length) { 
 
                                        stopSimulation(); 
                                        return; 
                                    } 
 
                                    double[] point = 
                                            DEMO_ROUTE[ 
                                                    simulationStep++ 
                                            ]; 
 
                                    Thread writer = 
                                            new Thread( 
                                                    () -> 
                                                            busLocationController 
                                                                    .updateCurrentDriverLocation( 
                                                                            point[0], 
                                                                            point[1], 
                                                                            BusLocation.SOURCE_SIMULATED 
                                                                    ) 
                                            ); 
 
                                    writer.setDaemon(true); 
                                    writer.start(); 
                                } 
                        ) 
                ); 
 
        simulation.setCycleCount( 
                javafx.animation.Animation.INDEFINITE 
        ); 
 
        simulation.play(); 
    } 
 
    private void stopSimulation() { 
 
        if (simulation != null) { 
 
            simulation.stop(); 
            simulation = null; 
        } 
 
        if (simulateBtn != null) { 
 
            simulateBtn.setText( 
                    "Simulate movement (demo)" 
            ); 
        } 
    } 
 
    /** 
     * Releases the poller, booking poller, playback timer and marker animation. 
     * 
     * <p>Must be called when navigating away from this screen. 
     */ 
    void dispose() { 
 
        stopPolling(); 
        stopBookingPolling(); 
        stopSimulation(); 
        stopMarkerPulse(); 
    } 
 
    /** 
     * Asks for a "lat, lng" pair. 
     * Needed for the very first fix, when there is no viewport to interpret 
     * a map click against. 
     */ 
    private void promptForPosition( 
            Label nextStop, 
            VBox stopsList) { 
 
        javafx.scene.control.TextInputDialog dialog = 
                new javafx.scene.control.TextInputDialog(); 
 
        dialog.setTitle( 
                "Set my position" 
        ); 
 
        dialog.setHeaderText( 
                "Enter your current position as \"latitude, longitude\"." 
        ); 
 
        dialog.setContentText( 
                "Position:" 
        ); 
 
        dialog.showAndWait().ifPresent( 
                text -> { 
 
                    double[] point = 
                            parseLatLng(text); 
 
                    if (point == null) { 
 
                        showPositionAlert( 
                                javafx.scene.control.Alert.AlertType.WARNING, 
                                "That doesn't look like a coordinate pair. " 
                                        + "Expected something like " 
                                        + "\"18.5204, 73.8567\"." 
                        ); 
 
                        return; 
                    } 
 
                    pushPosition( 
                            point[0], 
                            point[1], 
                            nextStop, 
                            stopsList 
                    ); 
                } 
        ); 
    } 
 
    /** 
     * Sends a manually chosen position for this driver's own bus. 
     * 
     * The controller refuses if the caller isn't the assigned driver, 
     * so a failure here is reported rather than silently ignored. 
     */ 
    private void pushPosition( 
            double lat, 
            double lng, 
            Label nextStop, 
            VBox stopsList) { 
 
        Thread worker = 
                new Thread(() -> { 
 
                    boolean ok = 
                            busLocationController 
                                    .updateCurrentDriverLocation( 
                                            lat, 
                                            lng, 
                                            com.core2web.model.BusLocation.SOURCE_MANUAL 
                                    ); 
 
                    Platform.runLater(() -> { 
 
                        if (ok) { 
 
                            /* 
                             * Re-enter the screen so the marker reflects 
                             * what was actually stored in Firestore. 
                             */ 
                            DriverDashboard.goTo( 
                                    "Live Navigation" 
                            ); 
 
                        } else { 
 
                            showPositionAlert( 
                                    javafx.scene.control.Alert.AlertType.ERROR, 
                                    "Couldn't update your position. " 
                                            + "You can only set the position " 
                                            + "of a bus you're assigned to." 
                            ); 
                        } 
                    }); 
                }); 
 
        worker.setDaemon(true); 
        worker.start(); 
    } 
 
    private void showPositionAlert( 
            javafx.scene.control.Alert.AlertType type, 
            String message) { 
 
        javafx.scene.control.Alert alert = 
                new javafx.scene.control.Alert(type); 
 
        alert.setHeaderText( 
                null 
        ); 
 
        alert.setContentText( 
                message 
        ); 
 
        alert.showAndWait(); 
    } 
 
    /** 
     * Parses "lat, lng", rejecting out-of-range values. 
     * Null when not a valid pair. 
     */ 
    private static double[] parseLatLng( 
            String text) { 
 
        if (text == null) { 
            return null; 
        } 
 
        java.util.regex.Matcher m = 
                java.util.regex.Pattern 
                        .compile( 
                                "(-?\\d{1,3}(?:\\.\\d+)?)\\s*,\\s*(-?\\d{1,3}(?:\\.\\d+)?)" 
                        ) 
                        .matcher(text); 
 
        if (!m.find()) { 
            return null; 
        } 
 
        double lat = 
                Double.parseDouble( 
                        m.group(1) 
                ); 
 
        double lng = 
                Double.parseDouble( 
                        m.group(2) 
                ); 
 
        if (lat < -90 
                || lat > 90 
                || lng < -180 
                || lng > 180) { 
 
            return null; 
        } 
 
        return new double[] { 
                lat, 
                lng 
        }; 
    } 
 
    private VBox buildStopsPanel( 
            VBox stopsList) { 
 
        VBox panel = 
                new VBox(12); 
 
        panel.getStyleClass().add( 
                "stops-panel" 
        ); 
 
        panel.setPadding( 
                new Insets(18) 
        ); 
 
        Label titleLbl = 
                new Label( 
                        "Stops on this leg" 
                ); 
 
        titleLbl.setStyle( 
                "-fx-text-fill:#f8fafc;" 
                        + "-fx-font-size:16px;" 
                        + "-fx-font-weight:bold;" 
        ); 
 
        panel.getChildren().addAll( 
                titleLbl, 
                stopsList 
        ); 
 
        return panel; 
    } 
 
    private HBox buildStopItem( 
            String name, 
            String details, 
            String dotColor, 
            boolean isNext) { 
 
        HBox item = 
                new HBox(12); 
 
        item.setPadding( 
                new Insets( 
                        12, 
                        14, 
                        12, 
                        14 
                ) 
        ); 
 
        item.setAlignment( 
                Pos.TOP_LEFT 
        ); 
 
        item.getStyleClass().add( 
                "stop-item" 
        ); 
 
        Circle dot = 
                new Circle( 
                        6, 
                        Color.web(dotColor) 
                ); 
 
        VBox dotContainer = 
                new VBox(); 
 
        dotContainer.setAlignment( 
                Pos.TOP_CENTER 
        ); 
 
        dotContainer.setPadding( 
                new Insets( 
                        4, 
                        0, 
                        0, 
                        0 
                ) 
        ); 
 
        dotContainer.getChildren().add( 
                dot 
        ); 
 
        VBox textBox = 
                new VBox(3); 
 
        HBox.setHgrow( 
                textBox, 
                Priority.ALWAYS 
        ); 
 
        Label nameLabel = 
                new Label(name); 
 
        nameLabel.setWrapText( 
                true 
        ); 
 
        if (isNext) { 
 
            nameLabel.setStyle( 
                    "-fx-text-fill:#f8fafc;" 
                            + "-fx-font-size:13px;" 
                            + "-fx-font-weight:bold;" 
            ); 
 
        } else { 
 
            nameLabel.setStyle( 
                    "-fx-text-fill:#cbd5e1;" 
                            + "-fx-font-size:13px;" 
                            + "-fx-font-weight:bold;" 
            ); 
        } 
 
        Label detailLabel = 
                new Label(details); 
 
        detailLabel.setWrapText( 
                true 
        ); 
 
        detailLabel.setStyle( 
                "-fx-text-fill:#64748b;" 
                        + "-fx-font-size:11px;" 
        ); 
 
        textBox.getChildren().addAll( 
                nameLabel, 
                detailLabel 
        ); 
 
        item.getChildren().addAll( 
                dotContainer, 
                textBox 
        ); 
 
        return item; 
    } 
 
    private Label label( 
            String text, 
            String styleClass) { 
 
        Label l = 
                new Label(text); 
 
        l.getStyleClass().add( 
                styleClass 
        ); 
 
        return l; 
    } 
} 
