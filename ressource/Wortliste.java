// Wortliste.java
// Stand: 14.05.2025
// Autoren: Lennart und Moritz

package ressource;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;

/*
    Über diese Klasse:
    Die Wortliste-Klasse enthält eine Liste der verfügbaren Wörter, die für das Spiel verwendet werden können.
    Dabei wird ein Wort aus dieser Liste zufällig ausgewählt und in Großbuchstaben zurückgegeben.
    Die Liste ist dabei alphabetisch sortiert und enthält lediglich Wörter mit fünf Buchstaben.
    Die Wörter werden aus der Datei "wordlist.txt" geladen.
*/

public class Wortliste {
    // Liste der verfügbaren Wörter
    private static final String[] WOERTER = loadWordsFromFile();

    // Zufallszahlengenerator für die Auswahl eines zufälligen Wortes
    // final, da das Wort einmalig beim Start der Anwendung ausgewählt wird
    private static final Random RANDOM = new Random();

    // Lädt die Wörter aus der Datei und filtert nach 5-buchstabigen Wörtern
    private static String[] loadWordsFromFile() {
        List<String> wordList = new ArrayList<>();
        
        try {
            // Versuche die Datei im aktuellen Verzeichnis zu öffnen
            File file = new File("wordlist.txt");
            
            // Wenn die Datei nicht im aktuellen Verzeichnis ist, versuche im ressource-Verzeichnis
            if (!file.exists()) {
                file = new File("ressource/wordlist.txt");
            }
            
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            
            while ((line = reader.readLine()) != null) {
                // Trimme die Zeile und überprüfe, ob sie genau 5 Zeichen hat
                line = line.trim();
                if (line.length() == 5) {
                    wordList.add(line);
                }
            }
            
            reader.close();
            
            if (wordList.isEmpty()) {
                System.err.println("Keine 5-buchstabigen Wörter in der wordlist.txt gefunden!");
                // Fallback auf einige Standardwörter
                return new String[]{"HALLO", "BAUCH", "KATZE", "HUNDE", "MAUER"};
            }
            
        } catch (IOException e) {
            System.err.println("Fehler beim Lesen der wordlist.txt: " + e.getMessage());
            // Fallback auf einige Standardwörter
            return new String[]{"HALLO", "BAUCH", "KATZE", "HUNDE", "MAUER"};
        }
        
        // Konvertiere die Liste in ein Array
        return wordList.toArray(new String[0]);
    }

    // Methode getRandomWord(), die das vorher ausgewählte Wort als String zurückgibt
    public static String getRandomWord() {
        // Erstellung eines Index mit einem Wort aus der Liste
        // RANDOM.nextInt wählt eine zufällige Position aus der Liste aus mit dem jeweiligen Wort
        int index = RANDOM.nextInt(WOERTER.length);

        // Rückgabe des Wortes in Großbuchstaben
        return WOERTER[index].toUpperCase();
    }

    public static boolean isInWordList(String input) {
        // Überprüfung, ob das eingegebene Wort in der Liste enthalten ist
        input = input.toUpperCase();
        for (int i = 0; i < WOERTER.length; i++) {
            if (input.equals(WOERTER[i].toUpperCase())) {
                // Wenn das Wort in der Liste enthalten ist, wird true zurückgegeben
                return true;
            }
        }
        return false;
    }
}
