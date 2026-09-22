package com.core2web.controller;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.core2web.dao.DriverEarningDao;
import com.core2web.model.AuthSession;
import com.core2web.model.DriverEarning;

public class DriverEarningController {

    private final DriverEarningDao earningDao = new DriverEarningDao();

    public List<DriverEarning> getMyEarnings() {
        AuthSession session = AuthSession.getCurrent();
        if (session == null) {
            return new ArrayList<>();
        }
        return earningDao.getEarningsForDriver(session.getUid(), session.getIdToken());
    }

    public List<DriverEarning> getMyEarningsToday() {
        AuthSession session = AuthSession.getCurrent();
        if (session == null) {
            return new ArrayList<>();
        }
        ZonedDateTime startOfDay = ZonedDateTime.now(ZoneId.of("UTC"))
                .withHour(0).withMinute(0).withSecond(0).withNano(0);
        return earningDao.getEarningsSince(
                session.getUid(), startOfDay.toInstant(), session.getIdToken());
    }

    public double getMyTotalEarned() {
        double total = 0;
        for (DriverEarning e : getMyEarnings()) {
            total += e.getAmount();
        }
        return total;
    }

    public double getMyEarnedSince(Instant since) {
        AuthSession session = AuthSession.getCurrent();
        if (session == null) {
            return 0;
        }
        double total = 0;
        for (DriverEarning e : earningDao.getEarningsSince(
                session.getUid(), since, session.getIdToken())) {
            total += e.getAmount();
        }
        return total;
    }
}
