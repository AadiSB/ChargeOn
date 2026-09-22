package com.core2web.view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
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

public class LoginPage extends Application {
    public static Stage loginPageStage;
    private static Scene loginPageScene;

    @Override
    public void start(Stage primaryStage) throws Exception {
        loginPageStage = primaryStage;

        Image img = new Image(getClass().getResource("/assets/images/loginimg1.png").toExternalForm());
        ImageView imageView = new ImageView(img);
        imageView.setFitWidth(520);
        imageView.setFitHeight(760);
        imageView.setPreserveRatio(true);

        Image img2 = new Image(getClass().getResource("/assets/images/loginimg2.png").toExternalForm());
        Image img3 = new Image(getClass().getResource("/assets/images/loginimg3.png").toExternalForm());

        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(5), event -> imageView.setImage(img2)));
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(10), event -> imageView.setImage(img3)));
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(15), event -> imageView.setImage(img)));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        VBox leftSection = new VBox(imageView);
        leftSection.setStyle("-fx-background-color:#1F2429;");
        leftSection.setAlignment(Pos.CENTER);
        leftSection.setPrefWidth(500);
        leftSection.setPrefHeight(760);
        leftSection.setMinWidth(430);

        Label welcomeLabel = new Label("Welcome Back!");
        welcomeLabel.setStyle("-fx-text-fill:#FFFFFF; -fx-font-size: 36px; -fx-font-weight: 1000;");

        Text loginText = new Text("Login to continue to your account");
        loginText.setFill(Color.WHITE);
        loginText.setStyle("-fx-font-size:18px;");

        Text emailText = new Text("Email");
        emailText.setFill(Color.WHITE);
        emailText.setStyle("-fx-font-size:16px;");

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
                        "-fx-padding:0 15px;");
        emailField.setPrefHeight(50);
        emailField.setPrefWidth(500);
        emailField.setMaxWidth(500);

        Text passwordText = new Text("Password");
        passwordText.setFill(Color.WHITE);
        passwordText.setStyle("-fx-font-size:16px;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setStyle(
                "-fx-background-color:#20252B;" +
                        "-fx-text-fill:#FFFFFF;" +
                        "-fx-prompt-text-fill:#8B929A;" +
                        "-fx-font-size:16px;" +
                        "-fx-border-color:#3A4149;" +
                        "-fx-border-width:1px;" +
                        "-fx-border-radius:8px;" +
                        "-fx-background-radius:8px;" +
                        "-fx-padding:0 15px;");
        passwordField.setPrefHeight(50);
        passwordField.setPrefWidth(500);
        passwordField.setMaxWidth(500);

        Button forgotPass = new Button("Forgot Password?");
        forgotPass.setStyle(
                "-fx-background-color:transparent;" +
                        "-fx-text-fill:#3CCB7F;" +
                        "-fx-padding:0;" +
                        "-fx-font-size:16px;" +
                        "-fx-font-weight:normal;" +
                        "-fx-border-color:transparent;" +
                        "-fx-cursor:hand;");
        forgotPass.setAlignment(Pos.CENTER);
        forgotPass.setOnAction(e -> {
            ForgotPass obj = new ForgotPass();
            Runnable callbackAction1 = this::backToLoginPage;
            loginPageStage.setScene(obj.getForgotPassScene(callbackAction1));
        });

        HBox forgotPassRow = new HBox(forgotPass);
        forgotPassRow.setAlignment(Pos.CENTER_RIGHT);
        forgotPassRow.setPrefWidth(500);

        ToggleGroup tg = new ToggleGroup();

        String roleCardStyle =
                "-fx-background-color:transparent;" +
                        "-fx-border-color:#35C979;" +
                        "-fx-border-width:2;" +
                        "-fx-border-radius:10;" +
                        "-fx-background-radius:10;";
        String roleTitleStyle = "-fx-text-fill:white; -fx-font-size:14px; -fx-font-weight:bold;";

        RadioButton rb1 = new RadioButton();
        rb1.setToggleGroup(tg);
        rb1.setSelected(true);
        Image ownerImg = new Image(getClass().getResource("/assets/images/usersymbol.png").toExternalForm());
        ImageView ownerView = new ImageView(ownerImg);
        ownerView.setFitWidth(40);
        ownerView.setFitHeight(40);
        ownerView.setPreserveRatio(true);
        Label ownerTitle = new Label("Owner");
        ownerTitle.setStyle(roleTitleStyle);
        VBox ownerContent = new VBox(6, rb1, ownerView, ownerTitle);
        ownerContent.setAlignment(Pos.CENTER);
        VBox ownerCard = new VBox(ownerContent);
        ownerCard.setAlignment(Pos.CENTER);
        ownerCard.setStyle(roleCardStyle);
        ownerCard.setPrefSize(150, 110);

        RadioButton rb2 = new RadioButton();
        rb2.setToggleGroup(tg);
        Image driverImg = new Image(getClass().getResource("/assets/images/driversymbol.png").toExternalForm());
        ImageView driverView = new ImageView(driverImg);
        driverView.setFitWidth(40);
        driverView.setFitHeight(40);
        driverView.setPreserveRatio(true);
        Label driverTitle = new Label("Driver");
        driverTitle.setStyle(roleTitleStyle);
        VBox driverContent = new VBox(6, rb2, driverView, driverTitle);
        driverContent.setAlignment(Pos.CENTER);
        VBox driverCard = new VBox(driverContent);
        driverCard.setAlignment(Pos.CENTER);
        driverCard.setStyle(roleCardStyle);
        driverCard.setPrefSize(150, 110);

        RadioButton rb3 = new RadioButton();
        rb3.setToggleGroup(tg);
        Image adminImg = new Image(getClass().getResource("/assets/images/adminsymbol.png").toExternalForm());
        ImageView adminView = new ImageView(adminImg);
        adminView.setFitWidth(40);
        adminView.setFitHeight(40);
        adminView.setPreserveRatio(true);
        Label adminTitle = new Label("Admin");
        adminTitle.setStyle(roleTitleStyle);
        VBox adminContent = new VBox(6, rb3, adminView, adminTitle);
        adminContent.setAlignment(Pos.CENTER);
        VBox adminCard = new VBox(adminContent);
        adminCard.setAlignment(Pos.CENTER);
        adminCard.setStyle(roleCardStyle);
        adminCard.setPrefSize(150, 110);

        HBox radio = new HBox(15, ownerCard, driverCard, adminCard);
        radio.setAlignment(Pos.CENTER);
        radio.setPadding(new Insets(10));

        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill:#FF6B6B; -fx-font-size:13px;");

        Button loginButton = new Button("Login");
        loginButton.setPrefWidth(500);
        loginButton.setPrefHeight(50);
        loginButton.setStyle(
                "-fx-background-color:#3CCB7F;" +
                        "-fx-text-fill:#FFFFFF;" +
                        "-fx-font-size:16px;" +
                        "-fx-font-weight:bold;" +
                        "-fx-font-family:'Arial';" +
                        "-fx-background-radius:8px;" +
                        "-fx-radius:8px;" +
                        "-fx-border-color:transparent;");

        AuthController controller = new AuthController();

        loginButton.setOnAction(e -> {
            String email = emailField.getText().trim();
            String password = passwordField.getText();

            if (email.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Please enter your email and password.");
                return;
            }

            RadioButton selected = (RadioButton) tg.getSelectedToggle();
            if (selected == null) {
                selected = rb1;
            }

            String role = (selected == rb2) ? "driver"
                        : (selected == rb3) ? "admin"
                        : "owner";

            errorLabel.setText("");

            AuthSession session = controller.signIn(email, password, role);
            if (session == null) {
                errorLabel.setText("Invalid credentials, or this account isn't a " + role + ".");
                return;
            }

            if (session.getRole().equals("owner")) {
                loginPageStage.setTitle("CHARGEON · EV OWNER PORTAL");
                OwnerDashboard.show(primaryStage);
            } else if (session.getRole().equals("driver")) {
                loginPageStage.setTitle("ChargeOn · Driver Portal");
                DriverDashboard.show(loginPageStage);
            } else if (session.getRole().equals("admin")) {
                AdminDashboard.show(loginPageStage);
            }
        });

        Label orLabel = new Label("or");
        orLabel.setStyle("-fx-text-fill:#B8BDC3; -fx-font-size:16px;");

        Separator line1 = new Separator();
        line1.setPrefWidth(220);

        Separator line2 = new Separator();
        line2.setPrefWidth(220);

        HBox orBox = new HBox(15, line1, orLabel, line2);
        orBox.setAlignment(Pos.CENTER);
        orBox.setPrefWidth(500);
        orBox.setPadding(new Insets(0, 35, 0, 35));

        Button createAccountButton = new Button("Create New Account");
        createAccountButton.setPrefWidth(500);
        createAccountButton.setPrefHeight(50);
        createAccountButton.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-border-color: #35B878;" +
                        "-fx-border-width: 1.5px;" +
                        "-fx-border-radius: 8px;" +
                        "-fx-background-radius: 8px;" +
                        "-fx-text-fill: #FFFFFF;" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-weight: normal;" +
                        "-fx-cursor: hand;");
        createAccountButton.setOnAction(event -> {
            CreateAccount obj = new CreateAccount();
            Runnable callbackAction = this::backToLoginPage;
            loginPageStage.setScene(obj.getCreateAccountPageScene(callbackAction));
        });

        Label copyright = new Label("© " + Year.now().getValue() + " ChargeOn. All rights reserved.");
        copyright.setTextFill(Color.web("#B8BDc2"));
        copyright.setFont(Font.font("Arial", FontWeight.NORMAL, 14));

        VBox formBox = new VBox(16,
                welcomeLabel,
                loginText,
                emailText,
                emailField,
                passwordText,
                passwordField,
                forgotPassRow,
                radio,
                errorLabel,
                loginButton,
                orBox,
                createAccountButton,
                copyright);
        formBox.setAlignment(Pos.CENTER);
        formBox.setPadding(new Insets(80, 80, 40, 80));
        formBox.setSpacing(10);
        formBox.setPrefWidth(700);
        formBox.setFillWidth(true);

        VBox rightSection = new VBox(formBox);
        rightSection.setStyle("-fx-background-color:#2A2F35;");
        rightSection.setPrefWidth(700);
        rightSection.setPrefHeight(760);

        HBox hb = new HBox(leftSection, rightSection);
        hb.setSpacing(0);
        hb.setFillHeight(true);

        HBox.setHgrow(leftSection, Priority.ALWAYS);
        HBox.setHgrow(rightSection, Priority.ALWAYS);

        double sceneWidth = AppWindowSize.width();
        double sceneHeight = AppWindowSize.height();

        Scene scene = new Scene(hb, sceneWidth, sceneHeight, Color.valueOf("#2A2F35"));
        loginPageScene = scene;
        loginPageStage.setScene(loginPageScene);

        loginPageStage.show();
    }

    public static void showLoginPage(Stage stage) {
    if (stage == null) {
        return;
    }
    stage.setMaximized(false);

    if (loginPageScene != null) {
        stage.setScene(loginPageScene);
        stage.setTitle("ChargeOn");

        return;
    }

    try {
        new LoginPage().start(stage);

    } catch (Exception e) {
        e.printStackTrace();
    }
}

    public void backToLoginPage() {
    if (loginPageStage != null) {
        loginPageStage.setMaximized(false);
        loginPageStage.setScene(loginPageScene);
    }
}
}
