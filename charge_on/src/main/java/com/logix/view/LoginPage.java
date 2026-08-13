package com.logix.view;



import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.Year;
import javafx.animation.*;
public class LoginPage extends Application {
    public static Stage loginPageStage;
    private Scene loginPageScene;
    
    public void start(Stage primaryStage)throws Exception{
        
            loginPageStage=primaryStage;
          Image img=new Image(getClass().getResource("/assets/images/loginimg1.png").toExternalForm());
          ImageView imageView=new ImageView(img);
       
           imageView.setFitHeight(800);
           imageView.setFitWidth(760);

            Image img2=new Image(getClass().getResource("/assets/images/loginimg2.png").toExternalForm());
         
            Image img3=new Image(getClass().getResource("/assets/images/loginimg3.png").toExternalForm());

            Timeline timeline = new Timeline();

timeline.getKeyFrames().add(
    new KeyFrame(Duration.seconds(5), event -> {
        imageView.setImage(img2);
    })
);

timeline.getKeyFrames().add(
    new KeyFrame(Duration.seconds(10), event -> {
        imageView.setImage(img3);
    })
);

timeline.getKeyFrames().add(
    new KeyFrame(Duration.seconds(15), event -> {
        imageView.setImage(img);
    })
);

timeline.setCycleCount(Timeline.INDEFINITE);
timeline.play();




         
            VBox leftSection=new VBox(imageView);

           Label welcomeLabel=new Label("Welcome Back!");
           welcomeLabel.setStyle("-fx-text-fill:#FFFFFF;"+
                            "-fx-font-size: 36px;"+
                            "-fx-font-weight: 1000;"
           );

            welcomeLabel.setTranslateX(150);
            welcomeLabel.setTranslateY(50);

            Text loginText=new Text("Login to continue to your account");
             loginText.setFill(Color.WHITE);
             loginText.setStyle("-fx-font-size:18px;");
             loginText.setTranslateY(55);
             loginText.setTranslateX(150);

             Text emailText=new Text("Email");
             emailText.setFill(Color.WHITE);
             emailText.setStyle("-fx-font-size:16px;");
             emailText.setTranslateX(150);
             emailText.setTranslateY(105);

             TextField emailField=new TextField();
             emailField.setPromptText("Enter your email");
             emailField.setStyle(
                "-fx-background-color:#20252B;"+
                "-fx-text-fill:#FFFFFF;"+
                "-fx-prompt-text-fill:#8B929A;"+
                "-fx-font-size:16px;"+
                "-fx-border-color:#3A4149;"+
                "-fx-border-width:1px;"+
                "-fx-border-radius:8px;"+
                "fx-background-border-radius:8px;"+
                "-fx-padding:0 15px;"
             );
             emailField.setTranslateX(150);
             emailField.setTranslateY(115);
             emailField.setPrefHeight(50);
             emailField.setMaxHeight(50);
             emailField.setPrefWidth(500);
             emailField.setMaxWidth(500);

            Text passwordText=new Text("Password");
             passwordText.setFill(Color.WHITE);
             passwordText.setStyle("-fx-font-size:16px;");
             passwordText.setTranslateX(150);
             passwordText.setTranslateY(140);

              PasswordField passwordField=new PasswordField();
             passwordField.setPromptText("Enter your password");
             passwordField.setStyle(
                "-fx-background-color:#20252B;"+
                "-fx-text-fill:#FFFFFF;"+
                "-fx-prompt-text-fill:#8B929A;"+
                "-fx-font-size:16px;"+
                "-fx-border-color:#3A4149;"+
                "-fx-border-width:1px;"+
                "-fx-border-radius:8px;"+
                "-fx-background-border-radius:8px;"+
                "-fx-padding:0 15px;"
             );
             passwordField.setTranslateX(150);
             passwordField.setTranslateY(150);
             passwordField.setPrefHeight(50);
             passwordField.setMaxHeight(50);
             passwordField.setPrefWidth(500);
             passwordField.setMaxWidth(500);

             

             Button forgotPass=new Button("Forgot Password?");
             forgotPass.setStyle(
                "-fx-background-color:transparent;"+
                "-fx-text-fill:#3CCB7F;"+
                "-fx-padding:0;"  +              
                "-fx-font-size:16px;"+
                "-fx-font-weight:normal;"+
                "-fx-border-color:transparent;"+
                "-fx-cursor:hand;"
             );
             forgotPass.setTranslateX(525);
             forgotPass.setTranslateY(160);

             forgotPass.setOnAction(e->{
                ForgotPass obj=new ForgotPass();
                Runnable callbackAction1=new Runnable() {
                    public void run(){
                    backToLoginPage();
                    }
                };
                loginPageStage.setScene(obj.getForgotPassScene(callbackAction1));
             });

             

             Button loginButton=new Button("Login");
             loginButton.setPrefWidth(500);
             loginButton.setPrefHeight(50);
             loginButton.setStyle(
                "-fx-background-color:#3CCB7F;"+
                "-fx-text-fill:#FFFFFF;"+
                
                "-fx-font-size:30px;"+
                "-fx-font-weight:bold;"+
                "-fx-font-family:'Arial';"+
                "-fx-background-radius:8px;"+
                "-fx-radius:8px;"+
                "-fx-border-color:transparent;"
           
             );
             loginButton.setTranslateX(150);
             loginButton.setTranslateY(210);

             Label orLabel=new Label("or");
             orLabel.setStyle(
                "-fx-text-fill:#B8BDC3;"+
                "-fx-font-size:16px;"
             );
             Separator line1=new Separator();
             line1.setPrefWidth(225);
           

             Separator line2=new Separator();
             line2.setPrefWidth(225);

             HBox orBox=new HBox(line1,orLabel,line2);
             orBox.setAlignment(Pos.CENTER);
             orBox.setTranslateX(10);
             orBox.setSpacing(15);
             orBox.setTranslateY(225);    
             orBox.setMaxWidth(Double.MAX_VALUE);        
              
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
    "-fx-cursor: hand;"
);
createAccountButton.setTranslateX(150);
createAccountButton.setTranslateY(240);

 createAccountButton.setOnAction(event->{
    CreateAccount obj=new CreateAccount();
    Runnable callbackAction=new Runnable(){
        public void run(){
          backToLoginPage();
        }
    };
    loginPageStage.setScene(obj.getCreateAccountPageScene(callbackAction));
 });

 


Label copyright = new Label("© " + Year.now().getValue() + " ChargeOn. All rights reserved.");
copyright.setTextFill(Color.web("#B8BDc2"));
copyright.setFont(Font.font("Arial",FontWeight.NORMAL,14));
copyright.setTranslateX(270);
copyright.setTranslateY(350);

           

      VBox rightSection=new VBox(welcomeLabel,loginText,emailText,emailField,passwordText,passwordField,forgotPass,loginButton,orBox,createAccountButton,copyright);
     rightSection.setStyle("-fx-background-color:#2A2F35;");
          

     HBox hb=new HBox(leftSection,rightSection);
         hb.setSpacing(0);  
         hb.setFillHeight(true);

     leftSection.setPrefWidth(600);
     leftSection.setPrefHeight(600);
     rightSection.setPrefHeight(900);
     rightSection.setPrefWidth(800);

      HBox.setHgrow(leftSection, Priority.ALWAYS);
      HBox.setHgrow(rightSection, Priority.ALWAYS); 
  
    Scene scene=new Scene(hb,1200,700,Color.valueOf("#2A2F35"));
    loginPageScene=scene;
    loginPageStage.setScene(loginPageScene);
    loginPageStage.setWidth(scene.getWidth());
loginPageStage.setHeight(scene.getHeight());
      loginPageStage.show();

    }
    public void backToLoginPage(){
        loginPageStage.setScene(loginPageScene);
    }


}