package ressource;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class GameField extends JPanel {
    public final int rows;
    private final JLabel[][] cells;
    protected int currentRow = 0;
    protected int currentCol = 0;
    private final StringBuilder hiddenInputText = new StringBuilder(); // Verstecktes Eingabefeld für interne Verwendung

    public GameField() {
        this(6); // Standard: 6 Zeilen
    }

    public GameField(int rows) {
        this.rows = rows;
        
        // BoxLayout für vertikale Anordnung mit Zentrierung
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        // Grid Panel erstellen
        JPanel gridPanel = new JPanel(new GridLayout(rows, 5, 5, 5));
        gridPanel.setOpaque(false);

        // Zellen erstellen
        cells = new JLabel[rows][5];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < 5; col++) {
                JLabel cell = new JLabel("", SwingConstants.CENTER);
                cell.setPreferredSize(new Dimension(50, 50));
                cell.setMinimumSize(new Dimension(50, 50));
                cell.setMaximumSize(new Dimension(50, 50));
                cell.setFont(new Font("Arial", Font.BOLD, 20));
                cell.setForeground(Color.BLACK);
                cell.setOpaque(true);
                cell.setBackground(Color.WHITE);
                // Neues Styling für Zellen
                cell.setBorder(new LineBorder(new Color(211, 214, 218), 2));
                gridPanel.add(cell);
                cells[row][col] = cell;
            }
        }

        // Hervorheben des ersten Kästchens
        highlightCurrentCell();

        // Grid Panel zentrieren
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new FlowLayout(FlowLayout.CENTER));
        wrapper.setOpaque(false);
        wrapper.add(gridPanel);
        
        add(Box.createVerticalGlue());
        add(wrapper);
        add(Box.createVerticalGlue());
    }

    // Methode zum Hervorheben des aktuellen Kästchens
    private void highlightCurrentCell() {
        // Zurücksetzen aller Zellen-Stile in der aktuellen Zeile
        for (int col = 0; col < 5; col++) {
            if (cells[currentRow][col].getText().isEmpty()) {
                cells[currentRow][col].setBackground(Color.WHITE);
                cells[currentRow][col].setBorder(new LineBorder(new Color(211, 214, 218), 2));
            }
        }
        
        // Aktuelles Kästchen hervorheben, wenn es noch nicht ausgefüllt ist
        if (currentCol < 5 && currentRow < rows) {
            cells[currentRow][currentCol].setBackground(Color.WHITE);
            cells[currentRow][currentCol].setBorder(new LineBorder(new Color(135, 138, 140), 2));
        }
    }

    // Buchstaben hinzufügen
    public void addLetter(String letter) {
        if (currentCol < 5) {
            setLetter(currentRow, currentCol, letter);
            hiddenInputText.append(letter);
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
            
            // Remove last character from internal text representation
            if (hiddenInputText.length() > 0) {
                hiddenInputText.setLength(hiddenInputText.length() - 1);
            }
            
            highlightCurrentCell();

            // Kurze Verzögerung, um schnelles wiederholtes Löschen zu verhindern
            // Verhindert Probleme bei schnellem Tippen
            Timer timer = new Timer(50, e -> isRemovingLetter = false);
            timer.setRepeats(false);
            timer.start();
        }
    }

    // Aktuelle Eingabe zurückgeben
    public String getCurrentInput() {
        return hiddenInputText.toString();
    }

    // Zeile abschließen und zur nächsten übergehen
    public void commitRow() {
        if (currentCol == 5) {
            currentCol = 0;
            currentRow++;
            hiddenInputText.setLength(0);
            
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
            cells[row][col].setBackground(color);
            cells[row][col].setBorder(new LineBorder(color, 2));
            cells[row][col].setForeground(Color.WHITE);
        }
    }

}
