import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MineSweeper extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        StackPane stackPane = new StackPane();
        GameDriver driver = new GameDriver(DifficultyLevel.EASY);
        driver.onGameWin((level) -> {
            driver.setDifficultyLevel(level);
            stackPane.getChildren().set(0, driver.getGameGridPane());
        });

        stackPane.getChildren().addAll(driver.getGameGridPane(), driver.getMediaView());

        Scene scene = new Scene(stackPane);
        scene.getStylesheets().add("/resources/css/styles.css");
        primaryStage.setScene(scene);
        primaryStage.setTitle("Mine Sweeper 💣");
        primaryStage.setWidth(1200);
        primaryStage.setHeight(1000);
        primaryStage.show();
    }
}