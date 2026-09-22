package com.core2web.view;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import com.core2web.controller.TicketController;
import com.core2web.model.Booking;

public class ReportIssue {

    public static ScrollPane buildMainContent(Booking booking) {

        VBox content =
                new VBox(16);

        content.setPadding(
                new Insets(16)
        );



        Button backButton =
                new Button("←  Back to Dashboard");

        backButton.setStyle(
                "-fx-background-color: transparent;"
                        + "-fx-text-fill: #94a3b8;"
                        + "-fx-font-size: 13px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-padding: 6 0 6 0;"
                        + "-fx-cursor: hand;"
        );

        backButton.setOnMouseEntered(e ->
                backButton.setStyle(
                        "-fx-background-color: transparent;"
                                + "-fx-text-fill: #f8fafc;"
                                + "-fx-font-size: 13px;"
                                + "-fx-font-weight: bold;"
                                + "-fx-padding: 6 0 6 0;"
                                + "-fx-cursor: hand;"
                )
        );

        backButton.setOnMouseExited(e ->
                backButton.setStyle(
                        "-fx-background-color: transparent;"
                                + "-fx-text-fill: #94a3b8;"
                                + "-fx-font-size: 13px;"
                                + "-fx-font-weight: bold;"
                                + "-fx-padding: 6 0 6 0;"
                                + "-fx-cursor: hand;"
                )
        );

        backButton.setOnAction(e ->
                OwnerDashboard.goTo("Dashboard")
        );



        VBox pageHeader =
                new VBox(4);

        Label title =
                new Label("Report an issue");

        title.getStyleClass().add(
                "section-title"
        );

        Label subtitle =
                new Label(
                        booking != null
                                ? "Tell us what went wrong with " + booking.shortRef()
                                        + ". The support team will receive it immediately."
                                : "Tell us what went wrong. The support team will receive it immediately."
                );

        subtitle.getStyleClass().add(
                "card-sub"
        );

        subtitle.setWrapText(true);

        pageHeader.getChildren().addAll(
                title,
                subtitle
        );



        VBox formCard =
                new VBox(12);

        formCard.getStyleClass().add(
                "card"
        );

        formCard.setPadding(
                new Insets(20)
        );

        ComboBox<String> issueType = new ComboBox<>();
        issueType.getItems().addAll("Bus delayed", "Charging problem", "Driver issue", "Payment issue", "Other");
        issueType.setValue("Bus delayed");
        issueType.setMaxWidth(Double.MAX_VALUE);

        ComboBox<String> priority = new ComboBox<>();
        priority.getItems().addAll("Medium", "High");
        priority.setValue("Medium");
        priority.setMaxWidth(Double.MAX_VALUE);

        TextArea description = new TextArea();
        description.setPromptText("Describe what happened");
        description.setWrapText(true);
        description.setPrefRowCount(5);

        Button submitButton =
                new Button("Submit ticket");

        submitButton.getStyleClass().add(
                "primary-btn"
        );

        submitButton.setDisable(true);

        description.textProperty().addListener((obs, oldText, newText) ->
                submitButton.setDisable(newText.trim().isEmpty())
        );

        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_RIGHT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        actions.getChildren().addAll(spacer, submitButton);

        Label statusLabel = new Label();
        statusLabel.setWrapText(true);
        statusLabel.setManaged(false);
        statusLabel.setVisible(false);

        HBox statusRow = new HBox(statusLabel);
        statusRow.setAlignment(Pos.CENTER_RIGHT);

        submitButton.setOnAction(e -> {

            String details = description.getText().trim();
            submitButton.setDisable(true);
            statusLabel.setVisible(false);
            statusLabel.setManaged(false);

            Thread submitter = new Thread(() -> {
                String ticketId = new TicketController().submitOwnerIssue(
                        booking, issueType.getValue(), priority.getValue(), details);

                Platform.runLater(() -> {

                    statusLabel.setText(ticketId == null
                            ? "We couldn't submit your issue. Please try again."
                            : "Your issue has been sent to the admin support team.");

                    statusLabel.setStyle(
                            "-fx-font-size:12px;-fx-font-weight:bold;-fx-text-fill:"
                                    + (ticketId == null ? "#ef4444" : "#10b981") + ";"
                    );

                    statusLabel.setManaged(true);
                    statusLabel.setVisible(true);

                    if (ticketId != null) {
                        issueType.setValue("Bus delayed");
                        priority.setValue("Medium");
                        description.clear();
                    } else {
                        submitButton.setDisable(details.isEmpty());
                    }
                });
            });

            submitter.setDaemon(true);
            submitter.start();
        });

        formCard.getChildren().addAll(
                fieldLabel("Issue type"), issueType,
                fieldLabel("Priority"), priority,
                fieldLabel("What happened?"), description,
                actions,
                statusRow
        );



        content.getChildren().addAll(
                backButton,
                pageHeader,
                formCard
        );


        ScrollPane scrollPane =
                new ScrollPane(content);

        scrollPane.setFitToWidth(
                true
        );

        scrollPane.getStyleClass().add(
                "scroll-pane"
        );

        return scrollPane;
    }



    private static Label fieldLabel(
            String text) {

        Label label =
                new Label(text);

        label.setStyle(
                "-fx-text-fill:#cbd5e1;"
                        + "-fx-font-size:11px;"
                        + "-fx-font-weight:bold;"
        );

        return label;
    }
}
