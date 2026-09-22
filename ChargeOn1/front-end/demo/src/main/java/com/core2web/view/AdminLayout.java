package com.core2web.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.OverrunStyle;
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

import com.core2web.controller.AdminController;
import com.core2web.controller.BusController;
import com.core2web.model.Admin;

public class AdminLayout {

    private static StackPane profileAvatar;
    private static Label profileInitials;
    private static ImageView profileImage;
    private static Label profileName;

public static VBox buildSidebar(
        ListView<String> nav,
        Stage stage) {

    VBox sb =
            new VBox(8);

    sb.getStyleClass().add(
            "sidebar"
    );

    sb.setPrefWidth(
            220
    );

    sb.setPadding(
            new Insets(
                    16,
                    12,
                    16,
                    12
            )
    );

    VBox.setVgrow(
            nav,
            Priority.ALWAYS
    );

    Button aboutUsBtn =
            new Button(
                    "About Us"
            );

    aboutUsBtn.getStyleClass().add(
            "nav-button"
    );

    aboutUsBtn.setMaxWidth(
            Double.MAX_VALUE
    );

    aboutUsBtn.setPrefHeight(
            42
    );

    aboutUsBtn.setOnAction(
            e -> AdminDashboard.goTo("About Us")
    );

    Button logoutBtn =
            new Button(
                    "⏻  Log out"
            );

    logoutBtn.getStyleClass().add(
            "logout-btn"
    );

    logoutBtn.setMaxWidth(
            Double.MAX_VALUE
    );

    logoutBtn.setOnAction(
            e -> LoginPage.showLoginPage(stage)
    );

    sb.getChildren().addAll(
            nav,
            aboutUsBtn,
            logoutBtn
    );

    return sb;
}
    public static HBox buildTopBar(
            Label heading,
            Label subheading,
            int alertBadge) {

        HBox bar =
                new HBox(14);

        bar.getStyleClass().add(
                "topbar"
        );

        bar.setPadding(
                new Insets(
                        14,
                        20,
                        14,
                        20
                )
        );

        bar.setAlignment(
                Pos.CENTER_LEFT
        );

        HBox logoRow =
                new HBox(6);

        logoRow.setAlignment(
                Pos.CENTER_LEFT
        );

        try {

            ImageView logo =
                    new ImageView(
                            new Image(
                                    "assets/logo/new_logo.png"
                            )
                    );

            logo.setFitHeight(
                    26
            );

            logo.setPreserveRatio(
                    true
            );

            logoRow.getChildren().add(
                    logo
            );

        } catch (Exception ignored) {
        }

        Label appName =
                new Label(
                        "ChargeOn"
                );

        appName.getStyleClass().add(
                "app-title"
        );

        logoRow.getChildren().add(
                appName
        );

        Label portalTag =
                new Label(
                        "OPERATIONS / ADMIN PORTAL"
                );

        portalTag.getStyleClass().add(
                "portal-tag"
        );

        VBox brand =
                new VBox(
                        1,
                        logoRow,
                        portalTag
                );

        brand.getStyleClass().add(
                "topbar-brand"
        );

        brand.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox left =
                new VBox(2);

        left.getChildren().addAll(
                heading,
                subheading
        );

        TextField search =
                new TextField();

        search.setPromptText(
                "🔍 Search buses, drivers, bookings, tickets..."
        );

        search.getStyleClass().add(
                "search-field"
        );

        search.setPrefWidth(
                300
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        BusController.FleetStatusCounts fleet = new BusController().getFleetStatusCounts();
        HBox statusPill =
                new HBox(
                        6,
                        new Circle(
                                5,
                                Color.web("#10b981")
                        ),
                        label(
                                "Live · " + fleet.online() + " buses online",
                                "small-muted"
                        )
                );

        statusPill.setAlignment(
                Pos.CENTER
        );

        statusPill.getStyleClass().add(
                "gps-pill"
        );

        StackPane bell =
                new StackPane();

        bell.getStyleClass().add(
                "notification-bell"
        );

        bell.setAlignment(
                Pos.CENTER
        );

        bell.setMinSize(
                36,
                36
        );

        bell.setPrefSize(
                36,
                36
        );

        Label bellIcon =
                new Label("\uD83D\uDD14");

        bellIcon.setStyle(
                "-fx-font-size: 18px;"
        );

        bell.getChildren().add(
                bellIcon
        );

        bell.setOnMouseClicked(
                e -> AdminDashboard.openNotifications()
        );

        Tooltip.install(
                bell,
                new Tooltip("Notifications")
        );

        profileAvatar =
                new StackPane();

        Circle avatarCircle =
                new Circle(
                        18,
                        Color.web("#2dd4bf")
                );

        profileInitials =
                new Label("A");

        profileInitials.setStyle(
                "-fx-text-fill:#0f1720;"
                        + "-fx-font-weight:bold;"
                        + "-fx-font-size:12px;"
        );

        profileImage =
                new ImageView();

        profileImage.setFitWidth(
                36
        );

        profileImage.setFitHeight(
                36
        );

        profileImage.setPreserveRatio(
                false
        );

        Circle imageClip =
                new Circle(
                        18,
                        18,
                        18
                );

        profileImage.setClip(
                imageClip
        );

        profileImage.setVisible(
                false
        );

        profileAvatar.getChildren().addAll(
                avatarCircle,
                profileInitials,
                profileImage
        );

        profileName =
                new Label(
                        "Admin"
                );

        profileName.setStyle(
                "-fx-text-fill:#f8fafc;"
                        + "-fx-font-size:13px;"
                        + "-fx-font-weight:bold;"
        );

        profileName.setMinWidth(
                100
        );

        profileName.setPrefWidth(
                140
        );

        profileName.setMaxWidth(
                160
        );

        profileName.setTextOverrun(
                OverrunStyle.ELLIPSIS
        );

        loadAdminProfile(
                avatarCircle
        );

        profileAvatar.setOnMouseClicked(
                e -> AdminDashboard.openProfile()
        );

        profileName.setOnMouseClicked(
                e -> AdminDashboard.openProfile()
        );

        HBox right =
                new HBox(
                        14,
                        statusPill,
                        bell,
                        profileAvatar,
                        profileName
                );

        right.getStyleClass().add(
                "topbar-account"
        );

        right.setAlignment(
                Pos.CENTER
        );

        right.setMinWidth(
                200
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
                                statusPill
                        )
        );

        return bar;
    }

    private static void updateTopBarVisibility(
            double width,
            javafx.scene.Node brand,
            javafx.scene.Node pageDetails,
            javafx.scene.Node search,
            javafx.scene.Node fleetStatus) {

        if (width <= 0) {
            return;
        }

        setHeaderElementVisible(
                fleetStatus,
                width >= 1260
        );

        setHeaderElementVisible(
                search,
                width >= 1000
        );

        setHeaderElementVisible(
                brand,
                width >= 680
        );

        setHeaderElementVisible(
                pageDetails,
                width >= 520
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

    private static void loadAdminProfile(
            Circle avatarCircle) {

        try {

            AdminController adminController =
                    new AdminController();

            Admin admin =
                    adminController.getCurrentAdmin();

            if (admin == null) {

                return;
            }

            if (admin.getName() != null
                    && !admin.getName().trim().isEmpty()) {

                String name =
                        admin.getName().trim();

                profileName.setText(
                        name
                );

                profileInitials.setText(
                        getInitials(name)
                );
            }

            String imageUrl =
                    admin.getProfileImageUrl();

            if (imageUrl == null
                    || imageUrl.trim().isEmpty()) {

                profileImage.setImage(
                        null
                );

                profileImage.setVisible(
                        false
                );

                avatarCircle.setVisible(
                        true
                );

                profileInitials.setVisible(
                        true
                );

                return;
            }

            loadProfileImage(
                    imageUrl,
                    avatarCircle
            );

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private static void loadProfileImage(
            String imageUrl,
            Circle avatarCircle) {

        try {

            Image image =
                    new Image(
                            imageUrl,
                            36,
                            36,
                            false,
                            true,
                            true
                    );

            if (image.isError()) {

                profileImage.setImage(
                        null
                );

                profileImage.setVisible(
                        false
                );

                avatarCircle.setVisible(
                        true
                );

                profileInitials.setVisible(
                        true
                );

                return;
            }

            profileImage.setImage(
                    image
            );

            profileImage.setVisible(
                    true
            );

            avatarCircle.setVisible(
                    false
            );

            profileInitials.setVisible(
                    false
            );

        } catch (Exception e) {

            e.printStackTrace();

            profileImage.setImage(
                    null
            );

            profileImage.setVisible(
                    false
            );

            avatarCircle.setVisible(
                    true
            );

            profileInitials.setVisible(
                    true
            );
        }
    }

    public static void refreshAdminProfile() {

        if (profileAvatar == null
                || profileImage == null
                || profileInitials == null
                || profileName == null) {

            return;
        }

        Circle avatarCircle = null;

        for (javafx.scene.Node node :
                profileAvatar.getChildren()) {

            if (node instanceof Circle) {

                avatarCircle =
                        (Circle) node;

                break;
            }
        }

        if (avatarCircle == null) {
            return;
        }

        loadAdminProfile(
                avatarCircle
        );
    }

    private static String getInitials(
            String fullName) {

        if (fullName == null
                || fullName.trim().isEmpty()) {

            return "A";
        }

        String[] parts =
                fullName
                        .trim()
                        .split("\\s+");

        if (parts.length == 1) {

            return parts[0]
                    .substring(0, 1)
                    .toUpperCase();
        }

        return (
                parts[0]
                        .substring(0, 1)
                        +
                parts[parts.length - 1]
                        .substring(0, 1)
        ).toUpperCase();
    }

    public static VBox card(
            String title) {

        VBox c =
                new VBox(6);

        c.getStyleClass().add(
                "card"
        );

        c.setPadding(
                new Insets(14)
        );

        c.getChildren().add(
                label(
                        title,
                        "card-title"
                )
        );

        return c;
    }

    public static VBox statCard(
            String title,
            String value,
            String valueColor,
            String sub) {

        VBox c =
                card(title);

        Label val =
                new Label(value);

        val.setStyle(
                "-fx-text-fill:" + valueColor
                        + ";-fx-font-size:26px;"
                        + "-fx-font-weight:bold;"
        );

        c.getChildren().add(
                val
        );

        if (sub != null) {

            c.getChildren().add(
                    label(
                            sub,
                            "card-sub"
                    )
            );
        }

        HBox.setHgrow(
                c,
                Priority.ALWAYS
        );

        return c;
    }

    public static HBox statusBadge(
            String text,
            String colorHex,
            double width) {

        Label badge =
                new Label(text);

        badge.setStyle(
                "-fx-background-color: "
                        + colorHex
                        + "22;"
                        + "-fx-text-fill: "
                        + colorHex
                        + ";"
                        + "-fx-font-size: 10px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-padding: 4 10 4 10;"
                        + "-fx-background-radius: 6;"
        );

        HBox box =
                new HBox(badge);

        if (width > 0) {

            box.setPrefWidth(
                    width
            );

            box.setMinWidth(
                    width
            );
        }

        box.setAlignment(
                Pos.CENTER_LEFT
        );

        return box;
    }

    public static HBox alertBanner(
            String icon,
            String title,
            String subtitle) {

        HBox banner =
                new HBox(14);

        banner.setPadding(
                new Insets(
                        14,
                        18,
                        14,
                        18
                )
        );

        banner.getStyleClass().add(
                "emergency-banner"
        );

        banner.setAlignment(
                Pos.CENTER_LEFT
        );

        StackPane iconPane =
                new StackPane(
                        new Circle(
                                14,
                                Color.web("#f87171")
                        ),
                        new Label(icon) {
                            {
                                setStyle(
                                        "-fx-text-fill:white;"
                                                + "-fx-font-size:13px;"
                                                + "-fx-font-weight:bold;"
                                );
                            }
                        }
                );

        VBox textBox =
                new VBox(2);

        HBox.setHgrow(
                textBox,
                Priority.ALWAYS
        );

        Label titleLbl =
                new Label(title);

        titleLbl.getStyleClass().add(
                "emergency-title"
        );

        Label subLbl =
                new Label(subtitle);

        subLbl.getStyleClass().add(
                "emergency-subtitle"
        );

        subLbl.setWrapText(
                true
        );

        textBox.getChildren().addAll(
                titleLbl,
                subLbl
        );

        banner.getChildren().addAll(
                iconPane,
                textBox
        );

        return banner;
    }

    public static Label colLabel(
            String text,
            double width) {

        Label l =
                new Label(text);

        l.setStyle(
                "-fx-text-fill:#64748b;"
                        + "-fx-font-size:10px;"
                        + "-fx-font-weight:bold;"
        );

        l.setPrefWidth(
                width
        );

        l.setMinWidth(
                width
        );

        l.setWrapText(
                true
        );

        return l;
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
