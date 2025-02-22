package src;

import java.util.ArrayList;
import java.util.List;

public class variables {
    public String filePath = ""; // Pfad für die Wortliste
    public String wordlist = ""; // Wortliste
    public List<String> fiveLetterWords = new ArrayList<>(); // Liste für die 5-Buchstaben-Wörter
    public String secretWord = ""; // Geheimes Wort
    public String userInput = ""; // Benutzereingabe
    public boolean gameWon = false; // Spiel gewonnen
    public int attempts = 0; // Versuche
    public int maxAttempts = 6; // Maximale Versuche
}