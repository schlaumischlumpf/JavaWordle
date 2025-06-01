// Variables.java
// Stand: 22.04.2025
// Autoren: Lennart und Moritz

package ressource;

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
    public static boolean debugMode = false; // Debug-Modus
    public static int timerSeconds = 210; // Timer in Sekunden; Standardwert 210 Sekunden, also 3:30 Minuten
    // Speichert das aktuell ausgewählte Theme
    public static String currentTheme = "light"; // Standardmäßig helles Theme


    // Methode, die das Zielwort zurücksetzt, damit ein neues Zielwort für die nächste Runde ausgewählt werden kann
    public static void resetTargetWord() {
        currentTarget = null;
    }
}