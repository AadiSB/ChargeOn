package com.core2web.controller;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.core2web.dao.AlertDao;
import com.core2web.dao.BusDao;
import com.core2web.dao.DriverDao;
import com.core2web.model.Alert;
import com.core2web.model.AuthSession;
import com.core2web.model.Bus;
import com.core2web.model.Driver;

public class AlertController {

    private final AlertDao alertDao = new AlertDao();
    private final DriverDao driverDao = new DriverDao();
    private final BusDao busDao = new BusDao();
    private final NotificationController notificationController =
            new NotificationController();

    public List<Alert> getMyReports() {
        AuthSession session = AuthSession.getCurrent();

        if (session == null) {
            return new ArrayList<>();
        }

        return alertDao.getAlertsForDriver(
                session.getUid(),
                session.getIdToken()
        );
    }

    public String submitAlert(
            String issueType,
            String severity,
            String description) {

        AuthSession session = AuthSession.getCurrent();

        if (session == null) {
            return null;
        }

        Driver driver = driverDao.getCurrentDriver();

        Bus bus = driver == null
                ? null
                : busDao.resolveBusForDriver(
                        session.getUid(),
                        driver.getAssignedBusId(),
                        session.getIdToken()
                );

        String busId = bus == null
                ? ""
                : bus.getId();

        String busCode = bus == null
                ? ""
                : bus.getBusCode();

        Alert alert = new Alert(
                null,
                session.getUid(),
                busId,
                issueType,
                severity,
                description,
                "OPEN",
                Instant.now().toString()
        );

        String newId = alertDao.createAlert(
                alert,
                session.getIdToken()
        );

        if (newId != null) {

            String busRef = busCode.isEmpty()
                    ? ""
                    : " on " + busCode;

            notificationController.notifyAdmins(
                    "critical_alert",
                    issueType
                            + " reported"
                            + busRef
                            + ": "
                            + description,
                    newId
            );
        }

        return newId;
    }

    public boolean cancelAlert(String alertId) {

        AuthSession session = AuthSession.getCurrent();

        if (session == null
                || alertId == null
                || alertId.trim().isEmpty()) {

            return false;
        }

        return alertDao.cancelAlert(
                alertId,
                session.getIdToken()
        );
    }

    public void deleteAlert(String alertId) {

        alertDao.deleteAlert(
                alertId,
                AuthSession.getCurrent().getIdToken()
        );
    }
}