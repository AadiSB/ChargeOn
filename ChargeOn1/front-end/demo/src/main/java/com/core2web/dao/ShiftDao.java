package com.core2web.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.core2web.model.Shift;
import com.core2web.util.DateTimeUtil;

public class ShiftDao {

    private static final String COLLECTION = "shifts";


    public Shift getActiveShift(String driverId, String idToken) {
        List<JSONObject> docs = FirestoreHelper.queryWithFilters(
                COLLECTION,
                Arrays.asList(
                        new FirestoreHelper.Filter("driverId", "EQUAL", driverId),
                        new FirestoreHelper.Filter("clockOutAt", "EQUAL", "")
                ),
                idToken
        );
        return docs.isEmpty() ? null : fromDocument(docs.get(0));
    }


    public List<Shift> getShiftsForDriver(String driverId, String idToken) {
        List<JSONObject> docs = FirestoreHelper.queryWithFilters(
                COLLECTION,
                Arrays.asList(new FirestoreHelper.Filter("driverId", "EQUAL", driverId)),
                idToken
        );
        List<Shift> shifts = new ArrayList<>();
        for (JSONObject doc : docs) {
            shifts.add(fromDocument(doc));
        }
        shifts.sort((a, b) -> {
            java.time.Instant right = DateTimeUtil.parse(b.getClockInAt());
            java.time.Instant left = DateTimeUtil.parse(a.getClockInAt());
            if (right == null) return left == null ? 0 : -1;
            return left == null ? 1 : right.compareTo(left);
        });
        return shifts;
    }


    public String clockIn(Shift shift, String idToken) {
        Map<String, Object> fields = Map.of(
                "driverId", shift.getDriverId(),
                "busId", shift.getBusId(),
                "depot", shift.getDepot(),
                "clockInAt", shift.getClockInAt(),
                "clockOutAt", "",
                "createdAt", shift.getCreatedAt()
        );
        return FirestoreHelper.createDocument(COLLECTION, fields, idToken);
    }


    public boolean clockOut(String shiftId, String clockOutAt, String idToken) {
        return FirestoreHelper.updateFields(COLLECTION, shiftId, Map.of("clockOutAt", clockOutAt), idToken);
    }


    private Shift fromDocument(JSONObject doc) {
        Shift shift = new Shift();
        shift.setId(FirestoreHelper.getDocumentId(doc));
        shift.setDriverId(FirestoreHelper.getString(doc, "driverId"));
        shift.setBusId(FirestoreHelper.getString(doc, "busId"));
        shift.setDepot(FirestoreHelper.getString(doc, "depot"));
        shift.setClockInAt(FirestoreHelper.getString(doc, "clockInAt"));
        shift.setClockOutAt(FirestoreHelper.getString(doc, "clockOutAt"));
        shift.setCreatedAt(FirestoreHelper.getString(doc, "createdAt"));
        return shift;
    }
}
