// Wortliste.java
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

public class Wortliste {
    // Liste der verfügbaren Wörter in alphabetischer Reihenfolge
    private static final String[] WOERTER = {
            "Abtei", "Acker", "Adler", "Äffin", "Aktie", "Alben", "Alter", "Ampel", "Angel", "Angst",
            "Anion", "Anker", "Apfel", "Arche", "Arena", "Argon", "Armut", "Asien", "Atlas", "Äther",
            "Bambi", "Bande", "Basis", "Bauch", "Bauer", "Beere", "Beruf", "Besen", "Beule", "Biene",
            "Biker", "Birke", "Birne", "Blase", "Blatt", "Blech", "Blick", "Block", "Blöße", "Blume",
            "Boden", "Bohne", "Bonze", "Börde", "Bowle", "Brand", "Braue", "Braut", "Brief", "Bruch",
            "Buben", "Bucht", "Bulle", "Busch", "Busen", "Büste", "Chart", "Chaos", "Cobol", "Creme",
            "Curie", "Cyber", "Dachs", "Dampf", "Datum", "Decke", "Delta", "Demut", "Diana", "Diele",
            "Docht", "Dogge", "Dolch", "Dosis", "Dover", "Draht", "Dreck", "Drink", "Druck", "Droge",
            "Drüse", "Duell", "Dunst", "Durst", "Ebene", "Eiche", "Eimer", "Eiter", "Eklat", "Ekzem",
            "Elend", "Elfen", "Engel", "Enzym", "Ernte", "Fahne", "Falte", "Fazit", "Feder", "Fehde",
            "Feier", "Feile", "Feind", "Felge", "Fermi", "Ferse", "Final", "Firma", "First", "Fisch",
            "Flaum", "Fluch", "Fluss", "Föhre", "Folie", "Forst", "Foyer", "Frist", "Fritz", "Frost",
            "Fuchs", "Fuder", "Futur", "Fürze", "Gämse", "Garde", "Gehör", "Geist", "Gemüt", "Getto",
            "Gilde", "Glanz", "Glück", "Götze", "Grace", "Grund", "Haifa", "Haken", "Hanse", "Harry",
            "Hertz", "Hitze", "Hobby", "Honda", "Hotel", "Hunne", "Hydra", "Imker", "Indiz", "Insel",
            "Intus", "Islam", "Jacht", "Jaffa", "Joule", "Jubel", "Julia", "Kabul", "Kaaba", "Kamin",
            "Kanal", "Karte", "Kasse", "Kater", "Kelch", "Kenia", "Kerze", "Kleie", "Kleid", "Kohle",
            "Komet", "Kopie", "Korso", "Kraft", "Kranz", "Krieg", "Kubus", "Kuhle", "Kupon", "Kurie",
            "Kurve", "Labor", "Lader", "Lampe", "Laser", "Leben", "Leber", "Leder", "Leier", "Lende",
            "Lepra", "Liebe", "Licht", "Limit", "Linie", "Lotse", "Löwin", "Lunge", "Luxus", "Mafia",
            "Makro", "Marge", "Markt", "Masse", "Mathe", "Media", "Metro", "Milbe", "Milch", "Mimik",
            "Model", "Modus", "Mönch", "Moral", "Motor", "Motiv", "Mumps", "Müsli", "Mütze", "Nagel",
            "Nacht", "Nadel", "Namen", "Neffe", "Notar", "Obhut", "Ocker", "Opfer", "Organ",
            "Paris", "Patch", "Pause", "Peter", "Petra", "Pfeil", "Pfarr", "Pferd", "Pfote", "Pfund",
            "Pille", "Pinie", "Platz", "Pokal", "Polle", "Posse", "Prime", "Probe", "Profi", "Pudel",
            "Puder", "Pulle", "Putin", "Quark", "Qualm", "Qubit", "Rabat", "Rache", "Radar", "Radio",
            "Rapid", "Rappe", "Rasen", "Rasse", "Reise", "Revue", "Ricke", "Rotor", "Rubel", "Rumpf",
            "Sahne", "Salat", "Samba", "Sauce", "Schal", "Schaf", "Schah", "Scheu", "Sicht", "Sippe",
            "Skalp", "Slawe", "Socke", "Sohle", "Solei", "Sonne", "Sound", "Späne", "Speer", "Spore",
            "Spree", "Spule", "Stadt", "Stahl", "Start", "Stase", "Stern", "Stock", "Stolz", "Streu",
            "Stuck", "Sudan", "Sühne", "Sülze", "Summe", "Szene", "Tafel", "Tango", "Tarif", "Tasse",
            "Taube", "Teint", "Tempo", "Tenne", "Thema", "Tisch", "Toner", "Tonne", "Törin", "Torus",
            "Trafo", "Traum", "Treue", "Truhe", "Trunk", "Ungar", "Untat", "Urahn", "Vater", "Vogel",
            "Volvo", "Wache", "Waise", "Walze", "Wange", "Wedel", "Weide", "Weihe", "Welle", "Weste",
            "Wille", "Winde", "Wiese", "Wunde", "Yacht", "Zange", "Zebra", "Zecke", "Zehnt", "Zuzug"
    };

    // Zufallszahlengenerator für die Auswahl eines zufälligen Wortes
    // final, da das Wort einmalig beim Start der Anwendung ausgewählt wird
    private static final Random RANDOM = new Random();

    // Methode getRandomWord(), die das vorher ausgewählte Wort als String zurückgibt
    public static String getRandomWord() {
        // Erstellung eines Index mit einem Wort aus der Liste
        // RANDOM.nextInt wählt eine zufällige Position aus der Liste aus mit dem jeweiligen Wort
        int index = RANDOM.nextInt(WOERTER.length);

        // Rückgabe des Wortes in Großbuchstaben
        return WOERTER[index].toUpperCase();
    }
}