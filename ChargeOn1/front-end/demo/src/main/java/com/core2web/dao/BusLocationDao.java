package com.core2web.dao;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.core2web.model.BusLocation;

/**
 * Current-position store. The document ID <em>is</em> the busId, one doc per bus.
 *
 * <p>That choice is deliberate: reads become O(1) {@code getDocument} calls and the
 * design never needs "most recent N", which {@link FirestoreHelper} cannot express
 * (no orderBy, no limit).
 */
public class BusLocationDao {

    private static final String COLLECTION = "BusLocation";


    /** @return the bus's current position, or null if no document exists (position unknown). */
    public BusLocation getBusLocation(String busId, String idToken) {
        if (busId == null || busId.isEmpty() || "null".equals(busId)) {
            return null;
        }
        JSONObject doc = FirestoreHelper.getDocument(COLLECTION, busId, idToken);
        return doc == null ? null : fromDocument(doc);
    }


    /**
     * Every known position in one round-trip, keyed by busId. Callers joining against
     * the fleet should use this rather than calling {@link #getLocation} per bus.
     */
    public Map<String, BusLocation> getAllLocations(String idToken) {
        Map<String, BusLocation> byBusId = new LinkedHashMap<>();
        List<JSONObject> docs = FirestoreHelper.listCollection(COLLECTION, idToken);
        for (JSONObject doc : docs) {
            BusLocation loc = fromDocument(doc);
            if (loc.getBusId() != null && !loc.getBusId().isEmpty()) {
                byBusId.put(loc.getBusId(), loc);
            }
        }
        return byBusId;
    }


    /**
     * Full overwrite of the bus's single position doc.
     *
     * <p>Uses {@code writeDocument} (explicit document ID) and never
     * {@code createDocument} (auto-ID) — auto-IDs would accumulate one row per update
     * per bus and break the one-doc-per-bus invariant this design depends on.
     */
    public boolean upsertLocation(BusLocation loc, String idToken) {
        if (loc == null || loc.getBusId() == null || loc.getBusId().isEmpty()) {
            System.out.println("Cannot upsert bus location: missing busId.");
            return false;
        }

        // All plain Strings and Doubles — no GeoPoint is ever handed to
        // FirestoreHelper.toFirestoreValue(), which has no geoPointValue branch.
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("busId", loc.getBusId());
        fields.put("latitude", loc.getLatitude());
        fields.put("longitude", loc.getLongitude());
        fields.put("heading", loc.getHeading());
        fields.put("speedKph", loc.getSpeedKph());
        fields.put("source", loc.getSource() == null ? BusLocation.SOURCE_MANUAL : loc.getSource());
        fields.put("updatedAt", loc.getUpdatedAt());
        fields.put("updatedById", loc.getUpdatedById() == null ? "" : loc.getUpdatedById());

        return FirestoreHelper.writeDocument(COLLECTION, loc.getBusId(), fields, idToken);
    }


    private BusLocation fromDocument(JSONObject doc) {
        BusLocation loc = new BusLocation();

        // The document ID is the source of truth for busId; the stored field is a
        // convenience copy and may be missing on hand-created docs.
        String docId = FirestoreHelper.getDocumentId(doc);
        String fieldBusId = FirestoreHelper.getString(doc, "busId");
        loc.setBusId(fieldBusId == null || fieldBusId.isEmpty() ? docId : fieldBusId);

        loc.setLatitude(FirestoreHelper.getNumber(doc, "latitude"));
        loc.setLongitude(FirestoreHelper.getNumber(doc, "longitude"));
        loc.setHeading(FirestoreHelper.getNumber(doc, "heading"));
        loc.setSpeedKph(FirestoreHelper.getNumber(doc, "speedKph"));
        loc.setSource(FirestoreHelper.getString(doc, "source"));
        loc.setUpdatedAt(FirestoreHelper.getString(doc, "updatedAt"));
        loc.setUpdatedById(FirestoreHelper.getString(doc, "updatedById"));
        return loc;
    }
}
