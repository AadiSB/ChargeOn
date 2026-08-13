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
import javafx.scene.control.*;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.nio.channels.AlreadyBoundException;
import java.time.Year;
import javafx.animation.*;

public class CreateAccount  {
    private Scene createAccountPageScene;
    public Scene getCreateAccountPageScene(Runnable callbackAction){

  
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


           Label createAccLabel=new Label("Create Your Account");
           createAccLabel.setStyle("-fx-text-fill:#FFFFFF;"+
                            "-fx-font-size: 30px;"+
                            "-fx-font-weight: bold;"
           );
         
            createAccLabel.setTranslateX(70);
            createAccLabel.setTranslateY(20);
      
               Text joinText=new Text("Join ChargeOn and power the future");
             joinText.setFill(Color.WHITE);
             joinText.setStyle("-fx-font-size:15px;");
             joinText.setTranslateY(15);
             joinText.setTranslateX(70);

             Text nameText=new Text("Full Name");
             nameText.setFill(Color.WHITE);
             nameText.setStyle("-fx-font-size:16px;");
             nameText.setTranslateX(75);
             nameText.setTranslateY(35);

                  TextField nameField=new TextField();
             nameField.setPromptText("Enter your full name");
             nameField.setStyle(
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
             nameField.setTranslateX(75);
             nameField.setTranslateY(40);
             nameField.setPrefHeight(50);
             nameField.setMaxHeight(50);
             nameField.setPrefWidth(600);
             nameField.setMaxWidth(600);
            
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
        
              Text passText=new Text("Password");
             passText.setFill(Color.WHITE);
             passText.setStyle("-fx-font-size:16px;");
             passText.setTranslateX(75);
             passText.setTranslateY(70);


             
                  PasswordField passField=new PasswordField();
             passField.setPromptText("Create a password");
            passField.setStyle(
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
            passField.setTranslateX(75);
            passField.setTranslateY(75);
            passField.setPrefHeight(50);
            passField.setMaxHeight(50);
            passField.setPrefWidth(250);
            passField.setMaxWidth(250);

            
              Text confpassText=new Text("Confirm Password");
             confpassText.setFill(Color.WHITE);
             confpassText.setStyle("-fx-font-size:16px;");
             confpassText.setTranslateX(355);
             confpassText.setTranslateY(70);


             
                  PasswordField confPassField=new PasswordField();
             confPassField.setPromptText("Confirm your password");
            confPassField.setStyle(
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
            confPassField.setTranslateX(170);
            confPassField.setTranslateY(75);
            confPassField.setPrefHeight(50);
            confPassField.setMaxHeight(50);
            confPassField.setPrefWidth(250);
            confPassField.setMaxWidth(250);
        
            HBox hbpass1=new HBox(passText,confpassText);
            HBox hbpass2=new HBox(passField,confPassField);
           
           hbpass1.setPickOnBounds(false);
           hbpass2.setPickOnBounds(false);
             
           
              Text roleText=new Text("Select Your Role");
             roleText.setFill(Color.WHITE);
             roleText.setStyle("-fx-font-size:16px;");
             roleText.setTranslateX(75);
             roleText.setTranslateY(90);

            

            ToggleGroup roleGroup=new ToggleGroup();
            
            // EV owner
            RadioButton evOwnerRadio=new RadioButton();
            evOwnerRadio.setToggleGroup(roleGroup);
            Image evImage=new Image(getClass().getResource("/assets/images/usersymbol.png").toExternalForm());

            ImageView evView=new ImageView(evImage);
            evView.setFitWidth(55);
            evView.setFitHeight(55);
            evView.setPreserveRatio(true);

            Label evTitle=new Label("EV Owner");
            evTitle.setStyle(
                "-fx-text-fill:white;"+
                "-fx-font-size:18px;"+
                "-fx-font-weight:bold;"
            );
            Label evDescription=new Label("I own an EV and\n need charging");
            evDescription.setStyle(
                "-fx-text-fill:#B8BEC5;"+
                "-fx-font-size:14px;"+
                "-fx-alignment:center;"
            );
            VBox evContent=new VBox(8,evOwnerRadio,evView,evTitle,evDescription);
            evContent.setAlignment(Pos.CENTER);

            VBox evCard=new VBox(evContent);
            evCard.setAlignment(Pos.CENTER);
            evCard.setStyle(
                "-fx-background-color:transparent;"+
                "-fx-border-color:#35C979;"+
                "-fx-border-width:2;"+
                "-fx-border-radius:10;"+
                "-fx-background-radius:10;"
            );
            evCard.setPrefSize(185, 160);

             //Driver
                RadioButton driverRadio=new RadioButton();
              driverRadio.setToggleGroup(roleGroup);
            Image busImage=new Image(getClass().getResource("/assets/images/driversymbol.png").toExternalForm());

            ImageView busView=new ImageView(busImage);
            busView.setFitWidth(55);
            busView.setFitHeight(55);
            busView.setPreserveRatio(true);

            Label driverTitle=new Label("Driver");
            driverTitle.setStyle(
                "-fx-text-fill:white;"+
                "-fx-font-size:18px;"+
                "-fx-font-weight:bold;"
            );
            Label driverDescription=new Label("I operate a mobile\n charging bus");
            driverDescription.setStyle(
                "-fx-text-fill:#B8BEC5;"+
                "-fx-font-size:14px;"+
                "-fx-alignment:center;"
            );
            VBox driverContent=new VBox(8,driverRadio,busView,driverTitle,driverDescription);
            driverContent.setAlignment(Pos.CENTER);


            VBox driverCard=new VBox(driverContent);
            driverCard.setAlignment(Pos.CENTER);
            driverCard.setStyle(
                "-fx-background-color:transparent;"+
                "-fx-border-color:#35C979;"+
                "-fx-border-width:2;"+
                "-fx-border-radius:10;"+
                "-fx-background-radius:10;"
            );
            driverCard.setPrefSize(185, 160);
             
            //Admin

                RadioButton adminRadio=new RadioButton();
              adminRadio.setToggleGroup(roleGroup);
            Image adminImage=new Image(getClass().getResource("/assets/images/adminsymbol.png").toExternalForm());

            ImageView adminView=new ImageView(adminImage);
            adminView.setFitWidth(55);
            adminView.setFitHeight(55);
            adminView.setPreserveRatio(true);

            Label adminTitle=new Label("Admin");
            adminTitle.setStyle(
                "-fx-text-fill:white;"+
                "-fx-font-size:18px;"+
                "-fx-font-weight:bold;"
            );
            Label adminDescription=new Label("I manage the\n platform");
            adminDescription.setStyle(
                "-fx-text-fill:#B8BEC5;"+
                "-fx-font-size:14px;"+
                "-fx-alignment:center;"
            );
            VBox adminContent=new VBox(8,adminRadio,adminView,adminTitle,adminDescription);
            adminContent.setAlignment(Pos.CENTER);


            VBox adminCard=new VBox(adminContent);
            adminCard.setAlignment(Pos.CENTER);
            adminCard.setStyle(
                "-fx-background-color:transparent;"+
                "-fx-border-color:#35C979;"+
                "-fx-border-width:2;"+
                "-fx-border-radius:10;"+
                "-fx-background-radius:10;"
            );
            adminCard.setPrefSize(185, 160);

            HBox roleCards=new HBox(evCard,driverCard,adminCard);
            roleCards.setTranslateX(75);
            roleCards.setTranslateY(95);
            roleCards.setSpacing(15);

            
             Button createAccButton=new Button("Create Account");
             createAccButton.setPrefWidth(500);
             createAccButton.setPrefHeight(50);
             createAccButton.setStyle(
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
             createAccButton.setTranslateX(75);
             createAccButton.setTranslateY(115);
             createAccButton.setPrefWidth(600);
             createAccButton.setPrefHeight(50);
             
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
             orBox.setTranslateX(2);
             orBox.setTranslateY(125);
             orBox.setMaxWidth(Double.MAX_VALUE);

          
            
            Button alreadyAccButton = new Button("Already have an Account? Login");

          alreadyAccButton.setStyle(
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
alreadyAccButton.setTranslateX(75);
alreadyAccButton.setTranslateY(135);
alreadyAccButton.setPrefWidth(600);
alreadyAccButton.setPrefHeight(50);

alreadyAccButton.setOnAction(e->{
    callbackAction.run();
});






           Label copyright = new Label("© " + Year.now().getValue() + " ChargeOn. All rights reserved.");
           copyright.setTextFill(Color.web("#B8BDc2"));
          copyright.setFont(Font.font("Arial",FontWeight.NORMAL,14));
          copyright.setTranslateX(270);
            copyright.setTranslateY(160);

      VBox rightSection=new VBox(createAccLabel,joinText,nameText,nameField,emailText,emailField,hbpass1,hbpass2,roleText,roleCards,createAccButton,orBox,alreadyAccButton,copyright);
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
        
        createAccountPageScene=scene;
        return createAccountPageScene;
 
  

     }
    
}
