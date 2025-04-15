package ressource;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class gameField extends VBox {
    private static final int COLS = 5;
    private static final int CELL_SIZE = 60;
    private static final int CELL_GAP = 10;
    private final int rows;
    private final Rectangle[][] cells;
    private final GridPane grid;
    private final TextField inputField;
    private int currentRow = 0;

    public gameField() {
        this(6); // Standard: 6 Zeilen
    }

    public gameField(int rows) {
        this.rows = rows;
        this.setAlignment(Pos.CENTER);
        this.setSpacing(20);
        cells = new Rectangle[rows][COLS];

        // Grid erstellen
        grid = new GridPane();
        grid.setHgap(CELL_GAP);
        grid.setVgap(CELL_GAP);
        grid.setPadding(new Insets(CELL_GAP));
        grid.setAlignment(Pos.CENTER);

        // Grid konfigurieren
        setupGrid();

        // Eingabebereich erstellen
        HBox inputBox = new HBox(10);
        VBox.setMargin(inputBox, new Insets(20, 0, 0, 0));
        inputBox.setAlignment(Pos.CENTER);

        inputField = new TextField();
        inputField.setPrefWidth(250);
        inputField.setMaxWidth(250);
        inputField.setPrefHeight(30);
        inputField.setOnAction(_ -> handleInput());

        Button submitButton = new Button("Eingabe");
        submitButton.setPrefHeight(30);
        submitButton.setOnAction(e -> handleInput());

        inputBox.getChildren().addAll(inputField, submitButton);

        // Komponenten zusammenfügen
        this.getChildren().addAll(grid, inputBox);
    }

    private void setupGrid() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < COLS; col++) {
                Rectangle cell = createCell();
                cells[row][col] = cell;

                Label label = new Label("");
                label.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
                label.setTextFill(Color.WHITE);
                StackPane cellWrapper = new StackPane();
                cellWrapper.setAlignment(Pos.CENTER);
                cellWrapper.getChildren().addAll(cell, label);
                grid.add(cellWrapper, col, row);
            }
        }
    }

    public void setLetter(int row, int col, String letter) {
        StackPane cellWrapper = (StackPane) grid.getChildren().get(row * COLS + col);
        Label label = (Label) cellWrapper.getChildren().get(1);
        label.setText(letter);
    }

    private void handleInput() {
        String text = inputField.getText().toUpperCase();
        if (text.length() == COLS && currentRow < rows) {
            for (int col = 0; col < COLS; col++) {
                setLetter(currentRow, col, String.valueOf(text.charAt(col)));
            }
            currentRow++;
            inputField.clear();
        }
    }

    private Rectangle createCell() {
        Rectangle cell = new Rectangle(CELL_SIZE, CELL_SIZE);
        cell.setFill(Color.WHITE);
        cell.setStroke(Color.LIGHTGRAY);
        cell.setStrokeWidth(2);
        cell.setArcHeight(15);
        cell.setArcWidth(15);
        return cell;
    }

    public void setCellColor(int row, int col, Color color) {
        cells[row][col].setFill(color);
    }

    public TextField getInputField() {
        return inputField;
    }

    public int getRows() {
        return rows;
    }
}
