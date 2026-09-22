package com.core2web.dao;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.core2web.model.Booking;
import com.core2web.util.DateTimeUtil;

public class BookingDao {

    private static final String COLLECTION = "bookings";


    public List<Booking> getAllBookings(String idToken) {

        List<JSONObject> docs =
                FirestoreHelper.listCollection(
                        COLLECTION,
                        idToken
                );

        return toDomainList(docs);
    }


    /**
     * Existing assignment method preserved for backward compatibility.
     *
     * Normal/legacy callers continue to create an ASSIGNED booking.
     *
     * Emergency callers should use the overload that explicitly supplies
     * isEmergency so the driver receives the pending-accept state.
     */
    public boolean assignBus(
            String bookingId,
            String busId,
            String driverId,
            String idToken) {

        return assignBus(
                bookingId,
                busId,
                driverId,
                false,
                idToken
        );
    }


    /**
     * Assigns the ADMIN-selected bus and its permanently assigned driver
     * to a booking.
     *
     * Normal bookings:
     *
     *     ASSIGNED
     *
     * Emergency bookings:
     *
     *     PENDING_DRIVER_ACCEPT
     *
     * The emergency state is important because Driver-side emergency
     * handling looks specifically for STATUS_PENDING_DRIVER_ACCEPT.
     */
    public boolean assignBus(
            String bookingId,
            String busId,
            String driverId,
            boolean isEmergency,
            String idToken) {

        String nextStatus =
                isEmergency
                        ? Booking.STATUS_PENDING_DRIVER_ACCEPT
                        : Booking.STATUS_ASSIGNED;

        return FirestoreHelper.updateFields(
                COLLECTION,
                bookingId,
                Map.of(
                        "busId",
                        busId,

                        "driverId",
                        driverId == null
                                ? ""
                                : driverId,

                        "status",
                        nextStatus,

                        "assignedAt",
                        Instant.now()
                ),
                idToken
        );
    }


    public List<Booking> getBookingsForOwner(
            String uid,
            String idToken) {

        List<JSONObject> docs =
                FirestoreHelper.queryWithFilters(
                        COLLECTION,
                        Arrays.asList(
                                new FirestoreHelper.Filter(
                                        "ownerId",
                                        "EQUAL",
                                        uid
                                )
                        ),
                        idToken
                );

        return toDomainList(docs);
    }


    public Booking getUpcomingBooking(
            String uid,
            String idToken) {

        List<Booking> list =
                getBookingsForOwner(
                        uid,
                        idToken
                );

        Booking upcoming = null;

        for (Booking b : list) {

            if (b.isCompleted()
                    || Booking.STATUS_CANCELLED.equals(
                            b.getStatus())) {

                continue;
            }

            if (upcoming == null
                    || compareTimestamps(
                            b.getCreatedAt(),
                            upcoming.getCreatedAt()) < 0) {

                upcoming = b;
            }
        }

        return upcoming;
    }


    public List<Booking> getBookingsThisMonth(
            String uid,
            String idToken) {

        ZonedDateTime now =
                ZonedDateTime.now(
                        ZoneId.of("UTC")
                );

        ZonedDateTime startOfMonth =
                now.withDayOfMonth(1)
                        .withHour(0)
                        .withMinute(0)
                        .withSecond(0)
                        .withNano(0);

        Instant startInstant =
                startOfMonth.toInstant();

        List<JSONObject> docs =
                FirestoreHelper.queryWithFilters(
                        COLLECTION,
                        Arrays.asList(
                                new FirestoreHelper.Filter(
                                        "ownerId",
                                        "EQUAL",
                                        uid
                                )
                        ),
                        idToken
                );

        docs.removeIf(
                doc -> !FirestoreHelper.isFieldOnOrAfter(
                        doc,
                        "createdAt",
                        startInstant
                )
        );

        return toDomainList(docs);
    }


    public List<Booking> getBookingsToday(
            String idToken) {

        ZonedDateTime now =
                ZonedDateTime.now(
                        ZoneId.of("UTC")
                );

        ZonedDateTime startOfDay =
                now.withHour(0)
                        .withMinute(0)
                        .withSecond(0)
                        .withNano(0);

        Instant startInstant =
                startOfDay.toInstant();

        List<JSONObject> docs =
                FirestoreHelper.listCollection(
                        COLLECTION,
                        idToken
                );

        docs.removeIf(
                doc -> !FirestoreHelper.isFieldOnOrAfter(
                        doc,
                        "createdAt",
                        startInstant
                )
        );

        return toDomainList(docs);
    }


    /**
     * Bookings stamped with this driver.
     */
    public List<Booking> getBookingsForDriver(
            String driverId,
            String idToken) {

        List<JSONObject> docs =
                FirestoreHelper.queryWithFilters(
                        COLLECTION,
                        Arrays.asList(
                                new FirestoreHelper.Filter(
                                        "driverId",
                                        "EQUAL",
                                        driverId
                                )
                        ),
                        idToken
                );

        return toDomainList(docs);
    }


    public List<Booking> getBookingsForBus(
            String busId,
            String idToken) {

        List<JSONObject> docs =
                FirestoreHelper.queryWithFilters(
                        COLLECTION,
                        Arrays.asList(
                                new FirestoreHelper.Filter(
                                        "busId",
                                        "EQUAL",
                                        busId
                                )
                        ),
                        idToken
                );

        return toDomainList(docs);
    }


    public String createBooking(
            Booking booking,
            String idToken) {

        /*
         * LinkedHashMap is used because:
         *
         * 1. driverId can be empty
         * 2. FirestoreHelper already handles these fields
         * 3. There are more than ten fields
         */
        Map<String, Object> fields =
                new LinkedHashMap<>();

        fields.put(
                "ownerId",
                booking.getOwnerId()
        );

        fields.put(
                "vehicleId",
                booking.getVehicleId()
        );

        fields.put(
                "status",
                booking.getStatus()
        );

        fields.put(
                "scheduledTime",
                booking.getScheduledTime()
        );

        fields.put(
                "location",
                booking.getLocation()
        );

        fields.put(
                "busId",
                booking.getBusId()
        );

        fields.put(
                "kwh",
                booking.getKwh()
        );

        fields.put(
                "createdAt",
                booking.getCreatedAt()
        );

        fields.put(
                "driverId",
                booking.getDriverId() == null
                        ? ""
                        : booking.getDriverId()
        );

        fields.put(
                "isEmergency",
                booking.isEmergency()
        );

        /*
         * Store the actual owner-selected booking mode.
         *
         * instant
         * reserve
         * emergency
         */
        fields.put(
                "bookingType",
                booking.getBookingType()
        );

        /*
         * Two separate doubles.
         * Never use a GeoPoint here.
         */
        fields.put(
                "pickupLatitude",
                booking.getPickupLatitude()
        );

        fields.put(
                "pickupLongitude",
                booking.getPickupLongitude()
        );

        /*
         * OTP verification starts empty.
         */
        fields.put(
                "otpVerifiedAt",
                ""
        );

        /*
         * Fare frozen at booking time.
         */
        fields.put(
                "amount",
                booking.getAmount()
        );

        fields.put(
                "serviceFee",
                booking.getServiceFee()
        );

        fields.put(
                "driverPayout",
                booking.getDriverPayout()
        );

        return FirestoreHelper.createDocument(
                COLLECTION,
                fields,
                idToken
        );
    }


    public boolean updateStatus(
            String bookingId,
            String newStatus,
            String idToken) {

        return FirestoreHelper.updateFields(
                COLLECTION,
                bookingId,
                Map.of(
                        "status",
                        newStatus,

                        "updatedAt",
                        Instant.now()
                ),
                idToken
        );
    }


    /**
     * Submits the driver's OTP and, when accepted by Firestore rules,
     * moves the booking to CHARGING.
     */
    public boolean startChargingWithOtp(
            String bookingId,
            String otpAttempt,
            String idToken) {

        Map<String, Object> fields =
                new LinkedHashMap<>();

        fields.put(
                "status",
                Booking.STATUS_CHARGING
        );

        fields.put(
                "otpAttempt",
                otpAttempt == null
                        ? ""
                        : otpAttempt
        );

        fields.put(
                "otpVerifiedAt",
                Instant.now()
        );

        return FirestoreHelper.updateFields(
                COLLECTION,
                bookingId,
                fields,
                idToken
        );
    }


    /**
     * Undoes an emergency dispatch.
     *
     * The booking becomes available in the ADMIN dispatch queue again.
     */
    public boolean clearEmergencyDispatch(
            String bookingId,
            String idToken) {

        return FirestoreHelper.updateFields(
                COLLECTION,
                bookingId,
                Map.of(
                        "driverId",
                        "",

                        "busId",
                        "",

                        "status",
                        Booking.STATUS_QUEUED
                ),
                idToken
        );
    }


    public Booking getBooking(
            String bookingId,
            String idToken) {

        JSONObject doc =
                FirestoreHelper.getDocument(
                        COLLECTION,
                        bookingId,
                        idToken
                );

        return doc == null
                ? null
                : fromDocument(doc);
    }


    private List<Booking> toDomainList(
            List<JSONObject> docs) {

        List<Booking> list =
                new ArrayList<>();

        if (docs == null) {
            return list;
        }

        for (JSONObject doc : docs) {

            if (doc == null) {
                continue;
            }

            list.add(
                    fromDocument(doc)
            );
        }

        return list;
    }


    private static int compareTimestamps(
            String left,
            String right) {

        Instant a =
                DateTimeUtil.parse(left);

        Instant b =
                DateTimeUtil.parse(right);

        if (a == null) {
            return b == null
                    ? 0
                    : -1;
        }

        return b == null
                ? 1
                : a.compareTo(b);
    }


    private Booking fromDocument(
            JSONObject doc) {

        Booking b =
                new Booking();

        b.setId(
                FirestoreHelper.getDocumentId(
                        doc
                )
        );

        b.setOwnerId(
                FirestoreHelper.getString(
                        doc,
                        "ownerId"
                )
        );

        b.setVehicleId(
                FirestoreHelper.getString(
                        doc,
                        "vehicleId"
                )
        );

        b.setStatus(
                FirestoreHelper.getString(
                        doc,
                        "status"
                )
        );

        b.setScheduledTime(
                FirestoreHelper.getString(
                        doc,
                        "scheduledTime"
                )
        );

        b.setLocation(
                FirestoreHelper.getString(
                        doc,
                        "location"
                )
        );

        b.setBusId(
                FirestoreHelper.getString(
                        doc,
                        "busId"
                )
        );

        b.setKwh(
                FirestoreHelper.getNumber(
                        doc,
                        "kwh"
                )
        );

        b.setCreatedAt(
                FirestoreHelper.getString(
                        doc,
                        "createdAt"
                )
        );

        b.setDriverId(
                FirestoreHelper.getString(
                        doc,
                        "driverId"
                )
        );

        b.setEmergency(
                FirestoreHelper.getBoolean(
                        doc,
                        "isEmergency"
                )
        );

        /*
         * Read bookingType.
         *
         * Old Firestore bookings will not have this field.
         * Therefore fall back to the old isEmergency field.
         */
        String bookingType =
                FirestoreHelper.getString(
                        doc,
                        "bookingType"
                );

        if (bookingType == null
                || bookingType.isEmpty()) {

            bookingType =
                    FirestoreHelper.getBoolean(
                            doc,
                            "isEmergency"
                    )
                            ? Booking.TYPE_EMERGENCY
                            : Booking.TYPE_INSTANT;
        }

        b.setBookingType(
                bookingType
        );

        b.setPickupLatitude(
                FirestoreHelper.getNumber(
                        doc,
                        "pickupLatitude"
                )
        );

        b.setPickupLongitude(
                FirestoreHelper.getNumber(
                        doc,
                        "pickupLongitude"
                )
        );

        b.setOtpVerifiedAt(
                FirestoreHelper.getString(
                        doc,
                        "otpVerifiedAt"
                )
        );

        b.setAmount(
                FirestoreHelper.getNumber(
                        doc,
                        "amount"
                )
        );

        b.setServiceFee(
                FirestoreHelper.getNumber(
                        doc,
                        "serviceFee"
                )
        );

        b.setDriverPayout(
                FirestoreHelper.getNumber(
                        doc,
                        "driverPayout"
                )
        );

        return b;
    }
}