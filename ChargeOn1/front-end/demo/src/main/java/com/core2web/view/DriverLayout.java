package com.core2web.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import com.core2web.controller.DriverController;
import com.core2web.model.Driver;

public class DriverLayout {

    private static StackPane profileAvatar;
    private static Label profileInitials;
    private static Label profileName;

    private static final DriverController driverController = new DriverController();

    public static VBox buildSidebar(ListView<String> nav, Stage stage) {

        VBox sb = new VBox(8);
        sb.getStyleClass().add("sidebar");
        sb.setPrefWidth(210);
        sb.setPadding(new Insets(16, 12, 16, 12));

        VBox.setVgrow(nav, Priority.ALWAYS);

        Button logoutbtn = new Button("⏻  Log out");

        logoutbtn.setStyle(
                "-fx-background-color: #17f782;" +
                "-fx-text-fill: black;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;" +
                "-fx-padding: 10 16 10 16;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"
        );

        logoutbtn.setMaxWidth(Double.MAX_VALUE);

        logoutbtn.setOnAction(
                e -> LoginPage.showLoginPage(stage)
        );

        sb.getChildren().addAll(
                nav,
                logoutbtn
        );

        return sb;
    }

    public static HBox buildTopBar(
            Label heading,
            Label subheading) {

        HBox bar = new HBox(14);

        bar.getStyleClass().add("topbar");

        bar.setPadding(
                new Insets(14, 20, 14, 20)
        );

        bar.setAlignment(Pos.CENTER_LEFT);

        HBox logoRow = new HBox(6);

        logoRow.setAlignment(Pos.CENTER_LEFT);

        try {

            ImageView logo =
                    new ImageView(
                            new Image(
                                    "assets/logo/new_logo.png"
                            )
                    );

            logo.setFitHeight(26);
            logo.setPreserveRatio(true);

            logoRow.getChildren().add(logo);

        } catch (Exception ignored) {
        }

        Label appName =
                new Label("ChargeOn");

        appName.getStyleClass().add(
                "app-title"
        );

        logoRow.getChildren().add(appName);

        VBox brand = new VBox(logoRow);

        brand.getStyleClass().add(
                "topbar-brand"
        );

        brand.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox left = new VBox(2);

        left.getChildren().addAll(
                heading,
                subheading
        );

        TextField search = new TextField();

        search.setPromptText(
                "Search bookings, stops, bus ID..."
        );

        search.getStyleClass().add(
                "search-field"
        );

        search.setPrefWidth(320);

        Region spacer = new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        HBox gps = new HBox(
                6,
                new Circle(
                        5,
                        Color.web("#10b981")
                ),
                label(
                        "GPS live",
                        "small-muted"
                )
        );

        gps.setAlignment(Pos.CENTER);

        gps.getStyleClass().add(
                "gps-pill"
        );

        StackPane bell = new StackPane();

        bell.getStyleClass().add("notification-bell");
        bell.setAlignment(Pos.CENTER);
        bell.setMinSize(36, 36);
        bell.setPrefSize(36, 36);

        Label bellIcon =
                new Label("\uD83D\uDD14");

        bellIcon.setStyle(
                "-fx-font-size: 18px;"
        );

      StackPane badge =
                new StackPane(  );

        badge.setTranslateX(10);
        badge.setTranslateY(-10);

        bell.getChildren().addAll(
                bellIcon,
                badge
        );

        bell.setOnMouseClicked(
                e -> DriverDashboard.openNotifications()
        );

        Tooltip.install(
                bell,
                new Tooltip("Notifications")
        );

        profileAvatar = new StackPane();

        Circle avatarCircle =
                new Circle(
                        18,
                        Color.web("#2dd4bf")
                );

        profileInitials =
                new Label("D");

        profileInitials.setStyle(
                "-fx-text-fill:#0f1720;" +
                "-fx-font-weight:bold;" +
                "-fx-font-size:12px;"
        );

        profileAvatar.getChildren().addAll(
                avatarCircle,
                profileInitials
        );

        profileName =
                new Label("Loading...");

        profileName.setStyle(
                "-fx-text-fill:#f8fafc;" +
                "-fx-font-size:13px;" +
                "-fx-font-weight:bold;"
        );

        profileName.setMinWidth(
                Region.USE_PREF_SIZE
        );

        refreshProfile();

        profileAvatar.setOnMouseClicked(
                e -> DriverDashboard.openProfile()
        );

        profileName.setOnMouseClicked(
                e -> DriverDashboard.openProfile()
        );

        HBox right = new HBox(
                14,
                gps,
                bell,
                profileAvatar,
                profileName
        );

        right.setAlignment(
                Pos.CENTER
        );

        right.setMinWidth(
                Region.USE_PREF_SIZE
        );

        bar.getChildren().addAll(
                brand,
                left,
                search,
                spacer,
                right
        );

        bar.widthProperty().addListener(
                (observable, oldWidth, newWidth) ->
                        updateTopBarVisibility(
                                newWidth.doubleValue(),
                                brand,
                                left,
                                search,
                                gps
                        )
        );

        return bar;
    }

    private static void updateTopBarVisibility(
            double width,
            javafx.scene.Node brand,
            javafx.scene.Node pageDetails,
            javafx.scene.Node search,
            javafx.scene.Node gpsStatus) {

        if (width <= 0) {
            return;
        }

        setHeaderElementVisible(
                gpsStatus,
                width >= 1120
        );

        setHeaderElementVisible(
                search,
                width >= 980
        );

        setHeaderElementVisible(
                brand,
                width >= 660
        );

        setHeaderElementVisible(
                pageDetails,
                width >= 500
        );
    }

    private static void setHeaderElementVisible(
            javafx.scene.Node element,
            boolean visible) {

        element.setVisible(
                visible
        );

        element.setManaged(
                visible
        );
    }

    public static void refreshProfile() {

        if (profileAvatar == null
                || profileInitials == null
                || profileName == null) {

            return;
        }

        Driver driver =
                driverController.getCurrentDriver();

        if (driver == null) {

            profileName.setText(
                    "Driver"
            );

            profileInitials.setText(
                    "D"
            );

            showInitials();

            return;
        }

        String name = driver.getName();

        if (name != null
                && !name.trim().isEmpty()) {

            profileName.setText(
                    name
            );

            profileInitials.setText(
                    getInitials(name)
            );

        } else {

            profileName.setText(
                    "Driver"
            );

            profileInitials.setText(
                    "D"
            );
        }

        String imageUrl =
                driver.getProfileImageUrl();

        if (imageUrl != null
                && !imageUrl.trim().isEmpty()) {

            try {

                Image image =
                        new Image(
                                imageUrl,
                                true
                        );

                ImageView imageView =
                        new ImageView(image);

                imageView.setFitWidth(36);
                imageView.setFitHeight(36);
                imageView.setPreserveRatio(false);

                Circle clip =
                        new Circle(
                                18,
                                18,
                                18
                        );

                imageView.setClip(clip);

                profileAvatar.getChildren().clear();

                profileAvatar.getChildren().add(
                        imageView
                );

            } catch (Exception e) {

                showInitials();
            }

        } else {

            showInitials();
        }
    }

    private static void showInitials() {

        if (profileAvatar == null) {
            return;
        }

        profileAvatar.getChildren().clear();

        Circle avatarCircle =
                new Circle(
                        18,
                        Color.web("#2dd4bf")
                );

        profileAvatar.getChildren().addAll(
                avatarCircle,
                profileInitials
        );
    }

    private static String getInitials(
            String fullName) {

        if (fullName == null
                || fullName.trim().isEmpty()) {

            return "D";
        }

        String[] parts =
                fullName.trim().split("\\s+");

        if (parts.length == 1) {

            return parts[0]
                    .substring(0, 1)
                    .toUpperCase();
        }

        return (
                parts[0].substring(0, 1)
                +
                parts[parts.length - 1]
                        .substring(0, 1)
        ).toUpperCase();
    }

    public static Label label(
            String text,
            String styleClass) {

        Label l =
                new Label(text);

        l.getStyleClass().add(
                styleClass
        );

        return l;
    }
}
