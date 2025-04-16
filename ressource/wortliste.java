package ressource;

import java.util.Random;

public class wortliste {
    // Listen der verfügbaren 5-Buchstaben-Wörter

    // Ursprüngliche Liste
    private static final String[] WOERTER_1 = {
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

    // Erweiterte Liste mit zusätzlichen Wörtern, hier als Wortliste 2
    private static final String[] WOERTER_2 = {
            "buben", "stets", "börde", "toner", "asien", "braun", "ernte", "bambi", "unart", "walze",
            "äffin", "gilde", "unten", "wohin", "namen", "negev", "abweg", "stock", "rache", "aktie",
            "köder", "lepra", "allwo", "imker", "solei", "still", "fünft", "dampf", "köche", "blank",
            "berge", "hunde", "woher", "abhol", "genus", "alice", "hydra", "drüse", "masse", "puder",
            "vater", "korso", "tritt", "mönch", "küche", "moral", "milbe", "immun", "julia", "armut",
            "quasi", "pfeil", "alben", "kenia", "bande", "hinab", "walöl", "block", "indiz", "ämter",
            "delta", "kupon", "gefäß", "rasse", "tasse", "ragen", "geübt", "stamm", "sagen", "hatte",
            "liege", "slawe", "hobby", "trunk", "makro", "tegel", "zehnt", "sinti", "kirch", "miami",
            "werks", "öfter", "carol", "üppig", "kranz", "ekzem", "nutte", "feile", "sieht", "lägen",
            "sprüh", "krank", "angab", "spröd", "udssr", "täter", "after", "bohne", "wicke", "fuder",
            "trost", "bucht", "allzu", "halte", "blieb", "berta", "ricke", "sühne", "hager", "preis",
            "spore", "klemm", "diwan", "bauer", "paket", "posse", "cyber", "kleie", "speer", "krieg",
            "ungar", "diana", "oblag", "duell", "nötig", "vorab", "fuchs", "adeln", "kopie", "meist",
            "mimik", "stemm", "müsli", "meran", "jubel", "setup", "patch", "ernst", "statt", "saldo",
            "bulle", "nagel", "törin", "satin", "sowie", "sippe", "bonze", "hüben", "stößt", "eklat",
            "getan", "krass", "abhör", "honda", "carlo", "lager", "todes", "affen", "quark", "kuhle",
            "trafo", "komet", "arten", "pulle", "devot", "obhut", "prima", "intus", "zäune", "beere",
            "gerda", "liszt", "stunt", "mumps", "kroch", "ideen", "zudem", "bläst", "tönen", "götze",
            "blink", "beule", "fritz", "zuzug", "weben", "dogge", "kelch", "edeka", "rubel", "dämme",
            "fehde", "föhre", "hertz", "organ", "tonne", "exakt", "grace", "marge", "wovor", "garde",
            "xerox", "harry", "faxen", "hinzu", "pinie", "dosen", "spann", "kamin", "spien", "motiv",
            "musst", "gämse", "kater", "bauen", "szene", "büste", "argon", "cluny", "trist", "irrig",
            "zaire", "tafel", "tisch", "jaffa", "docht", "schul", "islam", "putin", "enzym", "loten",
            "chaos", "sooft", "start", "getto", "tröge", "alert", "inuit", "zeige", "eisig", "hagen",
            "birne", "kasse", "cobol", "kraus", "sülze", "otmar", "curie", "fürze", "kubus", "forsa",
            "karte", "löwen", "kabul", "modus", "solon", "thema", "doris", "derer", "schal", "rasch",
            "torus", "kohle", "biker", "rufen", "birke", "riete", "läuft", "zuber", "flaum", "nsdap",
            "datum", "bluff", "äthyl", "pokal", "leier", "engel", "lotse", "kaaba", "pudel", "fahne",
            "hunne", "bongo", "coats", "vegan", "vögel", "dasaß", "petra", "prime", "pylon", "limit",
            "jäten", "chart", "wanst", "anbei", "adler", "rapid", "drink", "mathe", "abzug", "rußig",
            "umher", "final", "basis", "hören", "essen", "hörig", "choke", "würge", "waise", "tarif",
            "lende", "revue", "davon", "fülle", "anzog", "kurie", "sahne", "heben", "elfen", "streu",
            "anker", "venen", "gebar", "beruf", "leder", "waren", "milch", "fazit", "fermi", "aurel",
            "umbra", "droge", "neuer", "acker", "fegen", "ruhig", "sound", "tempo", "rumpf", "bemaß",
            "bowle", "schah", "markt", "radar", "glitt", "fulda", "ocker", "first", "untat", "canon",
            "foyer", "vogts", "fließ", "bühne", "demut", "fisch", "neffe", "umsah", "kurve", "summe",
            "mafia", "paris", "leber", "busch", "blöße", "äugen", "zutat", "grund", "tuend", "pfarr",
            "blase", "baden", "laden", "erlös", "qubit", "tango", "urahn", "legal", "zecke", "peter",
            "braue", "außen", "merck", "sudan", "liebe", "orgie", "abtei", "samba", "dürer", "tenne",
            "mäuse", "dover", "hotel", "haifa", "glatt", "fäule", "volvo", "joule", "rabat", "pfote",
            "gehör", "stuck", "glück", "vista", "rasen", "mutig", "ebnen", "fußen", "sicht", "weide",
            "weihe", "ulken", "fatal", "klotz", "atlas", "pille", "reise", "rotor", "wunde", "rappe",
            "media", "rosen", "desto", "äther", "wagen", "wache", "nizza", "rosig", "labor", "scheu",
            "celle", "prost", "hanse", "lader", "geben", "spind", "kanne", "opfer", "knabe", "gemüt",
            "linus", "spree", "wedel", "futur", "ehest", "sohle", "wirst", "stück", "blume", "skalp",
            "teint", "grenz", "luxus", "vorig", "anion", "platz", "metro", "selbe", "beute", "annie",
            "engen", "säbel", "kleid", "antat", "daher", "späne", "profi", "blüte", "letzt", "lasso",
            "emsig", "sinus", "toben", "stolz", "stieß", "enorm", "jacht", "hinan", "damen", "watte",
            "unmut", "weder", "wange", "polle", "pfund", "trotz", "genug", "probe", "dafür", "spule",
            "kuren", "laser", "hitze", "boxer", "socke", "notar", "sauce", "arche", "porno", "banal",
            "gödel", "worms", "junge", "stase", "dosis", "hobby", "löse"
    };

    private static final Random RANDOM = new Random();

    /**
     * Wählt ein zufälliges Wort aus der Liste aus.
     *
     * @return Ein zufälliges Wort in Großbuchstaben
     */

    // Methode zum Abrufen eines zufälligen Wortes aus der Liste; WOERTER_1 oder WOERTER_2 können verwendet werden
    public static String getRandomWord() {
        int index = RANDOM.nextInt(WOERTER_2.length);
        return WOERTER_2[index].toUpperCase();
    }

}