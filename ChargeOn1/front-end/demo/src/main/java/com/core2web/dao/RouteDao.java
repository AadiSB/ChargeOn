package com.core2web.dao;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.core2web.model.Route;

public class RouteDao {

    private static final String COLLECTION = "routes";


    public List<Route> getAllRoutes(String idToken) {

        List<JSONObject> docs =
                FirestoreHelper.listCollection(COLLECTION, idToken);

        List<Route> routes = new ArrayList<>();

        for (JSONObject doc : docs) {
            routes.add(fromDocument(doc));
        }

        return routes;
    }


    public String createRoute(Route route, String idToken) {

        Map<String, Object> fields = new LinkedHashMap<>();

        fields.put("routeCode", route.getRouteCode());
        fields.put("name", route.getName());
        fields.put("startPoint", route.getStartPoint());
        fields.put("destination", route.getDestination());
        fields.put("stops", route.getStops());
        fields.put("startTime", route.getStartTime());
        fields.put("endTime", route.getEndTime());
        fields.put("frequency", route.getFrequency());
        fields.put("assignedBusId", route.getAssignedBusId());
        fields.put("status", route.getStatus());
        fields.put("createdAt", route.getCreatedAt());

        return FirestoreHelper.createDocument(
                COLLECTION,
                fields,
                idToken
        );
    }


    public boolean deleteRoute(String routeId, String idToken) {

        if (routeId == null || routeId.isEmpty()) {
            return false;
        }

        return FirestoreHelper.deleteDocument(
                COLLECTION,
                routeId,
                idToken
        );
    }


    private Route fromDocument(JSONObject doc) {

        Route route = new Route();

        route.setId(
                FirestoreHelper.getDocumentId(doc)
        );

        route.setRouteCode(
                FirestoreHelper.getString(doc, "routeCode")
        );

        route.setName(
                FirestoreHelper.getString(doc, "name")
        );

        route.setStartPoint(
                FirestoreHelper.getString(doc, "startPoint")
        );

        route.setDestination(
                FirestoreHelper.getString(doc, "destination")
        );

        route.setStops(
                FirestoreHelper.getStringList(doc, "stops")
        );

        route.setStartTime(
                FirestoreHelper.getString(doc, "startTime")
        );

        route.setEndTime(
                FirestoreHelper.getString(doc, "endTime")
        );

        route.setFrequency(
                FirestoreHelper.getString(doc, "frequency")
        );

        route.setAssignedBusId(
                FirestoreHelper.getString(doc, "assignedBusId")
        );

        route.setStatus(
                FirestoreHelper.getString(doc, "status")
        );

        route.setCreatedAt(
                FirestoreHelper.getString(doc, "createdAt")
        );

        return route;
    }
}