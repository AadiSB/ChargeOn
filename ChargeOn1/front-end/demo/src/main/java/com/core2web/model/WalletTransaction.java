package com.core2web.model;

public class WalletTransaction {

    private String id;
    private String ownerId;
    private String type;
    private double amount;
    private String method;
    private String createdAt;
    private String bookingId;

    public WalletTransaction() {}

    public WalletTransaction(String id, String ownerId, String type,
                             double amount, String method,
                             String createdAt, String bookingId) {
        this.id = id;
        this.ownerId = ownerId;
        this.type = type;
        this.amount = amount;
        this.method = method;
        this.createdAt = createdAt;
        this.bookingId = bookingId;
    }


    public String getId()        { return id; }
    public String getOwnerId()   { return ownerId; }
    public String getType()      { return type; }
    public double getAmount()    { return amount; }
    public String getMethod()    { return method; }
    public String getCreatedAt() { return createdAt; }
    public String getBookingId() { return bookingId; }


    public void setId(String id)               { this.id = id; }
    public void setOwnerId(String ownerId)     { this.ownerId = ownerId; }
    public void setType(String type)           { this.type = type; }
    public void setAmount(double amount)       { this.amount = amount; }
    public void setMethod(String method)       { this.method = method; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public boolean isTopUp() {
        return "top_up".equalsIgnoreCase(type);
    }

    public boolean isChargePayment() {
        return "charge_payment".equalsIgnoreCase(type);
    }

    public String activityLabel() {
        String amt = "₹" + (int) amount;
        if (isTopUp()) {
            String suffix = (method != null && !method.isEmpty()) ? " · " + method : "";
            return "Wallet top-up " + amt + suffix;
        } else {
            String bkg = (bookingId != null && !bookingId.isEmpty())
                    ? " · " + bookingId : "";
            return "Session completed" + bkg + " · " + amt;
        }
    }

    public String activityDotColor() {
        return isTopUp() ? "#F59E0B" : "#64748b";
    }
}
