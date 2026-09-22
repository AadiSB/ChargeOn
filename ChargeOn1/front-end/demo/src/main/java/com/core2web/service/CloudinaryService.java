
package com.core2web.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

import org.json.JSONObject;

public class CloudinaryService {

    private static final String CLOUD_NAME = "comwmhui";
    private static final String UPLOAD_PRESET = "chargeon_ownerprofile";

    private final HttpClient client;

    public CloudinaryService() {
        client = HttpClient.newHttpClient();
    }


    public String uploadImage(Path imagePath) {

        if (imagePath == null || !Files.exists(imagePath)) {
            System.out.println("Image file not found.");
            return null;
        }

        String url =
                "https://api.cloudinary.com/v1_1/"
                        + CLOUD_NAME
                        + "/image/upload";

        try {

            String boundary = "----ChargeOnBoundary";

            byte[] fileBytes = Files.readAllBytes(imagePath);

            String fileName = imagePath.getFileName().toString();

            StringBuilder bodyStart = new StringBuilder();

            bodyStart.append("--")
                    .append(boundary)
                    .append("\r\n");

            bodyStart.append(
                    "Content-Disposition: form-data; name=\"upload_preset\"\r\n\r\n"
            );

            bodyStart.append(UPLOAD_PRESET)
                    .append("\r\n");

            bodyStart.append("--")
                    .append(boundary)
                    .append("\r\n");

            bodyStart.append(
                    "Content-Disposition: form-data; "
                            + "name=\"file\"; filename=\""
                            + fileName
                            + "\"\r\n"
            );

            bodyStart.append(
                    "Content-Type: application/octet-stream\r\n\r\n"
            );

            byte[] startBytes =
                    bodyStart.toString().getBytes();

            byte[] endBytes =
                    ("\r\n--" + boundary + "--\r\n").getBytes();

            byte[] requestBody =
                    new byte[
                            startBytes.length
                                    + fileBytes.length
                                    + endBytes.length
                    ];

            System.arraycopy(
                    startBytes,
                    0,
                    requestBody,
                    0,
                    startBytes.length
            );

            System.arraycopy(
                    fileBytes,
                    0,
                    requestBody,
                    startBytes.length,
                    fileBytes.length
            );

            System.arraycopy(
                    endBytes,
                    0,
                    requestBody,
                    startBytes.length + fileBytes.length,
                    endBytes.length
            );

            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(URI.create(url))
                            .header(
                                    "Content-Type",
                                    "multipart/form-data; boundary="
                                            + boundary
                            )
                            .POST(
                                    HttpRequest.BodyPublishers.ofByteArray(
                                            requestBody
                                    )
                            )
                            .build();

            HttpResponse<String> response =
                    client.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            if (response.statusCode() != 200) {

                System.out.println(
                        "Cloudinary upload failed: "
                                + response.statusCode()
                                + " "
                                + response.body()
                );

                return null;
            }

            JSONObject result =
                    new JSONObject(response.body());

            String secureUrl =
                    result.optString("secure_url", null);

            if (secureUrl == null || secureUrl.isEmpty()) {
                System.out.println(
                        "Cloudinary did not return an image URL."
                );
                return null;
            }

            System.out.println(
                    "Image uploaded successfully."
            );

            return secureUrl;

        } catch (IOException | InterruptedException e) {

            e.printStackTrace();
            return null;
        }
    }
}

