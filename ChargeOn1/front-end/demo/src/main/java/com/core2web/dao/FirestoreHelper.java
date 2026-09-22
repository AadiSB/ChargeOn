package com.core2web.dao;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.time.Instant;

import com.core2web.util.DateTimeUtil;

import org.json.JSONObject;

public class FirestoreHelper {

    public static final String PROJECT_ID = "chargeon-1793c";

    private static final String BASE_URL =
            "https://firestore.googleapis.com/v1/projects/" + PROJECT_ID + "/databases/(default)/documents/";

    public static boolean writeDocument(String collection, String documentId, Map<String, Object> fields, String idToken) {
        try {
            String url = BASE_URL + encodePathSegment(collection) + "?documentId=" + documentId;
            JSONObject body = new JSONObject().put("fields", toFirestoreFields(fields));

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + idToken)
                    .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                System.out.println("Failed to write " + collection + "/" + documentId + ": " + response.body());
                return false;
            }
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String createDocument(String collection, Map<String, Object> fields, String idToken) {
        try {
            String url = BASE_URL + encodePathSegment(collection);
            JSONObject body = new JSONObject().put("fields", toFirestoreFields(fields));

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + idToken)
                    .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                System.out.println("Failed to create document in " + collection + ": " + response.body());
                return null;
            }
            return getDocumentId(new JSONObject(response.body()));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean deleteDocument(String collection, String documentId, String idToken) {
        try {
            String url = BASE_URL + encodePathSegment(collection) + "/" + encodePathSegment(documentId);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + idToken)
                    .DELETE()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                System.out.println("Failed to delete " + collection + "/" + documentId + ": " + response.body());
                return false;
            }
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateFields(String collection, String documentId, Map<String, Object> fields, String idToken) {
        try {
            StringBuilder mask = new StringBuilder();
            for (String key : fields.keySet()) {
                if (mask.length() > 0) mask.append("&");
                mask.append("updateMask.fieldPaths=").append(key);
            }
            String url = BASE_URL + encodePathSegment(collection) + "/" + encodePathSegment(documentId) + "?" + mask;
            JSONObject body = new JSONObject().put("fields", toFirestoreFields(fields));

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + idToken)
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(body.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                System.out.println("Failed to update " + collection + "/" + documentId + ": " + response.body());
                return false;
            }
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static JSONObject getDocument(String collection, String documentId, String idToken) {
        try {
            String url = BASE_URL + encodePathSegment(collection) + "/" + encodePathSegment(documentId);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + idToken)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return null;
            return new JSONObject(response.body());

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean documentExists(String collection, String documentId, String idToken) {
        return getDocument(collection, documentId, idToken) != null;
    }

    private static String encodePathSegment(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }

    public static java.util.List<JSONObject> queryWhere(String collection, String fieldPath, String value, String idToken) {
        java.util.List<JSONObject> results = new java.util.ArrayList<>();
        try {
            JSONObject structuredQuery = new JSONObject()
                    .put("from", new org.json.JSONArray().put(new JSONObject().put("collectionId", collection)))
                    .put("where", new JSONObject()
                            .put("fieldFilter", new JSONObject()
                                    .put("field", new JSONObject().put("fieldPath", fieldPath))
                                    .put("op", "EQUAL")
                                    .put("value", new JSONObject().put("stringValue", value))));

            JSONObject body = new JSONObject().put("structuredQuery", structuredQuery);

            String url = "https://firestore.googleapis.com/v1/projects/" + PROJECT_ID + "/databases/(default)/documents:runQuery";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + idToken)
                    .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                System.out.println("Query failed: " + response.body());
                return results;
            }

            org.json.JSONArray array = new org.json.JSONArray(response.body());
            for (int i = 0; i < array.length(); i++) {
                JSONObject entry = array.getJSONObject(i);
                if (entry.has("document")) {
                    results.add(entry.getJSONObject("document"));
                }
            }
            return results;

        } catch (Exception e) {
            e.printStackTrace();
            return results;
        }
    }


    public static JSONObject toFirestoreFields(Map<String, Object> fields) {
        JSONObject result = new JSONObject();
        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            result.put(entry.getKey(), toFirestoreValue(entry.getValue(), isTimestampField(entry.getKey())));
        }
        return result;
    }

    public static JSONObject toFirestoreValue(Object value) {
        return toFirestoreValue(value, false);
    }

    private static JSONObject toFirestoreValue(Object value, boolean timestampField) {
        JSONObject fv = new JSONObject();
        if (value == null) {
            fv.put("nullValue", JSONObject.NULL);
        } else if (value instanceof Instant) {
            fv.put("timestampValue", value.toString());
        } else if (timestampField && value instanceof String) {
            Instant instant = DateTimeUtil.parse((String) value);
            if (instant != null) fv.put("timestampValue", instant.toString());
            else fv.put("stringValue", value);
        } else if (value instanceof String) {
            fv.put("stringValue", value);
        } else if (value instanceof Boolean) {
            fv.put("booleanValue", value);
        } else if (value instanceof Integer || value instanceof Long) {
            fv.put("integerValue", value.toString());
        } else if (value instanceof Double || value instanceof Float) {
            fv.put("doubleValue", value);
        } else if (value instanceof java.util.List) {
            org.json.JSONArray arr = new org.json.JSONArray();
            for (Object item : (java.util.List<?>) value) {
                arr.put(toFirestoreValue(item));
            }
            fv.put("arrayValue", new JSONObject().put("values", arr));
        } else {
            fv.put("stringValue", String.valueOf(value));
        }
        return fv;
    }

    private static boolean isTimestampField(String fieldName) {
        return fieldName != null && (fieldName.endsWith("At") || fieldName.endsWith("Time"));
    }

    public static java.util.List<String> getStringList(JSONObject document, String fieldName) {
        java.util.List<String> result = new java.util.ArrayList<>();
        try {
            JSONObject field = document.getJSONObject("fields").getJSONObject(fieldName);
            JSONObject arrayValue = field.optJSONObject("arrayValue");
            if (arrayValue == null) {
                return result;
            }
            org.json.JSONArray values = arrayValue.optJSONArray("values");
            if (values == null) {
                return result;
            }
            for (int i = 0; i < values.length(); i++) {
                result.add(values.getJSONObject(i).optString("stringValue", ""));
            }
        } catch (Exception e) {
        }
        return result;
    }

    public static class Filter {
        public final String fieldPath;
        public final String op;
        public final Object value;

        public Filter(String fieldPath, String op, Object value) {
            this.fieldPath = fieldPath;
            this.op = op;
            this.value = value;
        }
    }

    public static java.util.List<JSONObject> listCollection(String collection, String idToken) {
        return queryWithFilters(collection, java.util.Collections.emptyList(), idToken);
    }

    public static java.util.List<JSONObject> queryWithFilters(String collection, java.util.List<Filter> filters, String idToken) {
        java.util.List<JSONObject> results = new java.util.ArrayList<>();
        try {
            JSONObject structuredQuery = new JSONObject()
                    .put("from", new org.json.JSONArray().put(new JSONObject().put("collectionId", collection)));

            if (!filters.isEmpty()) {
                org.json.JSONArray fieldFilters = new org.json.JSONArray();
                for (Filter f : filters) {
                    fieldFilters.put(new JSONObject()
                            .put("fieldFilter", new JSONObject()
                                    .put("field", new JSONObject().put("fieldPath", f.fieldPath))
                                    .put("op", f.op)
                                    .put("value", toFilterValue(f.value))));
                }
                if (filters.size() == 1) {
                    structuredQuery.put("where", fieldFilters.getJSONObject(0));
                } else {
                    structuredQuery.put("where", new JSONObject()
                            .put("compositeFilter", new JSONObject()
                                    .put("op", "AND")
                                    .put("filters", fieldFilters)));
                }
            }

            JSONObject body = new JSONObject().put("structuredQuery", structuredQuery);
            String url = "https://firestore.googleapis.com/v1/projects/" + PROJECT_ID + "/databases/(default)/documents:runQuery";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + idToken)
                    .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                System.out.println("Query failed: " + response.body());
                return results;
            }

            org.json.JSONArray array = new org.json.JSONArray(response.body());
            for (int i = 0; i < array.length(); i++) {
                JSONObject entry = array.getJSONObject(i);
                if (entry.has("document")) {
                    results.add(entry.getJSONObject("document"));
                }
            }
            return results;

        } catch (Exception e) {
            e.printStackTrace();
            return results;
        }
    }

    private static JSONObject toFilterValue(Object value) {
        JSONObject fv = new JSONObject();
        if (value instanceof java.time.Instant) {
            fv.put("timestampValue", value.toString());
        } else if (value instanceof Integer || value instanceof Long) {
            fv.put("integerValue", value.toString());
        } else if (value instanceof Double || value instanceof Float) {
            fv.put("doubleValue", value);
        } else {
            fv.put("stringValue", String.valueOf(value));
        }
        return fv;
    }

    public static double getNumber(JSONObject document, String fieldName) {
        try {
            JSONObject field = document.getJSONObject("fields").getJSONObject(fieldName);
            if (field.has("integerValue")) return Double.parseDouble(field.getString("integerValue"));
            if (field.has("doubleValue")) return field.getDouble("doubleValue");
            return 0;
        } catch (Exception e) {
            return 0;
        }
    }

    public static boolean getBoolean(JSONObject document, String fieldName) {
        try {
            return document.getJSONObject("fields").getJSONObject(fieldName).optBoolean("booleanValue", false);
        } catch (Exception e) {
            return false;
        }
    }

    public static String getString(JSONObject document, String fieldName) {
        try {
            JSONObject field = document.getJSONObject("fields").getJSONObject(fieldName);
            if (field.has("timestampValue")) {
                Instant instant = DateTimeUtil.parse(field.getString("timestampValue"));
                return DateTimeUtil.canonical(instant);
            }
            return field.optString("stringValue", "");
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * True when {@code fieldName} holds an ISO-8601 instant string at or after
     * {@code threshold}. False when absent or unparseable.
     *
     * <p>All timestamps in this app are written as {@code Instant.now().toString()},
     * i.e. Firestore {@code stringValue}, not {@code timestampValue}. A structured
     * query filter built from a Java {@link java.time.Instant} serialises to
     * {@code timestampValue}, and Firestore never matches across value types — such
     * a filter silently returns nothing. So date ranges must be applied in memory
     * with this helper rather than pushed into {@link #queryWithFilters}.
     */
    public static boolean isFieldOnOrAfter(JSONObject document, String fieldName, java.time.Instant threshold) {
        try {
            Instant value = DateTimeUtil.parse(getString(document, fieldName));
            return value != null && !value.isBefore(threshold);
        } catch (Exception e) {
            return false;
        }
    }

    public static String getDocumentId(JSONObject document) {
        String fullName = document.optString("name", "");
        int lastSlash = fullName.lastIndexOf('/');
        return lastSlash >= 0 ? fullName.substring(lastSlash + 1) : fullName;
    }
}
