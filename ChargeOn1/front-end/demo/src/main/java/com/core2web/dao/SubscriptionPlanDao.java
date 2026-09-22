package com.core2web.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.core2web.model.SubscriptionPlan;

public class SubscriptionPlanDao {

    private static final String COLLECTION = "subscriptionPlans";


    public List<SubscriptionPlan> getAllPlans(String idToken) {
        List<JSONObject> docs = FirestoreHelper.listCollection(COLLECTION, idToken);
        List<SubscriptionPlan> plans = new ArrayList<>();
        for (JSONObject doc : docs) {
            plans.add(fromDocument(doc));
        }
        return plans;
    }


    public String createPlan(SubscriptionPlan plan, String idToken) {
        Map<String, Object> fields = Map.of(
                "name", plan.getName(),
                "priceInr", plan.getPriceInr(),
                "billingCycle", plan.getBillingCycle(),
                "benefits", plan.getBenefits(),
                "active", plan.isActive(),
                "createdAt", plan.getCreatedAt()
        );
        return FirestoreHelper.createDocument(COLLECTION, fields, idToken);
    }


    public boolean updatePlan(SubscriptionPlan plan, String idToken) {
        Map<String, Object> fields = Map.of(
                "name", plan.getName(),
                "priceInr", plan.getPriceInr(),
                "billingCycle", plan.getBillingCycle(),
                "benefits", plan.getBenefits(),
                "active", plan.isActive()
        );
        return FirestoreHelper.updateFields(COLLECTION, plan.getId(), fields, idToken);
    }


    public boolean deletePlan(String planId, String idToken) {
        return FirestoreHelper.deleteDocument(COLLECTION, planId, idToken);
    }


    private SubscriptionPlan fromDocument(JSONObject doc) {
        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setId(FirestoreHelper.getDocumentId(doc));
        plan.setName(FirestoreHelper.getString(doc, "name"));
        plan.setPriceInr(FirestoreHelper.getNumber(doc, "priceInr"));
        plan.setBillingCycle(FirestoreHelper.getString(doc, "billingCycle"));
        plan.setBenefits(FirestoreHelper.getStringList(doc, "benefits"));
        plan.setActive(FirestoreHelper.getBoolean(doc, "active"));
        plan.setCreatedAt(FirestoreHelper.getString(doc, "createdAt"));
        return plan;
    }
}
