package com.core2web.controller;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.core2web.dao.BookingDao;
import com.core2web.dao.BookingOtpDao;
import com.core2web.dao.BusDao;
import com.core2web.dao.ChargingSessionDao;
import com.core2web.dao.DriverEarningDao;
import com.core2web.dao.WalletDao;
import com.core2web.dao.DriverDao;
import com.core2web.model.AuthSession;
import com.core2web.model.Booking;
import com.core2web.model.Bus;
import com.core2web.model.ChargingSession;
import com.core2web.model.Driver;
import com.core2web.model.DriverEarning;
import com.core2web.model.Pricing;
import com.core2web.model.WalletTransaction;
import com.core2web.util.DateTimeUtil;

public class BookingController {

    private final BookingDao bookingDao =
            new BookingDao();

    private final BookingOtpDao bookingOtpDao =
            new BookingOtpDao();

    private final ChargingSessionDao sessionDao =
            new ChargingSessionDao();

    private final WalletDao walletDao =
            new WalletDao();

    private final DriverEarningDao driverEarningDao =
            new DriverEarningDao();

    private final DriverDao driverDao =
            new DriverDao();

    private final BusDao busDao =
            new BusDao();

    private final NotificationController notificationController =
            new NotificationController();

    private final DriverController driverController =
            new DriverController();

    public String createBooking(
            String vehicleId,
            String scheduledTime,
            String location,
            double kwh,
            String busId) {

        return createBooking(
                vehicleId,
                scheduledTime,
                location,
                kwh,
                busId,
                false
        );
    }

    public String createBooking(
            String vehicleId,
            String scheduledTime,
            String location,
            double kwh,
            String busId,
            boolean isEmergency) {

        return createBooking(
                vehicleId,
                scheduledTime,
                location,
                kwh,
                busId,
                isEmergency,
                0,
                0
        );
    }

    public String createBooking(
            String vehicleId,
            String scheduledTime,
            String location,
            double kwh,
            String busId,
            boolean isEmergency,
            double pickupLatitude,
            double pickupLongitude) {

        return createBooking(
                vehicleId,
                scheduledTime,
                location,
                kwh,
                busId,
                isEmergency,
                isEmergency
                        ? Booking.TYPE_EMERGENCY
                        : Booking.TYPE_INSTANT,
                pickupLatitude,
                pickupLongitude
        );
    }

    public String createBooking(
            String vehicleId,
            String scheduledTime,
            String location,
            double kwh,
            String busId,
            boolean isEmergency,
            String bookingType,
            double pickupLatitude,
            double pickupLongitude) {

        AuthSession session =
                AuthSession.getCurrent();

        if (session == null) {
            return null;
        }

        double fare =
                Pricing.fare(
                        kwh,
                        isEmergency
                );

        boolean hasBus =
                busId != null
                        && !busId.isEmpty();

        String selectedBusId =
                hasBus
                        ? busId
                        : "";

        String driverId = "";

        String status =
                Booking.STATUS_QUEUED;

        Booking booking =
                new Booking(
                        null,
                        session.getUid(),
                        vehicleId,
                        status,
                        canonicalScheduledTime(scheduledTime),
                        location,
                        selectedBusId,
                        kwh,
                        Instant.now().toString(),
                        driverId,
                        isEmergency
                );

        if (bookingType == null
                || bookingType.isEmpty()) {

            bookingType =
                    isEmergency
                            ? Booking.TYPE_EMERGENCY
                            : Booking.TYPE_INSTANT;
        }

        booking.setBookingType(
                bookingType
        );

        booking.setPickupLatitude(
                pickupLatitude
        );

        booking.setPickupLongitude(
                pickupLongitude
        );

        booking.setAmount(
                fare
        );

        booking.setServiceFee(
                Pricing.serviceFee(
                        isEmergency
                )
        );

        booking.setDriverPayout(
                Pricing.driverPayout(
                        fare
                )
        );

        String bookingId =
                bookingDao.createBooking(
                        booking,
                        session.getIdToken()
                );

        if (bookingId != null) {

            String otp =
                    BookingOtpDao.generateOtp();

            if (!bookingOtpDao.createOtp(
                    bookingId,
                    otp,
                    session.getUid(),
                    session.getIdToken())) {

                System.out.println(
                        "Warning: booking "
                                + bookingId
                                + " was created without a verification code."
                );
            }
        }

        if (bookingId != null) {

            String typeLabel;

            if (Booking.TYPE_EMERGENCY.equals(
                    bookingType)) {

                typeLabel = " · EMERGENCY";

            } else if (Booking.TYPE_RESERVE.equals(
                    bookingType)) {

                typeLabel = " · RESERVED";

            } else {

                typeLabel = " · INSTANT";
            }

            notificationController.notifyAdmins(
                    "booking_pending_dispatch",
                    "New booking "
                            + bookingId
                            + " requires driver assignment"
                            + typeLabel,
                    bookingId
            );
        }

        return bookingId;
    }

    private String resolveDriverId(
            Bus bus) {

        if (bus == null) {
            return null;
        }

        String driverId =
                bus.getAssignedDriverId();

        if (driverId == null
                || driverId.isEmpty()
                || "null".equals(driverId)) {

            return null;
        }

        return driverId;
    }

    private void notifyAssignedDriver(
            Bus bus,
            String driverId,
            String location,
            double kwh,
            String bookingId,
            boolean isEmergency) {

        notificationController.notifyUser(
                driverId,
                isEmergency
                        ? "emergency_dispatch"
                        : "booking_assigned",
                (isEmergency
                        ? "Emergency dispatch · "
                        : "New booking · ")
                        + location
                        + " · "
                        + (int) kwh
                        + " kWh · Bus "
                        + bus.getBusCode(),
                bookingId
        );
    }

    private void notifyAdminsOfDriverLink(
            String bookingId,
            String driverId,
            Bus bus,
            boolean isEmergency) {

        Driver driver =
                driverController.getDriver(
                        driverId
                );

        String driverLabel =
                driver != null
                        && driver.getName() != null
                        && !driver.getName().isEmpty()
                                ? driver.getName()
                                        + " ("
                                        + driverId
                                        + ")"
                                : driverId;

        String busLabel =
                bus == null
                        ? "unknown bus"
                        : bus.getBusCode();

        notificationController.notifyAdmins(
                isEmergency
                        ? "emergency_dispatch"
                        : "booking_assigned",
                "Booking "
                        + bookingId
                        + (isEmergency
                                ? " (emergency)"
                                : "")
                        + " assigned to "
                        + driverLabel
                        + " on "
                        + busLabel,
                bookingId
        );
    }

    public List<Booking> getAllBookings() {

        AuthSession session =
                AuthSession.getCurrent();

        if (session == null) {
            return new ArrayList<>();
        }

        return bookingDao.getAllBookings(
                session.getIdToken()
        );
    }

    public List<Booking> getDispatchQueueBookings() {

        AuthSession session =
                AuthSession.getCurrent();

        if (session == null) {
            return new ArrayList<>();
        }

        List<Booking> bookings =
                bookingDao.getAllBookings(
                        session.getIdToken()
                );

        bookings.removeIf(
                booking ->
                        !Booking.STATUS_QUEUED.equals(
                                booking.getStatus()
                        )
        );

        bookings.sort(
                BookingController::compareDispatchPriority
        );

        return bookings;
    }

    private static int compareDispatchPriority(
            Booking a,
            Booking b) {

        int aPriority =
                bookingPriority(a);

        int bPriority =
                bookingPriority(b);

        if (aPriority != bPriority) {
            return Integer.compare(
                    aPriority,
                    bPriority
            );
        }

        if (aPriority == 0
                || aPriority == 1) {

            return compareCreatedAt(
                    a,
                    b
            );
        }

        if (aPriority == 2) {

            int scheduledComparison =
                    compareScheduledTime(
                            a,
                            b
                    );

            if (scheduledComparison != 0) {
                return scheduledComparison;
            }

            return compareCreatedAt(
                    a,
                    b
            );
        }

        return compareCreatedAt(
                a,
                b
        );
    }

    private static int bookingPriority(
            Booking booking) {

        String type =
                booking.getBookingType();

        if (Booking.TYPE_EMERGENCY.equals(
                type)
                || booking.isEmergency()) {

            return 0;
        }

        if (Booking.TYPE_INSTANT.equals(
                type)) {

            return 1;
        }

        if (Booking.TYPE_RESERVE.equals(
                type)) {

            return 2;
        }

        return 3;
    }

    private static int compareCreatedAt(
            Booking a,
            Booking b) {

        String left =
                a.getCreatedAt() == null
                        ? ""
                        : a.getCreatedAt();

        String right =
                b.getCreatedAt() == null
                        ? ""
                        : b.getCreatedAt();

        return left.compareTo(
                right
        );
    }

    private static int compareScheduledTime(
            Booking a,
            Booking b) {

        LocalTime left =
                parseScheduledTime(
                        a.getScheduledTime()
                );

        LocalTime right =
                parseScheduledTime(
                        b.getScheduledTime()
                );

        if (left != null
                && right != null) {

            return left.compareTo(
                    right
            );
        }

        if (left != null) {
            return -1;
        }

        if (right != null) {
            return 1;
        }

        String leftText =
                a.getScheduledTime() == null
                        ? ""
                        : a.getScheduledTime();

        String rightText =
                b.getScheduledTime() == null
                        ? ""
                        : b.getScheduledTime();

        return leftText.compareTo(
                rightText
        );
    }

    private static LocalTime parseScheduledTime(
            String scheduledTime) {

        if (scheduledTime == null
                || scheduledTime.isEmpty()) {

            return null;
        }

        String value =
                scheduledTime.trim();

        int commaIndex =
                value.lastIndexOf(',');

        if (commaIndex >= 0) {

            String timePart =
                    value.substring(
                            commaIndex + 1
                    ).trim();

            try {

                return LocalTime.parse(
                        timePart,
                        DateTimeFormatter.ofPattern(
                                "h:mm a",
                                Locale.ENGLISH
                        )
                );

            } catch (Exception ignored) {
            }
        }

        try {

            return LocalTime.parse(
                    value,
                    DateTimeFormatter.ofPattern(
                            "h:mm a",
                            Locale.ENGLISH
                    )
            );

        } catch (Exception ignored) {
        }

        try {

            return LocalDateTime.parse(
                    value
            ).toLocalTime();

        } catch (Exception ignored) {
        }

        try {

            return LocalTime.parse(
                    value
            );

        } catch (Exception ignored) {
            return null;
        }
    }

    private static String canonicalScheduledTime(String scheduledTime) {

        Instant parsed =
                DateTimeUtil.parse(scheduledTime);

        if (parsed != null) {
            return parsed.toString();
        }

        if (scheduledTime != null
                && scheduledTime.startsWith("Today,")) {

            try {

                LocalTime time =
                        LocalTime.parse(
                                scheduledTime.substring(
                                        scheduledTime.indexOf(',') + 1
                                ).trim(),
                                DateTimeFormatter.ofPattern(
                                        "h:mm a",
                                        Locale.ENGLISH
                                )
                        );

                return java.time.ZonedDateTime
                        .now(DateTimeUtil.INDIA)
                        .toLocalDate()
                        .atTime(time)
                        .atZone(DateTimeUtil.INDIA)
                        .toInstant()
                        .toString();

            } catch (Exception ignored) {
            }
        }

        return scheduledTime == null
                ? ""
                : scheduledTime;
    }

    public boolean assignBus(
            String bookingId,
            String busId) {

        AuthSession session =
                AuthSession.getCurrent();

        if (session == null) {
            return false;
        }

        if (bookingId == null
                || bookingId.isEmpty()
                || busId == null
                || busId.isEmpty()) {

            return false;
        }

        Booking booking =
                bookingDao.getBooking(
                        bookingId,
                        session.getIdToken()
                );

        if (booking == null) {

            System.out.println(
                    "Cannot assign booking "
                            + bookingId
                            + ": booking not found."
            );

            return false;
        }

        Bus bus =
                busDao.getBus(
                        busId,
                        session.getIdToken()
                );

        if (bus == null) {

            System.out.println(
                    "Cannot assign booking "
                            + bookingId
                            + ": selected bus "
                            + busId
                            + " not found."
            );

            return false;
        }

        String driverId =
                resolveDriverId(
                        bus
                );

        if (driverId == null
                || driverId.isEmpty()) {

            System.out.println(
                    "Cannot assign booking "
                            + bookingId
                            + ": selected bus "
                            + bus.getBusCode()
                            + " has no assigned driver."
            );

            return false;
        }

        boolean ok =
                bookingDao.assignBus(
                        bookingId,
                        busId,
                        driverId,
                        booking.isEmergency(),
                        session.getIdToken()
                );

        if (!ok) {
            return false;
        }

        notifyAssignedDriver(
                bus,
                driverId,
                booking.getLocation(),
                booking.getKwh(),
                bookingId,
                booking.isEmergency()
        );

        notifyAdminsOfDriverLink(
                bookingId,
                driverId,
                bus,
                booking.isEmergency()
        );

        String assignmentStatus =
                booking.isEmergency()
                        ? Booking.STATUS_PENDING_DRIVER_ACCEPT
                        : Booking.STATUS_ASSIGNED;

        Driver assignedDriver =
                driverController.getDriver(
                        driverId
                );

        String driverName =
                assignedDriver != null
                        && assignedDriver.getName() != null
                        && !assignedDriver.getName().isEmpty()
                                ? assignedDriver.getName()
                                : driverId;

        String busCode =
                bus.getBusCode() == null
                        || bus.getBusCode().isEmpty()
                                ? bus.getId()
                                : bus.getBusCode();

        notificationController.notifyOwnerOfDriverUpdate(
                booking.getOwnerId(),
                "bus_assigned",
                "Bus " + busCode
                        + " assigned to "
                        + booking.shortRef()
                        + " | Driver: "
                        + driverName
                        + " | Pickup: "
                        + booking.getLocation()
                        + " | Scheduled: "
                        + booking.getScheduledTime()
                        + " | Status: "
                        + assignmentStatus,
                bookingId
        );

        return true;
    }

    public List<Booking> getMyBookings() {

        AuthSession session =
                AuthSession.getCurrent();

        if (session == null) {
            return new ArrayList<>();
        }

        return bookingDao.getBookingsForOwner(
                session.getUid(),
                session.getIdToken()
        );
    }

    public List<Booking> getBookingsForCurrentDriver() {

        AuthSession session =
                AuthSession.getCurrent();

        if (session == null) {
            return new ArrayList<>();
        }

        Driver driver =
                driverDao.getCurrentDriver();

        if (driver == null) {
            return new ArrayList<>();
        }

        String uid =
                session.getUid();

        Map<String, Booking> byId =
                new LinkedHashMap<>();

        for (Booking b :
                bookingDao.getBookingsForDriver(
                        uid,
                        session.getIdToken())) {

            byId.put(
                    b.getId(),
                    b
            );
        }

        Bus myBus =
                busDao.resolveBusForDriver(
                        uid,
                        driver.getAssignedBusId(),
                        session.getIdToken()
                );

        String busId =
                myBus == null
                        ? null
                        : myBus.getId();

        if (busId != null
                && !busId.isEmpty()) {

            for (Booking b :
                    bookingDao.getBookingsForBus(
                            busId,
                            session.getIdToken())) {

                String bookingDriverId =
                        b.getDriverId();

                boolean unclaimed =
                        bookingDriverId == null
                                || bookingDriverId.isEmpty()
                                || "null".equals(
                                        bookingDriverId
                                );

                if (unclaimed) {

                    byId.putIfAbsent(
                            b.getId(),
                            b
                    );
                }
            }
        }

        return new ArrayList<>(
                byId.values()
        );
    }

    public Booking getPendingEmergencyBookingForCurrentDriver() {

        Booking oldest =
                null;

        for (Booking b :
                getBookingsForCurrentDriver()) {

            if (!b.isEmergency()
                    || !b.isPendingDriverAccept()) {

                continue;
            }

            if (oldest == null
                    || compareCreatedAt(
                            b,
                            oldest
                    ) < 0) {

                oldest = b;
            }
        }

        return oldest;
    }

    public boolean rejectEmergency(
            String bookingId) {

        AuthSession session =
                AuthSession.getCurrent();

        if (session == null
                || bookingId == null
                || bookingId.isEmpty()) {

            return false;
        }

        Booking booking =
                bookingDao.getBooking(
                        bookingId,
                        session.getIdToken()
                );

        String rejectedDriverId =
                booking == null
                        ? ""
                        : booking.getDriverId();

        boolean ok =
                bookingDao.clearEmergencyDispatch(
                        bookingId,
                        session.getIdToken()
                );

        if (!ok) {
            return false;
        }

        Driver driver =
                driverController.getDriver(
                        rejectedDriverId
                );

        String driverLabel =
                driver != null
                        && driver.getName() != null
                        && !driver.getName().isEmpty()
                                ? driver.getName()
                                : (rejectedDriverId == null
                                        || rejectedDriverId.isEmpty()
                                        ? "the driver"
                                        : rejectedDriverId);

        notificationController.notifyAdmins(
                "emergency_rejected",
                "Emergency booking "
                        + bookingId
                        + " was declined by "
                        + driverLabel
                        + " and is back in the queue — needs reassignment",
                bookingId
        );

        return true;
    }

    public boolean startTrip(
            String bookingId) {

        AuthSession session =
                AuthSession.getCurrent();

        if (session == null
                || !"driver".equals(session.getRole())
                || bookingId == null
                || bookingId.isEmpty()) {

            return false;
        }

        Booking booking =
                bookingDao.getBooking(
                        bookingId,
                        session.getIdToken()
                );

        if (booking == null) {

            System.out.println(
                    "Cannot start trip: booking "
                            + bookingId
                            + " not found."
            );

            return false;
        }

        String assignedDriverId =
                booking.getDriverId();

        if (assignedDriverId == null
                || assignedDriverId.isEmpty()
                || "null".equals(assignedDriverId)) {

            System.out.println(
                    "Cannot start trip: booking "
                            + bookingId
                            + " has no assigned driver."
            );

            return false;
        }

        if (!session.getUid().equals(
                assignedDriverId)) {

            System.out.println(
                    "Cannot start trip: driver "
                            + session.getUid()
                            + " is not assigned to booking "
                            + bookingId
            );

            return false;
        }

        boolean updated =
                bookingDao.updateStatus(
                        bookingId,
                        Booking.STATUS_EN_ROUTE,
                        session.getIdToken()
                );

        if (!updated) {
            return false;
        }

        Bus bus =
                booking.getBusId() == null
                        || booking.getBusId().isEmpty()
                                ? null
                                : busDao.getBus(
                                        booking.getBusId(),
                                        session.getIdToken()
                                );

        String busCode =
                bus != null
                        && bus.getBusCode() != null
                        && !bus.getBusCode().isEmpty()
                                ? bus.getBusCode()
                                : booking.getBusId();

        Driver driver =
                driverController.getDriver(
                        assignedDriverId
                );

        String driverName =
                driver != null
                        && driver.getName() != null
                        && !driver.getName().isEmpty()
                                ? driver.getName()
                                : assignedDriverId;

        notificationController.notifyOwnerOfDriverUpdate(
                booking.getOwnerId(),
                "driver_booking_accepted",
                "Driver " + driverName
                        + " accepted "
                        + booking.shortRef()
                        + " | Bus: "
                        + busCode
                        + " | Pickup: "
                        + booking.getLocation()
                        + " | Scheduled: "
                        + booking.getScheduledTime()
                        + " | Status: "
                        + Booking.STATUS_EN_ROUTE,
                booking.getId()
        );

        notificationController.notifyAdmins(
                "driver_response",
                "Driver "
                        + driverName
                        + " started "
                        + booking.shortRef()
                        + " on bus "
                        + busCode
                        + ".",
                booking.getId()
        );

        return true;
    }

    public boolean markArrived(
            String bookingId) {

        AuthSession session =
                AuthSession.getCurrent();

        if (session == null) {
            return false;
        }

        return updateDriverStatusAndNotifyOwner(
                bookingId,
                "ARRIVED",
                "driver_response",
                "Your driver has arrived at the booking location."
        );
    }

    public String getOtpForBooking(
            String bookingId) {

        AuthSession session =
                AuthSession.getCurrent();

        if (session == null) {
            return null;
        }

        return bookingOtpDao.getOtp(
                bookingId,
                session.getIdToken()
        );
    }

    public boolean verifyOtpAndStartCharging(
            String bookingId,
            String otpAttempt) {

        AuthSession session =
                AuthSession.getCurrent();

        if (session == null
                || bookingId == null
                || bookingId.isEmpty()) {

            return false;
        }

        if (otpAttempt == null
                || !otpAttempt.matches(
                        "\\d{6}"
                )) {

            System.out.println(
                    "Rejecting OTP attempt: must be 6 digits."
            );

            return false;
        }

        boolean ok =
                bookingDao.startChargingWithOtp(
                        bookingId,
                        otpAttempt,
                        session.getIdToken()
                );

        if (ok) {

            Booking booking =
                    bookingDao.getBooking(
                            bookingId,
                            session.getIdToken()
                    );

            notifyOwnerOfDriverUpdate(
                    booking,
                    "driver_response",
                    "Charging has started for your booking."
            );

            openChargingSession(
                    bookingId,
                    session.getIdToken()
            );

            notificationController.notifyAdmins(
                    "charging_started",
                    "Booking "
                            + bookingId
                            + " verified at pickup; charging started",
                    bookingId
            );
        }

        return ok;
    }

    private void openChargingSession(
            String bookingId,
            String idToken) {

        Booking booking =
                bookingDao.getBooking(
                        bookingId,
                        idToken
                );

        if (booking == null) {

            System.out.println(
                    "Cannot open charging session: booking "
                            + bookingId
                            + " not found."
            );

            return;
        }

        ChargingSession charging =
                new ChargingSession();

        charging.setOwnerId(
                booking.getOwnerId()
        );

        charging.setBookingId(
                bookingId
        );

        charging.setStatus(
                ChargingSession.STATUS_ACTIVE
        );

        charging.setTotalKwh(
                booking.getKwh()
        );

        charging.setStartedAt(
                Instant.now().toString()
        );

        charging.setCompletedAt(
                ""
        );

        if (sessionDao.createSession(
                charging,
                idToken
        ) == null) {

            System.out.println(
                    "Warning: charging started for booking "
                            + bookingId
                            + " but the ChargingSession record could not be created."
            );
        }
    }

    public boolean startCharging(
            String bookingId) {

        AuthSession session =
                AuthSession.getCurrent();

        if (session == null) {
            return false;
        }

        return bookingDao.updateStatus(
                bookingId,
                "CHARGING",
                session.getIdToken()
        );
    }

    public boolean completeBooking(
            String bookingId) {

        AuthSession session =
                AuthSession.getCurrent();

        if (session == null) {
            return false;
        }

        Booking toSettle =
                bookingDao.getBooking(
                        bookingId,
                        session.getIdToken()
                );

        if (toSettle == null) {

            System.out.println(
                    "Cannot complete booking "
                            + bookingId
                            + ": not found."
            );

            return false;
        }

        if (!settlePayment(
                toSettle,
                session
        )) {

            return false;
        }

        if (!bookingDao.updateStatus(
                bookingId,
                Booking.STATUS_COMPLETED,
                session.getIdToken()
        )) {

            return false;
        }

        ChargingSession open =
                sessionDao.getSessionForBooking(
                        bookingId,
                        session.getIdToken()
                );

        if (open != null
                && !sessionDao.completeSession(
                        open.getId(),
                        session.getIdToken()
                )) {

            System.out.println(
                    "Warning: booking "
                            + bookingId
                            + " completed but its charging session is still marked active."
            );
        }

        notifyOwnerOfDriverUpdate(
                toSettle,
                "driver_response",
                "Charging has been completed for your booking."
        );

        return true;
    }

    private boolean updateDriverStatusAndNotifyOwner(
            String bookingId,
            String status,
            String notificationType,
            String notificationMessage) {

        AuthSession session =
                AuthSession.getCurrent();

        if (session == null) {
            return false;
        }

        Booking booking =
                bookingDao.getBooking(
                        bookingId,
                        session.getIdToken()
                );

        boolean updated =
                bookingDao.updateStatus(
                        bookingId,
                        status,
                        session.getIdToken()
                );

        if (updated) {

            notifyOwnerOfDriverUpdate(
                    booking,
                    notificationType,
                    notificationMessage
            );
        }

        return updated;
    }

    private void notifyOwnerOfDriverUpdate(
            Booking booking,
            String type,
            String message) {

        AuthSession session =
                AuthSession.getCurrent();

        if (session == null
                || !"driver".equals(session.getRole())
                || booking == null) {

            return;
        }

        notificationController.notifyOwnerOfDriverUpdate(
                booking.getOwnerId(),
                type,
                message,
                booking.getId()
        );
    }

    public Booking getMyUpcomingBooking() {

        AuthSession session =
                AuthSession.getCurrent();

        if (session == null) {
            return null;
        }

        return bookingDao.getUpcomingBooking(
                session.getUid(),
                session.getIdToken()
        );
    }

    private boolean settlePayment(
            Booking booking,
            AuthSession session) {

        String idToken =
                session.getIdToken();

        if (driverEarningDao.existsForBooking(
                booking.getId(),
                idToken
        )) {

            System.out.println(
                    "Booking "
                            + booking.getId()
                            + " is already settled; skipping payment."
            );

            return true;
        }

        double fare =
                booking.fareToBill();

        double payout =
                booking.payoutToPay();

        WalletTransaction charge =
                new WalletTransaction(
                        null,
                        booking.getOwnerId(),
                        "charge_payment",
                        fare,
                        "wallet",
                        Instant.now().toString(),
                        booking.getId()
                );

        if (walletDao.addTransaction(
                charge,
                idToken
        ) == null) {

            System.out.println(
                    "Failed to record the customer charge for booking "
                            + booking.getId()
                            + "; not completing."
            );

            return false;
        }

        String driverId =
                booking.getDriverId();

        if (driverId == null
                || driverId.isEmpty()
                || "null".equals(driverId)) {

            System.out.println(
                    "Booking "
                            + booking.getId()
                            + " has no driver; customer charged, no payout recorded."
            );

            return true;
        }

        DriverEarning earning =
                new DriverEarning(
                        null,
                        driverId,
                        booking.getId(),
                        booking.getOwnerId(),
                        payout,
                        fare,
                        Instant.now().toString()
                );

        if (driverEarningDao.createEarning(
                earning,
                idToken
        ) == null) {

            System.out.println(
                    "Warning: customer was charged for booking "
                            + booking.getId()
                            + " but the driver payout could not be recorded."
            );

            return false;
        }

        notificationController.notifyUser(
                driverId,
                "earning_credited",
                "Earned ₹"
                        + (int) payout
                        + " · "
                        + booking.shortRef()
                        + " · fare ₹"
                        + (int) fare,
                booking.getId()
        );

        return true;
    }

    public boolean cancelBooking(
            String bookingId) {

        AuthSession session =
                AuthSession.getCurrent();

        if (session == null) {
            return false;
        }

        Booking booking =
                bookingDao.getBooking(
                        bookingId,
                        session.getIdToken()
                );

        if (!bookingDao.updateStatus(
                bookingId,
                Booking.STATUS_CANCELLED,
                session.getIdToken()
        )) {

            return false;
        }

        if (booking != null) {

            String driverId =
                    booking.getDriverId();

            if (driverId != null
                    && !driverId.isEmpty()
                    && !"null".equals(driverId)) {

                notificationController.notifyUser(
                        driverId,
                        "booking_cancelled",
                        "Booking cancelled · "
                                + booking.shortRef()
                                + " · "
                                + booking.getLocation()
                                + " — do not proceed",
                        bookingId
                );
            }
        }

        return true;
    }
}