package com.core2web.model;

/**
 * A bus's CURRENT position — one document per bus, keyed by busId.
 *
 * <p>There is deliberately no position history: the app has no GPS and
 * FirestoreHelper has no orderBy/limit, so "most recent N" is not expressible as a
 * query. Keeping exactly one doc per bus makes every read an O(1)
 * {@code getDocument} instead.
 *
 * <p>Coordinates are two separate doubles, never a GeoPoint —
 * {@code FirestoreHelper.toFirestoreValue()} has no geoPointValue branch and would
 * silently stringify one.
 *
 * <p>"No BusLocation document" means <em>position unknown</em>, which is a
 * first-class state. It is never represented as 0,0.
 */
public class BusLocation {

    public static final String SOURCE_MANUAL    = "MANUAL";
    public static final String SOURCE_SIMULATED = "SIMULATED";
    public static final String SOURCE_DEVICE    = "DEVICE";

    /** A position older than this is shown as stale rather than live. */
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

    /**
     * Guards against a document that exists but carries no usable fix. A real
     * position is never exactly 0,0 for this fleet.
     */
    public boolean hasCoordinates() {
        return latitude != 0 || longitude != 0;
    }

    /** Minutes since this position was pushed, or -1 if updatedAt is missing/unparseable. */
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

    /** Stale when older than 5 minutes, or when the age can't be determined at all. */
    public boolean isStale() {
        long minutes = minutesSinceUpdate();
        return minutes < 0 || minutes > STALE_AFTER_MINUTES;
    }

    /** "position just now" / "position 3 min ago" / "position age unknown". */
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
