package com.logix.view;

import java.time.Year;



import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ForgotPass  {
    private Scene forgotPassScene;
     public Scene getForgotPassScene(Runnable callbackAction1){

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
   
           Label forgotPassLabel=new Label("Forgot Password?");
           forgotPassLabel.setStyle("-fx-text-fill:#FFFFFF;"+
                            "-fx-font-size: 30px;"+
                            "-fx-font-weight: bold;"
           );
         
            forgotPassLabel.setTranslateX(70);
            forgotPassLabel.setTranslateY(20);




      


               Text forgotPassText=new Text("No worries! Enter your email and we'll\nsend you a link to reset your password.");
             forgotPassText.setFill(Color.WHITE);
             forgotPassText.setStyle("-fx-font-size:15px;");
             forgotPassText.setTranslateY(25);
             forgotPassText.setTranslateX(70);
              

               Text emailText=new Text("Email");
             emailText.setFill(Color.WHITE);
             emailText.setStyle("-fx-font-size:16px;");
             emailText.setTranslateX(77);
             emailText.setTranslateY(50);

             
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
             emailField.setTranslateX(75);
             emailField.setTranslateY(55);
             emailField.setPrefHeight(50);
             emailField.setMaxHeight(50);
             emailField.setPrefWidth(600);
             emailField.setMaxWidth(600);



             
             Button sendresetlinkButton=new Button("Send Reset Link");
             sendresetlinkButton.setPrefWidth(500);
             sendresetlinkButton.setPrefHeight(50);
             sendresetlinkButton.setStyle(
                "-fx-background-color:#3CCB7F;"+
                "-fx-text-fill:#FFFFFF;"+
                
                "fx-font-size:50px;"+
                "-fx-font-weight:bold;"+
                "-fx-font-family:'Arial';"+
                "-fx-background-radius:8px;"+
                "-fx-radius:8px;"+
                   "-fx-font-size:16px;"+                
                "-fx-border-color:transparent;"
           
             );
             sendresetlinkButton.setTranslateX(75);
             sendresetlinkButton.setTranslateY(115);
             sendresetlinkButton.setPrefWidth(600);
             sendresetlinkButton.setPrefHeight(50);

                Label orLabel=new Label("or");
             orLabel.setStyle(
                "-fx-text-fill:#B8BDc3;"+
                "-fx-font-size:16px;"
             );

             Separator line1=new Separator();
             line1.setPrefWidth(230);

             Separator line2=new Separator();
             line2.setPrefWidth(230);

             HBox orBox=new HBox(line1,orLabel,line2);
             orBox.setAlignment(Pos.CENTER);
             orBox.setSpacing(15);
             orBox.setTranslateX(-10);
             orBox.setTranslateY(150);
             orBox.setMaxWidth(Double.MAX_VALUE);

             Button backToLoginButton = new Button("Back to Login");

          backToLoginButton.setStyle(
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
backToLoginButton.setTranslateX(80);
backToLoginButton.setTranslateY(190);
backToLoginButton.setPrefWidth(600);
backToLoginButton.setPrefHeight(50);

  backToLoginButton.setOnAction(e->{
    callbackAction1.run();
  });

      Label troubleLabel=new Label("Still having trouble?");
      troubleLabel.setStyle(
        "-fx-text-fill:#A9B0B8;"+
        "-fx-font-size:16px;"
      );

      Button contactSupportButton=new Button("Contact Support");
      contactSupportButton.setStyle(
        "-fx-background-color:transparent;"+
        "-fx-text-fill:#35C878;"+
        "-fx-font-size:16px;"+
        "-fx-font-weight:bold;"+
        "-fx-cursor:hand;"
      );

      HBox hbsupportBox=new HBox(5,troubleLabel,contactSupportButton);
      hbsupportBox.setAlignment(Pos.CENTER);
      hbsupportBox.setTranslateX(0);
      hbsupportBox.setTranslateY(300);

         
Label copyright = new Label("© " + Year.now().getValue() + " ChargeOn. All rights reserved.");
copyright.setTextFill(Color.valueOf("#B8BDc2"));
copyright.setFont(Font.font("Arial",FontWeight.NORMAL,14));
copyright.setTranslateX(270);
copyright.setTranslateY(400);

            VBox rightSection=new VBox(forgotPassLabel,forgotPassText,emailText,emailField,sendresetlinkButton,orBox,backToLoginButton,hbsupportBox,copyright);
            rightSection.setStyle("-fx-background-color:#2A2F35;");

             HBox hb=new HBox(leftSection,rightSection);
            HBox.setHgrow(rightSection, Priority.ALWAYS);

                Scene scene=new Scene(hb,1200,700,Color.valueOf("#2A2F35"));
              forgotPassScene=scene;
              return forgotPassScene;





     }
}
