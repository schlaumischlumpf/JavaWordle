import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.List;
import javax.swing.*;

/*
 * WordleGame.java von Moritz und Lennart

 * Kurze Beschreibung, was das Programm macht:
 * Dieses Programm ist ein einfaches Worträtsel, bei dem der Benutzer ein 5-Buchstaben-Wort erraten muss.
 * Dabei wird das Wort in einem 6 x 5-Grid dargestellt, in dem der Benutzer seine Vermutungen eingeben kann.
 * Für einen Buchstaben an der richtigen Stelle wird die Zelle grün markiert, für einen Buchstaben an der falschen Stelle gelb und für einen Buchstaben, der nicht im Wort enthalten ist, rot.
 * Sollte der Benutzer das Wort nicht erraten, wird ihm das Wort angezeigt und er kann entscheiden, ob er ein neues Spiel starten oder das Spiel beenden möchte.
 * Der Benutzer kann auch ein eigenes 5-Buchstaben-Wort eingeben, falls die Wortliste keine 5-Buchstaben-Wörter enthält oder Fehler beim Laden der Wörter auftreten.
 * Das Spiel kann in zwei Schwierigkeitsgraden gespielt werden: einem normalen Schwierigkeitsgrad (klassisches Wordle) und erhöhtem Schwierigkeitsgrad (bspw. weniger Versuche).
 * Das Spiel speichert die beste Zeit, die benötigt wurde, um das Wort zu erraten, die Anzahl der gespielten Spiele und die Anzahl der Versuche, die der Benutzer benötigt hat.
 * Diese Informationen werden dem Benutzer am Ende des Spiels angezeigt, wenn er das Wort erraten hat oder alle Versuche aufgebraucht sind. Jedoch werden die Informationen nicht gespeichert, wenn das Programm beendet wird.
 */

public class WordleGame {

    public static String getFilePath() { // Methode, um den Pfad zur Datei mit den Wörtern zu erhalten
        return filePath; // Der Pfad zur Datei mit den Wörtern wird zurückgegeben
    }

    int gametype; // 1 = normaler Schwierigkeits, 2 = erhöhter Schwierigkeitsgrad, 3 = Challenge-Modus (noch nicht implementiert; Idee für zukünftige Erweiterungen)
    int AnzahlSpiele; // Counter, der die Anzahl der gespielten Spiele zählt
    long besteZeit = Long.MAX_VALUE; // Variable für die beste Zeit, die benötigt wurde, um das Wort zu erraten
    boolean Debug; // Boolean um zu überprüfen ob der Debugmodus an ist

    // Liste der Wörter für das Spiel (initial leer, wird aus Datei geladen)
    private static List<String> WORD_LIST = new ArrayList<>();

    private final Set<Character> usedLetters = new HashSet<>(); // Set für die verwendeten Buchstaben
    private boolean allowRepeatedLetters = true; // Einstellung, ob wiederholte Buchstaben erlaubt sind

    // Das geheime Wort (wird intern zur Verarbeitung in Großbuchstaben verwendet)
    private String secretWord; // Das geheime Wort, das der Spieler erraten muss

    // Konstanten für die Anzahl der Spalten im Spielfeld
    private static final int COLUMNS = 5; // Anzahl der Spalten

    private int rows = 6; // Standardmäßig 6 Zeilen für das Spielfeld

    // Aktuelle Reihe, in der der Spieler sich befindet
    private int currentRow = 0;

    // Grid für das Spielfeld
    private final JTextField[][] grid = new JTextField[rows][COLUMNS];

    // Eingabefeld für die Benutzereingabe
    private final JTextField inputField = new JTextField(); // Eingabefeld für die Benutzereingabe; der Benutzer kann hier sein Wort eingeben und mit der Eingabetaste bestätigen/auf den "Absenden"-Button klicken

    // JFrame für die grafische Benutzeroberfläche
    private JFrame frame;
    
    // Zeitpunkt, zu dem das Spiel gestartet wurde
    private Instant startTime;

    // Pfad zur Datei mit den Wörtern
    private static final String filePath = "Wortliste.txt"; // Pfad bitte genau anpassen, damit das Programm immer reibungslos funktioniert (z.B. C:/Users/Name/Desktop/Wortliste.txt)
        
    // JFrame für das Hauptmenü
    private JFrame mainMenuFrame;
        
    // Konstruktor für das Spiel
    public WordleGame() {
        // Zeige das Hauptmenü beim Start der Anwendung
        showMainMenu();
    }
        
    // Zeigt das Hauptmenü
    private void showMainMenu() { // Methode, um das Hauptmenü anzuzeigen
        mainMenuFrame = new JFrame("Hauptmenü"); // Titel des Hauptmenü-Fensters wird auf "Hauptmenü" gesetzt
        mainMenuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Das Fenster wird geschlossen, wenn der "X"-Button oben rechts gedrückt wird
        mainMenuFrame.setSize(300, 300); // Größe des Fensters wird auf 300 x 300 Pixel gesetzt
        mainMenuFrame.setLayout(new GridLayout(5, 1, 10, 10)); // Layout des Fensters wird auf ein 5 x 1-Grid/Feld mit 10 Pixel Abstand zwischen den Zellen gesetzt
    
        JButton mode1Button = new JButton("Modus 1: Wordle (normale Schwierigkeit)"); // Button für den normalen Schwierigkeitsgrad (klassisches Wordle)
        JButton mode2Button = new JButton("Modus 2: Wordle (erhöhte Schwierigkeit)"); // Button für den erhöhten Schwierigkeitsgrad (weniger Versuche)
        JButton mode3Button = new JButton("Modus 3: Noch nicht implementiert"); // Button für den Challenge-Modus (noch nicht implementiert; folgt demnächst)
        JButton settingsButton = new JButton("Einstellungen");
    
        mode1Button.addActionListener(e -> startWordleGame()); // ActionListener für den Button "Modus 1: Wordle (normale Schwierigkeit)" hinzufügen, um das normale Wordle-Spiel zu starten
        mode2Button.addActionListener(e -> startWordleGame_2()); // ActionListener für den Button "Modus 2: Wordle (erhöhte Schwierigkeit)" hinzufügen, um das Wordle-Spiel mit erhöhter Schwierigkeit zu starten
        mode3Button.addActionListener(e -> JOptionPane.showMessageDialog(mainMenuFrame, "Modus 3 ist noch nicht implementiert. Bitte wählen Sie einen anderen Modus.")); // ActionListener für den Button "Modus 3: Noch nicht implementiert" hinzufügen, um eine Meldung anzuzeigen, dass der Challenge-Modus noch nicht implementiert ist
        settingsButton.addActionListener(e -> showSettingsMenu()); // ActionListener für den Button "Einstellungen" hinzufügen, um das Einstellungsmenü anzuzeigen
    
        mainMenuFrame.add(mode1Button); // Button "Modus 1: Wordle (normale Schwierigkeit)" wird dem Hauptmenü hinzugefügt
        mainMenuFrame.add(mode2Button); // Button "Modus 2: Wordle (erhöhte Schwierigkeit)" wird dem Hauptmenü hinzugefügt
        mainMenuFrame.add(mode3Button); // Button "Modus 3: Noch nicht implementiert" wird dem Hauptmenü hinzugefügt
        mainMenuFrame.add(settingsButton); // Button "Einstellungen" wird dem Hauptmenü hinzugefügt
    
        mainMenuFrame.setVisible(true); // Das Hauptmenü wird sichtbar gemacht
    }

    // Zeigt das Einstellungsmenü
    private void showSettingsMenu() {
        JFrame settingsFrame = new JFrame("Einstellungen"); // Titel des Einstellungsmenü-Fensters wird auf "Einstellungen" gesetzt
        settingsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Das Fenster wird geschlossen, wenn der "X"-Button oben rechts gedrückt wird
        settingsFrame.setSize(300, 200); // Größe des Fensters wird auf 300 x 200 Pixel gesetzt
        settingsFrame.setLayout(new GridLayout(2, 1, 10, 10)); // Layout des Fensters wird auf ein 2 x 1-Grid/Feld mit 10 Pixel Abstand zwischen den Zellen gesetzt
    
        JCheckBox allowRepeatedLettersCheckBox = new JCheckBox("Wiederholte Buchstaben erlauben", allowRepeatedLetters); // Checkbox, um wiederholte Buchstaben zu erlauben
        allowRepeatedLettersCheckBox.addItemListener(e -> allowRepeatedLetters = allowRepeatedLettersCheckBox.isSelected()); // ItemListener für die Checkbox hinzufügen, um zu prüfen, ob wiederholte Buchstaben erlaubt sind
        JCheckBox DebugCheckBox = new JCheckBox("Debug Modus", Debug); // Checkbox, um wiederholte Buchstaben zu erlauben
        DebugCheckBox.addItemListener(e -> Debug = DebugCheckBox.isSelected()); // Überprüfung, ob der Debugmodus aktiviert oder deaktiviert ist
    
        settingsFrame.add(allowRepeatedLettersCheckBox); // Checkbox wird dem Einstellungsmenü hinzugefügt, um wiederholte Buchstaben zu erlauben/deaktivieren
        settingsFrame.add(DebugCheckBox); // Checkbox wird dem Einstellungsmenü hinzugefügt, um den Debugmodus zu aktivieren/deaktivieren
    
        settingsFrame.setVisible(true); // Das Einstellungsmenü wird sichtbar gemacht
    }

    // Methode zum Starten des Wordle-Spiels
    private void startWordleGame() {
        // Schließe das Hauptmenü
        mainMenuFrame.dispose(); // Das sichtbare Haupmenü-Fenster wird geschlossen
        gametype = 1; // Das normale Wordle wird gestartet, daher wird der Schwierigkeitsgrad auf 1 gesetzt
        
        // Starte das Wordle-Spiel
        initializeGame();
    }

    private void startWordleGame_2() { // Methode, um das Wordle-Spiel mit erhöhter Schwierigkeit zu starten
        mainMenuFrame.dispose(); // Das sichtbare Haupmenü-Fenster wird geschlossen
        gametype = 2; // Der Schwierigkeitsgrad wird auf 2 gesetzt, um das Wordle mit erhöhter Schwierigkeit auszuführen
        rows = 4; // Die Anzahl der Zeilen wird auf 4 gesetzt, um die Anzahl der Versuche zu reduzieren, was die erhöhte Schwierigkeit ausmacht
        initializeGame();
    }

    /*
     * Die Methode, um den dritten Modus - also den Challenge-Modus - zu starten, wurde zum jetztigen Zeitpunkt noch nicht implementiert, soll aber demnächst folgen.
     */
    
    // Initialisierung des Spiels
    private void initializeGame() { // Methode die das Spiel initialisiert
        loadWordsFromFile();  // Laden der Wörter aus der Wörterliste
        if (!WORD_LIST.isEmpty())  { // if-Fallunterscheidung, die prüft, ob die Wortliste nicht leer ist
            secretWord = WORD_LIST.get(new Random().nextInt(WORD_LIST.size())).toUpperCase(); // sollte die Wortliste nicht leer sein, wird ein zufälliges Wort aus der Liste ausgewählt und in Großbuchstaben umgewandelt
        }
        currentRow = 0; // der aktuelle Spielzug wird auf 0 gesetzt, das heißt, dass der Spieler noch keinen Zug gemacht hat und sich in der ersten Zeile befindet.

        // Erstellen und Einrichten des Hauptfensters für das Spiel
        if (frame == null) { // if-Fallunterscheidung, die prüft, ob das Fenster noch nicht erstellt wurde
            frame = new JFrame("Wordle Spiel"); // Titel des nun angezeigten Fensters wird auf "Wordle Spiel" gesetzt
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Das Fenster wird geschlossen, sollte der "X"-Button oben rechts gedrückt werden
            frame.setSize(500, 700); // Größe des Fensters wird auf 500 x 700 Pixel gesetzt

            JPanel mainPanel = new JPanel(); // Erstellen eines neuen Panels, das als Hauptpanel für das Fenster dient
            mainPanel.setLayout(new BorderLayout(10, 10)); // Layout des Hauptpanels wird auf ein BorderLayout mit 10 Pixel Abstand zwischen den Zellen gesetzt
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Ein leerer Rand wird um das Hauptpanel erstellt

            // Grid-Panel für das Spielfeld
            JPanel gridPanel = new JPanel(); // Erstellen eines neuen Panels, das als Grid-Panel für das Spielfeld dient
            gridPanel.setLayout(new GridLayout(rows, COLUMNS, 10, 10)); // Das Layout des Grid-Panels wird auf ein 6 x 5-Grid/Feld mit 10 Pixel Abstand zwischen den Zellen gesetzt

            // Initialisieren des Grids
            for (int i = 0; i < rows; i++) { // Zählschleife, die die Anzahl der Reihen durchläuft
                for (int j = 0; j < COLUMNS; j++) { // Zählschleife zählt die Anzahl der Spalten durch
                    grid[i][j] = new JTextField(); // Ein neues JTextField wird für das Grid/Feld erstellt
                    grid[i][j].setEditable(false); // Das JTextField wird auf "nicht-editierbar" gesetzt, da man nicht in das Feld schreiben können soll
                    grid[i][j].setHorizontalAlignment(JTextField.CENTER); // Der Text im JTextField wird horizontal zentriert
                    grid[i][j].setFont(new Font("Arial", Font.BOLD, 18)); // Die Schriftart wird auf "Arial" gesetzt, mit der Schriftgröße 18pt und in Fett
                    grid[i][j].setFocusable(false); // Das JTextField wird auf "nicht-fokussierbar" gesetzt, da es nicht fokussiert werden soll
                    grid[i][j].setBackground(Color.WHITE); // Die Hintergrundfarbe des Feldes wird auf Weiß gesetzt
                    grid[i][j].setText(""); // Der Text im Feld wird auf leer gesetzt
                    gridPanel.add(grid[i][j]); // Das JTextField wird dem Grid-Panel hinzugefügt
                }
            }

            // Panel für die Benutzereingabe
            JPanel inputPanel = new JPanel(); // Erstellen eines neuen Panels, das als Eingabepanel für den Benutzer dient
            inputPanel.setLayout(new BorderLayout(10, 10)); // Das Layout des Eingabepanels wird auf ein BorderLayout mit 10 Pixel Abstand zwischen den Zellen gesetzt

            inputField.setFont(new Font("Arial", Font.PLAIN, 18)); // Die Schriftart des Eingabefeldes wird auf "Arial" gesetzt, mit der Schriftgröße 18pt
            inputPanel.add(inputField, BorderLayout.CENTER); // Das Eingabefeld wird dem Eingabepanel hinzugefügt; es wird in der Mitte des Panels platziert

            JButton submitButton = new JButton("Eingabe"); // Erstellen eines neuen Buttons mit der Beschriftung "Eingabe"
            inputPanel.add(submitButton, BorderLayout.EAST); // Der Button wird dem Eingabepanel hinzugefügt; er wird rechts im Panel platziert

            mainPanel.add(gridPanel, BorderLayout.CENTER); // Das Grid/Feld-Panel wird dem Hauptpanel hinzugefügt und in die Mitte des Panels platziert
            mainPanel.add(inputPanel, BorderLayout.SOUTH); // Das Eingabepanel für das Wort wird dem Hauptpanel hinzugefügt und unten im Panel platziert

            frame.add(mainPanel); // Das Hauptpanel wird dem Fenster hinzugefügt

            // ActionListener für den Submit-Button hinzufügen
            submitButton.addActionListener(e -> handleSubmit()); // ActionListener für den Submit-Button hinzufügen, um die Eingabe des Benutzers zu verarbeiten, wenn der Button gedrückt wird

            // KeyListener für die Eingabetaste hinzufügen
            inputField.addKeyListener(new KeyAdapter() { // KeyListener soll genutzt werden, um die Eingabe des Benutzers zu verarbeiten, sobald er die Eingabetaste drückt
                @Override
                public void keyPressed(KeyEvent e) { // Methode, die prüft, ob eine Taste gedrückt wurde
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) { // if-Fallunterscheidung, die prüft, ob die Eingabetaste gedrückt wurde
                        handleSubmit(); // wenn die Eingabetaste gedrückt wurde, wird die Methode "handleSubmit()" aufgerufen, um die Eingabe des Benutzers zu verarbeiten
                    }
                }
            });
        } else { // sollte die Wortliste leer sein, wird eine Meldung angezeigt, dass keine Wörter gefunden wurden
            // Zurücksetzen des Grids und des Eingabefeldes für ein neues Spiel
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < COLUMNS; j++) { // Zählschleife, die die Anzahl der Spalten durchläuft
                    grid[i][j].setText(""); // Der Text im Feld wird auf "leer" gesetzt
                    grid[i][j].setBackground(Color.WHITE); // Die Hintergrundfarbe des Feldes wird auf Weiß gesetzt
                }
            }
            inputField.setText(""); // Das Eingabefeld für den Benutzer wird auf "leer" gesetzt
        }

        // Fenster sichtbar machen
        frame.setVisible(true);
        startTime = Instant.now(); // Startzeit des Spiels setzen; wird benötigt, um die Dauer des Spiels zu berechnen
    }

    // Behandlung der Eingabe des Benutzers
    private void handleSubmit() {
        String guess = inputField.getText().toUpperCase(); // Eingabe des Benutzers wird in Großbuchstaben umgewandelt, dmait die Eingabe nicht auf Groß- und Kleinschreibung ankommt
        
        if (Debug == true)  { // Überprüfung, ob der Debugmodus aktiviert ist
            if (guess.toUpperCase().equals("DEBUG"))    { // Überprüfung, ob der Benutzer "DEBUG" eingegeben hat
                JOptionPane.showMessageDialog(frame, "Das Wort lautet: " + secretWord + "\nBereits geraten wurden: " + usedLetters); // Meldung, die das geheime Wort und die bereits geratenen Buchstaben anzeigt
                inputField.setText(""); // Eingabefeld wird geleert
                return;
            }
        }

        // Überprüfung, ob das eingegebene Wort in der Wortliste enthalten ist
        if (!WORD_LIST.contains(guess.toLowerCase())) {
            JOptionPane.showMessageDialog(frame, "Das eingegebene Wort befindet sich nicht in der Wortliste. Bitte versuche es erneut mit einem anderen Wort."); // Meldung, dass das eingegebene Wort nicht in der Wortliste enthalten ist
            return; // Die Methode wird beendet, wenn das eingegebene Wort nicht in der Wortliste enthalten ist
        }
    
        // Prüfen, ob die Eingabe die richtige Länge hat
        if (guess.length() != COLUMNS) {
            JOptionPane.showMessageDialog(frame, "Bitte gib ein Wort mit fünf Buchstaben ein (keine Umlaute/Sonderlaute)."); // Meldung, dass das eingegebene Wort fünf Buchstaben lang sein muss und keine Umlaute/Sonderlaute enthalten darf
            return; // Die Methode wird beendet, wenn das eingegebene Wort nicht fünf Buchstaben lang ist oder Umlaute/Sonderlaute enthält
        }
    
        // Überprüfen, ob alle Zeichen im Wort Buchstaben sind
        for (int i = 0; i < guess.length(); i++) {
            if (gametype == 2) { // Überprüfung, ob der erhöhte Schwierigkeitsgrad aktiviert ist
                if (!guess.contains("A") && !guess.contains("E") && !guess.contains("I") && // Überprüfung, ob das Wort einen Vokal enthält
                    !guess.contains("O") && !guess.contains("U")) { // Überprüfung, ob das Wort einen Vokal enthält
                    JOptionPane.showMessageDialog(frame, "Das Wort muss einen Vokal enthalten."); // Meldung, dass das Wort einen Vokal enthalten muss
                    return; // Die Methode wird beendet, wenn das Wort keinen Vokal enthält
                }
            }
            if (!Character.isLetter(guess.charAt(i))) { // Überprüfung, ob das Wort nur Buchstaben enthält
                JOptionPane.showMessageDialog(frame, "Das Wort darf nur Buchstaben enthalten."); // Meldung, dass das Wort nur Buchstaben enthalten darf
                return; // Die Methode wird beendet, wenn das Wort keine Buchstaben enthält
            }
            if (guess.charAt(i) == 'Ä' || guess.charAt(i) == 'Ö' || // Überprüfung, ob das Wort Umlaute/Sonderlaute enthält
                guess.charAt(i) == 'Ü' || guess.charAt(i) == 'ß') { // Überprüfung, ob das Wort Umlaute/Sonderlaute enthält
                JOptionPane.showMessageDialog(frame, "Das Wort darf keine Umlaute enthalten."); // Meldung, dass das Wort keine Umlaute/Sonderlaute enthalten darf
                return;
            }
            if ((!allowRepeatedLetters || gametype == 2) && usedLetters.contains(guess.charAt(i))) { // Überprüfung, ob wiederholte Buchstaben erlaubt sind und ob der Buchstabe bereits verwendet wurde; zusätzlich wird überprüft, ob der erhöhte Schwierigkeitsgrad aktiviert ist
                JOptionPane.showMessageDialog(frame, "Der Buchstabe '" + guess.charAt(i) + "' wurde bereits verwendet. Bitte versuche es mit einem anderen Buchstaben."); // Meldung, dass der Buchstabe bereits verwendet wurde und ein anderer Buchstabe eingegeben werden soll
                return; // Die Methode wird beendet, wenn der Buchstabe bereits verwendet wurde und ein anderer Buchstabe eingegeben werden soll
            }
        }
    
        // Wenn die Eingabe gültig ist, wird das Wort weiterverarbeitet
        processGuess(guess); // Die Methode "processGuess()" wird aufgerufen, um die Eingabe des Benutzers zu verarbeiten
        inputField.setText(""); // Das Eingabefeld für den Benutzer wird auf "leer" gesetzt	
    }

    private void processGuess(String guess) { // Methode, um die Eingabe des Benutzers zu verarbeiten
        if (currentRow >= rows) { // Überprüfung, ob der Spieler alle Versuche aufgebraucht hat
            endGame("Das Spiel ist beendet! Du hast alle Versuche aufgebraucht!"); // Meldung, dass das Spiel beendet ist, da der Spieler alle Versuche aufgebraucht hat
            return; // Die Methode wird beendet, wenn der Spieler alle Versuche aufgebraucht hat
        }
    
        boolean[] correctPositions = new boolean[COLUMNS]; // Array, um die korrekten Positionen der Buchstaben zu speichern
        boolean[] usedInSecret = new boolean[COLUMNS]; // Array, um die Buchstaben zu speichern, die bereits im geheimen Wort verwendet wurden
    
        for (int i = 0; i < COLUMNS; i++) { // Zählschleife, die die Anzahl der Spalten durchläuft
            char guessChar = guess.charAt(i); // Der Buchstabe, den der Benutzer eingegeben hat, wird in "guessChar" gespeichert
            if (secretWord.charAt(i) == guessChar) { // Überprüfung, ob der Buchstabe an der richtigen Position ist
                correctPositions[i] = true; // Der Buchstabe ist an der richtigen Position
                usedInSecret[i] = true; // Der Buchstabe wurde im geheimen Wort verwendet
            }
        }
    
        for (int i = 0; i < COLUMNS; i++) { // Zählschleife, die die Anzahl der Spalten durchläuft
            char guessChar = guess.charAt(i); // Der Buchstabe, den der Benutzer eingegeben hat, wird in "guessChar" gespeichert
            JTextField cell = grid[currentRow][i]; // Das JTextField in der aktuellen Zeile und Spalte wird in "cell" gespeichert
            cell.setText(String.valueOf(guessChar)); // Der Buchstabe wird im JTextField angezeigt
    
            Color richtig = new Color(63, 186, 41); // Farbe für den Buchstaben an der richtigen Stelle
            Color falsch = new Color(242, 93, 56); // Farbe für den Buchstaben an der falschen Stelle
            Color falscheStelle = new Color(250, 206, 10); // Farbe für den Buchstaben, der nicht im Wort enthalten ist
    
            if (correctPositions[i]) { // Überprüfung, ob der Buchstabe an der richtigen Stelle ist
                cell.setBackground(richtig); // Der Buchstabe wird grün markiert
            } else {  // Überprüfung, ob der Buchstabe an der falschen Stelle ist
                boolean found = false; // Variable, die prüft, ob der Buchstabe an einer falschen Stelle ist
                for (int j = 0; j < COLUMNS; j++) { // Zählschleife, die die Anzahl der Spalten durchläuft
                    if (!usedInSecret[j] && secretWord.charAt(j) == guessChar) { // Überprüfung, ob der Buchstabe an einer falschen Stelle ist
                        found = true; // Der Buchstabe ist an einer falschen Stelle
                        usedInSecret[j] = true; // Der Buchstabe wurde im geheimen Wort verwendet
                        break; // Die Schleife wird beendet, wenn der Buchstabe an einer falschen Stelle ist
                    }
                }
                if (found) { // Überprüfung, ob der Buchstabe an einer falschen Stelle ist
                    cell.setBackground(falscheStelle); // Der Buchstabe wird gelb markiert
                } else { // Überprüfung, ob der Buchstabe nicht im Wort enthalten ist
                    cell.setBackground(falsch); // Der Buchstabe wird rot markiert
                    usedLetters.add(guessChar); // Buchstabe zur Liste der verwendeten Buchstaben hinzufügen
                }
            }
        }
    
        if (guess.equals(secretWord)) {
            endGame("Herzlichen Glückwunsch! Du hast das Wort erraten."); // Meldung, dass das Wort erraten wurde
            return; // Die Methode wird beendet, wenn das Wort erraten wurde
        }
    
        currentRow++; // Der aktuelle Spielzug wird inkrementiert
    
        if (currentRow == rows) { // Überprüfung, ob der Spieler alle Versuche aufgebraucht hat
            endGame("Das Spiel ist beendet! Das gesuchte Wort war: " + formatWord(secretWord)); // Meldung, dass das Spiel beendet ist und das gesuchte Wort angezeigt wird
        }
    }

    // Beenden des Spiels und Anzeige einer Meldung
    private void endGame(String message) {
        Instant endTime = Instant.now(); // Benötigte Zeit wird gestoppt
        Duration duration = Duration.between(startTime, endTime); // Berechnung der Spieldauer
        long seconds = duration.getSeconds(); // Umwandlung der Dauer in Sekunden
        if (besteZeit > seconds && Debug == false) { // Prüfen, ob die benötigte Zeit besser ist als die bisher beste Zeit
            besteZeit = seconds; // Wenn ja, wird die benötigte Zeit wird als beste Zeit gespeichert
        }

        if (Debug == false) {
            AnzahlSpiele++; // Die Anzahl der gespielten Spiele wird um 1 inkrementiert
        }

        int response = JOptionPane.showConfirmDialog(frame, message + "\nBenötigte Zeit: " + seconds + " Sekunden.\nDeine beste Zeit war: " + besteZeit + " Sekunden\nDu hast bereits " + AnzahlSpiele + " Spiele gespielt.\nMöchtest du erneut spielen?", "Spielende", JOptionPane.YES_NO_OPTION); // Meldung, die anzeigt, ob das Spiel gewonnen oder verloren wurde, die benötigte Zeit, die beste Zeit, die Anzahl der gespielten Spiele und die Option, ein neues Spiel zu starten
        if (response == JOptionPane.YES_OPTION && Debug == false) { // Prüfen, ob der Spieler ein neues Spiel starten möchte oder die Anwendung beenden möchte
            initializeGame(); // Neues Spiel starten
        } else {
            if (response == JOptionPane.YES_OPTION && Debug == true)   {
                showMainMenu();
                frame.dispose();
            } else  {
                System.exit(0); // Anwendung beenden, wenn der Spieler kein neues Spiel starten möchte
            }
        }
    }

    // Methode zur Formatierung eines Wortes (erster Buchstabe groß, Rest klein)
    private String formatWord(String word) {
        return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase(); // Methode, die das Wort formatiert; der erste Buchstabe wird groß geschrieben, der Rest wird klein geschrieben
    }

    // Methode zum Laden der Wörter aus einer Datei
    private void loadWordsFromFile() {
        try {
            // Datei in String umwandeln
            Path path = Paths.get(filePath); // Pfad zur Datei wird in path gespeichert
            if (Files.exists(path) && Files.isReadable(path))  { // Prüfen, ob die Datei existiert und lesbar ist

                String content = new String(Files.readAllBytes(Paths.get(filePath))); // Inhalt der Datei wird in "content" gespeichert
                
                // Alle Sonderzeichen ersetzen
                content = content.replace("ö", "oe") // Sonderzeichen werden ersetzt, um die Wörter zu normalisieren
                                 .replace("Ö", "Oe") // Sonderzeichen werden ersetzt, um die Wörter zu normalisieren
                                 .replace("ä", "ae") // Sonderzeichen werden ersetzt, um die Wörter zu normalisieren
                                 .replace("Ä", "Ae") // Sonderzeichen werden ersetzt, um die Wörter zu normalisieren
                                 .replace("ü", "ue") // Sonderzeichen werden ersetzt, um die Wörter zu normalisieren
                                 .replace("Ü", "Ue") // Sonderzeichen werden ersetzt, um die Wörter zu normalisieren
                                 .replace("ß", "ss"); // Sonderzeichen werden ersetzt, um die Wörter zu normalisieren

                // Alle Wörter extrahieren
                String[] words = content.split("\\W+"); // Wörter werden aus dem Inhalt extrahiert und in "words" gespeichert

                // Liste für die 5-Buchstaben-Wörter
                List<String> fiveLetterWords = new ArrayList<>(); // Liste für die 5-Buchstaben-Wörter wird erstellt

                // Überprüfen, ob jedes Wort fünf Buchstaben hat und es der Liste hinzufügen
                for (String word : words) { // Durchlaufen aller Wörter in der Wortliste
                    // Umwandlung in Kleinbuchstaben
                    word = word.toLowerCase(); 

                    // Wenn das Wort 5 Buchstaben hat, füge es zur Liste hinzu
                    if (word.length() == 5) {
                        fiveLetterWords.add(word);
                    }
                }
                // Wenn keine 5-Buchstaben-Wörter gefunden wurden, wird die Liste manuell gefüllt
                if (fiveLetterWords.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Die geladene Wortliste enthält keine 5-Buchstaben-Wörter."); // Meldung, dass keine 5-Buchstaben-Wörter gefunden wurden
                    secretWord = promptForValidWord().toUpperCase(); // Benutzer wird aufgefordert, ein gültiges 5-Buchstaben-Wort einzugeben
                } else {
                    WORD_LIST = fiveLetterWords; // Die Liste der 5-Buchstaben-Wörter wird in die Wortliste gespeichert; in dem Fall das gewählte Wort
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Die geladene Wortliste enthält keine 5-Buchstaben-Wörter.");
                secretWord = promptForValidWord().toUpperCase(); // Benutzer wird aufgefordert, ein gültiges 5-Buchstaben-Wort einzugeben, wenn die Datei nicht existiert oder nicht lesbar ist, bzw. keine 5-Buchstaben-Wörter enthält
            }

        } catch (IOException e) {
            e.printStackTrace(); // Fehlermeldung wird ausgegeben, sollte ein Fehler beim Laden der Wörter aus der Datei auftreten
        }
    }

    // Benutzer auffordern, ein gültiges Wort einzugeben
    private String promptForValidWord() {
        String userWord = ""; // Variable für das Wort, das der Benutzer eingibt
        while (userWord.length() != 5) { // Schleife, die prüft, ob das Wort, welches der Benutzer eingibt, fünf Buchstaben lang ist
            userWord = JOptionPane.showInputDialog(frame, "Bitte gib ein gültiges 5-Buchstaben-Wort ein. Dieses Wort wird dann genutzt:"); // Benutzer wird aufgefordert, ein gültiges 5-Buchstaben-Wort einzugeben
            if (userWord != null && userWord.length() == 5 && !userWord.contains("Ä") && !userWord.contains("ä") && !userWord.contains("Ö") && !userWord.contains("ö") && !userWord.contains("Ü") && !userWord.contains("ü")) { // Prüfen, ob das Wort fünf Buchstaben lang ist und keine Umlaute/Sonderlaute enthält
                break; // Die Schleife wird beendet, wenn das Wort fünf Buchstaben lang ist und keine Umlaute/Sonderlaute enthält
            } else {
                JOptionPane.showMessageDialog(frame, "Das Wort muss fünf (5) Buchstaben lang sein."); // Meldung, dass das Wort fünf Buchstaben lang sein muss
            }
        }
        return userWord; // Das vom Benutzer eingegebene Wort wird zurückgegeben
    }

    // Hauptmethode zum Starten der Anwendung
    public static void main(String[] args) {
        SwingUtilities.invokeLater(WordleGame::new); // Die Anwendung wird gestartet, indem ein neues WordleGame-Objekt erstellt wird
    }
}