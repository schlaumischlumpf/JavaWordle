// CheckAlgo.java
// Stand: 23.04.2025
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
    // Methode pruefeWort(), die das eingegebene Wort mit dem gesuchten Wort vergleicht
    // Sie gibt ein Array von Farben zurück, das den Status der Übereinstimmung anzeigt
    public static Color[] pruefeWort(String eingabe, String zielwort) {
        // Konvertierung der Eingabe und des Zielwortes in Großbuchstaben
        eingabe = eingabe.toUpperCase();
        zielwort = zielwort.toUpperCase();

        // Speicherung der Länge des Zielwortes → Verwendung für Dimensionierung des Arrays
        int laenge = zielwort.length();

        // Array, das die Farben für die Übereinstimmungen speichert
        Color[] farben = new Color[laenge];

        // Hilfsarray, um zu verfolgen, welche Buchstaben im Zielwort bereits verwendet wurden → Vermeidung von Mehrfachzählungen
        boolean[] zielwortBenutzt = new boolean[laenge];

        // Erster Durchgang der Überprüfung: Finden von exakten Übereinstimmungen → Grün als Hintergrund markieren
        for (int i = 0; i < laenge; i++) {
            if (i < eingabe.length() && eingabe.charAt(i) == zielwort.charAt(i)) {
                farben[i] = Color.web("#6aaa64"); // Grün für richtige Position
                zielwortBenutzt[i] = true;
            } else {
                farben[i] = Color.web("#787c7e"); // Grau für falsche Buchstaben
            }
        }

        // Zweiter Durchgang: Finden vom Buchstaben an falscher Position (gelb)
        for (int i = 0; i < eingabe.length() && i < laenge; i++) {
            if (farben[i].equals(Color.web("#6aaa64"))) {
                continue; // Bereits als korrekt markiert
            }

            char buchstabe = eingabe.charAt(i);
            boolean gefunden = false;

            // Suche nach diesem Buchstaben im Zielwort
            for (int j = 0; j < laenge; j++) {
                if (!zielwortBenutzt[j] && zielwort.charAt(j) == buchstabe) {
                    farben[i] = Color.web("#c9b458"); // Gelb für richtige Buchstaben an falscher Stelle
                    zielwortBenutzt[j] = true;
                    gefunden = true;
                    break;
                }
            }

            if (!gefunden) {
                farben[i] = Color.web("#787c7e"); // Grau für nicht vorkommende Buchstaben
            }
        }

        return farben;
    }
}