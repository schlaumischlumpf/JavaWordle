package ressource;

import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

public class GameField extends VBox {
    public final int rows;
    private final Label[][] cells;
    protected int currentRow = 0;
    protected int currentCol = 0;
    private final TextField hiddenInputField; // Verstecktes Eingabefeld für interne Verwendung

    public GameField() {
        this(6); // Standard: 6 Zeilen
    }

    public GameField(int rows) {
        super(10);
        this.setAlignment(Pos.CENTER);

        this.rows = rows;
        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setAlignment(Pos.CENTER);

        // Zellen erstellen
        cells = new Label[rows][5];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < 5; col++) {
                Label cell = new Label("");
                cell.setPrefSize(50, 50);
                cell.setAlignment(Pos.CENTER);
                cell.setFont(Font.font("Arial", FontWeight.BOLD, 20));
                cell.setTextFill(Color.BLACK);
                // Neues Styling für Zellen
                cell.setStyle("-fx-background-color: white; -fx-border-color: #d3d6da; -fx-border-width: 2;");
                grid.add(cell, col, row);
                cells[row][col] = cell;
            }
        }

        // Hervorheben des ersten Kästchens
        highlightCurrentCell();

        // Verstecktes Eingabefeld für interne Verwaltung (nicht sichtbar)
        hiddenInputField = new TextField();
        hiddenInputField.setVisible(false);
        hiddenInputField.setPrefWidth(0);
        hiddenInputField.setPrefHeight(0);

        // Button ist für einige Funktionen noch nötig
        Button submitButton = new Button("");
        submitButton.setVisible(false);
        submitButton.setPrefWidth(0);
        submitButton.setPrefHeight(0);

        HBox hiddenContainer = new HBox();
        hiddenContainer.getChildren().addAll(hiddenInputField, submitButton);
        hiddenContainer.setVisible(false);

        this.getChildren().addAll(grid, hiddenContainer);
    }

    // Methode zum Hervorheben des aktuellen Kästchens
    private void highlightCurrentCell() {
        // Zurücksetzen aller Zellen-Stile in der aktuellen Zeile
        for (int col = 0; col < 5; col++) {
            if (cells[currentRow][col].getText().isEmpty()) {
                cells[currentRow][col].setStyle("-fx-background-color: white; -fx-border-color: #d3d6da; -fx-border-width: 2;");
            }
        }
        
        // Aktuelles Kästchen hervorheben, wenn es noch nicht ausgefüllt ist
        if (currentCol < 5 && currentRow < rows) {
            cells[currentRow][currentCol].setStyle("-fx-background-color: white; -fx-border-color: #878a8c; -fx-border-width: 2;");
        }
    }

    // Buchstaben hinzufügen
    public void addLetter(String letter) {
        if (currentCol < 5) {
            setLetter(currentRow, currentCol, letter);
            hiddenInputField.setText(hiddenInputField.getText() + letter);
            currentCol++;
            highlightCurrentCell();
        }
    }

    // Flag, um mehrfache gleichzeitige Löschoperationen zu verhindern
    public boolean isRemovingLetter = false; // Sperre für Löschvorgang

    public void removeLetter() {
        // Nur löschen, wenn es etwas zu löschen gibt und keine Löschung bereits läuft
        if (currentCol > 0 && !isRemovingLetter) {
            isRemovingLetter = true; // Sperre aktivieren

            // Direkte Löschung ohne komplexe Animation für bessere Stabilität
            currentCol--;
            setLetter(currentRow, currentCol, "");
            highlightCurrentCell();

            // Kurze Verzögerung, um schnelles wiederholtes Löschen zu verhindern
            // Verhindert Probleme bei schnellem Tippen
            PauseTransition pause = new PauseTransition(Duration.millis(50));
            pause.setOnFinished(_ -> isRemovingLetter = false); // Sperre aufheben
            pause.play();
        }
    }

    // Aktuelle Eingabe zurückgeben
    public String getCurrentInput() {
        return hiddenInputField.getText();
    }

    // Zeile abschließen und zur nächsten übergehen
    public void commitRow() {
        if (currentCol == 5) {
            currentCol = 0;
            currentRow++;
            hiddenInputField.clear();
            
            if (currentRow < rows) {
                highlightCurrentCell();
            }
        }
    }

    // Buchstaben in eine Zelle setzen
    public void setLetter(int row, int col, String letter) {
        if (row >= 0 && row < rows && col >= 0 && col < 5) {
            cells[row][col].setText(letter);
        }
    }

    // Farbe einer Zelle ändern
    public void setCellColor(int row, int col, Color color) {
        if (row >= 0 && row < rows && col >= 0 && col < 5) {
            String colorHex;
            if (color.equals(Color.web("#6aaa64"))) {
                colorHex = "#6aaa64"; // Grün
            } else if (color.equals(Color.web("#c9b458"))) {
                colorHex = "#c9b458"; // Gelb
            } else if (color.equals(Color.web("#787c7e"))) {
                colorHex = "#787c7e"; // Grau
            } else {
                colorHex = "white";
            }
            cells[row][col].setStyle("-fx-background-color: " + colorHex + "; -fx-border-color: " + colorHex + "; -fx-border-width: 2;");
            cells[row][col].setTextFill(Color.WHITE);
        }
    }

}
