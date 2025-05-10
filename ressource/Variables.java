// Variables.java
// Stand: 22.04.2025
// Autoren: Lennart und Moritz

package ressource;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
    Über diese Klasse:
    Die Wortliste-Klasse enthält eine Liste der verfügbaren Wörter, die für das Spiel verwendet werden können.
    Dabei wird ein Wort aus dieser Liste zufällig ausgewählt und in Großbuchstaben zurückgegeben.
    Die Liste ist dabei alphabetisch sortiert und enthält lediglich Wörter mit fünf Buchstaben.
*/

public class Variables {
    public static String currentTarget = null; // Zielwort
    public static Random random = new Random(); // Zufallszahlengenerator
    public static List<String> fiveLetterWords = new ArrayList<>(); // Liste für die 5-Buchstaben-Wörter
    public static boolean debugMode = false; // Debug-Modus
    public static int gameType = 1; // Spielmodus; 1 = normale Schwierigkeit (6 Versuche, das Wort zu erraten), 2 = erhöhte Schwierigkeit (4 Versuche, das Wort zu erraten, 3 = In Planung
    public static int timerSeconds = 210; // Timer in Sekunden; Standardwert 210 Sekunden, also 3:30 Minuten
    public String filePath = ""; // Pfad für die Wortliste
    public String secretWord = ""; // Geheimes Wort
    public String userInput = ""; // Benutzereingabe
    public boolean gameWon = false; // Spiel gewonnen
    public int attempts = 0; // Versuche
    public int maxAttempts = 6; // Maximale Versuche
    // Speichert das aktuell ausgewählte Theme
    public static String currentTheme = "light"; // Standardmäßig helles Theme



    public static String getTargetWord() {
        if (debugMode) {
            return "busen";
        }

        // Falls kein Debug-Modus wähle ein zufälliges Wort aus der Liste
        if (currentTarget == null && !fiveLetterWords.isEmpty()) {
            int index = random.nextInt(fiveLetterWords.size());
            currentTarget = fiveLetterWords.get(index);
        }

        return currentTarget;
    }

    /**
     * Setzt das Zielwort zurück, damit bei jedem neuen Spiel ein neues Wort gewählt wird
     */
    public static void resetTargetWord() {
        currentTarget = null;
    }
}