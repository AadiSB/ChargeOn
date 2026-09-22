package com.core2web.model;

public class BusLocation {

    public static final String SOURCE_MANUAL    = "MANUAL";
    public static final String SOURCE_SIMULATED = "SIMULATED";
    public static final String SOURCE_DEVICE    = "DEVICE";

    private static final long STALE_AFTER_MINUTES = 5;

    private String busId;
    private double latitude;
    private double longitude;
    private double heading;
    private double speedKph;
    private String source;
    private String updatedAt;
    private String updatedById;

    public BusLocation() {}

    public BusLocation(String busId, double latitude, double longitude, double heading,
                       double speedKph, String source, String updatedAt, String updatedById) {
        this.busId = busId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.heading = heading;
        this.speedKph = speedKph;
        this.source = source;
        this.updatedAt = updatedAt;
        this.updatedById = updatedById;
    }

    public String getBusId()        { return busId; }
    public double getLatitude()     { return latitude; }
    public double getLongitude()    { return longitude; }
    public double getHeading()      { return heading; }
    public double getSpeedKph()     { return speedKph; }
    public String getSource()       { return source; }
    public String getUpdatedAt()    { return updatedAt; }
    public String getUpdatedById() { return updatedById; }

    public void setBusId(String busId)               { this.busId = busId; }
    public void setLatitude(double latitude)         { this.latitude = latitude; }
    public void setLongitude(double longitude)       { this.longitude = longitude; }
    public void setHeading(double heading)           { this.heading = heading; }
    public void setSpeedKph(double speedKph)         { this.speedKph = speedKph; }
    public void setSource(String source)             { this.source = source; }
    public void setUpdatedAt(String updatedAt)       { this.updatedAt = updatedAt; }
    public void setUpdatedById(String updatedById) { this.updatedById = updatedById; }

    public boolean hasCoordinates() {
        return latitude != 0 || longitude != 0;
    }

    public long minutesSinceUpdate() {
        if (updatedAt == null || updatedAt.isEmpty()) {
            return -1;
        }
        try {
            return java.time.Duration.between(
                    java.time.Instant.parse(updatedAt),
                    java.time.Instant.now()).toMinutes();
        } catch (Exception e) {
            return -1;
        }
    }

    public boolean isStale() {
        long minutes = minutesSinceUpdate();
        return minutes < 0 || minutes > STALE_AFTER_MINUTES;
    }

    public String freshnessLabel() {
        long minutes = minutesSinceUpdate();
        if (minutes < 0)  return "position age unknown";
        if (minutes < 1)  return "position just now";
        if (minutes < 60) return "position " + minutes + " min ago";

        long hours = minutes / 60;
        if (hours < 24)   return "position " + hours + " hr ago";

        long days = hours / 24;
        return "position " + days + (days == 1 ? " day ago" : " days ago");
    }
}
