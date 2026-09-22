package com.core2web.dao;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONObject;

import com.core2web.model.Admin;
import com.core2web.model.AuthSession;

public class AdminDao {

    private static final String PROJECT_ID = "chargeon-1793c";

    private static final String COLLECTION = "Admin";

    private final HttpClient client;

    public AdminDao() {
        client = HttpClient.newHttpClient();
    }


    public Admin getCurrentAdmin() {

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
                    client.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            if (response.statusCode() != 200) {

                System.out.println(
                        "Failed to load admin: "
                                + response.statusCode()
                                + " "
                                + response.body()
                );

                return null;
            }

            JSONObject document =
                    new JSONObject(response.body());

            JSONObject fields =
                    document.optJSONObject("fields");

            if (fields == null) {
                return null;
            }

            Admin admin = new Admin();

            admin.setUid(uid);

            admin.setName(
                    getStringField(fields, "name")
            );

            admin.setEmail(
                    getStringField(fields, "email")
            );

            admin.setPhone(
                    getStringField(fields, "phone")
            );

            admin.setStatus(
                    getStringField(fields, "status")
            );

            admin.setCreatedAt(
                    getStringField(fields, "createdAt")
            );

            admin.setProfileImageUrl(
                    getStringField(fields, "profileImageUrl")
            );

            return admin;

        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }
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

            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(URI.create(url))
                            .header(
                                    "Content-Type",
                                    "application/json"
                            )
                            .header(
                                    "Authorization",
                                    "Bearer " + idToken
                            )
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
                        "Admin phone updated successfully."
                );

                return true;
            }

            System.out.println(
                    "Failed to update admin phone: "
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

            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(URI.create(url))
                            .header(
                                    "Content-Type",
                                    "application/json"
                            )
                            .header(
                                    "Authorization",
                                    "Bearer " + idToken
                            )
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
                        "Admin profile image URL updated successfully."
                );

                return true;
            }

            System.out.println(
                    "Failed to update admin profile image: "
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


    private String getStringField(
            JSONObject fields,
            String fieldName) {

        JSONObject field =
                fields.optJSONObject(fieldName);

        if (field == null) {
            return "";
        }

        return field.optString(
                "stringValue",
                ""
        );
    }
}
