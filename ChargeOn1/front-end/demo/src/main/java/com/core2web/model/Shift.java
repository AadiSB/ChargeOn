package com.core2web.model;

import java.time.Duration;
import java.time.Instant;

import com.core2web.util.DateTimeUtil;

public class Shift {

    private String id;
    private String driverId;
    private String busId;
    private String depot;
    private String clockInAt;
    private String clockOutAt;
    private String createdAt;

    public Shift() {}

    public Shift(String id, String driverId, String busId, String depot,
                 String clockInAt, String clockOutAt, String createdAt) {
        this.id = id;
        this.driverId = driverId;
        this.busId = busId;
        this.depot = depot;
        this.clockInAt = clockInAt;
        this.clockOutAt = clockOutAt;
        this.createdAt = createdAt;
    }

    public String getId()          { return id; }
    public String getDriverId()    { return driverId; }
    public String getBusId()       { return busId; }
    public String getDepot()       { return depot; }
    public String getClockInAt()   { return clockInAt; }
    public String getClockOutAt()  { return clockOutAt; }
    public String getCreatedAt()   { return createdAt; }

    public void setId(String id)                   { this.id = id; }
    public void setDriverId(String driverId)       { this.driverId = driverId; }
    public void setBusId(String busId)             { this.busId = busId; }
    public void setDepot(String depot)             { this.depot = depot; }
    public void setClockInAt(String clockInAt)     { this.clockInAt = clockInAt; }
    public void setClockOutAt(String clockOutAt)   { this.clockOutAt = clockOutAt; }
    public void setCreatedAt(String createdAt)     { this.createdAt = createdAt; }

    public boolean isActive() {
        return clockOutAt == null || clockOutAt.isEmpty();
    }

    public Duration elapsed() {
        try {
            Instant start = DateTimeUtil.parse(clockInAt);
            Instant end = isActive() ? Instant.now() : DateTimeUtil.parse(clockOutAt);
            if (start == null || end == null) return Duration.ZERO;
            return Duration.between(start, end);
        } catch (Exception e) {
            return Duration.ZERO;
        }
    }
}
