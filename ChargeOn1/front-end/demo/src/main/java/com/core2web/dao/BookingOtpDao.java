package com.core2web.dao;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONObject;

public class BookingOtpDao {

    private static final String COLLECTION = "bookingOtp";

    private static final SecureRandom RANDOM = new SecureRandom();

    private static final int OTP_DIGITS = 6;

    public static String generateOtp() {
        int max = (int) Math.pow(10, OTP_DIGITS);
        return String.format("%0" + OTP_DIGITS + "d", RANDOM.nextInt(max));
    }

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
