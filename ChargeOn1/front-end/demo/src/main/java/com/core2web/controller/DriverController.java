package com.core2web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.core2web.dao.BusDao;
import com.core2web.dao.DriverDao;
import com.core2web.model.AuthSession;
import com.core2web.model.Bus;
import com.core2web.model.Driver;


public class DriverController {

    private final DriverDao driverDao = new DriverDao();
    private final BusDao busDao = new BusDao();

    /** Outcome of {@link #repairBusLinks()}, for reporting back to the admin. */
    public static final class LinkRepairReport {

        public final List<String> repaired = new ArrayList<>();
        public final List<String> skipped = new ArrayList<>();
        public int driversChecked;
        public int busesChecked;

        public boolean changedAnything() {
            return !repaired.isEmpty();
        }

        public String summary() {
            StringBuilder sb = new StringBuilder();
            sb.append("Checked ").append(busesChecked).append(" buses and ")
              .append(driversChecked).append(" drivers.\n\n");

            if (repaired.isEmpty()) {
                sb.append("No broken links found — nothing to repair.\n");
            } else {
                sb.append("Repaired ").append(repaired.size()).append(":\n");
                for (String line : repaired) {
                    sb.append("  • ").append(line).append('\n');
                }
            }

            if (!skipped.isEmpty()) {
                sb.append("\nNeeds a human decision (").append(skipped.size()).append("):\n");
                for (String line : skipped) {
                    sb.append("  • ").append(line).append('\n');
                }
            }
            return sb.toString();
        }
    }

    /**
     * Rewrites drifted driver<->bus links so both sides agree.
     *
     * <p>{@code Bus.assignedDriverId} is the source of truth, so where a bus names a
     * driver, that driver's {@code assignedBusId} is rewritten to the bus's document
     * ID. This is what corrects a value holding a busCode ("BUS05") instead of a doc
     * ID, a blank value, or a value left pointing at a reassigned bus.
     *
     * <p>Where no bus claims a driver but the driver claims a bus, the link is
     * adopted only if that bus is genuinely free. Anything genuinely ambiguous — two
     * buses claiming one driver, a driver claiming someone else's bus, a pointer to a
     * bus that does not exist — is reported and left untouched rather than guessed at.
     */
    public LinkRepairReport repairBusLinks() {
        LinkRepairReport report = new LinkRepairReport();

        AuthSession session = AuthSession.getCurrent();
        if (session == null) {
            report.skipped.add("No active session — not signed in.");
            return report;
        }

        String idToken = session.getIdToken();

        List<Bus> buses = busDao.getAllBuses(idToken);
        List<Driver> drivers = getAllDrivers();
        report.busesChecked = buses.size();
        report.driversChecked = drivers.size();

        Map<String, Driver> driverByUid = new HashMap<>();
        for (Driver d : drivers) {
            driverByUid.put(d.getUid(), d);
        }

        Map<String, Bus> busById = new HashMap<>();
        Map<String, Bus> busByCode = new HashMap<>();
        for (Bus b : buses) {
            busById.put(b.getId(), b);
            if (b.getBusCode() != null && !b.getBusCode().isEmpty()) {
                busByCode.put(b.getBusCode(), b);
            }
        }

        // Pass 1 — the authoritative direction: every bus that names a driver.
        Map<String, Bus> claimedDrivers = new HashMap<>();
        Set<String> ambiguousDrivers = new HashSet<>();
        for (Bus bus : buses) {
            if (!BusController.hasDriver(bus)) {
                continue;
            }
            String uid = bus.getAssignedDriverId();
            Bus already = claimedDrivers.put(uid, bus);
            if (already != null) {
                ambiguousDrivers.add(uid);
            }
        }

        for (Map.Entry<String, Bus> entry : claimedDrivers.entrySet()) {
            String uid = entry.getKey();
            Bus bus = entry.getValue();
            Driver driver = driverByUid.get(uid);

            if (driver == null) {
                report.skipped.add("Bus " + bus.getBusCode() + " names driver " + uid
                        + ", who has no Driver document — clear it by hand or reassign the bus.");
                continue;
            }

            if (ambiguousDrivers.contains(uid)) {
                report.skipped.add(driver.getName() + " is claimed by more than one bus — "
                        + "clear assignedDriverId on the wrong bus, then repair again.");
                continue;
            }

            if (bus.getId().equals(driver.getAssignedBusId())) {
                continue;
            }

            if (driverDao.updateAssignedBusId(uid, bus.getId(), idToken)) {
                report.repaired.add(driver.getName() + ": assignedBusId \""
                        + describe(driver.getAssignedBusId()) + "\" -> \"" + bus.getId()
                        + "\" (" + bus.getBusCode() + ")");
            } else {
                report.skipped.add(driver.getName() + ": write failed, assignedBusId unchanged.");
            }
        }

        // Pass 2 — drivers nobody claims, but who claim a bus themselves.
        for (Driver driver : drivers) {
            if (claimedDrivers.containsKey(driver.getUid())) {
                continue;
            }

            String declared = driver.getAssignedBusId();
            if (declared == null || declared.isEmpty() || "null".equals(declared)) {
                continue;
            }

            Bus target = busById.get(declared);
            if (target == null) {
                target = busByCode.get(declared);
            }

            if (target == null) {
                report.skipped.add(driver.getName() + " points at \"" + declared
                        + "\", which is not a bus id or bus code — no such bus exists.");
                continue;
            }

            if (BusController.hasDriver(target)) {
                report.skipped.add(driver.getName() + " points at " + target.getBusCode()
                        + ", which already belongs to another driver — reassign one of them.");
                continue;
            }

            // The bus is free and the driver claims it: complete the link both ways.
            if (driverDao.linkDriverAndBus(driver.getUid(), target.getId(), idToken)) {
                report.repaired.add(driver.getName() + ": linked to " + target.getBusCode()
                        + " on both sides (bus had no driver).");
            } else {
                report.skipped.add(driver.getName() + ": failed to link to "
                        + target.getBusCode() + ".");
            }
        }

        return report;
    }

    private static String describe(String value) {
        return value == null || value.isEmpty() ? "(empty)" : value;
    }

    public List<Driver> getAllDrivers() {
        AuthSession session = AuthSession.getCurrent();
        if (session == null) {
            return new ArrayList<>();
        }
        return driverDao.getAllDrivers(session.getIdToken());
    }

    public boolean addDriver(String name, String email, String phone,
                              String assignedBusId, String depot, String shift,
                              String password) {

        Driver driver = new Driver(
                null,
                name,
                email,
                phone,
                assignedBusId,
                depot,
                shift,
                "active",
                ""
        );

        return driverDao.addDriver(driver, password);
    }

    /**
     * Creates a driver and links them to {@code busId} on both sides
     * (Driver.assignedBusId and Bus.assignedDriverId). Returns false if either side
     * could not be written, rather than leaving a half-written link.
     */
    public boolean addDriverWithBus(String name, String email, String phone,
                                    String busId, String depot, String shift,
                                    String password) {

        Driver driver = new Driver(
                null,
                name,
                email,
                phone,
                busId,
                depot,
                shift,
                "active",
                ""
        );

        return driverDao.addDriverAndAssignBus(driver, password, busId) != null;
    }

    /** One driver by uid — a single document read, not a roster scan. */
    public Driver getDriver(String driverUid) {
        AuthSession session = AuthSession.getCurrent();
        if (session == null) {
            return null;
        }
        return driverDao.getDriver(driverUid, session.getIdToken());
    }

    /**
     * Re-points an existing driver at a different bus, keeping both sides in step.
     * Also releases the bus's previous holder — see
     * {@link DriverDao#linkDriverAndBus}.
     */
    public boolean assignBusToDriver(String driverUid, String busId) {
        AuthSession session = AuthSession.getCurrent();
        if (session == null) {
            return false;
        }
        return driverDao.linkDriverAndBus(driverUid, busId, session.getIdToken());
    }

    public Driver getCurrentDriver() {
        return driverDao.getCurrentDriver();
    }

    public boolean updatePhone(String phone) {
        return driverDao.updatePhone(phone);
    }

    public boolean updateProfileImageUrl(String imageUrl) {
        return driverDao.updateProfileImageUrl(imageUrl);
    }
}
