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

import com.core2web.model.ChargingSession;

public class ChargingSessionDao {

    // Firestore collection IDs are case-sensitive and the real collection is
    // "ChargingSession". The previous lowercase value queried a collection that does
    // not exist, which returns an empty result rather than an error.
    private static final String COLLECTION = "ChargingSession";


    public ChargingSession getActiveSession(String uid, String idToken) {
        List<JSONObject> docs = FirestoreHelper.queryWithFilters(
                COLLECTION,
                Arrays.asList(
                        new FirestoreHelper.Filter("ownerId", "EQUAL", uid),
                        new FirestoreHelper.Filter("status",  "EQUAL", "active")
                ),
                idToken
        );

        if (docs.isEmpty()) {
            return null;
        }

        return fromDocument(docs.get(0));
    }


    public List<ChargingSession> getSessionsThisMonth(String uid, String idToken) {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        ZonedDateTime startOfMonth = now.withDayOfMonth(1)
                                        .withHour(0)
                                        .withMinute(0)
                                        .withSecond(0)
                                        .withNano(0);
        Instant startInstant = startOfMonth.toInstant();

        // startedAt is an ISO-8601 string, so the date range cannot be a query filter
        // (it would serialise to timestampValue and match nothing). Filter in memory,
        // same as WalletDao does.
        List<JSONObject> docs = FirestoreHelper.queryWithFilters(
                COLLECTION,
                Arrays.asList(
                        new FirestoreHelper.Filter("ownerId", "EQUAL", uid)
                ),
                idToken
        );
        docs.removeIf(doc -> !FirestoreHelper.isFieldOnOrAfter(doc, "startedAt", startInstant));
        return toDomainList(docs);
    }


    /**
     * Opens a session. Called when the driver verifies the customer's code, so this
     * runs with the DRIVER's token — the security rules must permit that.
     *
     * <p>Only fields we actually know are populated: owner, booking, start time and
     * the requested kWh. Telemetry (batteryPct/currentKwh/powerKw/runningCost) stays
     * 0 because nothing measures it; see ChargingSession.hasLiveMeter().
     *
     * @return the new document id, or null on failure
     */
    public String createSession(ChargingSession session, String idToken) {
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("ownerId", session.getOwnerId());
        fields.put("bookingId", session.getBookingId());
        fields.put("status", session.getStatus());
        fields.put("batteryPct", session.getBatteryPct());
        fields.put("currentKwh", session.getCurrentKwh());
        fields.put("totalKwh", session.getTotalKwh());
        fields.put("powerKw", session.getPowerKw());
        fields.put("runningCost", session.getRunningCost());
        fields.put("targetSoc", session.getTargetSoc());
        fields.put("startedAt", session.getStartedAt());
        fields.put("completedAt", session.getCompletedAt() == null ? "" : session.getCompletedAt());
        return FirestoreHelper.createDocument(COLLECTION, fields, idToken);
    }


    /** The open session for a booking, or null. Used to close it on completion. */
    public ChargingSession getSessionForBooking(String bookingId, String idToken) {
        if (bookingId == null || bookingId.isEmpty()) {
            return null;
        }
        List<JSONObject> docs = FirestoreHelper.queryWithFilters(
                COLLECTION,
                Arrays.asList(
                        new FirestoreHelper.Filter("bookingId", "EQUAL", bookingId),
                        new FirestoreHelper.Filter("status", "EQUAL", ChargingSession.STATUS_ACTIVE)
                ),
                idToken
        );
        return docs.isEmpty() ? null : fromDocument(docs.get(0));
    }


    /** Closes a session: status completed, completedAt stamped. */
    public boolean completeSession(String sessionId, String idToken) {
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("status", ChargingSession.STATUS_COMPLETED);
        fields.put("completedAt", Instant.now().toString());
        return FirestoreHelper.updateFields(COLLECTION, sessionId, fields, idToken);
    }


    private List<ChargingSession> toDomainList(List<JSONObject> docs) {
        List<ChargingSession> list = new ArrayList<>();
        for (JSONObject doc : docs) {
            list.add(fromDocument(doc));
        }
        return list;
    }

    private ChargingSession fromDocument(JSONObject doc) {
        ChargingSession s = new ChargingSession();
        s.setId(FirestoreHelper.getDocumentId(doc));
        s.setOwnerId(FirestoreHelper.getString(doc, "ownerId"));
        s.setBookingId(FirestoreHelper.getString(doc, "bookingId"));
        s.setStatus(FirestoreHelper.getString(doc, "status"));
        s.setBatteryPct(FirestoreHelper.getNumber(doc, "batteryPct"));
        s.setCurrentKwh(FirestoreHelper.getNumber(doc, "currentKwh"));
        s.setTotalKwh(FirestoreHelper.getNumber(doc, "totalKwh"));
        s.setPowerKw(FirestoreHelper.getNumber(doc, "powerKw"));
        s.setRunningCost(FirestoreHelper.getNumber(doc, "runningCost"));
        s.setTargetSoc((int) FirestoreHelper.getNumber(doc, "targetSoc"));
        s.setStartedAt(FirestoreHelper.getString(doc, "startedAt"));
        s.setCompletedAt(FirestoreHelper.getString(doc, "completedAt"));
        return s;
    }
}
