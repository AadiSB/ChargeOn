package com.core2web.model;

public class Bus {

    private String id;
    private String busCode;
    private String status;
    private double batteryLevel;
    private String depot;
    private String assignedDriverId;
    private String createdAt;

    // Position deliberately does NOT live here — see model/BusLocation.java. A bus
    // with no BusLocation document has an unknown position, which is a distinct
    // state from 0,0 and must stay distinguishable.

    public Bus() {}

    public Bus(String id, String busCode, String status, double batteryLevel,
               String depot, String assignedDriverId, String createdAt) {
        this.id = id;
        this.busCode = busCode;
        this.status = status;
        this.batteryLevel = batteryLevel;
        this.depot = depot;
        this.assignedDriverId = assignedDriverId;
        this.createdAt = createdAt;
    }


    public String getId()               { return id; }
    public String getBusCode()          { return busCode; }
    public String getStatus()           { return status; }
    public double getBatteryLevel()     { return batteryLevel; }
    public String getDepot()            { return depot; }
    public String getAssignedDriverId() { return assignedDriverId; }
    public String getCreatedAt()        { return createdAt; }


    public void setId(String id)                             { this.id = id; }
    public void setBusCode(String busCode)                   { this.busCode = busCode; }
    public void setStatus(String status)                     { this.status = status; }
    public void setBatteryLevel(double batteryLevel)         { this.batteryLevel = batteryLevel; }
    public void setDepot(String depot)                       { this.depot = depot; }
    public void setAssignedDriverId(String assignedDriverId) { this.assignedDriverId = assignedDriverId; }
    public void setCreatedAt(String createdAt)               { this.createdAt = createdAt; }

    public boolean isLowBattery() {
        return batteryLevel > 0 && batteryLevel < 20;
    }
}
