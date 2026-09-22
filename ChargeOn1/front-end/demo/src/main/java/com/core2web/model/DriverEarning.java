package com.core2web.model;

public class DriverEarning {

    private String id;
    private String driverId;
    private String bookingId;
    private String ownerId;
    private double amount;
    private double fare;
    private String createdAt;

    public DriverEarning() {}

    public DriverEarning(String id, String driverId, String bookingId, String ownerId,
                         double amount, double fare, String createdAt) {
        this.id = id;
        this.driverId = driverId;
        this.bookingId = bookingId;
        this.ownerId = ownerId;
        this.amount = amount;
        this.fare = fare;
        this.createdAt = createdAt;
    }

    public String getId()        { return id; }
    public String getDriverId()  { return driverId; }
    public String getBookingId() { return bookingId; }
    public String getOwnerId()   { return ownerId; }
    public double getAmount()    { return amount; }
    public double getFare()      { return fare; }
    public String getCreatedAt() { return createdAt; }

    public void setId(String id)               { this.id = id; }
    public void setDriverId(String driverId)   { this.driverId = driverId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }
    public void setOwnerId(String ownerId)     { this.ownerId = ownerId; }
    public void setAmount(double amount)       { this.amount = amount; }
    public void setFare(double fare)           { this.fare = fare; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
