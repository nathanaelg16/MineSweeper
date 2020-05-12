package com.nathanaelg.cmp168.minesweeper;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.StageStyle;

import java.util.Objects;
import java.util.Optional;

/**
 * Handles anything that has to do with
 * the GUI elements of the game. "Drives" the game.
 */
public class GameDriver {
    private static final Media bombSound = new Media(String.valueOf(ClassLoader.getSystemResource("resources/sounds/gameover.mp3")));
    private static final Media plopSound = new Media(String.valueOf(ClassLoader.getSystemResource("resources/sounds/plop.mp3")));
    private static final Media winSound = new Media(String.valueOf(ClassLoader.getSystemResource("resources/sounds/win.mp3")));
    private static final Media confettiVideo = new Media(String.valueOf(ClassLoader.getSystemResource("resources/animations/confetti.mp4")));
    private static final MediaPlayer winnerVideo = new MediaPlayer(confettiVideo);
    private static MediaPlayer soundEffects; //used to play the different sounds available

    private final MediaView mediaView;
    private final StackPane gameStackPane;
    private boolean isGameRunning;
    private GridPane gameGridPane;
    private BombGrid bombGrid;
    private Cell[][] cells;

    /**
     * Creates a new GameDriver
     * object to drive the game.
     * This will create a new {@link BombGrid}
     * with the default values.
     *
     * @see BombGrid
     */
    public GameDriver() {
        this.isGameRunning = false;
        this.mediaView = new MediaView(winnerVideo);
        this.mediaView.setOpacity(0.0);
        this.mediaView.setDisable(true);

        this.bombGrid = new BombGrid(5, 5, 5);
        createGameGrid();

        this.gameStackPane = new StackPane();
        this.gameStackPane.getChildren().addAll(this.gameGridPane, this.mediaView);
    }

    /**
     * Creates a new GameDriver
     * object to drive the game.
     * This will create a new {@link BombGrid}
     * with the number of bombs, rows,
     * and columns as specified by the
     * level of difficulty passed in
     * as an argument.
     *
     * @param difficultyLevel level of difficulty
     * @see DifficultyLevel
     */
    public GameDriver(DifficultyLevel difficultyLevel) {
        this.isGameRunning = false;
        this.mediaView = new MediaView(winnerVideo);
        this.mediaView.setOpacity(0.0);
        this.mediaView.setDisable(true);

        bombGrid = new BombGrid(difficultyLevel.getRows(), difficultyLevel.getColumns(), difficultyLevel.getBombs());
        createGameGrid();

        this.gameStackPane = new StackPane();
        this.gameStackPane.getChildren().addAll(this.gameGridPane, this.mediaView);
    }

    /**
     * Sets the difficulty level and creates
     * a new BombGrid and updates the GUI
     * accordingly.
     *
     * @param difficultyLevel level of difficulty
     * @see DifficultyLevel
     */
    public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
        bombGrid = new BombGrid(difficultyLevel.getRows(), difficultyLevel.getColumns(), difficultyLevel.getBombs());
        createGameGrid();

        this.gameStackPane.getChildren().set(0, gameGridPane);
    }

    /**
     * Determines whether the game is running or not
     * (i.e. if the user has clicked on a cell and
     * game isn't over.)
     *
     * @return true if the user has clicked on a cell
     * and the game hasn't ended
     */
    public boolean isGameRunning() {
        return this.isGameRunning;
    }

    /**
     * Updates the status of the game
     *
     * @param isGameRunning true if the game has begun
     */
    private void setGameRunning(boolean isGameRunning) {
        this.isGameRunning = isGameRunning;
    }

    /**
     * Gets the StackPane with all
     * the GUI components of the game.
     *
     * @return GUI pane being used by the game
     */
    public StackPane getGamePane() {
        return gameStackPane;
    }

    /**
     * Creates the GUI grid of {@link Cell}s used by
     * the game.
     */
    private void createGameGrid() {
        this.gameGridPane = new GridPane();
        this.gameGridPane.setId("grid-pane");
        this.gameGridPane.setPadding(new Insets(10, 5, 5, 5));
        this.gameGridPane.setVgap(0.0);

        this.cells = new Cell[this.bombGrid.getNumRows()][this.bombGrid.getNumColumns()];

        int numRows = this.bombGrid.getNumRows();
        int numColumns = this.bombGrid.getNumColumns();

        Task<Void> timer = new Task<Void>() {
            @Override
            public Void call() throws InterruptedException {
                int i = 0;
                while (true) {
                    boolean wasGameRunning = isGameRunning();
                    while (isGameRunning()) {
                        Thread.sleep(1000);
                        updateMessage(String.format("%03d", ++i));
                    }
                    if (wasGameRunning) break;
                    updateMessage("000");
                }
                return null;
            }
        };

        Label timerLabel = new Label("000");
        timerLabel.setId("timer");
        timerLabel.textProperty().bind(timer.messageProperty());
        new Thread(timer).start();

        this.gameGridPane.add(timerLabel, 0, 0, numColumns, 1);

        GridPane.setHalignment(timerLabel, HPos.CENTER);

        boolean alternate = true;
        for (int i = 0; i < numRows; i++) {
            if (numColumns % 2 == 0) alternate = !alternate;
            for (int j = 0; j < numColumns; j++) {
                alternate = !alternate;
                Cell cell = new Cell(i, j, this.bombGrid.getCountAtLocation(i, j), this.bombGrid.isBombAtLocation(i, j));
                cell.prefWidthProperty().bind(this.gameGridPane.widthProperty().divide(numColumns));
                cell.prefHeightProperty().bind(this.gameGridPane.heightProperty().multiply(0.90).divide(numRows));
                cell.setId(alternate ? "shade1" : "shade2");

                cell.setOnMouseClicked(ev -> {
                    if (!this.isGameRunning) {
                        this.setGameRunning(true);
                    }

                    if (cell.isHidden()) {
                        if (ev.getButton() == MouseButton.SECONDARY) cell.toggleFlag();
                        else if (!cell.hasFlag()) {
                            if (!cell.hasBomb()) revealCells(cell.getRow(), cell.getColumn());
                            else {
                                cell.setStyle("-fx-background-color: red");
                                cell.reveal();
                                this.gameOver(false);
                                return;
                            }

                            if (this.bombGrid.getArea() - Cell.getNumCellsRevealed() == this.bombGrid.getNumBombs())
                                this.gameOver(true);
                        }
                        soundEffects = new MediaPlayer(plopSound);
                        soundEffects.play();
                    }
                });

                this.gameGridPane.add(cell, j, i + 1, 1, 1); //start adding cells on the second row of the GridPane
                this.cells[i][j] = cell;
            }
        }
    }

    /**
     * Calls to reveal the clicked cell and
     * all nearby cells if the count of the
     * clicked cell is 0 using recursion.
     *
     * @param row    row index of the clicked cell
     * @param column column index of the clicked cell
     */
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

    /**
     * Reveals all of the cells in the grid and
     * asks if the user would like to play again.
     *
     * @param winner true if the user has won the game
     */
    private void gameOver(boolean winner) {
        this.setGameRunning(false);
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

        option.ifPresent(this::setDifficultyLevel);
        Cell.resetNumCellsRevealed();
    }
}