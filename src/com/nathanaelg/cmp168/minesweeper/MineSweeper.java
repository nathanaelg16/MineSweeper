package com.nathanaelg.cmp168.minesweeper;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * This MineSweeper game was created for my final project
 * of CMP 168 - Programming Methods II.
 * <p>
 * This class starts the JavaFX application and shows the
 * primary stage. The functionality of the game including
 * the graphical user interface is handled by the
 * {@link GameDriver} class.
 */
public class MineSweeper extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        GameDriver driver = new GameDriver(DifficultyLevel.EASY);
        Scene scene = new Scene(driver.getGamePane());
        scene.getStylesheets().add("/resources/css/styles.css");
        primaryStage.setScene(scene);
        primaryStage.setTitle("Mine Sweeper 💣");
        primaryStage.setWidth(1200);
        primaryStage.setHeight(1000);
        primaryStage.show();
    }
}