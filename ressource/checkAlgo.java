package ressource;

import javafx.scene.paint.Color;

public class checkAlgo {

    /**
     * Überprüft das eingegebene Wort mit dem Zielwort und bestimmt die Farben für jede Zelle.
     *
     * @param eingabe  Das eingegebene Wort
     * @param zielwort Das zu erratende Wort
     * @return Ein Array mit Farben für jede Position
     */
    public static Color[] pruefeWort(String eingabe, String zielwort) {
        eingabe = eingabe.toUpperCase();
        zielwort = zielwort.toUpperCase();

        int laenge = zielwort.length();
        Color[] farben = new Color[laenge];
        boolean[] zielwortBenutzt = new boolean[laenge]; // Markiert bereits "verbrauchte" Buchstaben

        // Erster Durchgang: Finde exakte Übereinstimmungen (grün)
        for (int i = 0; i < laenge; i++) {
            if (i < eingabe.length() && eingabe.charAt(i) == zielwort.charAt(i)) {
                farben[i] = Color.GREEN;
                zielwortBenutzt[i] = true;
            } else {
                farben[i] = Color.web("#b52121"); // Vorläufig als falsch markieren
            }
        }

        // Zweiter Durchgang: Finde Buchstaben an falscher Position (gelb)
        for (int i = 0; i < eingabe.length() && i < laenge; i++) {
            if (farben[i] == Color.GREEN) {
                continue; // Bereits als korrekt markiert
            }

            char buchstabe = eingabe.charAt(i);
            boolean gefunden = false;

            // Suche nach diesem Buchstaben im Zielwort
            for (int j = 0; j < laenge; j++) {
                if (!zielwortBenutzt[j] && zielwort.charAt(j) == buchstabe) {
                    farben[i] = Color.DARKGOLDENROD;
                    zielwortBenutzt[j] = true;
                    gefunden = true;

                    break;
                }
            }

            if (!gefunden) {
                farben[i] = Color.web("#b52121");
            }
        }

        return farben;
    }
}