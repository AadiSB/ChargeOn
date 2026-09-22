package com.core2web.controller;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import javafx.application.Platform;
import org.json.JSONArray;
import org.json.JSONObject;

public class CopilotController {

    private static String API_KEY = "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";

    public static String getApiKey() {
        return API_KEY;
    }

    public static void setApiKey(String apiKey) {
        if (apiKey != null) {
            API_KEY = apiKey.trim();
        }
    }

    public static boolean hasApiKey() {
        return API_KEY != null && !API_KEY.trim().isEmpty() && !API_KEY.equals("YOUR_GROQ_API_KEY_HERE");
    }

    public static void askCopilotAsync(String userPrompt, String systemPrompt, Consumer<String> onSuccess,
            Consumer<String> onError) {
        CompletableFuture.runAsync(() -> {
            try {
                if (!hasApiKey()) {
                    Platform.runLater(() -> onError.accept(
                            "Groq API Key is missing. Please put your Groq API Key in CopilotController.java."));
                    return;
                }

                HttpClient client = HttpClient.newHttpClient();
                URI uri = URI.create("https://api.groq.com/openai/v1/chat/completions");

                JSONObject systemMsg = new JSONObject().put("role", "system").put("content", systemPrompt);
                JSONObject userMsg = new JSONObject().put("role", "user").put("content", userPrompt);

                JSONArray messages = new JSONArray().put(systemMsg).put(userMsg);

                JSONObject payload = new JSONObject();
                payload.put("model", "openai/gpt-oss-20b");
                payload.put("messages", messages);
                payload.put("temperature", 0.7);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(uri)
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + API_KEY)
                        .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    JSONObject jsonResponse = new JSONObject(response.body());
                    String aiMessage = jsonResponse
                            .getJSONArray("choices")
                            .getJSONObject(0)
                            .getJSONObject("message")
                            .getString("content");
                    Platform.runLater(() -> onSuccess.accept(stripMarkdownAsterisks(aiMessage)));
                } else {
                    String errMsg = response.body();
                    try {
                        JSONObject errJson = new JSONObject(response.body());
                        if (errJson.has("error") && errJson.getJSONObject("error").has("message")) {
                            errMsg = errJson.getJSONObject("error").getString("message");
                        }
                    } catch (Exception ignored) {
                    }
                    String finalErr = "Groq API Error (" + response.statusCode() + "): " + errMsg;
                    Platform.runLater(() -> onError.accept(finalErr));
                }
            } catch (Exception e) {
                e.printStackTrace();
                String errMessage = "Network Connection Error: " + e.getMessage();
                Platform.runLater(() -> onError.accept(errMessage));
            }
        });
    }

    /**
     * The chat UI renders plain text, so Markdown emphasis/bullets (*, **) would
     * otherwise show up as literal asterisks instead of being styled.
     */
    private static String stripMarkdownAsterisks(String text) {
        if (text == null) {
            return text;
        }
        String[] lines = text.replace("*", "").split("\n", -1);
        StringBuilder cleaned = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            cleaned.append(lines[i].stripLeading());
            if (i < lines.length - 1) {
                cleaned.append("\n");
            }
        }
        return cleaned.toString();
    }
}
