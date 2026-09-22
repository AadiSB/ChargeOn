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

/**
 * Address -> coordinates, via OpenStreetMap Nominatim.
 *
 * <p>The single geocoding entry point for the whole app: both the pickup field in
 * BookCharging and the "Nearest bus" quick-pick go through {@link #geocode(String)}.
 *
 * <p><b>Blocking.</b> Every call performs network I/O. Never call it on the JavaFX
 * thread — run it on a background thread and marshal the result back with
 * {@code Platform.runLater}.
 *
 * <p>Nominatim's usage policy is respected as follows:
 * <ul>
 *   <li>an explicit {@code User-Agent} identifying this app — requests without one
 *       are refused;</li>
 *   <li>at most one request per second, enforced in {@link #throttle()};</li>
 *   <li>resolved queries are cached in memory, so repeats never re-hit the server;</li>
 *   <li>callers use an explicit search action rather than per-keystroke lookup.</li>
 * </ul>
 */
public final class GeocodingService {

    /** Nominatim refuses requests without a genuine identifying User-Agent. */
    private static final String USER_AGENT =
            "ChargeOn/1.0 (JavaFX EV bus charging app; contact: admin@chargeon.test)";

    private static final String SEARCH_URL = "https://nominatim.openstreetmap.org/search";

    /** Nominatim's published limit is 1 request/second. */
    private static final long MIN_INTERVAL_MS = 1100;

    /** Recognises a coordinate pair typed directly, e.g. "18.5204, 73.8567". */
    private static final Pattern COORD_PAIR = Pattern.compile(
            "(-?\\d{1,3}(?:\\.\\d+)?)\\s*,\\s*(-?\\d{1,3}(?:\\.\\d+)?)");

    private static final Map<String, Result> CACHE = new LinkedHashMap<>();

    private static long lastRequestAt = 0;

    /** A resolved location. {@code displayName} is Nominatim's canonical label. */
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

    /**
     * Resolves free text to coordinates.
     *
     * <p>Returns null rather than throwing when the query is blank, unresolvable, or
     * the service is unreachable — callers are expected to carry on without
     * coordinates ("position unknown") rather than block the user.
     */
    public static Result geocode(String query) {

        if (query == null || query.trim().isEmpty()) {
            return null;
        }
        String q = query.trim();

        // A typed coordinate pair needs no network round-trip, and Nominatim would
        // handle it poorly anyway. Kept inside this class so there is still exactly
        // one geocoding entry point.
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

        // Negative results are cached too: repeatedly asking about a typo should not
        // repeatedly hit the server.
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

    /** Blocks until at least MIN_INTERVAL_MS has passed since the previous request. */
    private static synchronized void throttle() throws InterruptedException {
        long since = System.currentTimeMillis() - lastRequestAt;
        if (since < MIN_INTERVAL_MS) {
            Thread.sleep(MIN_INTERVAL_MS - since);
        }
        lastRequestAt = System.currentTimeMillis();
    }

    /** Coordinates typed directly, or null when the text isn't a valid pair. */
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
