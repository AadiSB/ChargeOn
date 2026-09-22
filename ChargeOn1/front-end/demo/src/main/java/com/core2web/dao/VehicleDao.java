package com.core2web.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.core2web.model.Vehicle;

public class VehicleDao {

    private static final String COLLECTION = "vehicles";


    public List<Vehicle> getVehiclesForOwner(String ownerId, String idToken) {
        List<JSONObject> docs = FirestoreHelper.queryWithFilters(
                COLLECTION,
                Arrays.asList(new FirestoreHelper.Filter("ownerId", "EQUAL", ownerId)),
                idToken
        );
        return toDomainList(docs);
    }


    public String addVehicle(Vehicle vehicle, String idToken) {
        Map<String, Object> fields = Map.of(
                "ownerId", vehicle.getOwnerId(),
                "make", vehicle.getMake(),
                "model", vehicle.getModel(),
                "plateNumber", vehicle.getPlateNumber(),
                "connectorType", vehicle.getConnectorType(),
                "batteryCapacityKwh", vehicle.getBatteryCapacityKwh(),
                "isPrimary", vehicle.isPrimary(),
                "photoUrl", vehicle.getPhotoUrl(),
                "createdAt", vehicle.getCreatedAt()
        );
        return FirestoreHelper.createDocument(COLLECTION, fields, idToken);
    }


    public boolean updateVehicle(Vehicle vehicle, String idToken) {
        Map<String, Object> fields = Map.of(
                "make", vehicle.getMake(),
                "model", vehicle.getModel(),
                "plateNumber", vehicle.getPlateNumber(),
                "connectorType", vehicle.getConnectorType(),
                "batteryCapacityKwh", vehicle.getBatteryCapacityKwh(),
                "isPrimary", vehicle.isPrimary(),
                "photoUrl", vehicle.getPhotoUrl()
        );
        return FirestoreHelper.updateFields(COLLECTION, vehicle.getId(), fields, idToken);
    }


    public boolean setPrimary(String vehicleId, String ownerId, String idToken) {
        for (Vehicle v : getVehiclesForOwner(ownerId, idToken)) {
            if (v.isPrimary() && !v.getId().equals(vehicleId)) {
                FirestoreHelper.updateFields(COLLECTION, v.getId(), Map.of("isPrimary", false), idToken);
            }
        }
        return FirestoreHelper.updateFields(COLLECTION, vehicleId, Map.of("isPrimary", true), idToken);
    }


    public boolean deleteVehicle(String vehicleId, String idToken) {
        return FirestoreHelper.deleteDocument(COLLECTION, vehicleId, idToken);
    }


    private List<Vehicle> toDomainList(List<JSONObject> docs) {
        List<Vehicle> list = new ArrayList<>();
        for (JSONObject doc : docs) {
            list.add(fromDocument(doc));
        }
        return list;
    }

    private Vehicle fromDocument(JSONObject doc) {
        Vehicle v = new Vehicle();
        v.setId(FirestoreHelper.getDocumentId(doc));
        v.setOwnerId(FirestoreHelper.getString(doc, "ownerId"));
        v.setMake(FirestoreHelper.getString(doc, "make"));
        v.setModel(FirestoreHelper.getString(doc, "model"));
        v.setPlateNumber(FirestoreHelper.getString(doc, "plateNumber"));
        v.setConnectorType(FirestoreHelper.getString(doc, "connectorType"));
        v.setBatteryCapacityKwh(FirestoreHelper.getNumber(doc, "batteryCapacityKwh"));
        v.setPrimary(FirestoreHelper.getBoolean(doc, "isPrimary"));
        v.setPhotoUrl(FirestoreHelper.getString(doc, "photoUrl"));
        v.setCreatedAt(FirestoreHelper.getString(doc, "createdAt"));
        return v;
    }
}
