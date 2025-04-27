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
xyz