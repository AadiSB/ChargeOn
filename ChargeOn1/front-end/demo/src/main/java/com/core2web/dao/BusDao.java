package com.core2web.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.core2web.model.Bus;

public class BusDao {

    private static final String COLLECTION = "buses";

    public List<Bus> getAllBuses(String idToken) {
        List<JSONObject> docs = FirestoreHelper.listCollection(COLLECTION, idToken);
        return toDomainList(docs);
    }

    public Bus getBus(String busId, String idToken) {
        JSONObject doc = FirestoreHelper.getDocument(COLLECTION, busId, idToken);
        return doc == null ? null : fromDocument(doc);
    }

    public boolean updateAssignedDriver(String busId, String driverId, String idToken) {
        return FirestoreHelper.updateFields(
                COLLECTION, busId,
                Map.of("assignedDriverId", driverId == null ? "" : driverId),
                idToken);
    }

    public List<Bus> getBusesForDriver(String driverId, String idToken) {
        List<JSONObject> docs = FirestoreHelper.queryWithFilters(
                COLLECTION,
                Arrays.asList(new FirestoreHelper.Filter("assignedDriverId", "EQUAL", driverId)),
                idToken);
        return toDomainList(docs);
    }

    public Bus getBusByCode(String busCode, String idToken) {
        if (busCode == null || busCode.isEmpty()) {
            return null;
        }
        List<JSONObject> docs = FirestoreHelper.queryWithFilters(
                COLLECTION,
                Arrays.asList(new FirestoreHelper.Filter("busCode", "EQUAL", busCode)),
                idToken);
        List<Bus> matches = toDomainList(docs);
        return matches.isEmpty() ? null : matches.get(0);
    }

    public Bus resolveBusForDriver(String driverUid, String declaredBusId, String idToken) {

        if (driverUid != null && !driverUid.isEmpty()) {
            List<Bus> claiming = getBusesForDriver(driverUid, idToken);

            if (claiming.size() > 1) {
                System.out.println("Data warning: " + claiming.size()
                        + " buses claim driver " + driverUid + "; using the first.");
            }

            if (!claiming.isEmpty()) {
                Bus authoritative = claiming.get(0);
                if (!authoritative.getId().equals(declaredBusId)) {
                    System.out.println("Data warning: driver " + driverUid
                            + " has assignedBusId=\"" + declaredBusId + "\" but bus "
                            + authoritative.getId() + " (" + authoritative.getBusCode()
                            + ") claims them. Using the bus, per assignedDriverId being"
                            + " the source of truth.");
                }
                return authoritative;
            }
        }

        if (declaredBusId == null || declaredBusId.isEmpty() || "null".equals(declaredBusId)) {
            return null;
        }

        Bus byId = getBus(declaredBusId, idToken);
        if (byId != null) {
            return byId;
        }

        Bus byCode = getBusByCode(declaredBusId, idToken);
        if (byCode != null) {
            System.out.println("Data warning: driver " + driverUid + " has assignedBusId=\""
                    + declaredBusId + "\", which is a busCode, not a document ID. Resolved to "
                    + byCode.getId() + ".");
        }
        return byCode;
    }

    private List<Bus> toDomainList(List<JSONObject> docs) {
        List<Bus> list = new ArrayList<>();
        for (JSONObject doc : docs) {
            list.add(fromDocument(doc));
        }
        return list;
    }

    private Bus fromDocument(JSONObject doc) {
        Bus bus = new Bus();
        bus.setId(FirestoreHelper.getDocumentId(doc));
        bus.setBusCode(FirestoreHelper.getString(doc, "busCode"));
        bus.setStatus(FirestoreHelper.getString(doc, "status"));
        bus.setBatteryLevel(FirestoreHelper.getNumber(doc, "batteryLevel"));
        bus.setDepot(FirestoreHelper.getString(doc, "depot"));
        bus.setAssignedDriverId(FirestoreHelper.getString(doc, "assignedDriverId"));
        bus.setCreatedAt(FirestoreHelper.getString(doc, "createdAt"));
        return bus;
    }
}
