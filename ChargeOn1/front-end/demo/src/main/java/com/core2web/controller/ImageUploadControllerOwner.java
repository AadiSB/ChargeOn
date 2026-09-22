package com.core2web.controller;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import org.json.JSONObject;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.core2web.config.CloudinaryConfig;
import com.core2web.model.AuthSession;

public class ImageUploadControllerOwner {

    private static final String PROJECT_ID = "chargeon-1793c";


    public String imageUpload(File file) {

        Cloudinary cloudinary = CloudinaryConfig.getCloudinary();

        try {

            Map<String, Object> result = cloudinary.uploader()
                    .upload(file, ObjectUtils.asMap(
                            "resource_type", "image"
                    ));

            String url = String.valueOf(result.get("secure_url"));

            System.out.println("Cloudinary image URL: " + url);

            return url;

        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }
    }


    public boolean saveProfilePictureUrl(String imageUrl) {

        AuthSession session = AuthSession.getCurrent();

        if (session == null) {
            System.out.println("No logged-in user.");
            return false;
        }

        try {

            String uid = session.getUid();
            String idToken = session.getIdToken();

            String url =
                    "https://firestore.googleapis.com/v1/projects/"
                    + PROJECT_ID
                    + "/databases/(default)/documents/Owner/"
                    + uid
                    + "?updateMask.fieldPaths=profilePictureUrl";

            JSONObject fields = new JSONObject();

            fields.put(
                    "profilePictureUrl",
                    new JSONObject().put("stringValue", imageUrl)
            );

            JSONObject body = new JSONObject()
                    .put("fields", fields);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + idToken)
                    .method("PATCH",HttpRequest.BodyPublishers.ofString(body.toString()))
                    .build();

            HttpResponse<String> response =
                    HttpClient.newHttpClient()
                            .send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {

                System.out.println("Profile picture URL saved.");
                return true;
            }

            System.out.println(
                    "Failed to save profile picture: "
                    + response.body()
            );

        } catch (Exception e) {

            e.printStackTrace();
        }

        return false;
    }


    public String getProfilePictureUrl() {

        AuthSession session = AuthSession.getCurrent();

        if (session == null) {
            return null;
        }

        try {

            String uid = session.getUid();
            String idToken = session.getIdToken();

            String url =
                    "https://firestore.googleapis.com/v1/projects/"
                    + PROJECT_ID
                    + "/databases/(default)/documents/Owner/"
                    + uid;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + idToken)
                    .GET()
                    .build();

            HttpResponse<String> response =
                    HttpClient.newHttpClient()
                            .send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {

                System.out.println(
                        "Could not get owner document: "
                        + response.body()
                );

                return null;
            }

            JSONObject body = new JSONObject(response.body());

            if (!body.has("fields")) {
                return null;
            }

            JSONObject fields = body.getJSONObject("fields");

            if (!fields.has("profilePictureUrl")) {
                return null;
            }

            return fields
                    .getJSONObject("profilePictureUrl")
                    .getString("stringValue");

        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }
    }


    public boolean removeProfilePictureUrl() {

        AuthSession session = AuthSession.getCurrent();

        if (session == null) {
            System.out.println("No logged-in user.");
            return false;
        }

        try {

            String uid = session.getUid();
            String idToken = session.getIdToken();

            String url =
                    "https://firestore.googleapis.com/v1/projects/"
                    + PROJECT_ID
                    + "/databases/(default)/documents/Owner/"
                    + uid
                    + "?updateMask.fieldPaths=profilePictureUrl";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + idToken)
                    .method(
                            "PATCH",
                            HttpRequest.BodyPublishers.ofString(
                                    "{\"fields\":{}}"
                            )
                    )
                    .build();

            HttpResponse<String> response =
                    HttpClient.newHttpClient()
                            .send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {

                System.out.println("Profile picture removed.");
                return true;
            }

            System.out.println(
                    "Failed to remove profile picture: "
                    + response.body()
            );

        } catch (Exception e) {

            e.printStackTrace();
        }

        return false;
    }
}
