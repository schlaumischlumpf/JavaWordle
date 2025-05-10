// Main.java
// Stand: 22.04.2025
// Autoren: Lennart und Moritz

package src;
import javafx.application.Application;

/*
    Über diese Klasse:
    Die Main-Klasse ist der Einstiegspunkt für die JavaFX-Anwendung.
    Sie startet die Anwendung und zeigt das Hauptfenster an.
*/

public class Main {
    public static void main(String[] args) {
        // Die JavaFX-Anwendung wird gestartet
        // Die UI-Klasse ist hierbei die Hauptklasse, das sie die Darstellung der Anwendung beinhaltet
        Application.launch(UI.class, args);
    }
}