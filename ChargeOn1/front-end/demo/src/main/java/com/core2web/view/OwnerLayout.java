
package com.core2web.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import com.core2web.controller.OwnerController;
import com.core2web.model.Owner;

public class OwnerLayout {

    public static VBox buildSidebar(ListView<String> nav, Stage stage) {

        VBox sb = new VBox(8);

        sb.getStyleClass().add("sidebar");

        sb.setPrefWidth(180);

        sb.setPadding(
                new Insets(16, 12, 16, 12)
        );

        VBox.setVgrow(
                nav,
                Priority.ALWAYS
        );

        Button logoutBtn = new Button("⏻  Log out");

        logoutBtn.setStyle(
                "-fx-background-color: #17f782;"
                        + "-fx-text-fill: black;"
                        + "-fx-font-weight: bold;"
                        + "-fx-font-size: 13px;"
                        + "-fx-padding: 10 16 10 16;"
                        + "-fx-background-radius: 8;"
                        + "-fx-cursor: hand;"
        );

        logoutBtn.setMaxWidth(
                Double.MAX_VALUE
        );

        logoutBtn.setOnAction(e ->
                LoginPage.showLoginPage(stage)
        );

        sb.getChildren().addAll(
                nav,
                logoutBtn
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

        bar.setAlignment(
                Pos.CENTER_LEFT
        );



        HBox logoRow = new HBox(6);

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

        logoRow.getChildren().add(
                appName
        );


        VBox brand =
                new VBox(logoRow);

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


        Region sp = new Region();

        HBox.setHgrow(
                sp,
                Priority.ALWAYS
        );



        TextField search =
                new TextField();

        search.setPromptText(
                "🔍  Search locations, buses, bookings…"
        );

        search.getStyleClass().add(
                "search-field"
        );

        search.setPrefWidth(260);



        StackPane bellPane =
                new StackPane();

        bellPane.getChildren().add(
                styledLabel(
                        "🔔",
                        "#F8FAFC",
                        18,
                        false
                )
        );




        bellPane.setOnMouseClicked(
                e -> OwnerDashboard.openNotifications()
        );



        HBox profile =
                new HBox(8);

        profile.setAlignment(
                Pos.CENTER_LEFT
        );


        Owner owner =
                new OwnerController().getCurrentOwner();

        String ownerName = "Owner";
        String ownerInitials = "O";


        if (owner != null
                && owner.getName() != null
                && !owner.getName().trim().isEmpty()) {

            ownerName =
                    owner.getName().trim();

            String[] parts =
                    ownerName.split("\\s+");


            if (parts.length == 1) {

                ownerInitials =
                        parts[0]
                                .substring(0, 1)
                                .toUpperCase();

            } else {

                ownerInitials =
                        (
                                parts[0]
                                        .substring(0, 1)
                                        +
                                parts[parts.length - 1]
                                        .substring(0, 1)
                        ).toUpperCase();
            }
        }



        StackPane avatar =
                new StackPane();


        Label avatarInitials =
                new Label(ownerInitials);

        avatarInitials.getStyleClass().add(
                "avatar"
        );


        ImageView profileImage =
                new ImageView();

        profileImage.setFitWidth(38);

        profileImage.setFitHeight(38);

        profileImage.setPreserveRatio(false);


        Circle imageClip =
                new Circle(
                        19,
                        19,
                        19
                );

        profileImage.setClip(
                imageClip
        );

        profileImage.setVisible(false);


        avatar.getChildren().addAll(
                avatarInitials,
                profileImage
        );



        if (owner != null
                && owner.getProfileImageUrl() != null
                && !owner.getProfileImageUrl()
                        .trim()
                        .isEmpty()) {

            try {

                Image image =
                        new Image(
                                owner.getProfileImageUrl(),
                                38,
                                38,
                                false,
                                true
                        );

                profileImage.setImage(
                        image
                );

                profileImage.setVisible(
                        true
                );

                avatarInitials.setVisible(
                        false
                );

            } catch (Exception e) {

                profileImage.setVisible(
                        false
                );

                avatarInitials.setVisible(
                        true
                );
            }
        }



        VBox profileText =
                new VBox(1);

        profileText.setTranslateY(25);

        profileText.getChildren().addAll(
                styledLabel(
                        ownerName,
                        "#F8FAFC",
                        15,
                        true
                )
        );

        profile.getChildren().addAll(
                avatar,
                profileText
        );


        profile.setOnMouseClicked(
                e -> OwnerDashboard.openProfile()
        );



        bar.getChildren().addAll(
                brand,
                left,
                sp,
                search,
                bellPane,
                profile
        );

        return bar;
    }



    public static void refreshTopProfile() {

        if (OwnerDashboard.scene == null) {
            return;
        }

        HBox newTopBar =
                buildTopBar(
                        OwnerDashboard.heading,
                        OwnerDashboard.subheading
                );


        if (!(OwnerDashboard.scene.getRoot()
                instanceof BorderPane)) {

            return;
        }

        BorderPane root =
                (BorderPane)
                        OwnerDashboard.scene.getRoot();


        if (!(root.getCenter()
                instanceof VBox)) {

            return;
        }

        VBox center =
                (VBox) root.getCenter();


        if (!center.getChildren().isEmpty()) {

            center.getChildren().set(
                    0,
                    newTopBar
            );
        }
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



    public static Label styledLabel(
            String text,
            String color,
            int size,
            boolean bold) {

        Label l =
                new Label(text);

        l.setStyle(
                "-fx-text-fill:" + color + ";"
                        + "-fx-font-size:" + size + "px;"
                        + (bold
                        ? "-fx-font-weight:bold;"
                        : "")
        );

        return l;
    }
}
