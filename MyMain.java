package com.dinu.connect4;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class MyMain extends Application {

    private Controller controller;


    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("Game.fxml"));
        GridPane rootGridPane = loader.load();

        MenuBar menuBar = createMenu();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());

        Pane menuPane =(Pane) rootGridPane.getChildren().get(0);
        menuPane.getChildren().add(menuBar);

        controller = loader.getController();
        controller.createPlayGround();

        Scene scene = new Scene(rootGridPane);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Connect Four");
        primaryStage.setResizable(false);
        primaryStage.show();

    }

    private MenuBar createMenu() {

        Menu fileMenu = new Menu("File");
        MenuItem newGame = new MenuItem("New Game");

        newGame.setOnAction(event -> controller.resetGame());
        MenuItem resetGame = new MenuItem("Reset Game");
        resetGame.setOnAction(event -> controller.resetGame());
        SeparatorMenuItem seperatorMenuItem = new SeparatorMenuItem();
        MenuItem exitGame = new MenuItem("Exit Game");
        exitGame.setOnAction(event -> exitGame());

        fileMenu.getItems().addAll(newGame, resetGame, seperatorMenuItem, exitGame);

        Menu helpMenu = new Menu("Help");
        MenuItem aboutGame = new MenuItem("About");
        aboutGame.setOnAction(event -> aboutGame());
        SeparatorMenuItem seperatorMenuItem1 = new SeparatorMenuItem();
        MenuItem aboutMe = new MenuItem("About Me");
        aboutMe.setOnAction(event -> aboutMe());

        helpMenu.getItems().addAll(aboutGame, seperatorMenuItem1, aboutMe);

        MenuBar menuBar= new MenuBar();
        menuBar.getMenus().addAll(fileMenu, helpMenu);

        return menuBar;
    }

    private void aboutMe() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Developer");
        alert.setHeaderText("Who is the developer");
        alert.setContentText("I am Dinobandu Chakraborty from Purulia, WestBengal. " +
                "I am a student of Computer Science Engineering ");
        alert.show();
    }

    private void aboutGame() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Connect4 Game");
        alert.setHeaderText("How to play?");
        alert.setContentText("Connect Four is a two-player connection game" +
                " in which the players first choose a color and then " +
                "take turns dropping colored discs from the top into" +
                " a seven-column, six-row vertically suspended grid. " +
                "The pieces fall straight down, occupying the next" +
                " available space within the column. The objective of the" +
                " game is to be the first to form a horizontal, vertical," +
                " or diagonal line of four of one's own discs. " +
                "Connect Four is a solved game. The first player can" +
                " always win by playing the right moves.");
        alert.show();
    }

    private void exitGame() {
        Platform.exit();
        System.exit(0);
    }

    private void resetGame() {
        //TODO
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
