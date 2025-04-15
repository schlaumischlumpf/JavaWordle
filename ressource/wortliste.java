package ressource;

import java.util.Random;

public class wortliste {
    // Liste der verfügbaren 5-Buchstaben-Wörter
    private static final String[] WOERTER = {
            "Apfel", "Blume", "Eiche", "Feder", "Geist", "Hotel", "Käfig", "Nacht",
            "Pferd", "Radio", "Sonne", "Truhe", "Wache", "Wiese", "Birne", "Eimer",
            "Fluss", "Glanz", "Kerze", "Lampe", "Nacht", "Regen", "Stahl", "Taube",
            "Vogel", "Brand", "Busch", "Ernte", "Fuchs", "Berge", "Creme", "Druck",
            "Faden", "Gurke", "Insel", "Jacke", "Kanal", "Licht", "Motor", "Nadel",
            "Pause", "Qualm", "Rasen", "Schaf", "Tasse", "Zebra", "Blatt", "Welle",
            "Zange", "Haken", "Lunge", "Moped", "Winde", "Mütze", "Ampel", "Eiche",
            "Kegel", "Larve", "Mauer", "Sauna", "Alpen", "Alter", "Ampel", "Angel",
            "Anker", "Apfel", "Arena", "Bauch", "Beere", "Beleg", "Berge", "Besen",
            "Biene", "Birne", "Blase", "Blatt", "Blech", "Blick", "Blume", "Boden",
            "Bohne", "Borke", "Braue", "Braut", "Brief", "Bruch", "Brust", "Buchs",
            "Buero", "Busen", "Dachs", "Dampf", "Decke", "Diele", "Dolch", "Draht",
            "Dreck", "Druck", "Duene", "Duett", "Dunst", "Durst", "Ecken", "Eiche",
            "Eimer", "Eiter", "Elend", "Falte", "Feder", "Feier", "Feind", "Felge",
            "Ferse", "Firma", "Fluch", "Fluss", "Folie", "Forst", "Foyer", "Frist",
            "Frost", "Fugen", "Funde", "Liebe", "Angst", "Stolz", "Kraft", "Traum",
            "Leben", "Salat", "Biene", "Sonne", "Stern", "Feuer", "Klang", "Geist",
            "Wille", "Reise", "Gebet", "Welle", "Start", "Dauer", "Tiefe", "Treue",
            "Dauer", "Ferne"
    };

    private static final Random RANDOM = new Random();

    /**
     * Wählt ein zufälliges Wort aus der Liste aus.
     *
     * @return Ein zufälliges Wort in Großbuchstaben
     */
    public static String getRandomWord() {
        int index = RANDOM.nextInt(WOERTER.length);
        return WOERTER[index].toUpperCase();
    }

}