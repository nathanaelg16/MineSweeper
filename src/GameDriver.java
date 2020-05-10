import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class GameDriver {
    private static final Media bombSound = new Media(String.valueOf(ClassLoader.getSystemResource("resources/sounds/gameover.mp3")));
    private static final Media plopSound = new Media(String.valueOf(ClassLoader.getSystemResource("resources/sounds/plop.mp3")));
    private static final Media winSound = new Media(String.valueOf(ClassLoader.getSystemResource("resources/sounds/win.mp3")));
    private static final Media confettiVideo = new Media(String.valueOf(ClassLoader.getSystemResource("resources/animations/confetti.mp4")));

    private static MediaPlayer soundEffects;
    private static final MediaPlayer winnerVideo = new MediaPlayer(confettiVideo);

    private final MediaView mediaView;

    private GridPane gameGridPane;

    private BombGrid bombGrid;
    private DifficultyLevel difficultyLevel;
    private Cell[][] cells;

    private Callback callback;

    public GameDriver() {
        this.bombGrid = new BombGrid();
        setDifficultyLevel(DifficultyLevel.REGULAR);
        this.mediaView = new MediaView(winnerVideo);
        this.mediaView.setOpacity(0.0);
        this.mediaView.setDisable(true);
    }

    public GameDriver(DifficultyLevel difficultyLevel) {
        setDifficultyLevel(difficultyLevel);
        this.mediaView = new MediaView(winnerVideo);
        this.mediaView.setOpacity(0.0);
        this.mediaView.setDisable(true);
    }

    public void setDifficultyLevel(DifficultyLevel difficultyLevel) { //todo tweak level intensities
        this.difficultyLevel = difficultyLevel;
        switch (difficultyLevel) {
            case EASY:
                bombGrid = new BombGrid(5, 5, 5);
                createGameGrid();
                break;
            case REGULAR:
                bombGrid = new BombGrid();
                createGameGrid();
                break;
            case HARD:
                bombGrid = new BombGrid(13, 13, 13);
                createGameGrid();
                break;
        }
    }

    public DifficultyLevel getDifficultyLevel() {
        return difficultyLevel;
    }

    public GridPane getGameGridPane() {
        return gameGridPane;
    }

    private void gameOver(boolean winner) {
        if (winner) {
            soundEffects = new MediaPlayer(winSound);

            int[][] bombLocations = this.bombGrid.getBombLocations();

            for (int i = 0; i < this.bombGrid.getNumBombs(); i++) {
                int row = bombLocations[i][0];
                int column = bombLocations[i][1];
                Cell cell = cells[row][column];
                cell.setStyle("-fx-background-color: blue;");
                cell.reveal();
            }

            Thread t = new Thread(() -> {
                try {
                    mediaView.setOpacity(0.4);
                    winnerVideo.play();
                    soundEffects.play();
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    winnerVideo.stop();
                    soundEffects.stop();
                    mediaView.setOpacity(0.0);
                }
            });

            t.start();
        } else {
            for (int i = 0; i < this.bombGrid.getNumRows(); i++) {
                for (int j = 0; j < this.bombGrid.getNumColumns(); j++) {
                    Cell cell = cells[i][j];
                    if (cell.hasBomb() && cell.isHidden()) cell.setStyle("-fx-background-color: orange;");
                    cells[i][j].reveal();
                }
            }

            Thread t = new Thread(() -> {
                try {
                    soundEffects = new MediaPlayer(bombSound);
                    soundEffects.play();
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    soundEffects.stop();
                }
            });

            t.start();
        }

        String dialogTitle = winner ? "YOU WON!" : "GAME OVER";
        String dialogMessage = "Would you like to play again?";

        Dialog<DifficultyLevel> dialog = new Dialog<>();
        dialog.setTitle(dialogTitle);
        dialog.getDialogPane().setStyle("-fx-font-family: Orbitron;");

        ButtonType easy = new ButtonType("EASY", ButtonBar.ButtonData.LEFT);
        ButtonType regular = new ButtonType("NORMAL", ButtonBar.ButtonData.LEFT);
        ButtonType hard = new ButtonType("HARD", ButtonBar.ButtonData.LEFT);
        ButtonType exit = new ButtonType("EXIT", ButtonBar.ButtonData.LEFT);

        dialog.getDialogPane().getButtonTypes().addAll(easy, regular, hard, exit);
        Image image = new Image(Objects.requireNonNull(ClassLoader.getSystemResourceAsStream(winner ? "resources/images/crown.png" : "resources/images/explosion.png")));
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(125);
        dialog.setGraphic(imageView);
        dialog.setHeaderText(dialogMessage);
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.getDialogPane().setPrefWidth(400);
        dialog.setResultConverter((c) -> {
            if (c.equals(easy)) {
                return DifficultyLevel.EASY;
            } else if (c.equals(regular)) {
                return DifficultyLevel.REGULAR;
            } else if (c.equals(hard)) {
                return DifficultyLevel.HARD;
            } else {
                Platform.exit();
                System.exit(0);
                return null;
            }
        });

        Optional<DifficultyLevel> option = dialog.showAndWait();
        option.ifPresent(level -> {
            try {
                callback.perform(level);
            } catch (IOException e) {
                e.printStackTrace();
                Platform.exit();
                System.exit(1);
            }
        });
        Cell.resetNumCellsRevealed();
    }

    private void createGameGrid() {
        this.gameGridPane = new GridPane();
        this.gameGridPane.setId("grid-pane");

        this.cells = new Cell[this.bombGrid.getNumRows()][this.bombGrid.getNumColumns()];
        this.gameGridPane.setPadding(new Insets(5, 5, 5, 5));

        int numRows = this.bombGrid.getNumRows();
        int numColumns = this.bombGrid.getNumColumns();

        boolean alternate = true;
        for (int i = 0; i < numRows; i++) {
            if (numColumns % 2 == 0) alternate = !alternate;
            for (int j = 0; j < numColumns; j++) {
                alternate = !alternate;
                Cell cell = new Cell(i, j, this.bombGrid.getCountAtLocation(i, j), this.bombGrid.isBombAtLocation(i, j));
                cell.prefWidthProperty().bind(this.gameGridPane.widthProperty().divide(numColumns));
                cell.prefHeightProperty().bind(this.gameGridPane.heightProperty().divide(numRows));
                cell.setId(alternate ? "shade1" : "shade2");

                cell.setOnMouseClicked(ev -> {
                    ImageView imageView = new ImageView();
                    imageView.fitWidthProperty().bind(cell.widthProperty().multiply(0.70));
                    imageView.maxHeight(cell.getHeight() * 0.90);
                    imageView.setPreserveRatio(true);

                    if (ev.getButton() == MouseButton.SECONDARY) cell.toggleFlag();
                    else if (!cell.hasFlag()) {
                        if (!cell.hasBomb()) revealCells(cell.getRow(), cell.getColumn());
                        else {
                            cell.setStyle("-fx-background-color: red");
                            cell.reveal();
                            this.gameOver(false);
                            return;
                        }
                        soundEffects = new MediaPlayer(plopSound);
                        soundEffects.play();

                        if (this.bombGrid.getArea() - Cell.getNumCellsRevealed() == this.bombGrid.getNumBombs()) this.gameOver(true);
                    }
                });

                this.gameGridPane.add(cell, j, i, 1, 1);
                this.cells[i][j] = cell;
            }
        }
    }

    private void revealCells(int row, int column) {
        Cell originalCell = this.cells[row][column];
        originalCell.reveal(); //reveal original cell
        if (originalCell.getCount() != 0) return;

        if (row > 0) {
            Cell cell = this.cells[row - 1][column];
            if (cell.getCount() == 0 && cell.isHidden()) revealCells(row - 1, column);
            else if (!cell.hasBomb()) cell.reveal();
        }

        if (row > 0 && column > 0) {
            Cell cell = this.cells[row - 1][column - 1];
            if (cell.getCount() == 0 && cell.isHidden()) revealCells(row - 1, column - 1);
            else if (!cell.hasBomb()) cell.reveal();
        }

        if (row > 0 && column < this.bombGrid.getNumColumns() - 1) {
            Cell cell = this.cells[row - 1][column + 1];
            if (cell.getCount() == 0 && cell.isHidden()) revealCells(row - 1, column + 1);
            else if (!cell.hasBomb()) cell.reveal();
        }

        if (row < this.bombGrid.getNumRows() - 1) {
            Cell cell = this.cells[row + 1][column];
            if (cell.getCount() == 0 && cell.isHidden()) revealCells(row + 1, column);
            else if (!cell.hasBomb()) cell.reveal();
        }

        if (row < this.bombGrid.getNumRows() - 1 && column > 0) {
            Cell cell = this.cells[row + 1][column - 1];
            if (cell.getCount() == 0 && cell.isHidden()) revealCells(row + 1, column - 1);
            else if (!cell.hasBomb()) cell.reveal();
        }

        if (column > 0) {
            Cell cell = this.cells[row][column - 1];
            if (cell.getCount() == 0 && cell.isHidden()) revealCells(row, column - 1);
            else if (!cell.hasBomb()) cell.reveal();
        }

        if (column < this.bombGrid.getNumColumns() - 1) {
            Cell cell = this.cells[row][column + 1];
            if (cell.getCount() == 0 && cell.isHidden()) revealCells(row, column + 1);
            else if (!cell.hasBomb()) cell.reveal();
        }

        if (column < this.bombGrid.getNumColumns() - 1 && row < this.bombGrid.getNumRows() - 1) {
            Cell cell = this.cells[row + 1][column + 1];
            if (cell.getCount() == 0 && cell.isHidden()) revealCells(row + 1, column + 1);
            else if (!cell.hasBomb()) cell.reveal();
        }
    }

    public MediaView getMediaView() {
        return this.mediaView;
    }

    public void onGameWin(Callback callback) {
        this.callback = callback;
    }
}



enum DifficultyLevel {
    EASY, REGULAR, HARD
}

interface Callback {
    void perform(DifficultyLevel level);
}