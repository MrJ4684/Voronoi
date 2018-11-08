package voronoiGUI;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import java.util.ArrayList;

/**
 * @author Jesse Gey
 * @version 11/15/2018.
 */

public class VoronoiGUI extends Application {

    private Pane game = new Pane();
    private final int SCENE_HEIGHT= 500;
    private final int SCENE_WIDTH = 750;

    private BorderPane root = new BorderPane();
    private Canvas canvas = new Canvas(700, 425);
    private GraphicsContext gc = canvas.getGraphicsContext2D();
    private ChoiceBox<String> choiceBox = new ChoiceBox<>();
    private Button clearButton = new Button("clear all");
    private Button randomButton = new Button("Randomize Color");
    private CheckBox checkBox = new CheckBox("Show Seeds");

    private ArrayList<Color> colors = new ArrayList<>();
    private ArrayList<Circle> dots = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) {

        //Creates components of visuals
        HBox menu = new HBox(60);
        Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT);

        //Styles components
        menu.setMinHeight(50);
        game.setPrefSize(SCENE_WIDTH, SCENE_HEIGHT);
        game.setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        root.setPadding(new Insets(0, 25, 0, 25));
        BorderPane.setMargin(menu, new Insets(25, 0, 0, 0));
        game.getChildren().add(canvas);

        //Toggles visibility of seeds
        checkBox.setSelected(true);
        checkBox.setOnAction(event -> toggleSeed());

        //Sets mark upon players click
        //Maybe a way to move getX and getY into function?
        //At moment program needs separate drawSeeds, need to take into account
        game.setOnMousePressed(event -> {
            double x = event.getX();
            double y = event.getY();

            drawSeed(x,y);
        });

        //Randomizes the colors of each cell
        randomButton.setOnAction(event -> randomize());

        //Removes all objects in the game pane
        clearButton.setOnAction(event -> clear());

        //Adds a choice box to determine algorithm
        choiceBox.getItems().addAll("Euclidean distance", "Manhattan distance", "Chessboard distance");
        choiceBox.getSelectionModel().selectFirst(); //Defaults algorithm to first item
        choiceBox.setOnAction(event -> fillIn());

        //Brings all components to one screen
        root.setCenter(game);
        root.setBottom(menu);
        menu.getChildren().addAll(randomButton, clearButton, checkBox, choiceBox);

        primaryStage.setTitle("VoronoiGUI");
        primaryStage.setScene(scene);
        primaryStage.show();
    }





    private void drawSeed(double x, double y){
        Circle dot = new Circle(x, y, 2, Color.BLACK);
        dots.add(dot);

        if (checkBox.isSelected()) {
            game.getChildren().add(dot);
        }

        colors.add(randomColor());
        fillIn();
    }//drawCentres

    private void drawSeed(){
        colors.clear();
        for (int i = 0; i < dots.size();i++) {
            colors.add(randomColor());

        }
        colors.add(randomColor());
        fillIn();
    }

    private void toggleSeed(){
        if (checkBox.isSelected()) {
            for (Circle dot : dots) {
                game.getChildren().addAll(dot);
            }
        } else {
            for (Circle dot : dots) {
                game.getChildren().removeAll(dot);
            }
        }
    }

    private void randomize(){
        drawSeed();
    }

    private void clear(){
        game.getChildren().clear();
        game.getChildren().add(canvas);
        for(int y=0;y<SCENE_HEIGHT;y++){
            for(int x=0;x<SCENE_WIDTH;x++) {
                //Kind of hacky... Better way?
                gc.setFill(Color.WHITE);
                gc.fillRect(x,y,1,1);
            }
        }

        dots.clear();
    }

    private String getDistance() {
        return choiceBox.getValue();
    }

    /**
     * Returns a color with a randomly generated Hex value.
     */
    private Color randomColor() {

        return Color.rgb((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));
    }

    //Uses formula D = sqrt((x2 - x1)^2+(y2 - y1)^2)
    private double getEuclideanDistance(int x1, int y1, double x2, double y2) {
        int x = x1 - (int) x2;
        int y = y1 - (int) y2;

        return Math.sqrt((x * x) + (y * y));
    }

    /**
     * @param x1 Point 1 x coordinate
     * @param y1 Point 1 y coordinate
     * @param x2 Points 2 x coordinate
     * @param y2 Point 2 y coordinate
     * @return distance between two points based on algorithm
     */
    //Uses formula D =(x2-x1)+(y2-y1)
    private double getManhattanDistance(int x1, int y1, double x2, double y2) {
        double x = (double)x1 - x2;
        double y = (double)y1 - y2;

        return (Math.abs(x) + Math.abs(y));
    }

    //Uses formula shortest distance between x and y
    private double getChessboardDistance(int x1, int y1, double x2, double y2) {
        double x = (double)x1 - x2;
        double y = (double)y1 - y2;
        if (Math.abs(x) > Math.abs(y)) {
            return Math.abs(x);
        } else {
            return Math.abs(y);
        }
    }

    private int findNearestCentre(int x, int y){
        int n = 0;
        double distance;
        double nearestDistance = Integer.MAX_VALUE;
        for(int i =0;i<dots.size();i++){

            switch (getDistance()) {
                case "Euclidean distance":
                    distance = getEuclideanDistance(x, y, dots.get(i).getCenterX(), dots.get(i).getCenterY());
                    break;
                case "Manhattan distance":
                    distance = getManhattanDistance(x, y, dots.get(i).getCenterX(), dots.get(i).getCenterY());
                    break;
                default:
                    distance = getChessboardDistance(x, y, dots.get(i).getCenterX(), dots.get(i).getCenterY());
                    break;
            }

            if(distance<nearestDistance){
                nearestDistance = distance;
                n = i;
            }
        }
        return n;
    }

    private void fillIn(){
        int closestSeed;
        for(int y=0;y<SCENE_HEIGHT;y++){
            for(int x=0;x<SCENE_WIDTH;x++){

                closestSeed = findNearestCentre(x,y);

                gc.setFill(colors.get(closestSeed));
                gc.fillRect(x,y,1,1);
            }
        }
    }//Draw
}
