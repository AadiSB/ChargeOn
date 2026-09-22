package com.core2web.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.io.InputStream;

public class AboutUs {

    private static final String SHASHI_SIR_IMAGE =
            "/assets/images/shashi_sir.png";

    public static ScrollPane buildMainContent() {

        VBox root = new VBox(24);

        root.setPadding(
                new Insets(25, 30, 35, 30)
        );

        root.setFillWidth(true);

        root.getStyleClass().add(
                "about-us-root"
        );

        VBox header = new VBox(8);

        Label title =
                new Label("About Us");

        title.getStyleClass().add(
                "heading"
        );

        Label subtitle =
                new Label(
                        "Get to know the team behind ChargeOn and the "
                        + "people who guided us throughout our journey."
                );

        subtitle.setWrapText(true);

        subtitle.setStyle(
                "-fx-text-fill: #cbd5e1;" +
                "-fx-font-size: 15px;"
        );

        header.getChildren().addAll(
                title,
                subtitle
        );

        VBox projectSection =
                new VBox(13);

        Label projectTitle =
                new Label("Project Description");

        projectTitle.getStyleClass().add(
                "section-title"
        );

        VBox projectCard =
                new VBox(12);

        projectCard.setPadding(
                new Insets(20)
        );

        projectCard.getStyleClass().add(
                "about-card"
        );

        Label projectDescription =
                new Label(
                        "ChargeOn is a smart EV and fleet management "
                        + "platform developed to simplify electric vehicle "
                        + "charging, fleet monitoring, driver management "
                        + "and booking operations. The platform connects "
                        + "owners, drivers and administrators through a "
                        + "unified system and provides an efficient digital "
                        + "experience for managing electric mobility."
                );

        projectDescription.setWrapText(true);

        projectDescription.setMaxWidth(
                Double.MAX_VALUE
        );

        projectDescription.setStyle(
                "-fx-text-fill: #e2e8f0;" +
                "-fx-font-size: 16px;"
        );

        projectCard.getChildren().add(
                projectDescription
        );

        projectSection.getChildren().addAll(
                projectTitle,
                projectCard
        );

        VBox teamSection =
                new VBox(13);

        Label teamTitle =
                new Label("Our Team");

        teamTitle.getStyleClass().add(
                "section-title"
        );

        Label teamSubtitle =
                new Label(
                        "The team behind the design, development and "
                        + "implementation of ChargeOn."
                );

        teamSubtitle.setStyle(
                "-fx-text-fill: #cbd5e1;" +
                "-fx-font-size: 15px;"
        );

        FlowPane teamCards =
                new FlowPane();

        teamCards.setHgap(18);

        teamCards.setVgap(18);

        teamCards.setPrefWrapLength(950);

        teamCards.getChildren().add(
                createTeamCard(
                        "Aaditya Bhand",
                        "Team Leader",
                        "Contributed to the development and leadership of the project and "
                        + "project implementation of ChargeOn."
                )
        );

        teamCards.getChildren().add(
                createTeamCard(
                        "Anuj Shinde",
                        "Team Member",
                        "Contributed to the development and "
                        + "implementation of the ChargeOn platform."
                )
        );

        teamCards.getChildren().add(
                createTeamCard(
                        "Pranav Nagur",
                        "Team Member",
                        "Contributed to the development and "
                        + "implementation of the ChargeOn platform."
                )
        );

        teamCards.getChildren().add(
                createTeamCard(
                        "Dhruva Satpute",
                        "Team Member",
                        "Contributed to the development and "
                        + "overall implementation of the project."
                )
        );

        teamSection.getChildren().addAll(
                teamTitle,
                teamSubtitle,
                teamCards
        );

        VBox acknowledgementSection =
                new VBox(13);

        Label acknowledgementTitle =
                new Label("Acknowledgements");

        acknowledgementTitle.getStyleClass().add(
                "section-title"
        );

        VBox acknowledgementCard =
                new VBox(12);

        acknowledgementCard.setPadding(
                new Insets(20)
        );

        acknowledgementCard.getStyleClass().add(
                "about-card"
        );

        Label acknowledgementText =
                new Label(
                        "We sincerely express our gratitude to everyone "
                        + "who guided, supported and encouraged us throughout "
                        + "the development of ChargeOn. Their valuable "
                        + "knowledge, feedback and continuous motivation "
                        + "helped us transform our ideas into a practical "
                        + "and meaningful project."
                );

        acknowledgementText.setWrapText(true);

        acknowledgementText.setStyle(
                "-fx-text-fill: #e2e8f0;" +
                "-fx-font-size: 16px;"
        );

        acknowledgementCard.getChildren().add(
                acknowledgementText
        );

        acknowledgementSection.getChildren().addAll(
                acknowledgementTitle,
                acknowledgementCard
        );

        VBox specialThanksSection =
                new VBox(13);

        Label specialThanksTitle =
                new Label("Special Thanks");

        specialThanksTitle.getStyleClass().add(
                "section-title"
        );

        HBox shashiCard =
                createSpecialThanksCard();

        specialThanksSection.getChildren().addAll(
                specialThanksTitle,
                shashiCard
        );

        VBox instructorsSection =
                new VBox(13);

        Label instructorsTitle =
                new Label("Instructors");

        instructorsTitle.getStyleClass().add(
                "section-title"
        );

        Label instructorsSubtitle =
                new Label(
                        "We are grateful for the guidance and support "
                        + "provided by our instructors."
                );

        instructorsSubtitle.setStyle(
                "-fx-text-fill: #cbd5e1;" +
                "-fx-font-size: 15px;"
        );

        FlowPane instructors =
                new FlowPane();

        instructors.setHgap(18);

        instructors.setVgap(18);

        instructors.setPrefWrapLength(950);

        instructors.getChildren().addAll(

                createInfoCard(
                        "Sachin Sir",
                        "Instructor",
                        "Instructor who supported and guided us "
                        + "throughout our learning journey."
                ),

                createInfoCard(
                        "Pramod Sir",
                        "Instructor",
                        "Instructor who provided valuable guidance "
                        + "and technical support."
                ),

                createInfoCard(
                        "Akshay Sir",
                        "Instructor",
                        "Instructor who contributed through guidance, "
                        + "feedback and encouragement."
                )
        );

        instructorsSection.getChildren().addAll(
                instructorsTitle,
                instructorsSubtitle,
                instructors
        );

        VBox superMentorsSection =
                new VBox(13);

        Label superMentorsTitle =
                new Label("Super Mentors");

        superMentorsTitle.getStyleClass().add(
                "section-title"
        );

        Label superMentorsSubtitle =
                new Label(
                        "Special appreciation for the guidance and "
                        + "mentorship that helped us grow."
                );

        superMentorsSubtitle.setStyle(
                "-fx-text-fill: #cbd5e1;" +
                "-fx-font-size: 15px;"
        );

        FlowPane superMentors =
                new FlowPane();

        superMentors.setHgap(18);

        superMentors.setVgap(18);

        superMentors.setPrefWrapLength(950);

        superMentors.getChildren().addAll(

                createInfoCard(
                        "Shiv Sir",
                        "Super Mentor",
                        "For valuable mentorship, guidance and "
                        + "continuous encouragement."
                ),

                createInfoCard(
                        "Subodh Sir",
                        "Super Mentor",
                        "For valuable mentorship, support and "
                        + "guidance throughout our journey."
                )
        );

        superMentorsSection.getChildren().addAll(
                superMentorsTitle,
                superMentorsSubtitle,
                superMentors
        );

        VBox mentorsTeamSection =
                new VBox(13);

        Label mentorsTeamTitle =
                new Label("Mentors & Team");

        mentorsTeamTitle.getStyleClass().add(
                "section-title"
        );

        Label mentorsTeamSubtitle =
                new Label(
                        "Our sincere appreciation to the mentors and "
                        + "team members who supported our journey."
                );

        mentorsTeamSubtitle.setStyle(
                "-fx-text-fill: #cbd5e1;" +
                "-fx-font-size: 15px;"
        );

        FlowPane mentorsTeam =
                new FlowPane();

        mentorsTeam.setHgap(18);

        mentorsTeam.setVgap(18);

        mentorsTeam.setPrefWrapLength(950);

        mentorsTeam.getChildren().addAll(

                createInfoCard(
                        "Mauli Rajmane Sir",
                        "Mentor & Team",
                        "For valuable guidance, support and "
                        + "contribution throughout our journey."
                ),

                createInfoCard(
                        "Sumit Katkar Sir",
                        "Mentor & Team",
                        "For valuable guidance, encouragement and "
                        + "support throughout the project journey."
                )
        );

        mentorsTeamSection.getChildren().addAll(
                mentorsTeamTitle,
                mentorsTeamSubtitle,
                mentorsTeam
        );

        root.getChildren().addAll(

                header,

                projectSection,

                teamSection,

                acknowledgementSection,

                specialThanksSection,

                instructorsSection,

                superMentorsSection,

                mentorsTeamSection
        );

        ScrollPane scrollPane =
                new ScrollPane(root);

        scrollPane.setFitToWidth(true);

        scrollPane.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scrollPane.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
        );

        scrollPane.getStyleClass().add(
                "scroll-pane"
        );

        return scrollPane;
    }

    private static VBox createTeamCard(
            String name,
            String role,
            String description
    ) {

        VBox card =
                new VBox(10);

        card.setPadding(
                new Insets(18)
        );

        card.setAlignment(
                Pos.TOP_LEFT
        );

        card.setPrefWidth(215);

        card.setMinHeight(165);

        card.setMaxWidth(215);

        card.getStyleClass().add(
                "team-card"
        );

        Label nameLabel =
                new Label(name);

        nameLabel.setWrapText(true);

        nameLabel.setStyle(
                "-fx-text-fill: #ffffff;" +
                "-fx-font-size: 24px;" +
                "-fx-font-weight: bold;"
        );

        Label roleLabel =
                new Label(role);

        roleLabel.setWrapText(true);

        roleLabel.setStyle(
                "-fx-text-fill: #ffffff;" +
                "-fx-font-size: 17px;" +
                "-fx-font-weight: bold;"
        );

        Label descriptionLabel =
                new Label(description);

        descriptionLabel.setWrapText(true);

        descriptionLabel.setMaxWidth(175);

        descriptionLabel.setStyle(
                "-fx-text-fill: #e2e8f0;" +
                "-fx-font-size: 16px;"
        );

        card.getChildren().addAll(
                nameLabel,
                roleLabel,
                descriptionLabel
        );

        return card;
    }

    private static VBox createInfoCard(
            String name,
            String role,
            String description
    ) {

        VBox card =
                new VBox(10);

        card.setPadding(
                new Insets(18)
        );

        card.setAlignment(
                Pos.CENTER_LEFT
        );

        card.setPrefWidth(210);

        card.setMinHeight(165);

        card.setMaxWidth(210);

        card.getStyleClass().add(
                "person-card"
        );

        Label nameLabel =
                new Label(name);

        nameLabel.setWrapText(true);

        nameLabel.setStyle(
                "-fx-text-fill: #ffffff;" +
                "-fx-font-size: 24px;" +
                "-fx-font-weight: bold;"
        );

        Label roleLabel =
                new Label(role);

        roleLabel.setWrapText(true);

        roleLabel.setStyle(
                "-fx-text-fill: #ffffff;" +
                "-fx-font-size: 17px;" +
                "-fx-font-weight: bold;"
        );

        Label descriptionLabel =
                new Label(description);

        descriptionLabel.setWrapText(true);

        descriptionLabel.setMaxWidth(175);

        descriptionLabel.setStyle(
                "-fx-text-fill: #e2e8f0;" +
                "-fx-font-size: 16px;"
        );

        card.getChildren().addAll(
                nameLabel,
                roleLabel,
                descriptionLabel
        );

        return card;
    }

    private static HBox createSpecialThanksCard() {

        HBox card =
                new HBox(24);

        card.setPadding(
                new Insets(22)
        );

        card.setAlignment(
                Pos.CENTER_LEFT
        );

        card.setMaxWidth(
                Double.MAX_VALUE
        );

        card.getStyleClass().add(
                "special-thanks-card"
        );

        ImageView imageView =
                createCircularImage(
                        SHASHI_SIR_IMAGE,
                        250
                );

        VBox information =
                new VBox(8);

        information.setAlignment(
                Pos.CENTER_LEFT
        );

        Label gratitudeLabel =
                new Label("With Gratitude");

        gratitudeLabel.setStyle(
                "-fx-text-fill: #ffffff;" +
                "-fx-font-size: 22px;" +
                "-fx-font-weight: bold;"
        );

        Label nameLabel =
                new Label("Shashi Sir");

        nameLabel.setStyle(
                "-fx-text-fill: #ffffff;" +
                "-fx-font-size: 42px;" +
                "-fx-font-weight: bold;"
        );

        Label organizationLabel =
                new Label("Core2Web");

        organizationLabel.setStyle(
                "-fx-text-fill: #ffffff;" +
                "-fx-font-size: 48px;" +
                "-fx-font-weight: bold;"
        );

        Label description =
                new Label(
                        "We extend our heartfelt thanks to Shashi Sir "
                        + "for his constant guidance, encouragement and "
                        + "valuable knowledge throughout our learning "
                        + "and project development journey."
                );

        description.setWrapText(true);

        description.setMaxWidth(
                650
        );

        description.setStyle(
                "-fx-text-fill: #e2e8f0;" +
                "-fx-font-size: 16px;"
        );

        information.getChildren().addAll(
                gratitudeLabel,
                nameLabel,
                organizationLabel,
                description
        );

        card.getChildren().addAll(
                imageView,
                information
        );

        HBox.setHgrow(
                information,
                Priority.ALWAYS
        );

        return card;
    }

    private static ImageView createCircularImage(
            String imagePath,
            double size
    ) {

        ImageView imageView =
                new ImageView();

        imageView.setFitWidth(
                size
        );

        imageView.setFitHeight(
                size
        );

        imageView.setPreserveRatio(
                false
        );

        try {

            InputStream inputStream =
                    AboutUs.class.getResourceAsStream(
                            imagePath
                    );

            if (inputStream != null) {

                Image image =
                        new Image(inputStream);

                imageView.setImage(
                        image
                );

                Circle clip =
                        new Circle(
                                size / 2,
                                size / 2,
                                size / 2
                        );

                imageView.setClip(
                        clip
                );

            } else {

                createPlaceholder(
                        imageView,
                        size
                );
            }

        } catch (Exception e) {

            createPlaceholder(
                    imageView,
                    size
            );
        }

        return imageView;
    }

    private static void createPlaceholder(
            ImageView imageView,
            double size
    ) {

        Circle placeholder =
                new Circle(
                        size / 2,
                        size / 2,
                        size / 2
                );

        placeholder.setFill(
                Color.web("#202936")
        );

        imageView.setClip(
                placeholder
        );
    }
}