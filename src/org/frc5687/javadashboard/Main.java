package org.frc5687.javadashboard;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class Main extends Application {
    Group root;
    Scene scene;
    HBox outerBox; // A horizontal layout outerBox to hold the robot feed, stats pane, and polecam feed

    Pane innerLeft; // An absolute-positioned pane to hold the robot feed and the goalOverlay
    ImageView robotFeed; // Image pane for the robot feed
    Pane goalOverlay; // Freestyle pane for the goalOverlay
    Pane boulderOverlay; // Freestyle pane for the goalOverlay

    GridPane statsPane; // Vertical layout pane to hold the stats in the center


    ImageView poleFeed; // Image pane for the robot feed

    public static Main instance;


    @Override
    public void start(Stage primaryStage) throws Exception{
        instance = this;
        robotFeed = new ImageView();
        robotFeed.setFitHeight(480);
        robotFeed.setFitWidth(640);

        poleFeed = new ImageView();
        poleFeed.setFitHeight(480);
        poleFeed.setFitWidth(640);


        root = new Group();
        scene = new Scene(root);
        scene.setFill(Color.WHITE);
        outerBox = new HBox();

        statsPane = new GridPane();
        statsPane.setPrefSize(300, 480);
        innerLeft = new Pane();
        goalOverlay = new Pane();
        boulderOverlay = new Pane();

        innerLeft.getChildren().add(robotFeed);
        innerLeft.getChildren().add(goalOverlay);
        innerLeft.getChildren().add(boulderOverlay);

        outerBox.getChildren().add(innerLeft);
        outerBox.getChildren().add(statsPane);
        outerBox.getChildren().add(poleFeed);

        root.getChildren().add(outerBox);

        primaryStage.setTitle("Outliers Dashboard");
        primaryStage.setWidth(640);
        primaryStage.setHeight(480);
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();

        Thread cameraThread = new Thread(new CameraController(robotFeed, poleFeed, goalOverlay));
        cameraThread.start();

        Thread rioThread = new Thread(new RoboRioReader());
        rioThread.start();


    }


    public static void main(String[] args) {
        launch(args);
    }

    private static final Paint boulderColor = Color.FORESTGREEN;
    public void renderBoulder(int targetWidth, int targetCenterX) {
        int centerY = 120;
        int radius = (int)((targetWidth * 0.8)/2);

        boulderOverlay.getChildren().clear();
        Arc arc = new Arc(targetCenterX,centerY,radius,radius,0,360);
        arc.setFill(Color.TRANSPARENT);
        arc.setStroke(boulderColor);
        arc.setType(ArcType.ROUND);

        boulderOverlay.getChildren().add(arc);

    }
    private static final Paint goalColor = Color.MAROON;

    public void renderGoal(int width, int centerX) {
        // Target is 20" wide, but goal is only 16" wide.
        int goalWidth = (int)(width * 0.8);
        int radius = (int)(goalWidth/2);
        int centerY = 120;

        goalOverlay.getChildren().clear();
        Arc arc = new Arc(centerX,centerY,radius,radius,0,180);
        arc.setFill(Color.TRANSPARENT);
        arc.setStroke(goalColor);
        arc.setType(ArcType.ROUND);

        Line left = new Line(centerX - radius, centerY, centerX - radius, centerY + goalWidth);
        left.setStroke(goalColor);

        Line bottom = new Line(centerX - radius, centerY + goalWidth, centerX + radius, centerY + goalWidth);
        bottom.setStroke(goalColor);

        Line right = new Line(centerX + radius, centerY + goalWidth, centerX + radius, centerY);
        left.setStroke(goalColor);

        goalOverlay.getChildren().add(arc);
        goalOverlay.getChildren().add(left);
        goalOverlay.getChildren().add(bottom);
        goalOverlay.getChildren().add(right);
    }
}
