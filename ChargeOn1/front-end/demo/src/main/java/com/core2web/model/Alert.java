package com.core2web.model;

public class Alert {

    private String id;
    private String driverId;
    private String busId;
    private String issueType;
    private String severity;
    private String description;
    private String status;
    private String createdAt;

    public Alert() {}

    public Alert(String id, String driverId, String busId, String issueType, String severity,
                 String description, String status, String createdAt) {
        this.id = id;
        this.driverId = driverId;
        this.busId = busId;
        this.issueType = issueType;
        this.severity = severity;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
    }


    public String getId()          { return id; }
    public String getDriverId()    { return driverId; }
    public String getBusId()       { return busId; }
    public String getIssueType()   { return issueType; }
    public String getSeverity()    { return severity; }
    public String getDescription() { return description; }
    public String getStatus()      { return status; }
    public String getCreatedAt()   { return createdAt; }


    public void setId(String id)                   { this.id = id; }
    public void setDriverId(String driverId)       { this.driverId = driverId; }
    public void setBusId(String busId)             { this.busId = busId; }
    public void setIssueType(String issueType)     { this.issueType = issueType; }
    public void setSeverity(String severity)       { this.severity = severity; }
    public void setDescription(String description) { this.description = description; }
    public void setStatus(String status)           { this.status = status; }
    public void setCreatedAt(String createdAt)     { this.createdAt = createdAt; }

    public String statusDisplay() {
        return status == null ? "" : status.replace('_', ' ');
    }

    public String statusColor() {
        switch (status == null ? "" : status) {
            case "OPEN":        return "#ef4444";
            case "IN_PROGRESS": return "#10b981";
            case "RESOLVED":    return "#64748b";
            default:            return "#6b7280";
        }
    }

    public String raisedLabel() {
        if (createdAt == null || createdAt.isEmpty()) return "—";
        try {
            java.time.Instant createdInstant = java.time.Instant.parse(createdAt);
            java.time.Duration elapsed = java.time.Duration.between(createdInstant, java.time.Instant.now());

            long minutes = elapsed.toMinutes();
            if (minutes < 1)   return "just now";
            if (minutes < 60)  return minutes + " min ago";

            long hours = elapsed.toHours();
            if (hours < 24)    return "today " + hours + " hr ago";

            long days = elapsed.toDays();
            if (days == 1)     return "yesterday";
            return days + " days ago";

        } catch (Exception e) {
            return createdAt;
        }
    }
}
