package com.core2web.view;

import java.util.List;

import com.core2web.model.Notification;
import com.core2web.controller.NotificationController;
import com.core2web.util.DateTimeUtil;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class DriverNotifications {

    public static ScrollPane buildMainContent() {

        VBox content = new VBox(16);
        content.setPadding(new Insets(16));

        Button backButton = new Button("←  Back to Dashboard");

        backButton.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #94a3b8;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 6 0 6 0;" +
                "-fx-cursor: hand;"
        );

        backButton.setOnMouseEntered(e ->
                backButton.setStyle(
                        "-fx-background-color: transparent;" +
                        "-fx-text-fill: #f8fafc;" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 6 0 6 0;" +
                        "-fx-cursor: hand;"
                )
        );

        backButton.setOnMouseExited(e ->
                backButton.setStyle(
                        "-fx-background-color: transparent;" +
                        "-fx-text-fill: #94a3b8;" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 6 0 6 0;" +
                        "-fx-cursor: hand;"
                )
        );

        backButton.setOnAction(e -> DriverDashboard.goTo("Dashboard"));

        VBox header = new VBox(4);

        Label title = new Label("Notifications");
        title.getStyleClass().add("section-title");

        Label description = new Label(
                "Recent alerts and important updates for your shift"
        );
        description.getStyleClass().add("card-sub");

        header.getChildren().addAll(title, description);

        VBox notificationList = new VBox(10);

        NotificationController controller = new NotificationController();
        List<Notification> notifications = controller.getMyNotifications();

        if (notifications.isEmpty()) {
            Label empty = new Label("No notifications yet.");
            empty.getStyleClass().add("booking-detail-muted");
            notificationList.getChildren().add(empty);
        } else {
            for (Notification n : notifications) {
                notificationList.getChildren().add(
                        notificationCard(
                                n.displayColor(),
                                n.displayTitle(),
                                n.getMessage(),
                                DateTimeUtil.display(n.getCreatedAt())
                        )
                );
            }
        }
        content.getChildren().addAll(
                backButton,
                header,
                notificationList
        );

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("scroll-pane");

        return scrollPane;
    }

    private static HBox notificationCard(
            String dotColor,
            String notificationType,
            String message,
            String time
    ) {

        HBox card = new HBox(12);
        card.setAlignment(Pos.TOP_LEFT);
        card.setPadding(new Insets(15));

        card.setStyle(
                "-fx-background-color: #141e29;" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: #243244;" +
                "-fx-border-radius: 8;"
        );

        Circle dot = new Circle(5, Color.web(dotColor));
        dot.setTranslateY(6);

        VBox textBox = new VBox(5);

        Label typeLabel = new Label(notificationType);

        typeLabel.setStyle(
                "-fx-text-fill: #f8fafc;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;"
        );

        Label messageLabel = new Label(message);

        messageLabel.setWrapText(true);

        messageLabel.setStyle(
                "-fx-text-fill: #cbd5e1;" +
                "-fx-font-size: 12px;"
        );

        Label timeLabel = new Label(time);
        timeLabel.getStyleClass().add("booking-detail-muted");

        textBox.getChildren().addAll(
                typeLabel,
                messageLabel,
                timeLabel
        );

        HBox.setHgrow(textBox, Priority.ALWAYS);



        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        card.getChildren().addAll(
                dot,
                textBox,
                spacer
        );

        return card;
    }
}
