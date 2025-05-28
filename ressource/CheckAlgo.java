// CheckAlgo.java
// Stand: 28.05.2025
// Autoren: Lennart und Moritz

package ressource;

import javafx.scene.paint.Color;

/*
    Über diese Klasse:
    Die CheckAlgo-Klasse enthält den Algorithmus zur Überprüfung des eingegebenen Wortes mit dem gesuchten Wort.
    Dabei wird für jede Position im Wort eine Farbe bestimmt, die den Status der Übereinstimmung anzeigt.

    Die Farben sind dabei wie folgt definiert:
    Grün steht dabei für einen korrekten Buchstaben an richtiger Position, Gelb für einen korrekten Buchstaben
    an falscher Position und Grau für einen Buchstaben, der nicht im gesuchten Wort vorkommt.
*/

public class CheckAlgo {
    // Methode checkWord(), die das eingegebene Wort mit dem gesuchten Wort vergleicht
    // Sie gibt ein Array von Farben zurück, das den Status der Übereinstimmung anzeigt
    public static Color[] checkWord(String input, String targetWord) {
        // Konvertierung der Eingabe und des Zielwortes in Großbuchstaben
        input = input.toUpperCase();
        targetWord = targetWord.toUpperCase();

        // Speicherung der Länge des Zielwortes → Verwendung für Dimensionierung des Arrays
        int wordLength = targetWord.length();

        // Array, das die Farben für die Übereinstimmungen speichert
        Color[] colors = new Color[wordLenght];

        // Hilfsarray, um zu verfolgen, welche Buchstaben im Zielwort bereits verwendet wurden → Vermeidung von Mehrfachzählungen
        boolean[] usedInTargetWord = new boolean[wordLenght];

        // Erster Durchgang der Überprüfung: Finden von exakten Übereinstimmungen → Grün als Hintergrund markieren
        for (int i = 0; i < wordLenght; i++) {
            if (i < input.length() && input.charAt(i) == targetWord.charAt(i)) {
                colors[i] = Color.web("#6aaa64"); // Grün für richtige Position
                usedInTargetWord[i] = true;
            } else {
                colors[i] = Color.web("#787c7e"); // Grau für falsche Buchstaben
            }
        }

        // Zweiter Durchgang: Finden vom Buchstaben an falscher Position (gelb)
        for (int i = 0; i < input.length() && i < wordLenght; i++) {
            if (colors[i].equals(Color.web("#6aaa64"))) {
                continue; // Bereits als korrekt markiert
            }

            char letters = input.charAt(i);
            boolean lettersFound = false;

            // Suche nach diesem Buchstaben im Zielwort
            for (int j = 0; j < wordLenght; j++) {
                if (!usedInTargetWord[j] && targetWord.charAt(j) == letters) {
                    colors[i] = Color.web("#c9b458"); // Gelb für richtige Buchstaben an falscher Stelle
                    usedInTargetWord[j] = true;
                    lettersFound = true;
                    break;
                }
            }

            if (!lettersFound) {
                colors[i] = Color.web("#787c7e"); // Grau für nicht vorkommende Buchstaben
            }
        }

        return colors;
    }
}
