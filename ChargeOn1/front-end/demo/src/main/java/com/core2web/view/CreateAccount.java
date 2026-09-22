
package com.core2web.view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.Year;

import com.core2web.controller.AuthController;
import com.core2web.model.AuthSession;

public class CreateAccount {

    private Scene createAccountPageScene;

    public Scene getCreateAccountPageScene(Runnable callbackAction) {


        Image img = new Image(
                getClass().getResource("/assets/images/loginimg1.png").toExternalForm()
        );

        Image img2 = new Image(
                getClass().getResource("/assets/images/loginimg2.png").toExternalForm()
        );

        Image img3 = new Image(
                getClass().getResource("/assets/images/loginimg3.png").toExternalForm()
        );

        ImageView imageView = new ImageView(img);

        imageView.setFitWidth(520);
        imageView.setPreserveRatio(true);

        Timeline timeline = new Timeline();

        timeline.getKeyFrames().add(
                new KeyFrame(
                        Duration.seconds(5),
                        event -> imageView.setImage(img2)
                )
        );

        timeline.getKeyFrames().add(
                new KeyFrame(
                        Duration.seconds(10),
                        event -> imageView.setImage(img3)
                )
        );

        timeline.getKeyFrames().add(
                new KeyFrame(
                        Duration.seconds(15),
                        event -> imageView.setImage(img)
                )
        );

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        VBox leftSection = new VBox(imageView);

        leftSection.setAlignment(Pos.CENTER);
        leftSection.setFillWidth(true);

        leftSection.setStyle(
                "-fx-background-color:#1F2429;"
        );

        leftSection.setMinWidth(430);


        Label createAccLabel = new Label("Create Your Account");

        createAccLabel.setStyle(
                "-fx-text-fill:#FFFFFF;" +
                "-fx-font-size:30px;" +
                "-fx-font-weight:bold;"
        );

        Text joinText = new Text(
                "Join ChargeOn and power the future"
        );

        joinText.setFill(Color.WHITE);

        joinText.setStyle(
                "-fx-font-size:15px;"
        );


        Text nameText = new Text("Full Name");

        nameText.setFill(Color.WHITE);

        nameText.setStyle(
                "-fx-font-size:16px;"
        );

        TextField nameField = new TextField();

        nameField.setPromptText("Enter your full name");

        nameField.setStyle(
                "-fx-background-color:#20252B;" +
                "-fx-text-fill:#FFFFFF;" +
                "-fx-prompt-text-fill:#8B929A;" +
                "-fx-font-size:16px;" +
                "-fx-border-color:#3A4149;" +
                "-fx-border-width:1px;" +
                "-fx-border-radius:8px;" +
                "-fx-background-radius:8px;" +
                "-fx-padding:0 15px;"
        );

        nameField.setPrefHeight(50);
        nameField.setMaxWidth(Double.MAX_VALUE);

        VBox nameBox = new VBox(
                6,
                nameText,
                nameField
        );


        Text emailText = new Text("Email");

        emailText.setFill(Color.WHITE);

        emailText.setStyle(
                "-fx-font-size:16px;"
        );

        TextField emailField = new TextField();

        emailField.setPromptText("Enter your email");

        emailField.setStyle(
                "-fx-background-color:#20252B;" +
                "-fx-text-fill:#FFFFFF;" +
                "-fx-prompt-text-fill:#8B929A;" +
                "-fx-font-size:16px;" +
                "-fx-border-color:#3A4149;" +
                "-fx-border-width:1px;" +
                "-fx-border-radius:8px;" +
                "-fx-background-radius:8px;" +
                "-fx-padding:0 15px;"
        );

        emailField.setPrefHeight(50);
        emailField.setMaxWidth(Double.MAX_VALUE);

        VBox emailBox = new VBox(
                6,
                emailText,
                emailField
        );


        Text passText = new Text("Password");

        passText.setFill(Color.WHITE);

        passText.setStyle(
                "-fx-font-size:16px;"
        );

        PasswordField passField = new PasswordField();

        passField.setPromptText("Create a password");

        passField.setStyle(
                "-fx-background-color:#20252B;" +
                "-fx-text-fill:#FFFFFF;" +
                "-fx-prompt-text-fill:#8B929A;" +
                "-fx-font-size:16px;" +
                "-fx-border-color:#3A4149;" +
                "-fx-border-width:1px;" +
                "-fx-border-radius:8px;" +
                "-fx-background-radius:8px;" +
                "-fx-padding:0 15px;"
        );

        passField.setPrefHeight(50);
        passField.setMaxWidth(Double.MAX_VALUE);

        VBox passBox = new VBox(
                6,
                passText,
                passField
        );


        Text confpassText = new Text("Confirm Password");

        confpassText.setFill(Color.WHITE);

        confpassText.setStyle(
                "-fx-font-size:16px;"
        );

        PasswordField confPassField = new PasswordField();

        confPassField.setPromptText(
                "Confirm your password"
        );

        confPassField.setStyle(
                "-fx-background-color:#20252B;" +
                "-fx-text-fill:#FFFFFF;" +
                "-fx-prompt-text-fill:#8B929A;" +
                "-fx-font-size:16px;" +
                "-fx-border-color:#3A4149;" +
                "-fx-border-width:1px;" +
                "-fx-border-radius:8px;" +
                "-fx-background-radius:8px;" +
                "-fx-padding:0 15px;"
        );

        confPassField.setPrefHeight(50);
        confPassField.setMaxWidth(Double.MAX_VALUE);

        VBox confirmBox = new VBox(
                6,
                confpassText,
                confPassField
        );


        HBox passwordRow = new HBox(
                15,
                passBox,
                confirmBox
        );

        passwordRow.setAlignment(Pos.CENTER_LEFT);
        passwordRow.setFillHeight(true);

        HBox.setHgrow(passBox, Priority.ALWAYS);
        HBox.setHgrow(confirmBox, Priority.ALWAYS);


        Label errorLabel = new Label("");

        errorLabel.setStyle(
                "-fx-text-fill:#FF6B6B;" +
                "-fx-font-size:13px;"
        );


        Button createAccButton = new Button(
                "Create Account"
        );

        createAccButton.setPrefHeight(50);

        createAccButton.setMaxWidth(
                Double.MAX_VALUE
        );

        createAccButton.setStyle(
                "-fx-background-color:#3CCB7F;" +
                "-fx-text-fill:#FFFFFF;" +
                "-fx-font-size:16px;" +
                "-fx-font-weight:bold;" +
                "-fx-font-family:'Arial';" +
                "-fx-background-radius:8px;" +
                "-fx-radius:8px;" +
                "-fx-border-color:transparent;"
        );


        Label orLabel = new Label("or");

        orLabel.setStyle(
                "-fx-text-fill:#B8BDc3;" +
                "-fx-font-size:16px;"
        );

        Separator line1 = new Separator();
        Separator line2 = new Separator();

        HBox.setHgrow(line1, Priority.ALWAYS);
        HBox.setHgrow(line2, Priority.ALWAYS);

        HBox orBox = new HBox(
                15,
                line1,
                orLabel,
                line2
        );

        orBox.setAlignment(Pos.CENTER);
        orBox.setFillHeight(true);


        Button alreadyAccButton = new Button(
                "Already have an Account? Login"
        );

        alreadyAccButton.setPrefHeight(50);

        alreadyAccButton.setMaxWidth(
                Double.MAX_VALUE
        );

        alreadyAccButton.setStyle(
                "-fx-background-color:transparent;" +
                "-fx-border-color:#35B878;" +
                "-fx-border-width:1.5px;" +
                "-fx-border-radius:8px;" +
                "-fx-background-radius:8px;" +
                "-fx-text-fill:#FFFFFF;" +
                "-fx-font-size:18px;" +
                "-fx-font-weight:normal;" +
                "-fx-cursor:hand;"
        );

        alreadyAccButton.setOnAction(
                e -> callbackAction.run()
        );


        Label copyright = new Label(
                "© " + Year.now().getValue()
                        + " ChargeOn. All rights reserved."
        );

        copyright.setTextFill(
                Color.web("#B8BDc2")
        );

        copyright.setFont(
                Font.font(
                        "Arial",
                        FontWeight.NORMAL,
                        14
                )
        );


        VBox formBox = new VBox(
                14,
                createAccLabel,
                joinText,
                nameBox,
                emailBox,
                passwordRow,
                errorLabel,
                createAccButton,
                orBox,
                alreadyAccButton,
                copyright
        );

        formBox.setAlignment(
                Pos.CENTER_LEFT
        );

        formBox.setPadding(
                new Insets(30, 70, 30, 70)
        );

        formBox.setFillWidth(true);

        VBox.setVgrow(formBox, Priority.ALWAYS);


        VBox rightSection = new VBox(
                formBox
        );

        rightSection.setStyle(
                "-fx-background-color:#2A2F35;"
        );

        rightSection.setAlignment(
                Pos.CENTER
        );

        rightSection.setFillWidth(true);

        rightSection.setMinWidth(500);

        VBox.setVgrow(formBox, Priority.ALWAYS);


        HBox hb = new HBox(
                leftSection,
                rightSection
        );

        hb.setSpacing(0);
        hb.setFillHeight(true);

        HBox.setHgrow(
                leftSection,
                Priority.SOMETIMES
        );

        HBox.setHgrow(
                rightSection,
                Priority.ALWAYS
        );


        leftSection.prefWidthProperty().bind(
                hb.widthProperty().multiply(0.40)
        );

        rightSection.prefWidthProperty().bind(
                hb.widthProperty().multiply(0.60)
        );


        AuthController controller =
                new AuthController();

        createAccButton.setOnAction(e -> {

            String name =
                    nameField.getText().trim();

            String email =
                    emailField.getText().trim();

            String password =
                    passField.getText();

            String confirmPassword =
                    confPassField.getText();

            if (name.isEmpty()
                    || email.isEmpty()
                    || password.isEmpty()) {

                errorLabel.setStyle(
                        "-fx-text-fill:#FF6B6B;" +
                        "-fx-font-size:13px;"
                );

                errorLabel.setText(
                        "Please fill in your name, email and password."
                );

                return;
            }

            if(!email.contains("@")&&!email.contains(".")){

                errorLabel.setStyle(
                        "-fx-text-fill:#FF6B6B;" +
                        "-fx-font-size:13px;"
                );

                errorLabel.setText(
                        "Please enter valid email address."
                );

                return;
            }

            if (!password.equals(confirmPassword)) {

                errorLabel.setStyle(
                        "-fx-text-fill:#FF6B6B;" +
                        "-fx-font-size:13px;"
                );

                errorLabel.setText(
                        "Passwords do not match."
                );

                return;
            }

            errorLabel.setText("");

            AuthSession session =
                    controller.signUp(
                            email,
                            password,
                            "owner",
                            name
                    );

            if (session != null) {

                errorLabel.setStyle(
                        "-fx-text-fill:#3CCB7F;" +
                        "-fx-font-size:13px;"
                );

                errorLabel.setText(
                        "Account created successfully."
                );

                Stage stage =
                        (Stage) createAccButton
                                .getScene()
                                .getWindow();

                stage.setTitle(
                        "CHARGEON · EV OWNER PORTAL"
                );

                OwnerDashboard.show(stage);

            } else {

                errorLabel.setStyle(
                        "-fx-text-fill:#FF6B6B;" +
                        "-fx-font-size:13px;"
                );

                errorLabel.setText(
                        "Could not create account. Please try again."
                );
            }
        });


        Scene scene = new Scene(
                hb,
                AppWindowSize.width(),
                AppWindowSize.height(),
                Color.valueOf("#2A2F35")
        );

        createAccountPageScene = scene;

        return createAccountPageScene;
    }
}

