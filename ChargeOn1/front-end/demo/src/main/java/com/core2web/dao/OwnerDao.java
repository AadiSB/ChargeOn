package com.core2web.dao;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.core2web.model.AuthSession;
import com.core2web.model.Owner;

public class OwnerDao {

    private static final String PROJECT_ID = "chargeon-1793c";

    private static final String COLLECTION = "Owner";

    private final HttpClient client;

    public OwnerDao() {
        client = HttpClient.newHttpClient();
    }

    public Owner getCurrentOwner() {

        AuthSession session = AuthSession.getCurrent();

        if (session == null) {
            System.out.println("No logged-in user found.");
            return null;
        }

        String uid = session.getUid();
        String idToken = session.getIdToken();

        String url = "https://firestore.googleapis.com/v1/projects/"
                + PROJECT_ID
                + "/databases/(default)/documents/"
                + COLLECTION
                + "/"
                + uid;

        try {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + idToken)
                    .GET()
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {

                System.out.println(
                        "Failed to load owner: "
                                + response.statusCode()
                                + " "
                                + response.body()
                );

                return null;
            }

            JSONObject document = new JSONObject(response.body());

            JSONObject fields = document.optJSONObject("fields");

            if (fields == null) {
                return null;
            }

            Owner owner = new Owner();

            owner.setUid(uid);
            owner.setName(getStringField(fields, "name"));
            owner.setEmail(getStringField(fields, "email"));
            owner.setPhone(getStringField(fields, "phone"));
            owner.setStatus(getStringField(fields, "status"));
            owner.setCreatedAt(getStringField(fields, "createdAt"));
            owner.setProfileImageUrl(
                    getStringField(fields, "profileImageUrl")
            );
            owner.setSubscriptionPlanId(getStringField(fields, "subscriptionPlanId"));
            owner.setSubscriptionPlanName(getStringField(fields, "subscriptionPlanName"));

            return owner;

        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }
    }

    public List<Owner> getAllOwners(String idToken) {
        List<JSONObject> docs = FirestoreHelper.listCollection(COLLECTION, idToken);
        List<Owner> owners = new ArrayList<>();

        for (JSONObject doc : docs) {
            Owner owner = fromDocument(FirestoreHelper.getDocumentId(doc), doc);
            if (owner != null) {
                owners.add(owner);
            }
        }

        return owners;
    }

    public Owner getOwner(String uid, String idToken) {
        if (uid == null || uid.isEmpty() || "null".equals(uid)) {
            return null;
        }
        JSONObject doc = FirestoreHelper.getDocument(COLLECTION, uid, idToken);
        return doc == null ? null : fromDocument(uid, doc);
    }

    private Owner fromDocument(String uid, JSONObject doc) {
        JSONObject fields = doc.optJSONObject("fields");
        if (fields == null) {
            return null;
        }

        Owner owner = new Owner();
        owner.setUid(uid);
        owner.setName(getStringField(fields, "name"));
        owner.setEmail(getStringField(fields, "email"));
        owner.setPhone(getStringField(fields, "phone"));
        owner.setStatus(getStringField(fields, "status"));
        owner.setCreatedAt(getStringField(fields, "createdAt"));
        owner.setProfileImageUrl(getStringField(fields, "profileImageUrl"));
        return owner;
    }

    public boolean updatePhone(String phone) {

        AuthSession session = AuthSession.getCurrent();

        if (session == null) {
            System.out.println("No logged-in user found.");
            return false;
        }

        String uid = session.getUid();
        String idToken = session.getIdToken();

        String url = "https://firestore.googleapis.com/v1/projects/"
                + PROJECT_ID
                + "/databases/(default)/documents/"
                + COLLECTION
                + "/"
                + uid
                + "?updateMask.fieldPaths=phone";

        JSONObject fields = new JSONObject();

        fields.put(
                "phone",
                new JSONObject().put(
                        "stringValue",
                        phone
                )
        );

        JSONObject body = new JSONObject();
        body.put("fields", fields);

        try {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + idToken)
                    .method(
                            "PATCH",
                            HttpRequest.BodyPublishers.ofString(
                                    body.toString()
                            )
                    )
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {

                System.out.println("Owner phone updated successfully.");
                return true;
            }

            System.out.println(
                    "Failed to update owner phone: "
                            + response.statusCode()
                            + " "
                            + response.body()
            );

            return false;

        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }
    }

public boolean updateProfileImageUrl(String imageUrl) {

    AuthSession session = AuthSession.getCurrent();

    if (session == null) {
        System.out.println("No logged-in user found.");
        return false;
    }

    String uid = session.getUid();
    String idToken = session.getIdToken();

    String url = "https://firestore.googleapis.com/v1/projects/"
            + PROJECT_ID
            + "/databases/(default)/documents/"
            + COLLECTION
            + "/"
            + uid
            + "?updateMask.fieldPaths=profileImageUrl";

    JSONObject fields = new JSONObject();

    fields.put(
            "profileImageUrl",
            new JSONObject().put(
                    "stringValue",
                    imageUrl
            )
    );

    JSONObject body = new JSONObject();
    body.put("fields", fields);

    try {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + idToken)
                .method(
                        "PATCH",
                        HttpRequest.BodyPublishers.ofString(
                                body.toString()
                        )
                )
                .build();

        HttpResponse<String> response =
                client.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        if (response.statusCode() == 200) {

            System.out.println(
                    "Owner profile image URL updated successfully."
            );

            return true;
        }

        System.out.println(
                "Failed to update owner profile image: "
                        + response.statusCode()
                        + " "
                        + response.body()
        );

        return false;

    } catch (Exception e) {

        e.printStackTrace();
        return false;
    }
}

    public boolean updateSubscription(String planId, String planName) {

        AuthSession session = AuthSession.getCurrent();

        if (session == null) {
            System.out.println("No logged-in user found.");
            return false;
        }

        String uid = session.getUid();
        String idToken = session.getIdToken();

        String url = "https://firestore.googleapis.com/v1/projects/"
                + PROJECT_ID
                + "/databases/(default)/documents/"
                + COLLECTION
                + "/"
                + uid
                + "?updateMask.fieldPaths=subscriptionPlanId&updateMask.fieldPaths=subscriptionPlanName";

        JSONObject fields = new JSONObject();

        fields.put(
                "subscriptionPlanId",
                new JSONObject().put("stringValue", planId)
        );

        fields.put(
                "subscriptionPlanName",
                new JSONObject().put("stringValue", planName)
        );

        JSONObject body = new JSONObject();
        body.put("fields", fields);

        try {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + idToken)
                    .method(
                            "PATCH",
                            HttpRequest.BodyPublishers.ofString(
                                    body.toString()
                            )
                    )
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {

                System.out.println("Owner subscription updated successfully.");
                return true;
            }

            System.out.println(
                    "Failed to update owner subscription: "
                            + response.statusCode()
                            + " "
                            + response.body()
            );

            return false;

        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }
    }

    private String getStringField(JSONObject fields, String fieldName) {

        JSONObject field = fields.optJSONObject(fieldName);

        if (field == null) {
            return "";
        }

        return field.optString("stringValue", "");
    }
}

