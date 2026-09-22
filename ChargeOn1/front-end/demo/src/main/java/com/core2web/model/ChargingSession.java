package com.core2web.model;

public class ChargingSession {

    private String id;
    private String ownerId;
    private String bookingId;
    private String status;
    private double batteryPct;
    private double currentKwh;
    private double totalKwh;
    private double powerKw;
    private double runningCost;
    private int    targetSoc;
    private String startedAt;
    private String completedAt;

    public ChargingSession() {}

    public ChargingSession(String id, String ownerId, String bookingId,
                           String status, double batteryPct,
                           double currentKwh, double totalKwh,
                           double powerKw, double runningCost,
                           int targetSoc, String startedAt, String completedAt) {
        this.id = id;
        this.ownerId = ownerId;
        this.bookingId = bookingId;
        this.status = status;
        this.batteryPct = batteryPct;
        this.currentKwh = currentKwh;
        this.totalKwh = totalKwh;
        this.powerKw = powerKw;
        this.runningCost = runningCost;
        this.targetSoc = targetSoc;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
    }

    public String getId()          { return id; }
    public String getOwnerId()     { return ownerId; }
    public String getBookingId()   { return bookingId; }
    public String getStatus()      { return status; }
    public double getBatteryPct()  { return batteryPct; }
    public double getCurrentKwh()  { return currentKwh; }
    public double getTotalKwh()    { return totalKwh; }
    public double getPowerKw()     { return powerKw; }
    public double getRunningCost() { return runningCost; }
    public int    getTargetSoc()   { return targetSoc; }
    public String getStartedAt()   { return startedAt; }
    public String getCompletedAt() { return completedAt; }

    public void setId(String id)                   { this.id = id; }
    public void setOwnerId(String ownerId)         { this.ownerId = ownerId; }
    public void setBookingId(String bookingId)     { this.bookingId = bookingId; }
    public void setStatus(String status)           { this.status = status; }
    public void setBatteryPct(double batteryPct)   { this.batteryPct = batteryPct; }
    public void setCurrentKwh(double currentKwh)   { this.currentKwh = currentKwh; }
    public void setTotalKwh(double totalKwh)       { this.totalKwh = totalKwh; }
    public void setPowerKw(double powerKw)         { this.powerKw = powerKw; }
    public void setRunningCost(double runningCost) { this.runningCost = runningCost; }
    public void setTargetSoc(int targetSoc)        { this.targetSoc = targetSoc; }
    public void setStartedAt(String startedAt)     { this.startedAt = startedAt; }
    public void setCompletedAt(String completedAt) { this.completedAt = completedAt; }

    public static final String STATUS_ACTIVE    = "active";
    public static final String STATUS_COMPLETED = "completed";

    public boolean isActive() {
        return STATUS_ACTIVE.equalsIgnoreCase(status);
    }

    public boolean hasLiveMeter() {
        return powerKw > 0 || currentKwh > 0 || batteryPct > 0;
    }

    public String elapsedLabel() {
        if (startedAt == null || startedAt.isEmpty()) {
            return "—";
        }
        try {
            long minutes = java.time.Duration.between(
                    java.time.Instant.parse(startedAt), java.time.Instant.now()).toMinutes();
            if (minutes < 1)  return "just started";
            if (minutes < 60) return minutes + " min";
            long hours = minutes / 60;
            long rest = minutes % 60;
            return rest == 0 ? hours + " hr" : hours + " hr " + rest + " min";
        } catch (Exception e) {
            return "—";
        }
    }

    public int estimatedMinutesLeft() {
        if (powerKw <= 0 || totalKwh <= currentKwh) return -1;
        double remainingKwh = totalKwh - currentKwh;
        return (int) Math.ceil((remainingKwh / powerKw) * 60);
    }
}
