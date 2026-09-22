package com.core2web.controller;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import com.core2web.dao.BusDao;
import com.core2web.dao.BusLocationDao;
import com.core2web.dao.DriverDao;
import com.core2web.model.AuthSession;
import com.core2web.model.Bus;
import com.core2web.model.BusLocation;
import com.core2web.model.Driver;

public class BusLocationController {

    private final BusLocationDao busLocationDao = new BusLocationDao();
    private final DriverDao driverDao = new DriverDao();
    private final BusDao busDao = new BusDao();

    public BusLocation getLocationForBus(String busId) {
        AuthSession session = AuthSession.getCurrent();
        if (session == null || busId == null || busId.isEmpty() || "null".equals(busId)) {
            return null;
        }
        return busLocationDao.getBusLocation(busId, session.getIdToken());
    }

    public Map<String, BusLocation> getAllBusLocations() {
        AuthSession session = AuthSession.getCurrent();
        if (session == null) {
            return new LinkedHashMap<>();
        }
        return busLocationDao.getAllLocations(session.getIdToken());
    }

    public BusLocation getLocationForCurrentDriver() {
        String busId = currentDriverBusId();
        if (busId == null) {
            return null;
        }
        return getLocationForBus(busId);
    }

    public boolean updateCurrentDriverLocation(double lat, double lng, String source) {
        AuthSession session = AuthSession.getCurrent();
        if (session == null) {
            System.out.println("Cannot update bus location: no active session.");
            return false;
        }

        String busId = currentDriverBusId();
        if (busId == null) {
            System.out.println("Cannot update bus location: caller has no assigned bus.");
            return false;
        }

        Bus bus = busDao.getBus(busId, session.getIdToken());
        if (bus == null) {
            System.out.println("Cannot update bus location: bus " + busId + " not found.");
            return false;
        }

        if (!session.getUid().equals(bus.getAssignedDriverId())) {
            System.out.println("Refusing to update location of bus " + busId
                    + ": caller is not the assigned driver.");
            return false;
        }

        BusLocation loc = new BusLocation(
                busId, lat, lng,
                0,
                0,
                source == null || source.isEmpty() ? BusLocation.SOURCE_MANUAL : source,
                Instant.now().toString(),
                session.getUid());

        return busLocationDao.upsertLocation(loc, session.getIdToken());
    }

    private String currentDriverBusId() {
        AuthSession session = AuthSession.getCurrent();
        if (session == null) {
            return null;
        }
        Driver driver = driverDao.getCurrentDriver();
        if (driver == null) {
            return null;
        }
        Bus bus = busDao.resolveBusForDriver(
                session.getUid(), driver.getAssignedBusId(), session.getIdToken());
        return bus == null ? null : bus.getId();
    }
}
