package com.nathanaelg.cmp168.minesweeper;

import java.util.Random;

/**
 * Creates the "grid" to be used in the game
 * holding the locations of all the bombs
 * and the count at each location. The bombs
 * are assigned to different cells at random.
 * <p>
 * This class conforms to the UML diagram
 * given by the project specifications.
 */
@SuppressWarnings("ManualArrayCopy")
public class BombGrid {
    private boolean[][] bombGrid;
    private int[][] countGrid;
    private int[][] bombLocations;
    private final int numRows;
    private final int numColumns;
    private final int numBombs;

    /**
     * Creates a bomb grid with the default
     * values of 10 rows, 10 columns,
     * and 25 bombs.
     */
    public BombGrid() {
        this.numRows = 10;
        this.numColumns = 10;
        this.numBombs = 25;
        this.createBombGrid();
        this.createCountGrid();
    }

    /**
     * Creates a bomb grid with the
     * default number of bombs, which is 25.
     * The number of rows and columns are passed
     * as arguments.
     *
     * @param numRows    number of rows in the grid
     * @param numColumns number of columns in the grid
     */
    public BombGrid(int numRows, int numColumns) {
        this.numRows = numRows;
        this.numColumns = numColumns;
        this.numBombs = 25;
        this.createBombGrid();
        this.createCountGrid();
    }

    /**
     * Creates a bomb grid with the number of bombs,
     * rows, and columns passed as arguments.
     *
     * @param numRows    number of rows in the grid
     * @param numColumns number of columns in the grid
     * @param numBombs   total number of bombs in the grid
     */
    public BombGrid(int numRows, int numColumns, int numBombs) {
        this.numRows = numRows;
        this.numColumns = numColumns;
        this.numBombs = numBombs;
        this.createBombGrid();
        this.createCountGrid();
    }

    /**
     * Getter for the bomb grid generated when
     * the BombGrid was created.
     *
     * @return copy of the bomb grid generated
     */
    public boolean[][] getBombGrid() {
        boolean[][] newArray = new boolean[numRows][numColumns];
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numColumns; j++) {
                newArray[i][j] = this.bombGrid[i][j];
            }
        }
        return newArray;
    }

    /**
     * Getter for the count grid that corresponds to
     * the bomb grid generated when the BombGrid
     * was created.
     *
     * @return copy of the count grid
     */
    public int[][] getCountGrid() {
        int[][] newArray = new int[numRows][numColumns];
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numColumns; j++) {
                newArray[i][j] = this.countGrid[i][j];
            }
        }
        return newArray;
    }

    /**
     * Getter for the number of rows in the BombGrid
     *
     * @return number of rows in the BombGrid
     */
    public int getNumRows() {
        return this.numRows;
    }

    /**
     * Getter for the number of columns in the BombGrid
     *
     * @return number of columns in the BombGrid
     */
    public int getNumColumns() {
        return this.numColumns;
    }

    /**
     * Getter for the number of bombs in the BombGrid
     *
     * @return number of bombs in the BombGrid
     */
    public int getNumBombs() {
        return this.numBombs;
    }

    /**
     * Checks the BombGrid to see if a bomb exists at the
     * location given by the row and column passed in as
     * arguments
     *
     * @param row    row index to check
     * @param column column index to check
     * @return true if a bomb is at the specified location,
     * false otherwise
     */
    public boolean isBombAtLocation(int row, int column) {
        return this.bombGrid[row][column];
    }

    /**
     * Gets the number of bombs surrounding the cell
     * given by the location specified by the row and
     * column passed in as arguments, including whether
     * a bomb exists in the cell itself.
     * <p>
     * The minimum number of bombs surrounding the cell is 0.
     * The maximum number of bombs surrounding the cell is 8.
     * <p>
     * NOTE: If a bomb exists at the cell specified by the row
     * and column passed as arguments, it will be included in the
     * count.
     * <p>
     * NOTE: While only a maximum of eight bombs can surround a cell
     * (e.g. to the left, top-left, top, top-right, right, bottom-right,
     * bottom, bottom-left), if the cell being check itself contains a bomb,
     * this method can return a value of 9. This would mean that all surrounding cells
     * and the cell itself all have bombs.
     *
     * @param row    row index to check
     * @param column column index to check
     * @return a value from 0-9 indicating the number of bombs surrounding
     * the location being checked
     */
    public int getCountAtLocation(int row, int column) {
        return this.countGrid[row][column];
    }

    /**
     * Creates the count grid specifying the number of bombs surrounding
     * each cell in the BombGrid based on the bomb grid generated
     * when the BombGrid was created.
     * <p>
     * NOTE: If a bomb exists at a cell itself, it will be included in the
     * count. This means that a cell with a bomb that has no surrounding
     * cells with bombs will have a count of 1.
     */
    private void createCountGrid() {
        this.countGrid = new int[numRows][numColumns];
        for (int i = 0; i < numBombs; i++) {
            int row = this.bombLocations[i][0];
            int column = this.bombLocations[i][1];

            this.countGrid[row][column] += 1;

            if (row > 0) this.countGrid[row - 1][column] += 1;

            if (row > 0 && column > 0) this.countGrid[row - 1][column - 1] += 1;

            if (row > 0 && column < this.numColumns - 1) this.countGrid[row - 1][column + 1] += 1;

            if (row < this.numRows - 1) this.countGrid[row + 1][column] += 1;

            if (row < this.numRows - 1 && column > 0) this.countGrid[row + 1][column - 1] += 1;

            if (column > 0) this.countGrid[row][column - 1] += 1;

            if (column < this.numColumns - 1) this.countGrid[row][column + 1] += 1;

            if (column < this.numColumns - 1 && row < this.numRows - 1) this.countGrid[row + 1][column + 1] += 1;
        }
    }

    /**
     * Generates a bomb grid based on the number of rows
     * and columns in this BombGrid. Bombs are assigned
     * to cells at random using the {@link Random} class.
     *
     * @see Random
     */
    private void createBombGrid() {
        Random random = new Random();
        this.bombGrid = new boolean[this.numRows][this.numColumns];
        this.bombLocations = new int[numBombs][2];

        int count = 0;
        while (count < numBombs) {
            int row = random.nextInt(numRows);
            int column = random.nextInt(numColumns);

            if (!bombGrid[row][column]) {
                bombGrid[row][column] = true;
                bombLocations[count][0] = row;
                bombLocations[count][1] = column;
                count++;
            }
        }
    }

    /**
     * Getter for the array containing only the row
     * and column indices of cells containing bombs.
     * The multidimensional array has a size of numBombs
     * in one dimension and a size of 2 in the other.
     * <p>
     * Example: array[0][0] returns the row index of the first
     * bomb, array[1][0] returns the row index of the second bomb,
     * and array[1][1] returns the column index of the second bomb.
     *
     * @return copy of the array containing the row and column indices
     * of cells containing bombs
     */
    protected int[][] getBombLocations() {
        int[][] newArray = new int[numBombs][2];
        for (int i = 0; i < numBombs; i++) {
            newArray[i][0] = bombLocations[i][0];
            newArray[i][1] = bombLocations[i][1];
        }
        return newArray;
    }

    /**
     * Gets the area of BombGrid by multiplying
     * the number of rows by the number of columns.
     *
     * @return area of the BombGrid
     */
    public int getArea() {
        return numRows * numColumns;
    }

    /**
     * Display the bomb grid and count grid side-by-side
     * on the console.
     */
    private void printGrids() {
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numColumns; j++) {
                System.out.print((bombGrid[i][j] ? "T" : "F") + " ");
            }

            System.out.print("\t");

            for (int j = 0; j < numColumns; j++) {
                System.out.print(countGrid[i][j] + " ");
            }

            System.out.println();
        }

        System.out.println();
        System.out.println();
    }
}