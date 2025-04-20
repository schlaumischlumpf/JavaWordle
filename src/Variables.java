package src;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Variables {
    public static String currentTarget = null; // Zielwort
    public static Random random = new Random(); // Zufallszahlengenerator
    public String filePath = ""; // Pfad für die Wortliste
    public static List<String> fiveLetterWords = new ArrayList<>(); // Liste für die 5-Buchstaben-Wörter
    public String secretWord = ""; // Geheimes Wort
    public String userInput = ""; // Benutzereingabe
    public boolean gameWon = false; // Spiel gewonnen
    public int attempts = 0; // Versuche
    public int maxAttempts = 6; // Maximale Versuche
    public static boolean debugMode = false; // Debug-Modus
    public static boolean disableDuplicateLetters = false; // Doppelte Buchstaben deaktivieren
    public int gameType = 1; // Spielmodus; 1 = normale Schwierigkeit (6 Versuche, das Wort zu erraten), 2 = erhöhte Schwierigkeit (4 Versuche, das Wort zu erraten, 3 = In Planung

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