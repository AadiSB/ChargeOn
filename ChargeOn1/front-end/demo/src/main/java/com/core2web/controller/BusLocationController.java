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

/**
 * Read/write access to current bus positions.
 *
 * <p>Reads are open to any signed-in user (admins and owners need to see where the
 * fleet is). Writes are restricted to the driver actually assigned to the bus — see
 * {@link #updateCurrentDriverLocation}.
 */
public class BusLocationController {

    private final BusLocationDao busLocationDao = new BusLocationDao();
    private final DriverDao driverDao = new DriverDao();
    private final BusDao busDao = new BusDao();

    /** @return current position, or null when unknown (no session, no busId, or no doc). */
    public BusLocation getLocationForBus(String busId) {
        AuthSession session = AuthSession.getCurrent();
        if (session == null || busId == null || busId.isEmpty() || "null".equals(busId)) {
            return null;
        }
        return busLocationDao.getBusLocation(busId, session.getIdToken());
    }

    /** Every known position keyed by busId, in a single round-trip. Empty when no session. */
    public Map<String, BusLocation> getAllBusLocations() {
        AuthSession session = AuthSession.getCurrent();
        if (session == null) {
            return new LinkedHashMap<>();
        }
        return busLocationDao.getAllLocations(session.getIdToken());
    }

    /** Position of the bus the signed-in driver is assigned to, or null if unknown. */
    public BusLocation getLocationForCurrentDriver() {
        String busId = currentDriverBusId();
        if (busId == null) {
            return null;
        }
        return getLocationForBus(busId);
    }

    /**
     * Pushes a new position for the signed-in driver's own bus.
     *
     * <p>Refuses — returning false without writing — when there is no session, when
     * the caller has no assigned bus, or when the bus's {@code assignedDriverId} is
     * not the caller. That last check is what stops an owner or another driver from
     * moving someone else's bus. It is a client-side guard only; the authoritative
     * check is the Firestore security rule for the BusLocation collection.
     */
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
                0,   // no heading source on a desktop client
                0,   // no speed source on a desktop client
                source == null || source.isEmpty() ? BusLocation.SOURCE_MANUAL : source,
                Instant.now().toString(),
                session.getUid());

        return busLocationDao.upsertLocation(loc, session.getIdToken());
    }

    /**
     * The signed-in driver's canonical bus document ID, or null if they aren't a
     * driver / have no bus.
     *
     * <p>Resolved through {@link com.core2web.dao.BusDao#resolveBusForDriver} rather
     * than read straight off {@code Driver.assignedBusId}: BusLocation documents are
     * keyed by bus ID, so a busCode-shaped or stale value would write the position
     * to a document that no reader ever looks at.
     */
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
