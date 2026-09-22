package com.core2web.controller;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.core2web.dao.RouteDao;
import com.core2web.model.AuthSession;
import com.core2web.model.Route;

public class RouteController {

    private final RouteDao routeDao = new RouteDao();


    public List<Route> getAllRoutes() {

        AuthSession session = AuthSession.getCurrent();

        if (session == null) {
            return new ArrayList<>();
        }

        return routeDao.getAllRoutes(
                session.getIdToken()
        );
    }


    public String createRoute(
            String routeCode,
            String name,
            String startPoint,
            String destination,
            List<String> stops,
            String startTime,
            String endTime,
            String frequency,
            String assignedBusId) {

        AuthSession session = AuthSession.getCurrent();

        if (session == null) {
            return null;
        }

        Route route = new Route(
                null,
                routeCode,
                name,
                startPoint,
                destination,
                stops,
                startTime,
                endTime,
                frequency,
                assignedBusId,
                "ACTIVE",
                Instant.now().toString()
        );

        return routeDao.createRoute(
                route,
                session.getIdToken()
        );
    }


    public boolean deleteRoute(String routeId) {

        AuthSession session = AuthSession.getCurrent();

        if (session == null) {
            return false;
        }

        return routeDao.deleteRoute(
                routeId,
                session.getIdToken()
        );
    }
}