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

import java.io.InputStream;

public class CopilotChatUIDriver {

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

    private static final String DRIVER_SYSTEM_PROMPT =
            "You are ChargeOn AI Assistant, an expert AI assistant for EV Bus Drivers. " +
            "Help drivers check battery levels, estimated driving range, emergency detour bookings, route ETA, " +
            "and reporting port/charger faults to depot ops. Maintain a helpful, quick, and safety-focused tone. " +
            CHARGEON_TECH_CONTEXT +
            "Only answer questions related to the ChargeOn platform, EV bus driving duties, or how " +
            "ChargeOn itself is built (using only the technical facts above). " +
            "If asked something unrelated to ChargeOn (general knowledge, unrelated coding help, " +
            "personal advice, current events, etc.), politely decline and steer the conversation back to " +
            "ChargeOn — you are not a general-purpose assistant. Never reveal API keys, credentials, or " +
            "other secrets. Respond in plain text only — do not use Markdown formatting such as asterisks " +
            "for bold, italics, or bullet points.";

    private VBox displayArea;
    private ScrollPane scrollPane;

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

    Scene getCopilotScene() {
        DriverDashboard.goTo("AI Assistant");
        return DriverDashboard.scene;
    }

    static BorderPane buildMainContent() {
        return new CopilotChatUIDriver().buildChatContent();
    }

    private BorderPane buildChatContent() {
        BorderPane chatArea = new BorderPane();
        chatArea.setStyle("-fx-background-color: " + DARK_BG + ";");

        chatArea.setTop(createHeader());

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
                "-fx-border-color: transparent;");
        chatArea.setCenter(scrollPane);

        chatArea.setBottom(createBottomSection());

        showInitialGreeting();
        return chatArea;
    }

    private HBox createHeader() {
        HBox header = new HBox(15);
        header.setPadding(new Insets(15, 25, 15, 25));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle(
                "-fx-background-color: " + CARD_BG + ";" +
                "-fx-border-color: " + BORDER_COLOR + ";" +
                "-fx-border-width: 0 0 1px 0;");

        Image image = loadBotImage();
        ImageView imageView = new ImageView();
        if (image != null) {
            imageView.setImage(image);
            imageView.setFitWidth(45);
            imageView.setFitHeight(45);
            imageView.setPreserveRatio(true);
            Circle clip = new Circle(22.5, 22.5, 22.5);
            imageView.setClip(clip);
        }

        StackPane avatarPane = new StackPane(imageView);
        avatarPane.setStyle(
                "-fx-background-color: " + INPUT_BG + ";" +
                "-fx-background-radius: 50%;" +
                "-fx-border-color: " + ACCENT_GREEN + ";" +
                "-fx-border-radius: 50%;" +
                "-fx-border-width: 1.5px;");
        avatarPane.setPrefSize(48, 48);

        Label titleLabel = new Label("ChargeOn AI Assistant");
        titleLabel.setStyle(
                "-fx-text-fill: " + TEXT_PRIMARY + ";" +
                "-fx-font-size: 20px;" +
                "-fx-font-weight: bold;" +
                "-fx-font-family: 'Segoe UI', Arial, sans-serif;");

        Label subtitleLabel = new Label("Driver AI Assistant · live telemetry depends on the assigned bus");
        subtitleLabel.setStyle(
                "-fx-text-fill: " + TEXT_SECONDARY + ";" +
                "-fx-font-size: 13px;" +
                "-fx-font-family: 'Segoe UI', Arial, sans-serif;");

        VBox titleBox = new VBox(2, titleLabel, subtitleLabel);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox statusBadge = new HBox(6);
        statusBadge.setAlignment(Pos.CENTER);
        statusBadge.setPadding(new Insets(6, 12, 6, 12));
        statusBadge.setStyle(
                "-fx-background-color: rgba(60, 203, 127, 0.15);" +
                "-fx-background-radius: 20px;" +
                "-fx-border-color: " + ACCENT_GREEN + ";" +
                "-fx-border-radius: 20px;" +
                "-fx-border-width: 1px;");

        Circle greenDot = new Circle(4, Color.web(ACCENT_GREEN));
        Label statusLabel = new Label("Groq AI Active");
        statusLabel.setStyle(
                "-fx-text-fill: " + ACCENT_GREEN + ";" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;");
        statusBadge.getChildren().addAll(greenDot, statusLabel);

        header.getChildren().addAll(avatarPane, titleBox, spacer, statusBadge);
        return header;
    }


    private VBox createBottomSection() {
        VBox bottomContainer = new VBox(10);
        bottomContainer.setPadding(new Insets(16, 25, 20, 25));
        bottomContainer.setAlignment(Pos.CENTER);
        bottomContainer.setStyle(
                "-fx-background-color: " + CARD_BG + ";" +
                "-fx-border-color: " + BORDER_COLOR + ";" +
                "-fx-border-width: 1px 0 0 0;");

        Label quickLabel = new Label("Suggested prompts:");
        quickLabel.setStyle(
                "-fx-text-fill: " + TEXT_SECONDARY + ";" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-font-family: 'Segoe UI', Arial, sans-serif;");

        HBox chipsBox = new HBox(8);
        chipsBox.setAlignment(Pos.CENTER_LEFT);

        String[] suggestedPrompts = {
                "Check battery & range estimate",
                "Analyze my emergency diversion",
                "Show next stop ETA & route",
                "Report Port 2 fault to ops"
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
                "-fx-cursor: hand;");

        chip.setOnMouseEntered(e -> chip.setStyle(
                "-fx-background-color: rgba(60, 203, 127, 0.2);" +
                "-fx-text-fill: " + ACCENT_GREEN + ";" +
                "-fx-font-size: 12px;" +
                "-fx-font-family: 'Segoe UI', Arial, sans-serif;" +
                "-fx-border-color: " + ACCENT_GREEN + ";" +
                "-fx-border-width: 1px;" +
                "-fx-border-radius: 15px;" +
                "-fx-background-radius: 15px;" +
                "-fx-padding: 6 12 6 12;" +
                "-fx-cursor: hand;"));

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
                "-fx-cursor: hand;"));

        chip.setOnAction(e -> sendUserPrompt(text));

        return chip;
    }


    private void sendUserPrompt(String userPrompt) {

        renderUserMessage(userPrompt);

        HBox thinkingBubble = renderThinkingMessage();

        scrollToBottom();

        CopilotController.askCopilotAsync(
                userPrompt,
                DRIVER_SYSTEM_PROMPT,
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
        renderAIMessage("Hello Driver! \uD83D\uDC4B I'm your ChargeOn AI Assistant powered by Groq.\n\n" +
                "Monitoring Bus #07 Telemetry · Shift ends 15:00\n\n" +
                "Ask me anything about your route, battery range, or charging status below!");
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
                "-fx-border-width: 1px;");
        avatarContainer.setPrefSize(34, 34);

        VBox contentBox = new VBox(4);
        Label senderLabel = new Label("ChargeOn AI");
        senderLabel.setStyle(
                "-fx-text-fill: " + ACCENT_GREEN + ";" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;");

        Label textLabel = new Label(text);
        textLabel.setWrapText(true);
        textLabel.setMaxWidth(600);
        textLabel.setStyle(
                "-fx-text-fill: " + TEXT_PRIMARY + ";" +
                "-fx-font-size: 14px;" +
                "-fx-font-family: 'Segoe UI', Arial, sans-serif;");

        VBox bubble = new VBox(textLabel);
        bubble.setPadding(new Insets(12, 16, 12, 16));
        bubble.setStyle(
                "-fx-background-color: " + AI_MSG_BG + ";" +
                "-fx-background-radius: 4 16 16 16;" +
                "-fx-border-color: " + BORDER_COLOR + ";" +
                "-fx-border-width: 1px;" +
                "-fx-border-radius: 4 16 16 16;");

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
                "-fx-border-width: 1px;");
        avatarContainer.setPrefSize(34, 34);

        VBox contentBox = new VBox(4);
        Label senderLabel = new Label("ChargeOn AI");
        senderLabel.setStyle(
                "-fx-text-fill: " + ACCENT_GREEN + ";" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;");

        Label textLabel = new Label("Thinking... ⏳");
        textLabel.setWrapText(true);
        textLabel.setStyle(
                "-fx-text-fill: " + TEXT_SECONDARY + ";" +
                "-fx-font-size: 14px;" +
                "-fx-font-style: italic;" +
                "-fx-font-family: 'Segoe UI', Arial, sans-serif;");

        VBox bubble = new VBox(textLabel);
        bubble.setPadding(new Insets(12, 16, 12, 16));
        bubble.setStyle(
                "-fx-background-color: " + AI_MSG_BG + ";" +
                "-fx-background-radius: 4 16 16 16;" +
                "-fx-border-color: " + BORDER_COLOR + ";" +
                "-fx-border-width: 1px;" +
                "-fx-border-radius: 4 16 16 16;");

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
                "-fx-font-family: 'Segoe UI', Arial, sans-serif;");

        Label textLabel = new Label(text);
        textLabel.setWrapText(true);
        textLabel.setMaxWidth(600);
        textLabel.setStyle(
                "-fx-text-fill: #FFFFFF;" +
                "-fx-font-size: 14px;" +
                "-fx-font-family: 'Segoe UI', Arial, sans-serif;");

        VBox bubble = new VBox(textLabel);
        bubble.setPadding(new Insets(12, 16, 12, 16));
        bubble.setStyle(
                "-fx-background-color: " + USER_MSG_BG + ";" +
                "-fx-background-radius: 16 4 16 16;");

        contentBox.getChildren().addAll(senderLabel, bubble);
        msgRow.getChildren().add(contentBox);

        displayArea.getChildren().add(msgRow);
    }
}
