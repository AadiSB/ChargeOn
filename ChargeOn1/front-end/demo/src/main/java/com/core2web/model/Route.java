package com.core2web.model;

import java.util.ArrayList;
import java.util.List;

public class Route {

    private String id;
    private String routeCode;
    private String name;
    private String startPoint;
    private String destination;
    private List<String> stops = new ArrayList<>();
    private String startTime;
    private String endTime;
    private String frequency;
    private String assignedBusId;
    private String status;
    private String createdAt;

    public Route() {}

    public Route(String id, String routeCode, String name, String startPoint, String destination,
                 List<String> stops, String startTime, String endTime, String frequency,
                 String assignedBusId, String status, String createdAt) {
        this.id = id;
        this.routeCode = routeCode;
        this.name = name;
        this.startPoint = startPoint;
        this.destination = destination;
        this.stops = stops;
        this.startTime = startTime;
        this.endTime = endTime;
        this.frequency = frequency;
        this.assignedBusId = assignedBusId;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getId()                 { return id; }
    public String getRouteCode()          { return routeCode; }
    public String getName()               { return name; }
    public String getStartPoint()         { return startPoint; }
    public String getDestination()        { return destination; }
    public List<String> getStops()        { return stops; }
    public String getStartTime()          { return startTime; }
    public String getEndTime()            { return endTime; }
    public String getFrequency()          { return frequency; }
    public String getAssignedBusId()      { return assignedBusId; }
    public String getStatus()             { return status; }
    public String getCreatedAt()          { return createdAt; }

    public void setId(String id)                       { this.id = id; }
    public void setRouteCode(String routeCode)         { this.routeCode = routeCode; }
    public void setName(String name)                   { this.name = name; }
    public void setStartPoint(String startPoint)       { this.startPoint = startPoint; }
    public void setDestination(String destination)     { this.destination = destination; }
    public void setStops(List<String> stops)           { this.stops = stops; }
    public void setStartTime(String startTime)         { this.startTime = startTime; }
    public void setEndTime(String endTime)             { this.endTime = endTime; }
    public void setFrequency(String frequency)         { this.frequency = frequency; }
    public void setAssignedBusId(String assignedBusId) { this.assignedBusId = assignedBusId; }
    public void setStatus(String status)               { this.status = status; }
    public void setCreatedAt(String createdAt)         { this.createdAt = createdAt; }

    public String stopsSummary() {
        if (startPoint == null || startPoint.isEmpty()) {
            return name;
        }
        return startPoint + " → " + destination;
    }

    public String scheduleSummary() {
        if (startTime == null || startTime.isEmpty()) {
            return "—";
        }
        return startTime + " – " + endTime + " · " + frequency;
    }

    public String statusColor() {
        switch (status == null ? "" : status) {
            case "ACTIVE": return "#10b981";
            case "PAUSED": return "#f59e0b";
            case "DRAFT":  return "#6b7280";
            default:       return "#6b7280";
        }
    }
}
