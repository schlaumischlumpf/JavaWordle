# Wordle in JavaFX

WordleFX ist eine Desktop-Anwendung, die das populäre Wortspiel Wordle imitiert. Die Applikation wurde dabei in JavaFX programmiert und offeriert dem Benutzer drei Spielmodi: Normal, Schwer sowie einen Challengemodus, der einen Timer beinhaltet.

## Features
- Normal-Modus: 
  - 6 Versuche, das Wort zu erraten
  - Wörter mit 5 Buchstaben
  - Keine Zeitbegrenzung durch den Timer
- Schwer-Modus
  - 4 Versuche, das Wort zu erraten
  - Wörter mit 5 Buchstaben
  - Zeitbegrenzung durch den Timer
- Challengemodus
  - 6 Versuche, das Wort zu erraten
  - Wörter mit 5 Buchstaben
  - Zeitbegrenzung durch den Timer 
  - Timer in Einstellungen anpassbar
- Umfangreiches Debug-Tool
  - mehr Informationen dazu unter [Debug-Tool](#debug-tool)

## Anforderungen
- Java 17 oder höher
- optional: JavaFX 19 oder höher

## Installation
Das Projekt kann installiert werden, indem das Repository geklont wird. 
```
 git clone https://github.com/schlaumischlumpf/JavaWordle.git
```
Anschließend kann die Anwendung mit der Ausführungsdatei `WordleJava2.jar` gestartet werden. Diese befindet sich im Ordner `out/artifacts/WordleJava2_jar` des Projekts. 
Hierfür wird keine JavaFX Installation benötigt, da die benötigten JavaFX-Bibliotheken bereits in der JAR-Datei enthalten sind.

## Debug-Tool
Das Debug-Tool ist ein umfangreiches Tool, das die Möglichkeit bietet, verschiedene Aspekte des Spiels zu debuggen. Es bietet eine Vielzahl von Funktionen in verschiedenen Tabs, diese sind:
- Tab "Spielinfo"
  - Anzeige des gesuchten Wortes
  - Anzeige der genutzten Buchstaben
  - Anzeige der Spielzeit
  - Möglichkeit, das Spiel mit einem neuen Wort neuzustarten
  - Möglichkeit, die letzte Eingabe im Wordle zu löschen
- Tab "Wort ändern"
  - Möglichkeit, das gesuchte Wort manuell zu ändern
- Tab "Wörterbuch-Prüfung"
  - Prüfung, ob ein Wort in der Wörterbuch-Datei existiert
  - Ausgabe eines Hinweises, ob das Wort existiert oder nicht → Hinweis: keine Prüfung auf Buchstabenlänge
- Tab "Statistik"
  - Anzahl der Neustarts
  - Anzahl der widerrufenen Eingaben
  - Anzahl der Wortänderungen
  - Anzahl der Wörterbuch-Prüfungen
  - Anzahl der Debug-Tool-Sitzungen
  - Möglichkeit, die Statistik zu aktualisieren/zurückzusetzen
  → Speicherung der Stats in `debug_stats.txt` unter `ressource/debug_stats.txt`
- Tab "Hilfe & Tipps"
  - Hinweise, die der Lösungsfindung dienen
    - Ersten Buchstaben des gesuchten Wortes anzeigen
    - Wortanalyse durchführen →
      - Länge des Wortes
      - Anzahl der Vokale im Wort
      - Anzahl der Konsonanten im Wort