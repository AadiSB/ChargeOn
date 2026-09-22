package com.core2web.controller;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONObject;

import com.core2web.model.AuthSession;

public class AuthController {

    private static final String API_KEY = "AIzaSyBWHcZ1eZiWn49k_RFCQSAb0G9kYDM6yos";

    private static final String PROJECT_ID = "chargeon-1793c";

    public AuthSession signUp(String email, String password, String role, String displayName) {
        JSONObject payload = new JSONObject()
                .put("email", email)
                .put("password", password)
                .put("returnSecureToken", true);

        try {
            HttpClient client = HttpClient.newHttpClient();
            URI uri = URI.create("https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=" + API_KEY);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.out.println("Sign up failed: " + response.body());
                return null;
            }

            JSONObject body = new JSONObject(response.body());
            String uid = body.getString("localId");
            String idToken = body.getString("idToken");
            String refreshToken = body.getString("refreshToken");

            AuthSession session = new AuthSession(uid, idToken, refreshToken, email, role);

            createUserDocument(session, role, displayName);

            AuthSession.setCurrent(session);
            return session;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public AuthSession signIn(String email, String password, String selectedRole) {
        JSONObject payload = new JSONObject()
                .put("email", email)
                .put("password", password)
                .put("returnSecureToken", true);

        try {
            HttpClient client = HttpClient.newHttpClient();
            URI uri = URI.create("https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + API_KEY);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.out.println("Sign in failed: " + response.body());
                return null;
            }

            JSONObject body = new JSONObject(response.body());
            String uid = body.getString("localId");
            String idToken = body.getString("idToken");
            String refreshToken = body.getString("refreshToken");

            boolean roleConfirmed = documentExists(capitalize(selectedRole), uid, idToken);
            if (!roleConfirmed) {
                System.out.println("Login rejected: no " + capitalize(selectedRole) + "/" + uid + " document exists.");
                return null;
            }

            AuthSession session = new AuthSession(uid, idToken, refreshToken, email, selectedRole);
            AuthSession.setCurrent(session);
            return session;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean sendPasswordResetEmail(String email) {
        JSONObject payload = new JSONObject()
                .put("requestType", "PASSWORD_RESET")
                .put("email", email);

        try {
            HttpClient client = HttpClient.newHttpClient();
            URI uri = URI.create("https://identitytoolkit.googleapis.com/v1/accounts:sendOobCode?key=" + API_KEY);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                System.out.println("Password reset failed: " + response.body());
                return false;
            }
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void createUserDocument(AuthSession session, String role, String displayName) throws Exception {
        String collection = capitalize(role);

        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("name", displayName);
        fields.put("email", session.getEmail());
        fields.put("status", "active");
        fields.put("createdAt", Instant.now());

        writeDocument(collection, session.getUid(), fields, session.getIdToken());
    }

    private boolean documentExists(String collection, String documentId, String idToken) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            String url = "https://firestore.googleapis.com/v1/projects/" + PROJECT_ID
                    + "/databases/(default)/documents/" + collection + "/" + documentId;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + idToken)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void writeDocument(String collection, String documentId, Map<String, Object> fields, String idToken) throws Exception {
        String url = "https://firestore.googleapis.com/v1/projects/" + PROJECT_ID
                + "/databases/(default)/documents/" + collection + "?documentId=" + documentId;

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
        }
    }

    private JSONObject toFirestoreFields(Map<String, Object> fields) {
        JSONObject result = new JSONObject();
        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            result.put(entry.getKey(), toFirestoreValue(entry.getValue()));
        }
        return result;
    }

    private JSONObject toFirestoreValue(Object value) {
        JSONObject fv = new JSONObject();
        if (value instanceof String) {
            fv.put("stringValue", value);
        } else if (value instanceof Boolean) {
            fv.put("booleanValue", value);
        } else if (value instanceof Integer || value instanceof Long) {
            fv.put("integerValue", value.toString());
        } else if (value instanceof Double || value instanceof Float) {
            fv.put("doubleValue", value);
        } else {
            fv.put("stringValue", String.valueOf(value));
        }
        return fv;
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
