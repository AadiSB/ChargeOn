package com.core2web.view;

import java.io.File;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import com.core2web.controller.OwnerController;
import com.core2web.model.Owner;
import com.core2web.service.CloudinaryService;

public class OwnerProfile {

    private static final OwnerController ownerController =
            new OwnerController();

    private static final CloudinaryService cloudinaryService =
            new CloudinaryService();


    public static ScrollPane buildMainContent() {

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
                new Label("Profile");

        title.getStyleClass().add(
                "section-title"
        );

        Label subtitle =
                new Label(
                        "Manage your owner profile and contact information"
                );

        subtitle.getStyleClass().add(
                "card-sub"
        );

        pageHeader.getChildren().addAll(
                title,
                subtitle
        );



        VBox profileCard =
                new VBox(16);

        profileCard.getStyleClass().add(
                "card"
        );

        profileCard.setPadding(
                new Insets(20)
        );

        HBox profileRow =
                new HBox(18);

        profileRow.setAlignment(
                Pos.CENTER_LEFT
        );



        StackPane avatar =
                new StackPane();

        Circle avatarCircle =
                new Circle(
                        38,
                        Color.web("#334155")
                );

        Label initials =
                new Label("AM");

        initials.setStyle(
                "-fx-text-fill:#f8fafc;"
                        + "-fx-font-size:20px;"
                        + "-fx-font-weight:bold;"
        );

        avatar.getChildren().addAll(
                avatarCircle,
                initials
        );


        ImageView profileImage =
                new ImageView();

        profileImage.setFitWidth(76);

        profileImage.setFitHeight(76);

        profileImage.setPreserveRatio(true);


        Circle imageClip =
                new Circle(
                        38,
                        38,
                        38
                );

        profileImage.setClip(
                imageClip
        );


        profileImage.setVisible(false);

        avatar.getChildren().add(
                profileImage
        );



        VBox profileInfo =
                new VBox(4);

        Label name =
                new Label("Loading...");

        name.setStyle(
                "-fx-text-fill:#f8fafc;"
                        + "-fx-font-size:18px;"
                        + "-fx-font-weight:bold;"
        );

        Label role =
                new Label("EV Owner");

        role.setStyle(
                "-fx-text-fill:#94a3b8;"
                        + "-fx-font-size:12px;"
        );

        profileInfo.getChildren().addAll(
                name,
                role
        );

        profileRow.getChildren().addAll(
                avatar,
                profileInfo
        );



        HBox pictureActions =
                new HBox(10);

        pictureActions.setAlignment(
                Pos.CENTER_LEFT
        );

        Button changePicture =
                new Button("Change picture");

        changePicture.getStyleClass().add(
                "primary-btn"
        );

        Button removePicture =
                new Button("Remove picture");

        removePicture.getStyleClass().add(
                "secondary-btn"
        );



        Owner owner =
                ownerController.getCurrentOwner();

        if (owner != null) {

            if (owner.getName() != null
                    && !owner.getName().isEmpty()) {

                name.setText(
                        owner.getName()
                );

                initials.setText(
                        getInitials(
                                owner.getName()
                        )
                );
            }

            if (owner.getProfileImageUrl() != null
                    && !owner.getProfileImageUrl()
                            .isEmpty()) {

                loadProfileImage(
                        owner.getProfileImageUrl(),
                        profileImage,
                        avatarCircle,
                        initials
                );
            }
        }



        changePicture.setOnAction(e -> {

            FileChooser fileChooser =
                    new FileChooser();

            fileChooser.setTitle(
                    "Select Profile Picture"
            );

            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(
                            "Image Files",
                            "*.png",
                            "*.jpg",
                            "*.jpeg",
                            "*.webp"
                    )
            );

            Window window =
                    changePicture.getScene() != null
                            ? changePicture.getScene().getWindow()
                            : null;

            File selectedFile =
                    fileChooser.showOpenDialog(
                            window
                    );

            if (selectedFile == null) {
                return;
            }

            System.out.println(
                    "Uploading profile picture..."
            );


            String imageUrl =
                    cloudinaryService.uploadImage(
                            selectedFile.toPath()
                    );


            if (imageUrl == null) {

                System.out.println(
                        "Profile picture upload failed."
                );

                return;
            }


            boolean saved =
                    ownerController.updateProfileImageUrl(
                            imageUrl
                    );


            if (!saved) {

                System.out.println(
                        "Image uploaded but Firestore update failed."
                );

                return;
            }


            loadProfileImage(
                    imageUrl,
                    profileImage,
                    avatarCircle,
                    initials
            );


            OwnerLayout.refreshTopProfile();


            System.out.println(
                    "Profile picture updated successfully."
            );
        });



        removePicture.setOnAction(e -> {

            boolean removed =
                    ownerController.updateProfileImageUrl(
                            ""
                    );


            if (!removed) {

                System.out.println(
                        "Failed to remove profile picture."
                );

                return;
            }


            profileImage.setImage(null);

            profileImage.setVisible(
                    false
            );

            avatarCircle.setVisible(
                    true
            );

            initials.setVisible(
                    true
            );


            OwnerLayout.refreshTopProfile();


            System.out.println(
                    "Profile picture removed successfully."
            );
        });


        pictureActions.getChildren().addAll(
                changePicture,
                removePicture
        );

        profileCard.getChildren().addAll(
                profileRow,
                pictureActions
        );



        VBox accountCard =
                new VBox(12);

        accountCard.getStyleClass().add(
                "card"
        );

        accountCard.setPadding(
                new Insets(18)
        );

        Label accountTitle =
                new Label(
                        "Account information"
                );

        accountTitle.getStyleClass().add(
                "section-title"
        );



        Label emailLabel =
                fieldLabel("Email");

        Label emailValue =
                new Label(
                        "Loading..."
                );

        emailValue.setStyle(
                "-fx-text-fill:#f8fafc;"
                        + "-fx-font-size:13px;"
        );

        Label emailNote =
                new Label(
                        "Managed by your account"
                );

        emailNote.setStyle(
                "-fx-text-fill:#64748b;"
                        + "-fx-font-size:10px;"
        );

        VBox emailBox =
                new VBox(4);

        emailBox.getChildren().addAll(
                emailValue,
                emailNote
        );



        Label roleLabel =
                fieldLabel("Role");

        Label roleValue =
                new Label(
                        "EV Owner"
                );

        roleValue.setStyle(
                "-fx-text-fill:#f8fafc;"
                        + "-fx-font-size:13px;"
        );

        Label roleNote =
                new Label(
                        "Assigned by the system"
                );

        roleNote.setStyle(
                "-fx-text-fill:#64748b;"
                        + "-fx-font-size:10px;"
        );

        VBox roleBox =
                new VBox(4);

        roleBox.getChildren().addAll(
                roleValue,
                roleNote
        );

        accountCard.getChildren().addAll(
                accountTitle,
                emailLabel,
                emailBox,
                roleLabel,
                roleBox
        );


        if (owner != null) {

            if (owner.getEmail() != null
                    && !owner.getEmail().isEmpty()) {

                emailValue.setText(
                        owner.getEmail()
                );
            }
        }



        VBox subscriptionCard =
                new VBox(12);

        subscriptionCard.getStyleClass().add(
                "card"
        );

        subscriptionCard.setPadding(
                new Insets(18)
        );

        Label subscriptionTitle =
                new Label("Subscription");

        subscriptionTitle.getStyleClass().add(
                "section-title"
        );

        HBox subscriptionRow =
                new HBox(14);

        subscriptionRow.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox subscriptionInfo =
                new VBox(4);

        HBox.setHgrow(
                subscriptionInfo,
                Priority.ALWAYS
        );

        Label planLabel =
                fieldLabel("Current plan");

        Label planValue =
                new Label(
                        owner != null
                                && owner.getSubscriptionPlanName() != null
                                && !owner.getSubscriptionPlanName().isEmpty()
                                ? owner.getSubscriptionPlanName()
                                : "No active subscription"
                );

        planValue.setStyle(
                "-fx-text-fill:#f8fafc;"
                        + "-fx-font-size:13px;"
                        + "-fx-font-weight:bold;"
        );

        subscriptionInfo.getChildren().addAll(
                planLabel,
                planValue
        );

        Button subscriptionButton =
                new Button("View subscription plans");

        subscriptionButton.getStyleClass().add(
                "primary-btn"
        );

        subscriptionButton.setOnAction(e ->
                OwnerDashboard.openSubscriptionPlans()
        );

        subscriptionRow.getChildren().addAll(
                subscriptionInfo,
                subscriptionButton
        );

        subscriptionCard.getChildren().addAll(
                subscriptionTitle,
                subscriptionRow
        );



        VBox contactCard =
                new VBox(12);

        contactCard.getStyleClass().add(
                "card"
        );

        contactCard.setPadding(
                new Insets(18)
        );

        Label contactTitle =
                new Label(
                        "Contact information"
                );

        contactTitle.getStyleClass().add(
                "section-title"
        );

        Label phoneLabel =
                fieldLabel(
                        "Phone number"
                );

        TextField phoneField =
                new TextField();

        phoneField.setPromptText(
                "Add phone number"
        );

        phoneField.getStyleClass().add(
                "search-field"
        );

        phoneField.setPrefWidth(350);


        if (owner != null
                && owner.getPhone() != null
                && !owner.getPhone().isEmpty()) {

            phoneField.setText(
                    owner.getPhone()
            );
        }


        contactCard.getChildren().addAll(
                contactTitle,
                phoneLabel,
                phoneField
        );



        HBox actions =
                new HBox(10);

        actions.setAlignment(
                Pos.CENTER_RIGHT
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Button saveButton =
                new Button(
                        "Save changes"
                );

        saveButton.getStyleClass().add(
                "primary-btn"
        );

        saveButton.setOnAction(e -> {

            String phone =
                    phoneField.getText().trim();

            boolean updated =
                    ownerController.updatePhone(
                            phone
                    );


            if (updated) {

                System.out.println(
                        "Profile changes saved successfully."
                );

            } else {

                System.out.println(
                        "Failed to save profile changes."
                );
            }
        });

        actions.getChildren().addAll(
                spacer,
                saveButton
        );



        content.getChildren().addAll(
                backButton,
                pageHeader,
                profileCard,
                accountCard,
                subscriptionCard,
                contactCard,
                actions
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



    private static void loadProfileImage(
            String imageUrl,
            ImageView profileImage,
            Circle avatarCircle,
            Label initials) {

        try {

            Image image =
                    new Image(
                            imageUrl
                    );

            profileImage.setImage(
                    image
            );

            profileImage.setFitWidth(
                    76
            );

            profileImage.setFitHeight(
                    76
            );

            profileImage.setPreserveRatio(
                    false
            );

            profileImage.setVisible(
                    true
            );

            avatarCircle.setVisible(
                    false
            );

            initials.setVisible(
                    false
            );

        } catch (Exception e) {

            e.printStackTrace();

            profileImage.setVisible(
                    false
            );

            avatarCircle.setVisible(
                    true
            );

            initials.setVisible(
                    true
            );
        }
    }



    private static String getInitials(
            String fullName) {

        if (fullName == null
                || fullName.trim().isEmpty()) {

            return "AM";
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
