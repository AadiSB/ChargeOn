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

import com.core2web.controller.AdminController;
import com.core2web.model.Admin;
import com.core2web.service.CloudinaryService;

public class AdminProfile {

    private static final AdminController adminController = new AdminController();

    private static final CloudinaryService cloudinaryService =
            new CloudinaryService();

    public static ScrollPane buildMainContent() {

        VBox content = new VBox(16);
        content.setPadding(new Insets(16));


        Button backButton =
                new Button("←  Back to Dashboard");

        backButton.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #94a3b8;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 6 0 6 0;" +
                "-fx-cursor: hand;"
        );

        backButton.setOnAction(e ->
                AdminDashboard.goTo("Dashboard")
        );


        VBox pageHeader = new VBox(4);

        Label title =
                new Label("Profile");

        title.getStyleClass().add(
                "section-title"
        );

        Label subtitle =
                new Label(
                        "Manage your administrator profile and contact information"
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
                        Color.web("#2dd4bf")
                );

        Label initials =
                new Label("A");

        initials.setStyle(
                "-fx-text-fill:#0f1720;" +
                "-fx-font-size:20px;" +
                "-fx-font-weight:bold;"
        );

        avatar.getChildren().addAll(
                avatarCircle,
                initials
        );

        ImageView profileImage =
                new ImageView();

        profileImage.setFitWidth(76);
        profileImage.setFitHeight(76);
        profileImage.setPreserveRatio(false);

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
                "-fx-text-fill:#f8fafc;" +
                "-fx-font-size:18px;" +
                "-fx-font-weight:bold;"
        );

        Label role =
                new Label("Administrator");

        role.setStyle(
                "-fx-text-fill:#94a3b8;" +
                "-fx-font-size:12px;"
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


        Admin admin =
                adminController.getCurrentAdmin();

        if (admin != null) {

            if (admin.getName() != null
                    && !admin.getName().trim().isEmpty()) {

                name.setText(
                        admin.getName()
                );

                initials.setText(
                        getInitials(
                                admin.getName()
                        )
                );
            }

            if (admin.getProfileImageUrl() != null
                    && !admin.getProfileImageUrl()
                    .trim()
                    .isEmpty()) {

                loadProfileImage(
                        admin.getProfileImageUrl(),
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
                    adminController.updateProfileImageUrl(
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

            AdminLayout.refreshAdminProfile();

            System.out.println(
                    "Profile picture updated successfully."
            );
        });


        removePicture.setOnAction(e -> {

            boolean removed =
                    adminController.updateProfileImageUrl(
                            ""
                    );

            if (!removed) {

                System.out.println(
                        "Failed to remove profile picture."
                );

                return;
            }

            profileImage.setImage(null);
            profileImage.setVisible(false);

            avatarCircle.setVisible(true);
            initials.setVisible(true);

            AdminLayout.refreshAdminProfile();

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
                new Label("Loading...");

        emailValue.setStyle(
                "-fx-text-fill:#f8fafc;" +
                "-fx-font-size:13px;"
        );

        Label emailNote =
                new Label(
                        "Managed by your account"
                );

        emailNote.setStyle(
                "-fx-text-fill:#64748b;" +
                "-fx-font-size:10px;"
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
                new Label("Administrator");

        roleValue.setStyle(
                "-fx-text-fill:#f8fafc;" +
                "-fx-font-size:13px;"
        );

        Label roleNote =
                new Label(
                        "Assigned by the system"
                );

        roleNote.setStyle(
                "-fx-text-fill:#64748b;" +
                "-fx-font-size:10px;"
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


        if (admin != null) {

            if (admin.getEmail() != null
                    && !admin.getEmail().trim().isEmpty()) {

                emailValue.setText(
                        admin.getEmail()
                );
            }

            if (admin.getRole() != null
                    && !admin.getRole().trim().isEmpty()) {

                roleValue.setText(
                        admin.getRole()
                );

                role.setText(
                        admin.getRole()
                );
            }
        }


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

        phoneField.setPrefWidth(
                350
        );

        if (admin != null
                && admin.getPhone() != null
                && !admin.getPhone()
                .trim()
                .isEmpty()) {

            phoneField.setText(
                    admin.getPhone()
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
                    phoneField
                            .getText()
                            .trim();

            boolean updated =
                    adminController.updatePhone(
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
                contactCard,
                actions
        );

        ScrollPane scrollPane =
                new ScrollPane(content);

        scrollPane.setFitToWidth(true);

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
                            imageUrl,
                            76,
                            76,
                            false,
                            true,
                            true
                    );

            if (image.isError()) {

                profileImage.setVisible(false);
                avatarCircle.setVisible(true);
                initials.setVisible(true);

                return;
            }

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

            profileImage.setVisible(false);

            avatarCircle.setVisible(true);

            initials.setVisible(true);
        }
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


    private static Label fieldLabel(
            String text) {

        Label label =
                new Label(text);

        label.setStyle(
                "-fx-text-fill:#cbd5e1;" +
                "-fx-font-size:11px;" +
                "-fx-font-weight:bold;"
        );

        return label;
    }
}
