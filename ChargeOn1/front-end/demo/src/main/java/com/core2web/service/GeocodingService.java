package com.core2web.service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

public final class GeocodingService {

    private static final String USER_AGENT =
            "ChargeOn/1.0 (JavaFX EV bus charging app; contact: admin@chargeon.test)";

    private static final String SEARCH_URL = "https://nominatim.openstreetmap.org/search";

    private static final long MIN_INTERVAL_MS = 1100;

    private static final Pattern COORD_PAIR = Pattern.compile(
            "(-?\\d{1,3}(?:\\.\\d+)?)\\s*,\\s*(-?\\d{1,3}(?:\\.\\d+)?)");

    private static final Map<String, Result> CACHE = new LinkedHashMap<>();

    private static long lastRequestAt = 0;

    public static final class Result {
        public final double latitude;
        public final double longitude;
        public final String displayName;

        public Result(double latitude, double longitude, String displayName) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.displayName = displayName;
        }
    }

    private GeocodingService() {
    }

    public static Result geocode(String query) {

        if (query == null || query.trim().isEmpty()) {
            return null;
        }
        String q = query.trim();

        Result literal = parseCoordinatePair(q);
        if (literal != null) {
            return literal;
        }

        synchronized (CACHE) {
            if (CACHE.containsKey(q)) {
                return CACHE.get(q);
            }
        }

        Result result = lookup(q);

        synchronized (CACHE) {
            CACHE.put(q, result);
        }
        return result;
    }

    private static Result lookup(String query) {
        try {
            throttle();

            String url = SEARCH_URL
                    + "?q=" + URLEncoder.encode(query, StandardCharsets.UTF_8)
                    + "&format=json&limit=1";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", USER_AGENT)
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.out.println("Geocoding failed (" + response.statusCode() + ") for \""
                        + query + "\": " + response.body());
                return null;
            }

            JSONArray hits = new JSONArray(response.body());
            if (hits.isEmpty()) {
                return null;
            }

            JSONObject first = hits.getJSONObject(0);
            return new Result(
                    Double.parseDouble(first.getString("lat")),
                    Double.parseDouble(first.getString("lon")),
                    first.optString("display_name", query));

        } catch (Exception e) {
            System.out.println("Geocoding error for \"" + query + "\": " + e.getMessage());
            return null;
        }
    }

    private static synchronized void throttle() throws InterruptedException {
        long since = System.currentTimeMillis() - lastRequestAt;
        if (since < MIN_INTERVAL_MS) {
            Thread.sleep(MIN_INTERVAL_MS - since);
        }
        lastRequestAt = System.currentTimeMillis();
    }

    private static Result parseCoordinatePair(String text) {
        Matcher m = COORD_PAIR.matcher(text);
        if (!m.find()) {
            return null;
        }
        double lat = Double.parseDouble(m.group(1));
        double lng = Double.parseDouble(m.group(2));
        if (lat < -90 || lat > 90 || lng < -180 || lng > 180) {
            return null;
        }
        return new Result(lat, lng, text);
    }
}
