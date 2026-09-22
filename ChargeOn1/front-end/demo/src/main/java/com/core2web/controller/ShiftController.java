package com.core2web.controller;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.core2web.dao.BusDao;
import com.core2web.dao.ShiftDao;
import com.core2web.model.AuthSession;
import com.core2web.model.Bus;
import com.core2web.model.Driver;
import com.core2web.model.Shift;

public class ShiftController {

    private final ShiftDao shiftDao = new ShiftDao();
    private final DriverController driverController = new DriverController();
    private final BusDao busDao = new BusDao();

    public Shift getMyActiveShift() {
        AuthSession session = AuthSession.getCurrent();
        if (session == null) {
            return null;
        }
        return shiftDao.getActiveShift(session.getUid(), session.getIdToken());
    }

    public List<Shift> getMyShiftHistory() {
        AuthSession session = AuthSession.getCurrent();
        if (session == null) {
            return new ArrayList<>();
        }
        return shiftDao.getShiftsForDriver(session.getUid(), session.getIdToken());
    }

    public boolean clockIn() {
        AuthSession session = AuthSession.getCurrent();
        if (session == null) {
            return false;
        }
        Driver driver = driverController.getCurrentDriver();
        if (driver == null) {
            return false;
        }

        Bus bus = busDao.resolveBusForDriver(
                session.getUid(), driver.getAssignedBusId(), session.getIdToken());

        Shift shift = new Shift(
                null, session.getUid(), bus == null ? "" : bus.getId(), driver.getDepot(),
                Instant.now().toString(), "", Instant.now().toString());
        return shiftDao.clockIn(shift, session.getIdToken()) != null;
    }

    public boolean clockOut(String shiftId) {
        AuthSession session = AuthSession.getCurrent();
        if (session == null) {
            return false;
        }
        return shiftDao.clockOut(shiftId, Instant.now().toString(), session.getIdToken());
    }
}
