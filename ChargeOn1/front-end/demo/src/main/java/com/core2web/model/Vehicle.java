package com.core2web.model;

public class Vehicle {

    private String id;
    private String ownerId;
    private String make;
    private String model;
    private String plateNumber;
    private String connectorType;
    private double batteryCapacityKwh;
    private boolean primary;
    private String photoUrl;
    private String createdAt;

    public Vehicle() {}

    public Vehicle(String id, String ownerId, String make, String model, String plateNumber,
                   String connectorType, double batteryCapacityKwh, boolean primary,
                   String photoUrl, String createdAt) {
        this.id = id;
        this.ownerId = ownerId;
        this.make = make;
        this.model = model;
        this.plateNumber = plateNumber;
        this.connectorType = connectorType;
        this.batteryCapacityKwh = batteryCapacityKwh;
        this.primary = primary;
        this.photoUrl = photoUrl;
        this.createdAt = createdAt;
    }


    public String getId()                   { return id; }
    public String getOwnerId()              { return ownerId; }
    public String getMake()                 { return make; }
    public String getModel()                { return model; }
    public String getPlateNumber()           { return plateNumber; }
    public String getConnectorType()        { return connectorType; }
    public double getBatteryCapacityKwh()   { return batteryCapacityKwh; }
    public boolean isPrimary()              { return primary; }
    public String getPhotoUrl()             { return photoUrl; }
    public String getCreatedAt()            { return createdAt; }


    public void setId(String id)                                     { this.id = id; }
    public void setOwnerId(String ownerId)                          { this.ownerId = ownerId; }
    public void setMake(String make)                                { this.make = make; }
    public void setModel(String model)                              { this.model = model; }
    public void setPlateNumber(String plateNumber)                  { this.plateNumber = plateNumber; }
    public void setConnectorType(String connectorType)              { this.connectorType = connectorType; }
    public void setBatteryCapacityKwh(double batteryCapacityKwh)    { this.batteryCapacityKwh = batteryCapacityKwh; }
    public void setPrimary(boolean primary)                         { this.primary = primary; }
    public void setPhotoUrl(String photoUrl)                        { this.photoUrl = photoUrl; }
    public void setCreatedAt(String createdAt)                      { this.createdAt = createdAt; }

    public String displayName() {
        return (make == null ? "" : make + " ") + (model == null ? "" : model);
    }

    public String detailLine() {
        StringBuilder sb = new StringBuilder();
        if (plateNumber != null && !plateNumber.isEmpty()) sb.append(plateNumber);
        if (connectorType != null && !connectorType.isEmpty()) {
            if (sb.length() > 0) sb.append(" · ");
            sb.append(connectorType);
        }
        if (batteryCapacityKwh > 0) {
            if (sb.length() > 0) sb.append(" · ");
            sb.append(batteryCapacityKwh).append(" kWh");
        }
        return sb.toString();
    }
}
