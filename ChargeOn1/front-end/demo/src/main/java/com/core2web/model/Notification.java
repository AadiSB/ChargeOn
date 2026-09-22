package com.core2web.model;

public class Notification {

    private String id;
    private String audience;
    private String userId;
    private String type;
    private String message;
    private String linkedEntityId;
    private String createdAt;

    public Notification(String id, String audience, String userId, String type,
                         String message, String linkedEntityId, String createdAt) {
        this.id = id;
        this.audience = audience;
        this.userId = userId;
        this.type = type;
        this.message = message;
        this.linkedEntityId = linkedEntityId;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public String getAudience() { return audience; }
    public String getUserId() { return userId; }
    public String getType() { return type; }
    public String getMessage() { return message; }
    public String getLinkedEntityId() { return linkedEntityId; }
    public String getCreatedAt() { return createdAt; }

    public String displayTitle() {
        switch (type) {
            case "critical_alert": return "Critical alert";
            case "route_updated": return "Route updated";
            case "booking_assigned": return "Booking assigned";
            case "emergency_diversion": return "Emergency booking diverted";
            case "emergency_dispatch": return "Emergency dispatched";
            case "emergency_rejected": return "Emergency declined";
            case "booking_cancelled": return "Booking cancelled";
            case "charging_started": return "Charging started";
            case "earning_credited": return "Earning credited";
            case "booking_confirmed": return "Booking confirmed";
            case "driver_response": return "Driver update";
            case "driver_arrived": return "Driver arrived";
            case "booking_completed": return "Charging completed";
            case "bus_arriving": return "Bus arriving soon";
            case "subscription_renewal": return "Subscription renewal";
            default: return "Notification";
        }
    }

    public String displayColor() {
        switch (type) {
            case "critical_alert": return "#ef4444";
            case "route_updated": return "#3b82f6";
            case "booking_assigned": return "#10b981";
            case "emergency_diversion": return "#ef4444";
            case "emergency_dispatch": return "#ef4444";
            case "emergency_rejected": return "#f59e0b";
            case "booking_cancelled": return "#ef4444";
            case "charging_started": return "#06b6d4";
            case "earning_credited": return "#10b981";
            case "booking_confirmed": return "#10b981";
            case "driver_response": return "#10b981";
            case "driver_arrived": return "#3b82f6";
            case "booking_completed": return "#10b981";
            case "bus_arriving": return "#3b82f6";
            case "subscription_renewal": return "#ef4444";
            default: return "#94a3b8";
        }
    }
}
