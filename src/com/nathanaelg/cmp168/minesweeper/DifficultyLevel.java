package com.nathanaelg.cmp168.minesweeper;

/**
 * Levels of difficulty used by the game.
 * EASY mode has 5 columns, rows, and bombs,
 * REGULAR mode has 10 columns, rows, and bombs,
 * HARD mode has 10 columns and rows, and 25 bombs.
 */
public enum DifficultyLevel {
    EASY(5, 5, 5),
    REGULAR(10, 10, 10),
    HARD(10, 10, 25);

    private final int rows;
    private final int columns;
    private final int bombs;

    DifficultyLevel(int rows, int columns, int bombs) {
        this.rows = rows;
        this.columns = columns;
        this.bombs = bombs;
    }

    public int getColumns() {
        return columns;
    }

    public int getBombs() {
        return bombs;
    }

    public int getRows() {
        return rows;
    }

    @Override
    public String toString() {
        return "com.nathanaelg.cmp168.minesweeper.DifficultyLevel{" +
                "rows=" + rows +
                ", columns=" + columns +
                ", bombs=" + bombs +
                '}';
    }
}
