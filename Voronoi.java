package voronoiGUI;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

/**
 * @author Jesse Gey
 * @version 11/1/2018.
 */

public class Voronoi extends Application{

    private final ArrayList<Circle> dots = new ArrayList<>();
    private final ArrayList<Polygon> cells = new ArrayList<>();
    private final ArrayList<Double> xPoints = new ArrayList<>();
    private final ArrayList<Double> yPoints = new ArrayList<>();
    final int SIZE = 500;

    @Override
    public void start(Stage primaryStage) {

        //Creates components of visuals
        BorderPane root = new BorderPane();
        Pane game = new Pane();
        HBox menu = new HBox(60);
        Scene scene = new Scene(root, 750,500);

        //Styles components
        menu.setMinHeight(50);
        game.setPrefSize(SIZE,SIZE);
        game.setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        root.setPadding(new Insets(0,25,0,25));
        root.setMargin(menu, new Insets(25,0,0,0));

        //Toggles visibility of seeds
        int i = 0;
        CheckBox checkBox = new CheckBox("Show Seeds");
        checkBox.setSelected(true);
        checkBox.setOnAction(event -> {
            if(checkBox.isSelected() == true) {
                for (int j = 0; j < dots.size(); j++) {
                    game.getChildren().addAll(dots.get(j));
                }
            }
            else {
                for (int j = 0; j < dots.size(); j++) {
                    game.getChildren().removeAll(dots.get(j));
                }
            }
        });

        //Sets mark upon players click
        game.setOnMousePressed(event -> {
            double x = event.getX();
            double y = event.getY();

            Polygon cell = new Polygon();
            Circle dot = new Circle(x,y,4,Color.BLACK);
            cell.getPoints().addAll(x, y);

            xPoints.add(x);
            yPoints.add(y);

            cells.add(cell);
            dots.add(dot);

            if(checkBox.isSelected()==true) {
                game.getChildren().add(dot);
            }

            euclidean();
        });

        //Randomizes the colors of each cell
        Button randomButton = new Button("Randomize Color");
        randomButton.setOnAction(event ->{
            game.setStyle("-fx-background-color: #"+randomColor()+";");
        });

        //Removes all objects in the game pane
        Button clearButton = new Button("clear all");
        clearButton.setOnAction(event -> {
            game.getChildren().clear();
            dots.clear();
            });

        //Adds a choice box to determine algorithm
        ChoiceBox choiceBox = new ChoiceBox();
        choiceBox.getItems().addAll("Euclidean distance", "Manhattan distance", "Chessboard distance");
        choiceBox.getSelectionModel().selectFirst(); //Defaults algorithm to first item
        choiceBox.setOnAction(event -> {
            if(choiceBox.getValue()=="Euclidean distance"){
                euclidean();
            }
            else if(choiceBox.getValue()=="Manhattan distance"){
                manhattan();
            }
            else{
                chessBoard();
            }
        });

        //Brings all components to one screen
        root.setCenter(game);
        root.setBottom(menu);
        menu.getChildren().addAll(randomButton,clearButton, checkBox, choiceBox);

        primaryStage.setTitle("Voronoi Diagram");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Returns a color with a randomly generated Hex value.
     */
    private String randomColor() {
        Random r = new Random();
        String random = "";
        char c;
        for(int j = 0; j < 6;j++) {
            int numLetter = r.nextInt(2);
            if (numLetter < 1)
                c = (char) (r.nextInt(5) + 'a');
            else
                c = (char) (r.nextInt(9) + '0');
            random += c;
        }
        return random;
    }

    //Uses formula D = sqrt((x2- x1)^2+(y2-y1)^2)
    private void euclidean() {
        System.out.println("Euclidean");
    }
    //Uses formula D =(x2-x1)+(y2-y1)
    private void manhattan(){
        System.out.println("Manhattan");
    }
    //Uses formula shortest distance between x and y
    private void chessBoard(){
        System.out.println("Chess Board");
    }


}
