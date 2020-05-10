import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.*;

public class Cell extends Button {
    private final int row;
    private final int column;
    private final int count;
    private final boolean hasBomb;
    private boolean isRevealed;
    private boolean hasFlag;
    private static HashMap<Integer, Paint> colors;
    private static final Image flagImage = new Image(Objects.requireNonNull(ClassLoader.getSystemResourceAsStream("resources/images/flag-100x.png")));
    private static final Image bombImage = new Image(Objects.requireNonNull(ClassLoader.getSystemResourceAsStream("resources/images/bomb-100x.png")));
    private static int numCellsRevealed = 0;
    private final ImageView imageView;

    public static void main(String[] args) {
        ResourceBundle colorsBundle = ResourceBundle.getBundle("resources/bundles/colors");
        colors = new HashMap<>();
        colors.put(0, Color.web(colorsBundle.getString("zero")));
        colors.put(1, Color.web(colorsBundle.getString("one")));
        colors.put(2, Color.web(colorsBundle.getString("two")));
        colors.put(3, Color.web(colorsBundle.getString("three")));
        colors.put(4, Color.web(colorsBundle.getString("four")));
        colors.put(5, Color.web(colorsBundle.getString("five")));
        colors.put(6, Color.web(colorsBundle.getString("six")));
        colors.put(7, Color.web(colorsBundle.getString("seven")));
        colors.put(8, Color.web(colorsBundle.getString("eight")));
        colors.put(9, Color.web(colorsBundle.getString("nine")));
    }

    public Cell(int row, int column, int count, boolean hasBomb) {
        super();
        if (colors == null) main(null);
        this.row = row;
        this.column = column;
        this.count = count;
        this.hasBomb = hasBomb;
        this.isRevealed = false;
        this.imageView = new ImageView();
        imageView.fitWidthProperty().bind(this.widthProperty().multiply(0.70));
        imageView.maxHeight(this.getHeight() * 0.90);
        imageView.setPreserveRatio(true);
        this.getStyleClass().add("cell");
    }

    public void reveal() {
        if (this.isRevealed) return;
        if (this.hasBomb) revealBomb();
        else this.revealCount();
        this.isRevealed = true;
        numCellsRevealed++;
    }

    private void revealCount() {
        this.setText(String.valueOf(this.count));
        this.setTextFill(colors.get(this.count));
    }

    private void revealBomb() {
        this.imageView.setImage(bombImage);
        this.setGraphic(imageView);
    }

    public int getCount() {
        return count;
    }

    public boolean hasBomb() {
        return hasBomb;
    }

    public boolean isHidden() {
        return !isRevealed;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public boolean hasFlag() {
        return hasFlag;
    }

    public void toggleFlag() {
        imageView.setImage(flagImage);
        if (!hasFlag) this.setGraphic(imageView);
        else this.setGraphic(null);
        this.hasFlag = !this.hasFlag;
    }

    public static int getNumCellsRevealed() {
        return numCellsRevealed;
    }

    public static void resetNumCellsRevealed() {
        numCellsRevealed = 0;
    }
}