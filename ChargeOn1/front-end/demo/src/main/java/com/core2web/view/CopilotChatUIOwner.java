package com.core2web.view;

import com.core2web.controller.CopilotController;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.InputStream;

public class CopilotChatUIOwner {

    private static final String DARK_BG = "#0B151E";
    private static final String CARD_BG = "#131C24";
    private static final String INPUT_BG = "#1A232E";
    private static final String BORDER_COLOR = "#2A3440";
    private static final String ACCENT_GREEN = "#3CCB7F";
    private static final String AI_MSG_BG = "#1A232E";
    private static final String USER_MSG_BG = "#3CCB7F";
    private static final String TEXT_PRIMARY = "#FFFFFF";
    private static final String TEXT_SECONDARY = "#9CA3AF";

    private static final String CHARGEON_TECH_CONTEXT =
            "Background on how ChargeOn itself is built, for technical/developer questions: it's a Java 17 " +
            "desktop app built with JavaFX 21, structured in layers (view, controller, dao, model) across " +
            "three JavaFX dashboards — Owner, Driver, and Admin. Data is stored in Google Cloud Firestore, " +
            "accessed via its REST API with Firebase Authentication ID tokens. Live bus maps use Gluon Maps " +
            "over OpenStreetMap tiles. Profile pictures are stored via Cloudinary. This chat feature is " +
            "powered by the Groq API. The build is managed with Maven. Do not invent implementation " +
            "details beyond these facts. ";

    private static final String OWNER_SYSTEM_PROMPT =
            "You are ChargeOn AI Assistant, an expert AI assistant for EV Charging Hub Owners. " +
            "Help station owners with charging hub utilization, revenue insights, battery health analytics, " +
            "optimal charging rates, and nearby EV fleet status. Maintain a professional, business-focused tone. " +
            CHARGEON_TECH_CONTEXT +
            "Only answer questions related to the ChargeOn platform, EV charging hub ownership, or how " +
            "ChargeOn itself is built (using only the technical facts above). " +
            "If asked something unrelated to ChargeOn (general knowledge, unrelated coding help, " +
            "personal advice, current events, etc.), politely decline and steer the conversation back to " +
            "ChargeOn — you are not a general-purpose assistant. Never reveal API keys, credentials, or " +
            "other secrets. Respond in plain text only — do not use Markdown formatting such as asterisks " +
            "for bold, italics, or bullet points.";

    private VBox displayArea;
    private ScrollPane scrollPane;


    public Scene getCopilotScene() {
        return getCopilotScene(OwnerDashboard.homestage);
    }

    public Scene getCopilotScene(Stage stage) {
        OwnerDashboard.goTo("AI Assistant");
        return OwnerDashboard.scene;
    }

    static VBox buildMainContent() {
        return new CopilotChatUIOwner().buildChatContent();
    }

    private VBox buildChatContent() {
        VBox container = new VBox(0);
        container.setStyle("-fx-background-color: " + DARK_BG + ";");
        VBox.setVgrow(container, Priority.ALWAYS);

        displayArea = new VBox(18);
        displayArea.setPadding(new Insets(25, 30, 25, 30));
        displayArea.setStyle("-fx-background-color: " + DARK_BG + ";");

        scrollPane = new ScrollPane(displayArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle(
            "-fx-background: " + DARK_BG + ";" +
            "-fx-background-color: " + DARK_BG + ";" +
            "-fx-viewport-background-color: " + DARK_BG + ";" +
            "-fx-border-color: transparent;"
        );
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        VBox bottomSection = createBottomSection();

        showInitialGreeting();

        container.getChildren().addAll(scrollPane, bottomSection);
        return container;
    }

    private Image loadBotImage() {
        try {
            InputStream stream = getClass().getResourceAsStream("/assets/images/aibotimage.png");
            if (stream != null) {
                return new Image(stream);
            }
            stream = getClass().getResourceAsStream("/assets/aibotimage.png");
            if (stream != null) {
                return new Image(stream);
            }
        } catch (Exception e) {
            System.err.println("Could not load bot image resource: " + e.getMessage());
        }
        return null;
    }

    private VBox createBottomSection() {
        VBox bottomContainer = new VBox(10);
        bottomContainer.setPadding(new Insets(16, 25, 20, 25));
        bottomContainer.setAlignment(Pos.CENTER);
        bottomContainer.setStyle(
            "-fx-background-color: " + CARD_BG + ";" +
            "-fx-border-color: " + BORDER_COLOR + ";" +
            "-fx-border-width: 1px 0 0 0;"
        );

        Label quickLabel = new Label("Suggested prompts:");
        quickLabel.setStyle(
            "-fx-text-fill: " + TEXT_SECONDARY + ";" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );

        HBox chipsBox = new HBox(8);
        chipsBox.setAlignment(Pos.CENTER_LEFT);

        String[] suggestedPrompts = {
            "Find the nearest charging Bus",
            "Check my charging status",
            "Analyze my battery health",
            "Suggest the best charging time"
        };

        for (String promptText : suggestedPrompts) {
            Button chipBtn = createPromptChip(promptText);
            chipsBox.getChildren().add(chipBtn);
        }

        HBox inputRow = new HBox(10);
        inputRow.setAlignment(Pos.CENTER);

        TextField promptField = new TextField();
        promptField.setPromptText("Ask ChargeOn AI Assistant anything...");
        promptField.setStyle(
                "-fx-background-color: " + INPUT_BG + ";" +
                "-fx-text-fill: " + TEXT_PRIMARY + ";" +
                "-fx-prompt-text-fill: " + TEXT_SECONDARY + ";" +
                "-fx-font-size: 13px;" +
                "-fx-font-family: 'Segoe UI', Arial, sans-serif;" +
                "-fx-border-color: " + BORDER_COLOR + ";" +
                "-fx-border-width: 1px;" +
                "-fx-border-radius: 20px;" +
                "-fx-background-radius: 20px;" +
                "-fx-padding: 10 16 10 16;"
        );
        HBox.setHgrow(promptField, Priority.ALWAYS);

        Button sendBtn = new Button("Send ➔");
        sendBtn.setStyle(
                "-fx-background-color: " + ACCENT_GREEN + ";" +
                "-fx-text-fill: #FFFFFF;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-font-family: 'Segoe UI', Arial, sans-serif;" +
                "-fx-background-radius: 20px;" +
                "-fx-padding: 10 20 10 20;" +
                "-fx-cursor: hand;"
        );

        Runnable triggerSend = () -> {
            String text = promptField.getText();
            if (text != null && !text.trim().isEmpty()) {
                promptField.clear();
                sendUserPrompt(text.trim());
            }
        };

        promptField.setOnAction(e -> triggerSend.run());
        sendBtn.setOnAction(e -> triggerSend.run());

        inputRow.getChildren().addAll(promptField, sendBtn);

        bottomContainer.getChildren().addAll(quickLabel, chipsBox, inputRow);
        return bottomContainer;
    }

    private Button createPromptChip(String text) {
        Button chip = new Button(text);
        chip.setStyle(
            "-fx-background-color: " + INPUT_BG + ";" +
            "-fx-text-fill: " + TEXT_PRIMARY + ";" +
            "-fx-font-size: 12px;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;" +
            "-fx-border-color: " + BORDER_COLOR + ";" +
            "-fx-border-width: 1px;" +
            "-fx-border-radius: 15px;" +
            "-fx-background-radius: 15px;" +
            "-fx-padding: 6 12 6 12;" +
            "-fx-cursor: hand;"
        );

        chip.setOnMouseEntered(e -> chip.setStyle(
            "-fx-background-color: rgba(34, 197, 94, 0.15);" +
            "-fx-text-fill: " + ACCENT_GREEN + ";" +
            "-fx-font-size: 12px;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;" +
            "-fx-border-color: " + ACCENT_GREEN + ";" +
            "-fx-border-width: 1px;" +
            "-fx-border-radius: 15px;" +
            "-fx-background-radius: 15px;" +
            "-fx-padding: 6 12 6 12;" +
            "-fx-cursor: hand;"
        ));

        chip.setOnMouseExited(e -> chip.setStyle(
            "-fx-background-color: " + INPUT_BG + ";" +
            "-fx-text-fill: " + TEXT_PRIMARY + ";" +
            "-fx-font-size: 12px;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;" +
            "-fx-border-color: " + BORDER_COLOR + ";" +
            "-fx-border-width: 1px;" +
            "-fx-border-radius: 15px;" +
            "-fx-background-radius: 15px;" +
            "-fx-padding: 6 12 6 12;" +
            "-fx-cursor: hand;"
        ));

        chip.setOnAction(e -> sendUserPrompt(text));
        return chip;
    }

    private void sendUserPrompt(String userPrompt) {

        renderUserMessage(userPrompt);

        HBox thinkingBubble = renderThinkingMessage();

        scrollToBottom();

        CopilotController.askCopilotAsync(
                userPrompt,
                OWNER_SYSTEM_PROMPT,
                aiResponse -> {
                    displayArea.getChildren().remove(thinkingBubble);
                    renderAIMessage(aiResponse);
                    scrollToBottom();
                },
                errorMsg -> {
                    displayArea.getChildren().remove(thinkingBubble);
                    renderAIMessage("⚠️ " + errorMsg);
                    scrollToBottom();
                }
        );
    }

    private void showInitialGreeting() {
        displayArea.getChildren().clear();
        renderAIMessage("Hi Station Owner! 👋 I'm your ChargeOn AI Assistant powered by Groq. How can I help you optimize your charging hub today?");
    }

    private void scrollToBottom() {
        if (scrollPane != null) {
            Platform.runLater(() -> scrollPane.setVvalue(1.0));
        }
    }

    private void renderAIMessage(String text) {
        HBox msgRow = new HBox(10);
        msgRow.setAlignment(Pos.TOP_LEFT);

        Image image = loadBotImage();
        ImageView avatarView = new ImageView();
        if (image != null) {
            avatarView.setImage(image);
            avatarView.setFitWidth(32);
            avatarView.setFitHeight(32);
            avatarView.setPreserveRatio(true);

            Circle clip = new Circle(16, 16, 16);
            avatarView.setClip(clip);
        }

        StackPane avatarContainer = new StackPane(avatarView);
        avatarContainer.setStyle(
            "-fx-background-color: " + INPUT_BG + ";" +
            "-fx-background-radius: 50%;" +
            "-fx-border-color: " + ACCENT_GREEN + ";" +
            "-fx-border-radius: 50%;" +
            "-fx-border-width: 1px;"
        );
        avatarContainer.setPrefSize(34, 34);

        VBox contentBox = new VBox(4);
        Label senderLabel = new Label("ChargeOn AI");
        senderLabel.setStyle(
            "-fx-text-fill: " + ACCENT_GREEN + ";" +
            "-fx-font-size: 11px;" +
            "-fx-font-weight: bold;"
        );

        Label textLabel = new Label(text);
        textLabel.setWrapText(true);
        textLabel.setMaxWidth(600);
        textLabel.setStyle(
            "-fx-text-fill: " + TEXT_PRIMARY + ";" +
            "-fx-font-size: 14px;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );

        VBox bubble = new VBox(textLabel);
        bubble.setPadding(new Insets(12, 16, 12, 16));
        bubble.setStyle(
            "-fx-background-color: " + AI_MSG_BG + ";" +
            "-fx-background-radius: 4 16 16 16;" +
            "-fx-border-color: " + BORDER_COLOR + ";" +
            "-fx-border-width: 1px;" +
            "-fx-border-radius: 4 16 16 16;"
        );

        contentBox.getChildren().addAll(senderLabel, bubble);
        msgRow.getChildren().addAll(avatarContainer, contentBox);

        displayArea.getChildren().add(msgRow);
    }

    private HBox renderThinkingMessage() {

        HBox msgRow = new HBox(10);
        msgRow.setAlignment(Pos.TOP_LEFT);

        Image image = loadBotImage();
        ImageView avatarView = new ImageView();
        if (image != null) {
            avatarView.setImage(image);
            avatarView.setFitWidth(32);
            avatarView.setFitHeight(32);
            avatarView.setPreserveRatio(true);
            Circle clip = new Circle(16, 16, 16);
            avatarView.setClip(clip);
        }

        StackPane avatarContainer = new StackPane(avatarView);
        avatarContainer.setStyle(
                "-fx-background-color: " + INPUT_BG + ";" +
                "-fx-background-radius: 50%;" +
                "-fx-border-color: " + ACCENT_GREEN + ";" +
                "-fx-border-radius: 50%;" +
                "-fx-border-width: 1px;"
        );
        avatarContainer.setPrefSize(34, 34);

        VBox contentBox = new VBox(4);
        Label senderLabel = new Label("ChargeOn AI");
        senderLabel.setStyle(
                "-fx-text-fill: " + ACCENT_GREEN + ";" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;"
        );

        Label textLabel = new Label("Thinking... ⏳");
        textLabel.setWrapText(true);
        textLabel.setStyle(
                "-fx-text-fill: " + TEXT_SECONDARY + ";" +
                "-fx-font-size: 14px;" +
                "-fx-font-style: italic;" +
                "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );

        VBox bubble = new VBox(textLabel);
        bubble.setPadding(new Insets(12, 16, 12, 16));
        bubble.setStyle(
                "-fx-background-color: " + AI_MSG_BG + ";" +
                "-fx-background-radius: 4 16 16 16;" +
                "-fx-border-color: " + BORDER_COLOR + ";" +
                "-fx-border-width: 1px;" +
                "-fx-border-radius: 4 16 16 16;"
        );

        contentBox.getChildren().addAll(senderLabel, bubble);
        msgRow.getChildren().addAll(avatarContainer, contentBox);

        displayArea.getChildren().add(msgRow);
        return msgRow;
    }

    private void renderUserMessage(String text) {
        HBox msgRow = new HBox(10);
        msgRow.setAlignment(Pos.TOP_RIGHT);

        VBox contentBox = new VBox(4);
        contentBox.setAlignment(Pos.TOP_RIGHT);

        Label senderLabel = new Label("You");
        senderLabel.setStyle(
            "-fx-text-fill: " + TEXT_SECONDARY + ";" +
            "-fx-font-size: 11px;" +
            "-fx-font-weight: bold;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );

        Label textLabel = new Label(text);
        textLabel.setWrapText(true);
        textLabel.setMaxWidth(600);
        textLabel.setStyle(
            "-fx-text-fill: #FFFFFF;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: normal;" +
            "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );

        VBox bubble = new VBox(textLabel);
        bubble.setPadding(new Insets(12, 16, 12, 16));
        bubble.setStyle(
            "-fx-background-color: " + USER_MSG_BG + ";" +
            "-fx-background-radius: 16 4 16 16;"
        );

        contentBox.getChildren().addAll(senderLabel, bubble);
        msgRow.getChildren().add(contentBox);

        displayArea.getChildren().add(msgRow);
    }
}
