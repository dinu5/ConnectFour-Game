package com.dinu.connect4;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;
import org.w3c.dom.css.Rect;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {

    private static final int COLUMNS = 7;
    private static final int ROWS = 6;
    private static final int CIRCLE_DIAMETER = 80;
    private static final String diskColor1 = "#24303E";
    private static final String diskColor2 = "#4CAABB";

    @FXML
    public TextField playerOne;

    @FXML
    public TextField playerTwo;

    /*private  String PLAYER_ONE = playerOne.getText();
    private String PLAYER_TWO = playerTwo.getText();*/

    private boolean isPlayerOneTurn = true;

    private Disc[][] insertedDiscsArray = new Disc[ROWS][COLUMNS];  //Structural changes for the developer.


    @FXML
    public GridPane rootGridPane;

    @FXML
    public Pane insertedDiskPane;

    @FXML
    public Label playerNameLabel;

    /*@FXML
   // public TextField playerOne;

    @FXML
   // public TextField playerTwo;*/

    @FXML
    public Button playBtn;

    private boolean isAllowedToInsert = true;

    public void createPlayGround() {
        Shape rectangleWithHoles = createGameStructuralGrid();
        rootGridPane.add(rectangleWithHoles, 0, 1);
        List<Rectangle> rectangleList= createClickableColumn();
        for (Rectangle rectangle:rectangleList) {
            rootGridPane.add(rectangle,0,1);
        }


    }

    private Shape createGameStructuralGrid(){

        Shape rectangleWithHoles = new Rectangle((COLUMNS + 1) * CIRCLE_DIAMETER, (ROWS + 1) * CIRCLE_DIAMETER);

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                Circle circle = new Circle();
                circle.setRadius(CIRCLE_DIAMETER/2);
                circle.setCenterX(CIRCLE_DIAMETER/2);
                circle.setCenterY(CIRCLE_DIAMETER/2);
                circle.setSmooth(true);

                circle.setTranslateX(col * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER/4);
                circle.setTranslateY(row * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER/4);
                rectangleWithHoles = Shape.subtract(rectangleWithHoles, circle);
            }

        }
        rectangleWithHoles.setFill(Color.WHITE);
        return rectangleWithHoles;

    }
    public List<Rectangle> createClickableColumn(){

        List<Rectangle> rectangleList=new ArrayList<>();



        for(int col=0;col<COLUMNS;col++) {
            Rectangle rectAngle = new Rectangle(CIRCLE_DIAMETER, (ROWS + 1) * CIRCLE_DIAMETER);
            rectAngle.setFill(Color.TRANSPARENT);
            rectAngle.setTranslateX(col * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);

            rectAngle.setOnMouseEntered(event-> rectAngle.setFill(Color.valueOf("eeeeee26")));
            rectAngle.setOnMouseExited(event-> rectAngle.setFill(Color.TRANSPARENT));

            final int column=col;
            rectAngle.setOnMouseClicked(event -> {
                if(isAllowedToInsert) {
                    isAllowedToInsert = false;  //When the disc is being dropped then nomore disc will be inserted.
                    try {
                        insertDisc(new Disc(isPlayerOneTurn), column);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            rectangleList.add(rectAngle);
        }
        return rectangleList;
    }

    private void insertDisc(Disc disc, int column) throws IOException


    {

        int row = ROWS - 1;
        while (row>=0){

            if(getDiscIfPresent(row, column)==null)
                break;

            row--;
        }

        if(row<0)
            return;

        insertedDiscsArray[row][column] = disc;   //For structural changes: For developer.
        insertedDiskPane.getChildren().add(disc);

        disc.setTranslateX(column * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);

        int currentRow=row;
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5),disc);

        translateTransition.setToY(row * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER/4);
        String PLAYER_ONE = playerOne.getText();
        String PLAYER_TWO = playerTwo.getText();
        translateTransition.setOnFinished(event -> {

            isAllowedToInsert=true; //Finally, when the disc is dropped allowed next player to insert disc.

            if(gameEnded(currentRow, column)){
                gameOver();
                return;
            }

            isPlayerOneTurn = !isPlayerOneTurn;
            playerNameLabel.setText(isPlayerOneTurn? PLAYER_ONE : PLAYER_TWO);

        });
        translateTransition.play();
    }

    private boolean gameEnded(int row, int column){

        //Vertical Points. A smsll: Player has onserted his last disc at row =2, column = 3
        //index of each element present in column [row][column] :0,3  1,3  2,3  3,3  4,3   5,3
        List<Point2D> verticalPoints = IntStream.rangeClosed(row - 3,row + 3)   //Range of row values = 0,1,2,3,4,5
                                        .mapToObj(r-> new Point2D(r, column))   //0,3  1,3  2,3  3,3  4,3   5,3 -->Point2D
                                        .collect(Collectors.toList());
        List<Point2D> horizontalPoints = IntStream.rangeClosed(column- 3,column + 3)   //Range of row values = 0,1,2,3,4,5
                .mapToObj(col-> new Point2D(row, col))
                .collect(Collectors.toList());

        Point2D startPoint1 = new Point2D(row - 3, column + 3);
        List<Point2D> diagonalPoints = IntStream.rangeClosed(0,6)
                                            .mapToObj(i -> startPoint1.add(i, -i))
                                            .collect(Collectors.toList());

        Point2D startPoint2 = new Point2D(row - 3, column - 3);
        List<Point2D> diagonal2Points = IntStream.rangeClosed(0,6)
                .mapToObj(i -> startPoint2.add(i, i))
                .collect(Collectors.toList());

        boolean isEnded = checkCombination(verticalPoints) || checkCombination(horizontalPoints)
                            || checkCombination(diagonalPoints) || checkCombination(diagonal2Points);


        return isEnded;
    }

    private boolean checkCombination(List<Point2D> points) {

        int chain = 0;
        for (Point2D point: points) {


            int rowIndexForArray = (int) point.getX();
            int columnIndexForArray = (int) point.getY();

            Disc disc = getDiscIfPresent(rowIndexForArray, columnIndexForArray);

            if(disc != null && disc.isPlayerOneMove == isPlayerOneTurn){
                chain++;
                if(chain==4){
                    return true;
                }
            }else{
                chain = 0;
            }
        }
        return false;
    }

    private Disc getDiscIfPresent(int row, int column) {

        if (row >= ROWS || row < 0 || column >= COLUMNS || column < 0)
            return null;

        return insertedDiscsArray[row][column];

    }

    private void gameOver(){

        String PLAYER_ONE = playerOne.getText();
        String PLAYER_TWO = playerTwo.getText();

        String winner = isPlayerOneTurn? PLAYER_ONE : PLAYER_TWO;
        System.out.println(winner+" is winner");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Connect Four");
        alert.setHeaderText("Winner is " +winner);
        alert.setContentText("want to play again?");

        Platform.runLater(() -> {   //To resolve illegalStateException.

            ButtonType yesBtn = new ButtonType("Yes");
            ButtonType noBtn = new ButtonType("No, Exit");
            alert.getButtonTypes().setAll(yesBtn,noBtn);
            Optional<ButtonType> btnClicked = alert.showAndWait();
            if(btnClicked.isPresent() && btnClicked.get() == yesBtn ){
                //Reset the game
                resetGame();
            }else{
                //Exit the Game
                Platform.exit();
                System.exit(0);
            }

        });


    }

    public void resetGame() {

        String PLAYER_ONE = playerOne.getText();
        String PLAYER_TWO = playerTwo.getText();

        insertedDiskPane.getChildren().clear();
        for(int row=0;row<insertedDiscsArray.length;row++){
            for(int col=0;col<insertedDiscsArray.length;col++){
                insertedDiscsArray[row][col]=null;
            }
        }
        isPlayerOneTurn=true;
        playerNameLabel.setText(PLAYER_ONE);
        createPlayGround();

    }

    private static class Disc extends Circle{
        private final boolean isPlayerOneMove;

        public Disc(boolean isPlayerOneMove){
            setRadius(CIRCLE_DIAMETER/2);
            this.isPlayerOneMove= isPlayerOneMove;
            setFill(isPlayerOneMove? Color.valueOf(diskColor1): Color.valueOf(diskColor2));
            setCenterX(CIRCLE_DIAMETER/2);
            setCenterY(CIRCLE_DIAMETER/2);


         }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        playBtn.setOnAction(event -> {
            resetGame();
        });


    }




}
