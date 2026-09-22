package com.core2web.view;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import com.core2web.controller.BookingController;
import com.core2web.controller.OwnerController;
import com.core2web.model.Booking;
import com.core2web.model.Owner;

/**
 * The driver's accept/reject banner for a pending emergency diversion.
 *
 * <p>Shared by the driver dashboard and the driver bookings page so the accept and
 * reject wiring exists once. Callers only build this when
 * {@link BookingController#getPendingEmergencyBookingForCurrentDriver()} returned a
 * booking; when there is none the banner is never added to the layout at all.
 */
final class EmergencyBanner {

    private static final BookingController bookingController = new BookingController();
    private static final OwnerController ownerController = new OwnerController();

    private EmergencyBanner() {
    }

    /**
     * @param pending    the emergency booking awaiting this driver's answer
     * @param onResolved run on the FX thread once accept or reject succeeded, so the
     *                   host view can re-read bookings and drop the banner
     */
    static HBox build(Booking pending, Runnable onResolved) {

        HBox banner = new HBox(14);
        banner.setPadding(new Insets(14, 18, 14, 18));
        banner.getStyleClass().add("emergency-banner");
        banner.setAlignment(Pos.CENTER_LEFT);

        StackPane icon = new StackPane(new Circle(14, Color.web("#f87171")),
                styled(new Label("!"), "-fx-text-fill:white;-fx-font-weight:bold;-fx-font-size:14px;"));

        VBox text = new VBox(3);
        text.getChildren().addAll(
                label("Emergency booking · " + pending.shortRef() + " diverted to your route",
                        "emergency-title"),
                label(subtitle(pending), "emergency-subtitle"));
        HBox.setHgrow(text, Priority.ALWAYS);

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        Label status = new Label();
        status.setStyle("-fx-text-fill:#f87171;-fx-font-size:11px;");
        status.setVisible(false);
        status.setManaged(false);

        Button reject = new Button("Reject");
        reject.getStyleClass().add("secondary-btn");
        Button accept = new Button("Accept diversion");
        accept.getStyleClass().add("danger-btn");

        // Accept reuses startTrip(), the single place EN_ROUTE is set.
        accept.setOnAction(e -> run(
                () -> bookingController.startTrip(pending.getId()),
                "Couldn't accept the diversion. Please try again.",
                accept, reject, status, onResolved));

        reject.setOnAction(e -> run(
                () -> bookingController.rejectEmergency(pending.getId()),
                "Couldn't reject the diversion. Please try again.",
                accept, reject, status, onResolved));

        banner.getChildren().addAll(icon, text, sp, status, reject, accept);
        return banner;
    }

    private static void run(java.util.concurrent.Callable<Boolean> action, String failureMessage,
            Button accept, Button reject, Label status, Runnable onResolved) {

        accept.setDisable(true);
        reject.setDisable(true);
        status.setVisible(false);
        status.setManaged(false);

        Thread worker = new Thread(() -> {
            boolean ok;
            try {
                ok = Boolean.TRUE.equals(action.call());
            } catch (Exception ex) {
                ex.printStackTrace();
                ok = false;
            }

            final boolean succeeded = ok;
            Platform.runLater(() -> {
                if (succeeded) {
                    onResolved.run();
                } else {
                    accept.setDisable(false);
                    reject.setDisable(false);
                    status.setText(failureMessage);
                    status.setVisible(true);
                    status.setManaged(true);
                }
            });
        });
        worker.setDaemon(true);
        worker.start();
    }

    /** Real pickup location, energy and requester for this booking. */
    private static String subtitle(Booking pending) {
        StringBuilder sb = new StringBuilder();

        String location = pending.getLocation();
        sb.append(location == null || location.isEmpty() ? "Pickup location not given" : location);

        sb.append(" · ").append((int) pending.getKwh()).append(" kWh");

        Owner requester = ownerController.getOwner(pending.getOwnerId());
        if (requester != null && requester.getName() != null && !requester.getName().isEmpty()) {
            sb.append(" · requested by ").append(requester.getName());
            if (requester.getPhone() != null && !requester.getPhone().isEmpty()) {
                sb.append(" (").append(requester.getPhone()).append(")");
            }
        } else if (pending.getOwnerId() != null && !pending.getOwnerId().isEmpty()) {
            sb.append(" · requested by ").append(pending.getOwnerId());
        }

        return sb.toString();
    }

    private static Label label(String text, String styleClass) {
        Label l = new Label(text);
        l.getStyleClass().add(styleClass);
        l.setWrapText(true);
        return l;
    }

    private static Label styled(Label label, String style) {
        label.setStyle(style);
        return label;
    }
}
