package com.core2web.controller;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.core2web.dao.VehicleDao;
import com.core2web.model.AuthSession;
import com.core2web.model.Vehicle;

public class VehicleController {

    private final VehicleDao vehicleDao = new VehicleDao();

    public List<Vehicle> getMyVehicles() {
        AuthSession session = AuthSession.getCurrent();
        if (session == null) {
            return new ArrayList<>();
        }
        return vehicleDao.getVehiclesForOwner(session.getUid(), session.getIdToken());
    }

    public List<Vehicle> getVehiclesForOwner(String ownerId) {
        AuthSession session = AuthSession.getCurrent();
        if (session == null) {
            return new ArrayList<>();
        }
        return vehicleDao.getVehiclesForOwner(ownerId, session.getIdToken());
    }

    public int getVehicleCountForOwner(String ownerId) {
        AuthSession session = AuthSession.getCurrent();
        if (session == null) {
            return 0;
        }
        return vehicleDao.getVehiclesForOwner(ownerId, session.getIdToken()).size();
    }

    public String addVehicle(String make, String model, String plateNumber, String connectorType,
                              double batteryCapacityKwh, boolean isPrimary, String photoUrl) {
        AuthSession session = AuthSession.getCurrent();
        if (session == null) {
            return null;
        }

        Vehicle vehicle = new Vehicle(
                null, session.getUid(), make, model, plateNumber,
                connectorType, batteryCapacityKwh, isPrimary, photoUrl,
                Instant.now().toString()
        );

        String newId = vehicleDao.addVehicle(vehicle, session.getIdToken());
        if (newId != null && isPrimary) {
            vehicleDao.setPrimary(newId, session.getUid(), session.getIdToken());
        }
        return newId;
    }

    public boolean updateVehicle(Vehicle vehicle) {
        AuthSession session = AuthSession.getCurrent();
        if (session == null) {
            return false;
        }

        boolean ok = vehicleDao.updateVehicle(vehicle, session.getIdToken());
        if (ok && vehicle.isPrimary()) {
            vehicleDao.setPrimary(vehicle.getId(), session.getUid(), session.getIdToken());
        }
        return ok;
    }

    public boolean setPrimary(String vehicleId) {
        AuthSession session = AuthSession.getCurrent();
        if (session == null) {
            return false;
        }
        return vehicleDao.setPrimary(vehicleId, session.getUid(), session.getIdToken());
    }

    public boolean deleteVehicle(String vehicleId) {
        AuthSession session = AuthSession.getCurrent();
        if (session == null) {
            return false;
        }
        return vehicleDao.deleteVehicle(vehicleId, session.getIdToken());
    }
}
