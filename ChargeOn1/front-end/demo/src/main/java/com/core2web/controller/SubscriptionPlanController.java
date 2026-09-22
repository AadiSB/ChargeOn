package com.core2web.controller;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.core2web.dao.SubscriptionPlanDao;
import com.core2web.model.AuthSession;
import com.core2web.model.SubscriptionPlan;

public class SubscriptionPlanController {

    private final SubscriptionPlanDao planDao = new SubscriptionPlanDao();

    public List<SubscriptionPlan> getAllPlans() {
        AuthSession session = AuthSession.getCurrent();
        if (session == null) {
            return new ArrayList<>();
        }
        return planDao.getAllPlans(session.getIdToken());
    }

    public String createPlan(String name, double priceInr, String billingCycle,
                              List<String> benefits, boolean active) {
        AuthSession session = AuthSession.getCurrent();
        if (session == null) {
            return null;
        }
        SubscriptionPlan plan = new SubscriptionPlan(
                null, name, priceInr, billingCycle, benefits, active, Instant.now().toString());
        return planDao.createPlan(plan, session.getIdToken());
    }

    public boolean updatePlan(SubscriptionPlan plan) {
        AuthSession session = AuthSession.getCurrent();
        if (session == null) {
            return false;
        }
        return planDao.updatePlan(plan, session.getIdToken());
    }

    public boolean deletePlan(String planId) {
        AuthSession session = AuthSession.getCurrent();
        if (session == null) {
            return false;
        }
        return planDao.deletePlan(planId, session.getIdToken());
    }
}
