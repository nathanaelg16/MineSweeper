import java.util.Random;

public class BombGrid {
    private boolean[][] bombGrid;
    private int[][] countGrid;
    private int[][] bombLocations;
    private final int numRows;
    private final int numColumns;
    private final int numBombs;

    public BombGrid() {
        this.numRows = 10;
        this.numColumns = 10;
        this.numBombs = 25;
        this.createBombGrid();
        this.createCountGrid();
    }

    public BombGrid(int numRows, int numColumns) {
        this.numRows = numRows;
        this.numColumns = numColumns;
        this.numBombs = 25;
        this.createBombGrid();
        this.createCountGrid();
    }

    public BombGrid(int numRows, int numColumns, int numBombs) {
        this.numRows = numRows;
        this.numColumns = numColumns;
        this.numBombs = numBombs;
        this.createBombGrid();
        this.createCountGrid();
    }

    public boolean[][] getBombGrid() {
        boolean[][] newArray = new boolean[numRows][numColumns];
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numColumns; j++) {
                newArray[i][j] = this.bombGrid[i][j];
            }
        }
        return newArray;
    }

    public int[][] getCountGrid() {
        int[][] newArray = new int[numRows][numColumns];
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numColumns; j++) {
                newArray[i][j] = this.countGrid[i][j];
            }
        }
        return newArray;
    }

    public int getNumRows() {
        return this.numRows;
    }

    public int getNumColumns() {
        return this.numColumns;
    }

    public int getNumBombs() {
        return this.numBombs;
    }

    public boolean isBombAtLocation(int row, int column) {
        return this.bombGrid[row][column];
    }

    public int getCountAtLocation(int row, int column) {
        return this.countGrid[row][column];
    }

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

        printGrids();
    }

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

    private void printGrids() { //TODO REMOVE THIS METHOD
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

    protected int[][] getBombLocations() {
        int[][] newArray = new int[numBombs][2];
        for (int i = 0; i < numBombs; i++) {
            newArray[i][0] = bombLocations[i][0];
            newArray[i][1] = bombLocations[i][1];
        }
        return newArray;
    }

    public int getArea() {
        return numRows * numColumns;
    }
}