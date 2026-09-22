package com.core2web.model;

public class Ticket {

    private String id;
    private String ownerId;
    private String customerName;
    private String subject;
    private String description;
    private String priority;
    private String status;
    private String bookingId;
    private String busId;
    private String createdAt;

    public Ticket() {}

    public Ticket(String id, String ownerId, String customerName, String subject,
                  String priority, String status, String bookingId, String busId,
                  String createdAt) {
        this.id = id;
        this.ownerId = ownerId;
        this.customerName = customerName;
        this.subject = subject;
        this.priority = priority;
        this.status = status;
        this.bookingId = bookingId;
        this.busId = busId;
        this.createdAt = createdAt;
    }


    public String getId()           { return id; }
    public String getOwnerId()      { return ownerId; }
    public String getCustomerName() { return customerName; }
    public String getSubject()      { return subject; }
    public String getDescription()  { return description; }
    public String getPriority()     { return priority; }
    public String getStatus()       { return status; }
    public String getBookingId()    { return bookingId; }
    public String getBusId()        { return busId; }
    public String getCreatedAt()    { return createdAt; }


    public void setId(String id)                       { this.id = id; }
    public void setOwnerId(String ownerId)             { this.ownerId = ownerId; }
    public void setCustomerName(String customerName)   { this.customerName = customerName; }
    public void setSubject(String subject)             { this.subject = subject; }
    public void setDescription(String description)     { this.description = description; }
    public void setPriority(String priority)           { this.priority = priority; }
    public void setStatus(String status)               { this.status = status; }
    public void setBookingId(String bookingId)         { this.bookingId = bookingId; }
    public void setBusId(String busId)                 { this.busId = busId; }
    public void setCreatedAt(String createdAt)         { this.createdAt = createdAt; }

    public boolean isOpen() {
        return "OPEN".equals(status);
    }

    public boolean isHighPriority() {
        return "HIGH".equals(priority);
    }

    public String priorityColor() {
        switch (priority == null ? "" : priority) {
            case "HIGH":   return "#ef4444";
            case "MEDIUM": return "#f59e0b";
            default:       return "#6b7280";
        }
    }

    public String statusColor() {
        switch (status == null ? "" : status) {
            case "OPEN":        return "#ef4444";
            case "IN_PROGRESS": return "#3b82f6";
            case "RESOLVED":    return "#10b981";
            default:            return "#6b7280";
        }
    }

    public String statusDisplay() {
        return status == null ? "" : status.replace('_', ' ');
    }

    public String raisedLabel() {
        if (createdAt == null || createdAt.isEmpty()) return "—";
        try {
            java.time.Instant createdInstant = java.time.Instant.parse(createdAt);
            java.time.Duration elapsed = java.time.Duration.between(createdInstant, java.time.Instant.now());

            long minutes = elapsed.toMinutes();
            if (minutes < 1)   return "Just now";
            if (minutes < 60)  return minutes + " min ago";

            long hours = elapsed.toHours();
            if (hours < 24)    return hours + " hr ago";

            long days = elapsed.toDays();
            if (days == 1)     return "Yesterday";
            return days + " days ago";

        } catch (Exception e) {
            return createdAt;
        }
    }
}
