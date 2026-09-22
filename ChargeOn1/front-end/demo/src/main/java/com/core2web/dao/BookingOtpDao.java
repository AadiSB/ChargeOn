package com.core2web.dao;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONObject;

/**
 * The pickup verification code for a booking, held in its own collection so that
 * <em>drivers cannot read it</em>.
 *
 * <p>Document ID is the bookingId, one code per booking.
 *
 * <p>Why a separate collection: Firestore rules are per-document, with no
 * field-level security. Drivers need read access to booking documents (location,
 * kWh, status), so a code stored on the booking would be readable by the very person
 * it is meant to challenge. Keeping it here lets the security rules grant read to the
 * owner and admin only, while the driver's guess is checked <em>inside a rule</em> —
 * rules run server-side and their {@code get()} bypasses client read permission.
 *
 * <p>Consequently there is deliberately no "fetch the code and compare it in Java"
 * method for the driver side. See BookingDao.startChargingWithOtp.
 */
public class BookingOtpDao {

    private static final String COLLECTION = "bookingOtp";

    private static final SecureRandom RANDOM = new SecureRandom();

    /** 6 digits: short enough to read aloud, long enough not to be guessed casually. */
    private static final int OTP_DIGITS = 6;

    /** Generates a fresh zero-padded numeric code. */
    public static String generateOtp() {
        int max = (int) Math.pow(10, OTP_DIGITS);
        return String.format("%0" + OTP_DIGITS + "d", RANDOM.nextInt(max));
    }

    /**
     * Stores the code for a booking. Written by the owner as they create the
     * booking, so the owner is the only non-admin who can read it back.
     *
     * @param ownerId recorded so the security rule can check ownership without
     *                needing to read the booking document
     */
    public boolean createOtp(String bookingId, String otp, String ownerId, String idToken) {
        if (bookingId == null || bookingId.isEmpty()) {
            System.out.println("Cannot store booking OTP: missing bookingId.");
            return false;
        }

        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("bookingId", bookingId);
        fields.put("otp", otp);
        fields.put("ownerId", ownerId == null ? "" : ownerId);
        fields.put("createdAt", Instant.now());

        return FirestoreHelper.writeDocument(COLLECTION, bookingId, fields, idToken);
    }

    /**
     * Reads the code back. Only usable by the booking's owner (to show it to the
     * driver) or an admin — the rules deny drivers, so this returns null for them.
     *
     * @return the code, or null when absent or not readable by this caller
     */
    public String getOtp(String bookingId, String idToken) {
        if (bookingId == null || bookingId.isEmpty()) {
            return null;
        }
        JSONObject doc = FirestoreHelper.getDocument(COLLECTION, bookingId, idToken);
        if (doc == null) {
            return null;
        }
        String otp = FirestoreHelper.getString(doc, "otp");
        return otp == null || otp.isEmpty() ? null : otp;
    }
}
