package com.core2web.model;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionPlan {

    private String id;
    private String name;
    private double priceInr;
    private String billingCycle;
    private List<String> benefits = new ArrayList<>();
    private boolean active;
    private String createdAt;

    public SubscriptionPlan() {}

    public SubscriptionPlan(String id, String name, double priceInr, String billingCycle,
                             List<String> benefits, boolean active, String createdAt) {
        this.id = id;
        this.name = name;
        this.priceInr = priceInr;
        this.billingCycle = billingCycle;
        this.benefits = benefits;
        this.active = active;
        this.createdAt = createdAt;
    }

    public String getId()                  { return id; }
    public String getName()                { return name; }
    public double getPriceInr()            { return priceInr; }
    public String getBillingCycle()        { return billingCycle; }
    public List<String> getBenefits()      { return benefits; }
    public boolean isActive()              { return active; }
    public String getCreatedAt()           { return createdAt; }

    public void setId(String id)                       { this.id = id; }
    public void setName(String name)                   { this.name = name; }
    public void setPriceInr(double priceInr)           { this.priceInr = priceInr; }
    public void setBillingCycle(String billingCycle)   { this.billingCycle = billingCycle; }
    public void setBenefits(List<String> benefits)     { this.benefits = benefits; }
    public void setActive(boolean active)              { this.active = active; }
    public void setCreatedAt(String createdAt)         { this.createdAt = createdAt; }
}
