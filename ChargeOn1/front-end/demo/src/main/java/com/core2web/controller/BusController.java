package com.core2web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.core2web.dao.BusDao;
import com.core2web.model.AuthSession;
import com.core2web.model.Bus;
import com.core2web.model.BusLocation;
import com.core2web.model.Driver;

public class BusController {

    public static class FleetStatusCounts {

        public final int total;
        public final int enRoute;
        public final int idle;
        public final int charging;
        public final int fault;

        public FleetStatusCounts(int total, int enRoute, int idle, int charging, int fault) {
            this.total = total;
            this.enRoute = enRoute;
            this.idle = idle;
            this.charging = charging;
            this.fault = fault;
        }

        public int online() {
            return enRoute + idle + charging;
        }
    }

    private final BusDao busDao = new BusDao();
    private final DriverController driverController = new DriverController();
    private final BusLocationController busLocationController = new BusLocationController();

    public List<Bus> getAllBuses() {
        AuthSession session = AuthSession.getCurrent();
        if (session == null) {
            return new ArrayList<>();
        }
        return busDao.getAllBuses(session.getIdToken());
    }

    public Bus getBus(String busId) {
        AuthSession session = AuthSession.getCurrent();
        if (session == null || busId == null || busId.isEmpty()) {
            return null;
        }
        return busDao.getBus(busId, session.getIdToken());
    }

    public Bus getBusForCurrentDriver() {
        AuthSession session = AuthSession.getCurrent();
        if (session == null) {
            return null;
        }
        Driver driver = driverController.getCurrentDriver();
        if (driver == null) {
            return null;
        }
        return busDao.resolveBusForDriver(
                session.getUid(), driver.getAssignedBusId(), session.getIdToken());
    }

    public String getBusIdForCurrentDriver() {
        Bus bus = getBusForCurrentDriver();
        return bus == null ? null : bus.getId();
    }

    public FleetStatusCounts getFleetStatusCounts() {
        List<Bus> buses = getAllBuses();

        int enRoute = 0, idle = 0, charging = 0, fault = 0;
        for (Bus bus : buses) {
            switch (getDisplayStatus(bus)) {
                case "EN_ROUTE": enRoute++; break;
                case "CHARGING": charging++; break;
                case "FAULT":    fault++;    break;
                default:         idle++;     break;
            }
        }

        return new FleetStatusCounts(buses.size(), enRoute, idle, charging, fault);
    }

    public List<Bus> getLowBatteryBuses() {
        List<Bus> lowBattery = new ArrayList<>();
        for (Bus bus : getAllBuses()) {
            if (bus.isLowBattery()) {
                lowBattery.add(bus);
            }
        }
        return lowBattery;
    }

    public List<Bus> getAvailableBuses() {
        List<Bus> available = new ArrayList<>();
        for (Bus bus : getAllBuses()) {
            if ("AVAILABLE".equals(bus.getStatus())) {
                available.add(bus);
            }
        }
        return available;
    }

    public List<Bus> getUnassignedBuses() {
        List<Bus> unassigned = new ArrayList<>();
        for (Bus bus : getAllBuses()) {
            if (!hasDriver(bus)) {
                unassigned.add(bus);
            }
        }
        return unassigned;
    }

    public Bus getNearestAvailableBus(double lat, double lng) {
        Map<String, BusLocation> positions = busLocationController.getAllBusLocations();

        Bus nearest = null;
        double nearestKm = Double.MAX_VALUE;

        for (Bus bus : getAvailableBuses()) {
            BusLocation loc = positions.get(bus.getId());
            if (loc == null || !loc.hasCoordinates()) {
                continue;
            }
            double km = distanceKm(lat, lng, loc.getLatitude(), loc.getLongitude());
            if (km < nearestKm) {
                nearestKm = km;
                nearest = bus;
            }
        }

        return nearest;
    }

    public static double distanceKm(double lat1, double lng1, double lat2, double lng2) {
        final double earthRadiusKm = 6371.0;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);

        return earthRadiusKm * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    public Map<String, String> getBusCodeLookup() {
        Map<String, String> lookup = new HashMap<>();
        for (Bus bus : getAllBuses()) {
            String code = bus.getBusCode();
            if (code == null || code.isEmpty()) {
                continue;
            }
            lookup.put(bus.getId(), code);
            lookup.put(code, code);
        }
        return lookup;
    }

    public static String busCodeLabel(String busIdOrCode, Map<String, String> lookup,
                                      String unknownLabel) {
        if (busIdOrCode == null || busIdOrCode.isEmpty() || "null".equals(busIdOrCode)) {
            return unknownLabel;
        }
        String code = lookup.get(busIdOrCode);
        return code == null || code.isEmpty() ? unknownLabel : code;
    }

    public String busCodeFor(String busIdOrCode, String unknownLabel) {
        if (busIdOrCode == null || busIdOrCode.isEmpty() || "null".equals(busIdOrCode)) {
            return unknownLabel;
        }
        Bus bus = getBus(busIdOrCode);
        if (bus == null) {
            bus = busDao.getBusByCode(busIdOrCode, AuthSession.getCurrent() == null
                    ? "" : AuthSession.getCurrent().getIdToken());
        }
        return bus == null || bus.getBusCode() == null || bus.getBusCode().isEmpty()
                ? unknownLabel
                : bus.getBusCode();
    }

    public static boolean hasDriver(Bus bus) {
        String driverId = bus == null ? null : bus.getAssignedDriverId();
        return driverId != null && !driverId.isEmpty() && !"null".equals(driverId);
    }

    public String getDisplayStatus(Bus bus) {
        switch (bus.getStatus()) {
            case "CHARGING": return "CHARGING";
            case "FAULT":    return "FAULT";
            case "AVAILABLE":
                return hasDriver(bus) ? "EN_ROUTE" : "IDLE";
            default: return "IDLE";
        }
    }

    public String getDisplayColor(String displayStatus) {
        switch (displayStatus) {
            case "EN_ROUTE": return "#3b82f6";
            case "CHARGING": return "#f59e0b";
            case "FAULT":    return "#ef4444";
            default:         return "#10b981";
        }
    }
}
