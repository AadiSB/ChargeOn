package com.core2web.dao;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.core2web.model.DriverEarning;

public class DriverEarningDao {

    private static final String COLLECTION = "DriverEarning";

    public String createEarning(DriverEarning earning, String idToken) {
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("driverId", earning.getDriverId());
        fields.put("bookingId", earning.getBookingId());
        fields.put("ownerId", earning.getOwnerId() == null ? "" : earning.getOwnerId());
        fields.put("amount", earning.getAmount());
        fields.put("fare", earning.getFare());
        fields.put("createdAt", earning.getCreatedAt());
        return FirestoreHelper.createDocument(COLLECTION, fields, idToken);
    }

    public List<DriverEarning> getEarningsForDriver(String driverId, String idToken) {
        List<JSONObject> docs = FirestoreHelper.queryWithFilters(
                COLLECTION,
                Arrays.asList(new FirestoreHelper.Filter("driverId", "EQUAL", driverId)),
                idToken
        );
        return toDomainList(docs);
    }

    public List<DriverEarning> getEarningsSince(String driverId, Instant since, String idToken) {
        List<JSONObject> docs = FirestoreHelper.queryWithFilters(
                COLLECTION,
                Arrays.asList(new FirestoreHelper.Filter("driverId", "EQUAL", driverId)),
                idToken
        );
        docs.removeIf(doc -> !FirestoreHelper.isFieldOnOrAfter(doc, "createdAt", since));
        return toDomainList(docs);
    }

    public boolean existsForBooking(String bookingId, String idToken) {
        List<JSONObject> docs = FirestoreHelper.queryWithFilters(
                COLLECTION,
                Arrays.asList(new FirestoreHelper.Filter("bookingId", "EQUAL", bookingId)),
                idToken
        );
        return !docs.isEmpty();
    }

    private List<DriverEarning> toDomainList(List<JSONObject> docs) {
        List<DriverEarning> list = new ArrayList<>();
        for (JSONObject doc : docs) {
            list.add(fromDocument(doc));
        }
        return list;
    }

    private DriverEarning fromDocument(JSONObject doc) {
        DriverEarning e = new DriverEarning();
        e.setId(FirestoreHelper.getDocumentId(doc));
        e.setDriverId(FirestoreHelper.getString(doc, "driverId"));
        e.setBookingId(FirestoreHelper.getString(doc, "bookingId"));
        e.setOwnerId(FirestoreHelper.getString(doc, "ownerId"));
        e.setAmount(FirestoreHelper.getNumber(doc, "amount"));
        e.setFare(FirestoreHelper.getNumber(doc, "fare"));
        e.setCreatedAt(FirestoreHelper.getString(doc, "createdAt"));
        return e;
    }
}
