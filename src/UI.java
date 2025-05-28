// UI.java
// Stand: 28.05.2025
// Autoren: Lennart und Moritz

package src;

import javafx.animation.Animation;
import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;
import ressource.*;


import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

// Diese Klasse ist für die Benutzeroberfläche des Spiels verantwortlich.
// Sie enthält Methoden zum Erstellen und Verwalten der verschiedenen Anwendungsfenster,
// einschließlich des Hauptmenüs, des Einstellungsmenüs und des Spiels.
public class UI extends Application {
    // Boolean, damit die Einstellungen nicht beim Starten der Anwendung geöffnet werden
    public static boolean openSettingsOnStart = false;
    // Container für die vertikale Anordnung der Elemente im Hauptfenster
    private static VBox rootNode;
    // Dedizierter Thread-Pool für das Vorladen von Wörtern im Hintergrund
    private final java.util.concurrent.ExecutorService preloadService =
            java.util.concurrent.Executors.newSingleThreadExecutor(r -> getPrepThread(new Thread(r, "VorladeThread")));
    // Variablen für das Precaching (vorladen von Wörtern für bessere Performance)
    public String preloadedTargetWord;
    public int preloadedGameType = 1; // Standard: Normal-Modus
    // Erstellung einer ToggleGroup für die Radiobuttons im Hauptmenü
    ToggleGroup tg;

    // Methode zum Setzen des Icons in der Titelleiste des Fensters
    private static void setStageIcon(Stage stage) {
        Image icon = new Image(Objects.requireNonNull(UI.class.getResourceAsStream("/ressource/logo.png")));
        stage.getIcons().add(icon);
    }

    private static Slider getSlider() {
        Slider setTimerValue = new Slider(30, 360, 210);
        setTimerValue.setMajorTickUnit(30); // Schrittweite für große Markierungen
        setTimerValue.setMinorTickCount(0); // Keine kleinen Markierungen
        setTimerValue.setSnapToTicks(true); // Auf Markierungen einrasten
        setTimerValue.setPrefWidth(350); // Breite des Sliders
        setTimerValue.setShowTickMarks(true); // Markierungen anzeigen
        setTimerValue.setShowTickLabels(false); // Keine Beschriftungen an den Markierungen
        return setTimerValue;
    }

    private static PauseTransition getColorPause(Color color, Label cell) {
        PauseTransition colorPause = new PauseTransition(Duration.millis(10));
        colorPause.setOnFinished(_ -> {
            // Hintergrundfarbe setzen
            String hexColor = String.format("#%02X%02X%02X",
                    (int) (color.getRed() * 255),
                    (int) (color.getGreen() * 255),
                    (int) (color.getBlue() * 255));
            cell.setStyle("-fx-background-color: " + hexColor + "; -fx-border-color: " + hexColor + "; -fx-border-width: 1; -fx-text-fill: white;");
        });
        return colorPause;
    }

    // Methode, mit der man ein CSS-Stylesheet auf eine Scene anwenden kann
    private void applyStylesheet(Scene scene) {
        // Der Pfad zu der CSS-Datei wird hier eingebunden
        String cssPath = Objects.requireNonNull(getClass().getResource("/ressource/style.css")).toExternalForm();
        scene.getStylesheets().add(cssPath);
    }

    // Startmethode der JavaFX-Anwendung - wird beim Start automatisch aufgerufen
    @Override
    public void start(Stage stage) {
        // Fenstertitel setzen
        stage.setTitle("Wordle");

        // Vertikaler Layout-Container mit einem Abstand von 10px zwischen Elementen
        rootNode = new VBox(10);

        // Padding für den Container; Anordnung im Center der Stage
        rootNode.setAlignment(Pos.CENTER);

        // Neue Scene mit dem Container und einer Breite von 600px und Höhe von 400px
        Scene scene = new Scene(rootNode, 600, 400);

        // CSS-Stylesheet anwenden
        applyStylesheet(scene);

        // Icon asynchron laden, um den Startvorgang zu beschleunigen
        // Dies verhindert Blockieren des UI-Threads beim Laden des Icons
        Platform.runLater(() -> setStageIcon(stage));

        // Hinzufügen der Scene zur Stage (Fenster)
        stage.setScene(scene);

        // Prüfen, ob Einstellungsmenü direkt geöffnet werden soll; true = öffnen, false = Wordle direkt starten
        if (openSettingsOnStart) {
            showSettingsMenu(stage);
        } else {
            showMainMenu(stage);
        }

        // Die Stage wird geöffnet und dem Benutzer angezeigt
        stage.show();

        // Optimiertes Vorladen der Wortliste im Hintergrund für bessere Performance
        // Dies passiert nach dem Anzeigen des Menüs, sodass die Benutzeroberfläche schneller erscheint
        new Thread(Wortliste::getRandomWord).start();
    }

    // Methode zum Erstellen und Anzeigen des Hauptmenüs
    private void showMainMenu(Stage stage) {
        // Hauptmenü leeren (alle vorherigen Elemente entfernen)
        rootNode.getChildren().clear();

        // Titel des Fensters setzen
        stage.setTitle("Hauptmenü");

        // Label für Spielmodusauswahl erstellen
        Label auswahl = new Label("Wähle einen Spielmodus");

        // Buttons für Spielstart und Einstellungsmenü erstellen
        Button btnConfirm = new Button("Spiel mit diesem Modus starten");
        Button openSettings = new Button("Einstellungen");

        // Breite des Einstellungsbuttons auf 150px setzen für einheitliche Größe
        openSettings.setPrefWidth(150);

        // Erstellung von Radiobuttons für die drei Spielmodi
        RadioButton rbModus1 = new RadioButton("Normal");
        RadioButton rbModus2 = new RadioButton("Schwer");
        RadioButton rbModus3 = new RadioButton("Challenge");

        // Festlegung der Fenstergröße
        stage.setHeight(500);
        stage.setWidth(600);

        // Minimale Fenstergröße
        stage.setMinHeight(500);
        stage.setMinWidth(600);

        // Neue ToggleGroup für die Radiobuttons (damit nur einer ausgewählt sein kann)
        tg = new ToggleGroup();

        // Zuweisung der ToggleGroup zu den Radiobuttons
        rbModus1.setToggleGroup(tg);
        rbModus2.setToggleGroup(tg);
        rbModus3.setToggleGroup(tg);
        rbModus1.setSelected(true); // Standardmäßig ist Normal-Modus ausgewählt

        // Listener für Radiobuttons, die das Zielwort vorladen
        rbModus1.setOnAction(_ -> preloadTargetWord(1));
        rbModus2.setOnAction(_ -> preloadTargetWord(2));
        rbModus3.setOnAction(_ -> preloadTargetWord(3));

        // Da rbModus1 standardmäßig ausgewählt ist, initial vorladen
        preloadTargetWord(1);

        // Aktion bei Klick auf den Startbutton definieren
        btnConfirm.setOnAction(_ -> {
            // Speicherung des ausgewählten Radiobuttons
            RadioButton rb = (RadioButton) tg.getSelectedToggle();
            switch (rb.getText()) {
                case "Normal" -> prepareAndStartNewGame(stage, 1);
                case "Schwer" -> prepareAndStartNewGame(stage, 2);
                case "Challenge" -> prepareAndStartNewGame(stage, 3);
            }
        });

        // Öffnen des Einstellungsmenüs bei Klick auf Einstellungsbutton
        openSettings.setOnAction(_ -> showSettingsMenu(stage));

        // Horizontale Anordnung der Radiobuttons in horizontalem Layoutcontainer mit 25px Abstand
        HBox radioButtons = new HBox(25, rbModus1, rbModus2, rbModus3);

        // Anordnung der Radiobuttons in Fenstermitte
        radioButtons.setAlignment(Pos.CENTER);

        // 50px Abstand über dem Einstellungsbutton für bessere Optik
        VBox.setMargin(openSettings, new Insets(50, 0, 0, 0));

        // Vertikale Abstandshalter erstellen für besseres Layout
        Region spacer1 = new Region();
        spacer1.setPrefHeight(5);
        Region spacer2 = new Region();
        spacer2.setPrefHeight(5);
        Region spacer3 = new Region();
        spacer3.setPrefHeight(20);

        // Alle Elemente zum rootNode hinzufügen in richtiger Reihenfolge
        rootNode.getChildren().addAll(
                auswahl,
                spacer1,
                radioButtons,
                spacer2,
                btnConfirm,
                spacer3,
                openSettings
        );

        // Fenster zentrieren auf dem Bildschirm
        stage.centerOnScreen();
    }

    // Methode zum Vorladen des Zielworts basierend auf dem ausgewählten Spieltyp
    private void preloadTargetWord(int gameType) {
        preloadedGameType = gameType;
        // Vorladen in separaten Thread auslagern, damit die UI nicht blockiert wird
        // und das Programm schneller startet
        preloadService.submit(() -> {
            Variables.resetTargetWord();
            preloadedTargetWord = Wortliste.getRandomWord();
        });
    }

    // Verbesserte Methode zum Anzeigen des Spielbildschirms mit reduzierter Startzeit
    private void showGameScreen(Stage stage) {
        // Grundlegende Fenstereigenschaften sofort setzen, ohne Verzögerung
        stage.setTitle("Wordle");
        stage.setWidth(700);
        stage.setHeight(800);
        stage.setMinWidth(700);
        stage.setMinHeight(800);
        setStageIcon(stage);

        // UI vorbereiten und ein einfaches Layout anzeigen
        rootNode.getChildren().clear();
        VBox gameLayout = new VBox(22);
        gameLayout.setAlignment(Pos.CENTER);

        // Ladeindikator hinzufügen während das Spiel im Hintergrund initialisiert wird
        ProgressIndicator loadingIndicator = new ProgressIndicator();
        loadingIndicator.setPrefSize(80, 80);
        gameLayout.getChildren().add(loadingIndicator);
        rootNode.getChildren().add(gameLayout);

        // Spielfeld in einem Hintergrund-Thread laden, um den UI-Thread nicht zu blockieren
        Thread initThread = getInitThread(stage, gameLayout);
        initThread.start();

        // Fenster zentrieren
        stage.centerOnScreen();
    }

    private Thread getInitThread(Stage stage, VBox gameLayout) {
        Thread initThread = new Thread(() -> {
            // Spielfeld im Hintergrund erstellen
            GameFieldWithCheck gameField = getGameFieldWithCheck(stage);

            // UI nach erfolgreicher Initialisierung aktualisieren
            Platform.runLater(() -> {
                gameLayout.getChildren().clear();
                Button btnBackToMenu = new Button("Zurück zum Hauptmenü");
                btnBackToMenu.setOnAction(_ -> showMainMenu(stage));
                gameLayout.getChildren().addAll(gameField, btnBackToMenu);

                // Nächstes Wort für das kommende Spiel vorladen
                preloadNextWord();
            });
        });

        // Als Daemon markieren, damit der Thread das Programmende nicht blockiert
        initThread.setDaemon(true);
        return initThread;
    }

    private GameFieldWithCheck getGameFieldWithCheck(Stage stage) {
        String targetWord = preloadedTargetWord;
        final GameFieldWithCheck[] gameField = new GameFieldWithCheck[1];

        // CountDownLatch zur Thread-Synchronisation
        if (Variables.debugMode) {
            CountDownLatch latch = new CountDownLatch(1);

            Platform.runLater(() -> {
                // Je nach Spielmodus unterschiedliche GameField-Einstellungen
                switchPreloadedGameType(stage, targetWord, gameField);
                latch.countDown(); // Signal, dass Spielfeld erstellt wurde
            });

            // Auf Fertigstellung warten mit Timeout
            try {
                if (!latch.await(2, TimeUnit.SECONDS)) {
                    System.err.println("Timeout beim Warten auf Spielfelderstellung");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } else switchPreloadedGameType(stage, targetWord, gameField);

        return gameField[0];
    }

    private void switchPreloadedGameType(Stage stage, String targetWord, GameFieldWithCheck[] gameField) {
        gameField[0] = switch (preloadedGameType) {
            case 2 -> new GameFieldWithCheck(targetWord, stage, this, 4, false); // Schwer: 4 Zeilen
            case 3 -> new GameFieldWithCheck(targetWord, stage, this, 6, true);  // Challenge: mit Timer
            default -> new GameFieldWithCheck(targetWord, stage, this, 6, false); // Normal: 6 Zeilen
        };
    }

    // Verbesserte Methode zum Vorladen des nächsten Worts
    private void preloadNextWord() {
        // Vorladen im dedizierten Thread-Pool ausführen
        preloadService.submit(() -> {
            Variables.resetTargetWord();
            preloadedTargetWord = Wortliste.getRandomWord();
        });
    }

    // Methode zum Anzeigen des Einstellungsmenüs
    public void showSettingsMenu(Stage stage) {
        // Hauptmenü leeren
        rootNode.getChildren().clear();

        // Ladebildschirm anzeigen
        VBox loadingLayout = new VBox(20);
        loadingLayout.setAlignment(Pos.CENTER);

        Label loadingLabel = new Label("Einstellungen werden geladen...");
        loadingLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        ProgressIndicator progress = new ProgressIndicator();
        progress.setPrefSize(50, 50);

        loadingLayout.getChildren().addAll(loadingLabel, progress);
        rootNode.getChildren().add(loadingLayout);

        // Kurze Animation für besseres visuelles Feedback
        PauseTransition delay = new PauseTransition(Duration.millis(300));
        delay.setOnFinished(_ -> {
            rootNode.getChildren().clear();
            stage.setTitle("Einstellungen");
            stage.setHeight(700);

            // Überschrift für allgemeine Einstellungen
            Label settingsLabel = new Label("Allgemeine Einstellungen");
            settingsLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

            // Checkbox für Debug-Modus
            CheckBox debugModeCheckBox = new CheckBox("Debug-Modus aktivieren");
            debugModeCheckBox.setSelected(Variables.debugMode);
            debugModeCheckBox.setOnAction(_ -> Variables.debugMode = debugModeCheckBox.isSelected());

            // Tooltip für den Debug-Modus
            Tooltip debugModeTooltip = new Tooltip("Wenn der Debug-Modus aktiviert ist, öffnet sich das Debug-Tool, was u. a. Änderungen am Wordle erlaubt.");
            debugModeTooltip.setShowDelay(Duration.millis(500));
            debugModeCheckBox.setTooltip(debugModeTooltip);

            // Region-Abstandshalter für besseres Layout
            Region spacer15 = new Region();
            spacer15.setPrefHeight(20);

            // Überschrift für den Theme-Bereich
            Label themeLabel = new Label("Design-Einstellungen");
            themeLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

            // ToggleGroup für die Theme-Buttons erstellen
            ToggleGroup themeToggleGroup = new ToggleGroup();

            // Die drei Theme-Optionen erstellen
            VBox lightThemeOption = createThemeButton("Hell", Color.rgb(240, 240, 240), Color.rgb(30, 30, 30), themeToggleGroup);
            VBox darkThemeOption = createThemeButton("Dunkel", Color.rgb(30, 30, 30), Color.rgb(230, 230, 230), themeToggleGroup);
            VBox mintThemeOption = createThemeButton("Mint", Color.rgb(200, 255, 220), Color.rgb(30, 30, 30), themeToggleGroup);
            VBox randomThemeOption = createThemeButton("Zufall",
                    Color.rgb(Variables.random.nextInt(256), Variables.random.nextInt(256), Variables.random.nextInt(256)),
                    Color.rgb(Variables.random.nextInt(256), Variables.random.nextInt(256), Variables.random.nextInt(256)),
                    themeToggleGroup);
            // Aktives Theme vorauswählen
            if (Variables.currentTheme != null) {
                switch (Variables.currentTheme) {
                    case "dark" -> ((RadioButton) darkThemeOption.getChildren().get(2)).setSelected(true);
                    case "mint" -> ((RadioButton) mintThemeOption.getChildren().get(2)).setSelected(true);
                    case "random" -> ((RadioButton) randomThemeOption.getChildren().get(2)).setSelected(true);
                    default -> ((RadioButton) lightThemeOption.getChildren().get(2)).setSelected(true);
                }
            } else {
                // Standardmäßig helles Theme auswählen
                ((RadioButton) lightThemeOption.getChildren().get(1)).setSelected(true);
            }

            // Container für die Theme-Buttons erstellen
            HBox themeOptions = new HBox(20);
            themeOptions.setAlignment(Pos.CENTER);
            themeOptions.getChildren().addAll(lightThemeOption, darkThemeOption, mintThemeOption, randomThemeOption);

            // Event-Handler für Theme-Änderungen hinzufügen
            themeToggleGroup.selectedToggleProperty().addListener((_, _, newValue) -> {
                if (newValue != null) {
                    applyTheme(stage, ((RadioButton) newValue).getUserData().toString());
                }
            });

            // Label über dem Slider für Timer-Einstellungen
            Label sliderLabel = new Label("Timer-Einstellung für den Challenge-Modus");
            sliderLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

            // Slider, mit dem man die Timerzeit für Challenge-Modus einstellen kann
            Slider setTimerValue = getSlider();

            // Container für Slider mit Beschriftungen links und rechts
            HBox sliderBox = new HBox(10);
            sliderBox.setAlignment(Pos.CENTER);
            Label minLabel = new Label("30s"); // Minimaler Wert
            Label maxLabel = new Label("360s"); // Maximaler Wert
            sliderBox.getChildren().addAll(minLabel, setTimerValue, maxLabel);

            // Label für die aktuelle Slider-Position (ausgewählte Zeit)
            Label sliderValueLabel = new Label("Eingestellte Timerzeit: 210 Sekunden");

            // Event-Listener für Änderungen am Slider
            setTimerValue.valueProperty().addListener((_, _, newValue) -> {
                int timerValue = newValue.intValue();
                sliderValueLabel.setText("Eingestellte Timerzeit: " + timerValue + " Sekunden");

                // Eingestellte Zeit in den globalen Variablen speichern
                Variables.timerSeconds = timerValue;
            });

            // Button zum Zurückkehren zum Hauptmenü
            Button btnBack = new Button("Zurück zum Hauptmenü");
            btnBack.setOnAction(_ -> showMainMenu(stage));

            // Container für alle Einstellungselemente
            VBox settingsContent = new VBox(20);
            settingsContent.setAlignment(Pos.CENTER);

            // Hauptüberschrift für die Einstellungsseite
            Label lableTitle = new Label("Einstellungen");
            lableTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));

            // Abstandshalter für besseres Layout
            Region spacer5 = new Region();
            spacer5.setPrefHeight(5);
            Region spacer10 = new Region();
            spacer10.setPrefHeight(10);

            // Alle UI-Elemente zum Settings-Container hinzufügen
            settingsContent.getChildren().addAll(
                    settingsLabel,
                    debugModeCheckBox,
                    spacer10,
                    sliderLabel,
                    sliderBox,
                    sliderValueLabel,
                    spacer5,  // 5px Abstand
                    themeLabel,
                    themeOptions,
                    spacer15,
                    btnBack
            );

            // Container zum Hauptfenster hinzufügen
            rootNode.getChildren().add(settingsContent);

            // Fenster zentrieren
            stage.centerOnScreen();
        });
        delay.play();
    }

    /**
     * Erstellt einen Theme-Button mit Vorschau der Farben, die durch eine Diagonale getrennt sind
     *
     * @param themeName Name des Themes (wird als Label angezeigt)
     * @param bgColor   Hintergrundfarbe des Themes
     * @param textColor Textfarbe des Themes
     * @param group     ToggleGroup für die Radiobuttons
     * @return VBox mit dem kompletten Theme-Button
     */
    private VBox createThemeButton(String themeName, Color bgColor, Color textColor, ToggleGroup group) {
        // RadioButton für die Auswahl erstellen
        RadioButton themeRadio = new RadioButton();
        themeRadio.setToggleGroup(group);
        themeRadio.setUserData(themeName.toLowerCase());

        // Pane für die Farbvorschau mit diagonaler Trennung erstellen
        Pane colorPreview = new Pane();
        colorPreview.setPrefSize(60, 60);
        colorPreview.setMinSize(60, 60);
        colorPreview.setMaxSize(60, 60);

        // Oberes Dreieck (Hintergrundfarbe)
        javafx.scene.shape.Polygon topHalf = new javafx.scene.shape.Polygon(
                0, 0,  // oben links
                60, 0, // oben rechts
                60, 60 // unten rechts
        );
        topHalf.setFill(bgColor);

        // Unteres Dreieck (Textfarbe)
        javafx.scene.shape.Polygon bottomHalf = new javafx.scene.shape.Polygon(
                0, 0,   // oben links
                0, 60,  // unten links
                60, 60  // unten rechts
        );
        bottomHalf.setFill(textColor);

        // Polygone zur Pane hinzufügen
        colorPreview.getChildren().addAll(topHalf, bottomHalf);

        // Rahmen hinzufügen
        colorPreview.setStyle("-fx-border-color: black; -fx-border-width: 1px;");

        // Label für den Theme-Namen
        Label nameLabel = new Label(themeName);

        // Container für Vorschau, Name und RadioButton erstellen
        VBox themeOption = new VBox(5);
        themeOption.setAlignment(Pos.CENTER);
        themeOption.getChildren().addAll(colorPreview, nameLabel, themeRadio);

        // Gesamte Komponente klickbar machen
        themeOption.setOnMouseClicked(_ -> themeRadio.setSelected(true));

        return themeOption;
    }

    /**
     * Wendet das ausgewählte Theme auf die Anwendung an
     *
     * @param stage     Die aktuelle Stage
     * @param themeName Name des zu aktivierenden Themes
     */
    private void applyTheme(Stage stage, String themeName) {
        Scene scene = stage.getScene();

        // Bisherige Theme-Klassen entfernen
        scene.getRoot().getStyleClass().removeAll("theme-dark", "theme-light", "theme-mint", "theme-random");

        // Ausgewähltes Theme anwenden
        switch (themeName) {
            case "dunkel" -> {
                // Dunkles Theme: CSS-Klasse anwenden
                scene.getRoot().getStyleClass().add("theme-dark");
                Variables.currentTheme = "dark";
            }
            case "hell" -> {
                // Helles Theme: CSS-Klasse anwenden
                scene.getRoot().getStyleClass().add("theme-light");
                Variables.currentTheme = "light";
            }
            case "mint" -> {
                // Mint Theme: CSS-Klasse anwenden
                scene.getRoot().getStyleClass().add("theme-mint");
                Variables.currentTheme = "mint";
            }
            case "zufall" -> {
                // Zufallstheme: Zufällige Hintergrund- und Textfarbe (inline CSS beibehalten)
                Color bgColor = Color.rgb(Variables.random.nextInt(256), Variables.random.nextInt(256), Variables.random.nextInt(256));
                Color textColor = Color.rgb(Variables.random.nextInt(256), Variables.random.nextInt(256), Variables.random.nextInt(256));

                String bgHex = String.format("#%02X%02X%02X",
                        (int)(bgColor.getRed()*255),
                        (int)(bgColor.getGreen()*255),
                        (int)(bgColor.getBlue()*255));
                scene.getRoot().setStyle("-fx-background-color: " + bgHex + ";");

                String textHex = String.format("#%02X%02X%02X",
                        (int)(textColor.getRed()*255),
                        (int)(textColor.getGreen()*255),
                        (int)(textColor.getBlue()*255));
                applyLabelStyles(scene.getRoot(), "-fx-text-fill: " + textHex + ";");

                Variables.currentTheme = "random";
            }
        }
    }

    private void applyLabelStyles(javafx.scene.Node node, String style) {
        // Wenn es ein Label ist, Style anwenden
        if (node instanceof Label label) {
            String currentStyle = label.getStyle();
            label.setStyle(currentStyle + " " + style);
        }

        // Rekursiv für alle Kinder anwenden, wenn es ein Parent ist
        if (node instanceof Parent parent) {
            for (javafx.scene.Node child : parent.getChildrenUnmodifiable()) {
                applyLabelStyles(child, style);
            }
        }
    }

    // Methode zum asynchronen Vorbereiten und Starten eines neuen Spiels mit Ladebildschirm
    private void prepareAndStartNewGame(Stage stage, int gameType) {
        // Einfachen Ladebildschirm anzeigen, um Ladeprozess zu verdecken
        VBox loadingLayout = new VBox(20);
        loadingLayout.setAlignment(Pos.CENTER);

        Label loadingLabel = new Label("Spiel wird vorbereitet ...");
        loadingLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        ProgressIndicator progress = new ProgressIndicator();
        progress.setPrefSize(50, 50);

        // Hinzufügen zur VBox
        loadingLayout.getChildren().addAll(loadingLabel, progress);
        rootNode.getChildren().clear();
        rootNode.getChildren().add(loadingLayout);

        // Spielvorbereitung in separatem Thread durchführen
        Thread prepThread = getPrepThread(new Thread(() -> {
            // Zeit für das Laden simulieren (bei Bedarf entfernen)
            try {
                sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // Spieltyp setzen und Zielwort laden
            preloadedGameType = gameType;
            if (preloadedTargetWord == null) {
                Variables.resetTargetWord();
                preloadedTargetWord = Wortliste.getRandomWord();
            }

            // UI-Thread mit dem vorbereiteten Spiel aktualisieren
            Platform.runLater(() -> showGameScreen(stage));
        }));
        prepThread.start();
    }

    private Thread getPrepThread(Thread preloadedGameType) {
        preloadedGameType.setDaemon(true);
        return preloadedGameType;
    }

    // Ressourcen beim Beenden der Anwendung freigeben
    @Override
    public void stop() {
        // Thread-Pool ordnungsgemäß beenden, um Ressourcenlecks zu vermeiden
        preloadService.shutdown();
    }

    // Eigene GameField-Klasse mit Überprüfungslogik für die Wordle-Spiellogik
    private static class GameFieldWithCheck extends GameField {
        private final Stage parentStage;  // Referenz auf das Hauptfenster
        private final UI uiReference;     // Referenz auf die UI-Klasse für Callbacks
        private final boolean withTimer; // Boolean für Timer-Modus (Challenge)
        private final Set<Character> incorrectLetters = new HashSet<>(); // Falsche Buchstaben im Challenge-Modus
        private final Map<Character, Button> keyboardButtons = new HashMap<>(); // Zuordnung von Buchstaben zu Tasten
        private String targetWord; // Zielwort, das erraten werden soll
        private Timeline timer; // Timer-Objekt für Challenge-Modus
        private Label timerLabel; // Label für die Anzeige der verbleibenden Zeit
        private int secondsRemaining; // Verbleibende Sekunden im Timer
        // Zwischenspeicher für die Zellen-Matrix, um wiederholte Reflection-Aufrufe zu vermeiden
        private Label[][] cellsCache;

        // Konstruktor für das Spielfeld mit individuellen Einstellungen
        public GameFieldWithCheck(String targetWord, Stage stage, UI uiReference, int rows, boolean withTimer) {
            super(rows); // Konstruktor der Elternklasse mit Zeilenanzahl
            this.targetWord = targetWord.toUpperCase(); // Zielwort in Großbuchstaben
            this.parentStage = stage; // Speichern der Stage-Referenz
            this.uiReference = uiReference; // Speichern der UI-Referenz für Callbacks
            this.withTimer = withTimer; // Timer-Modus aktivieren/deaktivieren
            // Referenz auf den Container des Hauptfensters

            // Virtuelle Tastatur erstellen für die Eingabe
            createKeyboard();

            // Tastatureingabe über physische Tastatur ermöglichen
            parentStage.getScene().setOnKeyPressed(event -> {
                // Spiel muss aktiv sein
                if (timer == null || timer.getStatus() == Animation.Status.RUNNING) {
                    KeyCode code = event.getCode();

                    // Verschiedene Tasten handhaben
                    if (code == KeyCode.ENTER) {
                        if (currentCol == 5) {
                            event.consume(); // Event konsumieren, um Doppelauslösung zu vermeiden
                            submitCurrentInput();
                        }
                    } else if (code == KeyCode.BACK_SPACE || code == KeyCode.DELETE) {
                        event.consume();
                        removeLetter();
                    } else {
                        // Buchstabentasten verarbeiten
                        String key = event.getText().toUpperCase();
                        if (key.length() == 1 && Character.isLetter(key.charAt(0))) {
                            event.consume();
                            addLetter(key);
                        }
                    }
                }
            });

            // Key-Release Events NICHT abfangen
            parentStage.getScene().setOnKeyReleased(null);

            // Sicherstellen, dass die Scene den Fokus hat
            Platform.runLater(() -> parentStage.getScene().getRoot().requestFocus());

            // Timer hinzufügen, falls Challenge-Modus (Modus 3) aktiv ist
            if (withTimer) {
                setupTimer();
            }

            // Fenster nur bei aktiviertem Debug-Modus zeigen
            if (Variables.debugMode) {
                Debug.showDebugWindow(targetWord,
                        // Callback zum Ändern des Zielworts im Debug-Modus
                        newWord -> this.targetWord = newWord,
                        // Callback zum Neustarten des Spiels
                        () -> uiReference.showGameScreen(parentStage),
                        // Callback zum Löschen der letzten Eingabe (z.B. bei Debugging)
                        () -> {
                            if (currentRow > 0) {
                                currentRow--;
                                currentCol = 0;
                                // Zellen der aktuellen Zeile zurücksetzen
                                for (int col = 0; col < 5; col++) {
                                    setLetter(currentRow, col, "");
                                    setCellColor(currentRow, col, Color.WHITE);
                                }
                                highlightCurrentCell(); // Aktuelles Eingabefeld hervorheben
                            }
                        }
                );
            }
        }

        // Methode zum Hervorheben des aktuellen Eingabefelds
        private void highlightCurrentCell() {
            // Standard-Zellenstil definieren
            String defaultCellStyle = "-fx-background-color: white; -fx-border-color: #d3d6da; -fx-border-width: 2;";
            String highlightedCellStyle = "-fx-background-color: white; -fx-border-color: #999b9e; -fx-border-width: 2;";

            // Zurücksetzen aller Zellen-Stile in der aktuellen Zeile auf den Standard
            for (int col = 0; col < 5; col++) {
                Label cell = getCellAt(currentRow, col);
                if (cell != null) {
                    // Nur zurücksetzen, wenn die Zelle nicht bereits durch checkInput gefärbt wurde
                    String currentStyle = cell.getStyle();
                    if (!currentStyle.contains("-fx-background-color: #787c7e") &&
                            !currentStyle.contains("-fx-background-color: #c9b458") &&
                            !currentStyle.contains("-fx-background-color: #6aaa64")) {

                        // Standard-Stil anwenden für alle nicht-aktiven Zellen
                        if (col != currentCol) {
                            cell.setStyle(defaultCellStyle);
                        }
                    }
                }
            }

            // Aktuelles Kästchen hervorheben, wenn die Zeile noch nicht voll ist
            if (currentCol < 5 && currentRow < rows) {
                Label cellToHighlight = getCellAt(currentRow, currentCol);
                if (cellToHighlight != null) {
                    // Nur hervorheben, wenn die Zelle nicht bereits gefärbt wurde
                    String currentStyle = cellToHighlight.getStyle();
                    if (!currentStyle.contains("-fx-background-color: #787c7e") &&
                            !currentStyle.contains("-fx-background-color: #c9b458") &&
                            !currentStyle.contains("-fx-background-color: #6aaa64")) {
                        cellToHighlight.setStyle(highlightedCellStyle);
                    }
                }
            }
        }

        // Hilfsmethode zum Abrufen des Buchstabens an einer bestimmten Position
        private String getLetterAt(int row, int col) {
            try {
                // Direkt auf den Text der Zelle zugreifen, wenn möglich
                Label cell = getCellAt(row, col);
                return cell != null ? cell.getText() : "";
            } catch (Exception e) {
                // Bei Fehler leeren String zurückgeben
                return "";
            }
        }

        // Verbesserte Methode für den Zugriff auf Zellen mit Caching
        private Label getCellAt(int row, int col) {
            // Zwischenspeicher initialisieren, falls noch nicht geschehen
            if (cellsCache == null) {
                try {
                    // Einmalige Verwendung von Reflection zum Abrufen des cells-Arrays
                    java.lang.reflect.Field cellsField = GameField.class.getDeclaredField("cells");
                    cellsField.setAccessible(true);
                    cellsCache = (Label[][]) cellsField.get(this);
                } catch (Exception e) {
                    System.err.println("Fehler beim Zugriff auf cells: " + e.getMessage());
                    return null;
                }
            }

            // Zwischengespeicherte Zellen-Matrix verwenden - deutlich schneller als Reflection
            try {
                return cellsCache[row][col];
            } catch (Exception e) {
                System.err.println("Fehler beim Zugriff auf Zelle: " + e.getMessage());
                return null;
            }
        }

        // Optimierte Methode zur Erstellung der Tastatur mit einem gemeinsamen Event-Handler
        private void createKeyboard() {
            // Tastatur-Layout erstellen mit drei Zeilen
            HBox keyboardRow1 = new HBox(7);  // Abstand erhöht von 5 auf 7
            keyboardRow1.setAlignment(Pos.CENTER); // Zeile zentrieren

            HBox keyboardRow2 = new HBox(7);  // Abstand erhöht von 5 auf 7
            keyboardRow2.setAlignment(Pos.CENTER); // Zeile zentrieren

            HBox keyboardRow3 = new HBox(7);  // Abstand erhöht von 5 auf 7
            keyboardRow3.setAlignment(Pos.CENTER); // Zeile zentrieren

            // Zeile 1: Q W E R T Z U I O P Ü
            String[] row1Keys = {"Q", "W", "E", "R", "T", "Z", "U", "I", "O", "P", "Ü"};
            for (String key : row1Keys) {
                Button keyButton = createKeyButton(key);
                keyboardRow1.getChildren().add(keyButton);
                keyboardButtons.put(key.charAt(0), keyButton);
            }

            // Zeile 2: A S D F G H J K L Ö Ä
            String[] row2Keys = {"A", "S", "D", "F", "G", "H", "J", "K", "L", "Ö", "Ä"};
            for (String key : row2Keys) {
                Button keyButton = createKeyButton(key);
                keyboardRow2.getChildren().add(keyButton);
                keyboardButtons.put(key.charAt(0), keyButton);
            }

            // Zeile 3: ENTER Y X C V B N M ß ⌫
            Button enterButton = createKeyButton("ENTER");
            enterButton.setPrefWidth(70); // Größerer Button für ENTER

            // Buchstaben der dritten Zeile
            String[] row3Keys = {"Y", "X", "C", "V", "B", "N", "M"};
            keyboardRow3.getChildren().add(enterButton);

            // Buchstaben-Tasten hinzufügen
            for (String key : row3Keys) {
                Button keyButton = createKeyButton(key);
                keyboardRow3.getChildren().add(keyButton);
                keyboardButtons.put(key.charAt(0), keyButton);
            }

            // Backspace-Taste zum Löschen von Buchstaben
            Button backspaceButton = createKeyButton("⌫");

            // Backspace-Taste zur Tastatur hinzufügen (dies fehlte!)
            keyboardRow3.getChildren().add(backspaceButton);

            // Tastaturzeilen in einen Container packen
            VBox keyboardContainer = new VBox(10);
            keyboardContainer.setAlignment(Pos.CENTER);
            keyboardContainer.getChildren().addAll(keyboardRow1, keyboardRow2, keyboardRow3);

            // Abstand über der Tastatur für bessere Optik (erhöht von 20 auf 22)
            VBox.setMargin(keyboardContainer, new Insets(22, 0, 0, 0));

            // Tastatur zum Spielfeld hinzufügen
            this.getChildren().add(keyboardContainer);

            // Optimierter Event-Handler für die virtuelle Tastatur
            EventHandler<ActionEvent> keyboardHandler = event -> {
                Button source = (Button) event.getSource();
                String text = source.getText();

                if (text.equals("ENTER")) {
                    // Enter-Taste behandeln
                    if (currentCol == 5) {
                        submitCurrentInput();
                    }
                    // Bei nicht vollständiger Eingabe keine Aktion ausführen
                } else if (text.length() == 1) {
                    // Buchstabentaste behandeln
                    if (!withTimer || !incorrectLetters.contains(text.charAt(0))) {
                        addLetter(text);
                    }
                }
            };

            // Handler auf alle Tasten anwenden - verbessert die Performance durch Wiederverwendung
            for (Button keyButton : keyboardButtons.values()) {
                keyButton.setOnAction(keyboardHandler);
            }
            enterButton.setOnAction(keyboardHandler);
            backspaceButton.setOnAction(_ -> removeLetter());
        }

        // Methode zum Erstellen einer Taste für die virtuelle Tastatur
        private Button createKeyButton(String text) {
            Button button = new Button(text);
            button.setPrefWidth(40); // Standard-Breite für Tasten
            button.setPrefHeight(40); // Höhe für Tasten
            button.setStyle("-fx-background-color: #d3d6da; -fx-background-radius: 5; -fx-text-fill: black; -fx-font-weight: bold;");
            return button;
        }

        // Methode zum Aktualisieren der Tastaturfarben nach einer Überprüfung
        private void updateKeyboard(String input, Color[] colors) {
            for (int i = 0; i < input.length(); i++) {
                char c = input.charAt(i);
                Button keyButton = keyboardButtons.get(c);
                if (keyButton == null) continue; // Falls Taste nicht gefunden

                // Nur aktualisieren, wenn der Button noch nicht grün ist
                String currentStyle = keyButton.getStyle();
                if (currentStyle.contains("-fx-background-color: #6aaa64")) continue;

                // Farbe basierend auf dem Ergebnis setzen
                if (colors[i].equals(Color.web("#6aaa64"))) {
                    // Grün für korrekte Buchstaben an korrekter Position
                    keyButton.setStyle("-fx-background-color: #6aaa64; -fx-background-radius: 5; -fx-text-fill: white; -fx-font-weight: bold;");
                } else if (colors[i].equals(Color.web("#c9b458")) && !currentStyle.contains("-fx-background-color: #6aaa64")) {
                    // Gelb für Buchstaben an falscher Position, aber nur wenn nicht bereits grün
                    keyButton.setStyle("-fx-background-color: #c9b458; -fx-background-radius: 5; -fx-text-fill: white; -fx-font-weight: bold;");
                } else if (colors[i].equals(Color.web("#787c7e")) && !currentStyle.contains("-fx-background-color: #6aaa64") && !currentStyle.contains("-fx-background-color: #c9b458")) {
                    // Grau für nicht vorhandene Buchstaben, aber nur wenn weder grün noch gelb
                    keyButton.setStyle("-fx-background-color: #787c7e; -fx-background-radius: 5; -fx-text-fill: white; -fx-font-weight: bold;");
                }
            }
        }

        // Methode zur Evaluierung der Eingabe des Nutzers
        private boolean checkInput(String input) {
            if (input == null || input.length() != 5) {
                return false;
            }

            String normalizedInput = input.toUpperCase();

            boolean isValid = Wortliste.isInWordList(normalizedInput);
            if (!isValid) {
                return false;
            }
            return true;
        }

        private void processValidInput(String normalizedInput) {
            // Hier kommt der Rest deiner ursprünglichen checkInput Logik
            final int rowToCheck = currentRow - 1;
            
            // Wort überprüfen und Farben bestimmen
            Color[] colors = CheckAlgo.checkWord(normalizedInput, targetWord);
            
            // Prüfen, ob alle Farben grün sind (Wort erraten)
            boolean allCorrect = Arrays.stream(colors).allMatch(color -> color.equals(Color.web("#6aaa64")));

            // Animation für das schrittweise Aufdecken der Ergebnisse
            PauseTransition initialPause = new PauseTransition(Duration.millis(200));
            initialPause.setOnFinished(_ -> {
                // Sequenz von Animationen erstellen, die nacheinander abgespielt werden
                SequentialTransition sequence = new SequentialTransition();

                for (int col = 0; col < 5; col++) {
                    // Animation für jeden Buchstaben erstellen
                    SequentialTransition cellAnimation = createCellAnimation(rowToCheck, col, colors[col]);
                    sequence.getChildren().add(cellAnimation);
                }

                // Nach Abschluss aller Animationen Spielstatus prüfen
                sequence.setOnFinished(_ -> {
                    // Tastatur-Farben aktualisieren
                    updateKeyboard(normalizedInput, colors);

                    // Im Challenge-Modus falsche Buchstaben zur incorrectLetters-Collection hinzufügen
                    if (withTimer) {
                        for (int i = 0; i < normalizedInput.length(); i++) {
                            // Wenn die Farbe grau ist (#787c7e), ist der Buchstabe nicht im Zielwort enthalten
                            if (colors[i].equals(Color.web("#787c7e"))) {
                                incorrectLetters.add(normalizedInput.charAt(i));
                            }
                        }
                    }

                    // Prüfen, ob gewonnen oder verloren
                    if (allCorrect) {
                        showWonDialog(targetWord, rowToCheck + 1);
                    } else if (rowToCheck + 1 >= rows) {
                        showLostDialog("Leider verloren! Das Wort war: " + targetWord);
                    }
                });

                sequence.play();
            });
            initialPause.play();
        }

        // Methode, die die komplette Animation für eine Zelle erstellt und zurückgibt
        private SequentialTransition createCellAnimation(int row, int col, Color color) {
            Label cell = getCellAt(row, col);
            SequentialTransition st = new SequentialTransition();

            if (cell == null) return st; // Sicherheitsprüfung

            // Erste Rotation (umdrehen)
            RotateTransition rotateOut = new RotateTransition(Duration.millis(250), cell);
            rotateOut.setAxis(Rotate.X_AXIS);
            rotateOut.setFromAngle(0);
            rotateOut.setToAngle(90);

            // Pause in der Mitte zum Ändern der Farbe
            PauseTransition colorPause = getColorPause(color, cell);

            // Zweite Rotation (zurückdrehen)
            RotateTransition rotateIn = new RotateTransition(Duration.millis(250), cell);
            rotateIn.setAxis(Rotate.X_AXIS);
            rotateIn.setFromAngle(90);
            rotateIn.setToAngle(0);

            // Alle Teile zur Sequenz hinzufügen
            st.getChildren().addAll(rotateOut, colorPause, rotateIn);
            return st;
        }

        // Überschreiben der addLetter-Methode für Challenge-Modus und Animations-Unterstützung
        @Override
        public void addLetter(String letter) {
            // Im Challenge-Modus keine falschen Buchstaben zulassen
            if (withTimer && incorrectLetters.contains(letter.charAt(0))) {
                return; // Buchstabe nicht hinzufügen
            }

            // Prüfen, ob wir tatsächlich einen Buchstaben hinzufügen können
            if (currentCol < 5 && currentRow < rows) {
                // Buchstaben in die Zelle setzen
                setLetter(currentRow, currentCol, letter);
                // Animation für das Hinzufügen
                animateLetterAdd(currentRow, currentCol);
                // Spaltenindex erhöhen *nach* dem Setzen und Animieren
                currentCol++;
                // Nächstes Feld hervorheben (oder keins, wenn Zeile voll)
                highlightCurrentCell();
                // Fokus sicherstellen, damit Tastatureingaben weiter funktionieren
                Platform.runLater(() -> parentStage.getScene().getRoot().requestFocus());
            }
        }

        @Override
        public void removeLetter() {
            // Prüfen, ob überhaupt ein Buchstabe gelöscht werden kann
            if (currentCol > 0 && currentRow < rows) { // Sicherstellen, dass wir nicht außerhalb des Rasters sind
                // Spaltenindex verringern *bevor* die Zelle bearbeitet wird
                currentCol--;
                // Buchstaben aus der Zelle entfernen
                setLetter(currentRow, currentCol, "");
                // Aktuelles (jetzt leeres) Feld hervorheben
                highlightCurrentCell();
                // Fokus sicherstellen, damit Tastatureingaben weiter funktionieren
                Platform.runLater(() -> parentStage.getScene().getRoot().requestFocus());
            }
        }

        // Methode für die Animation beim Hinzufügen eines Buchstabens
        private void animateLetterAdd(int row, int col) {
            Label cell = getCellAt(row, col);
            if (cell != null) {
                // Skalierungsanimation (Pop-Effekt)
                ScaleTransition st = new ScaleTransition(Duration.millis(100), cell);
                st.setFromX(0.8);
                st.setFromY(0.8);
                st.setToX(1.0);
                st.setToY(1.0);
                st.play();
            }
        }

        // Methode zum Überprüfen der aktuellen Eingabe (nach Enter)
        public void submitCurrentInput() {
            // Nur prüfen, wenn die Zeile voll ist
            if (currentCol == 5 && currentRow < rows) {
                StringBuilder sb = new StringBuilder();
                for (int col = 0; col < 5; col++) {
                    sb.append(getLetterAt(currentRow, col));
                }
                String input = sb.toString();


                // Zeile inkrementieren und Spalte zurücksetzen
                if (checkInput(input)) {
                    currentRow++;
                    currentCol = 0;
                }

                // Nächstes Feld hervorheben
                highlightCurrentCell();
            }
        }

        // Methode zum Einrichten des Timers für den Challenge-Modus
        private void setupTimer() {
            // Timer-Label erstellen und hinzufügen
            timerLabel = new Label();
            timerLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
            timerLabel.setAlignment(Pos.CENTER);
            timerLabel.setPadding(new Insets(5, 0, 10, 0)); // Abstand nach unten

            // Timer-Label über dem Spielfeld hinzufügen
            // Annahme: Das GameField ist in einem VBox-Container
            if (this.getParent() instanceof VBox parentVBox) {
                parentVBox.getChildren().addFirst(timerLabel); // An erster Stelle einfügen
            } else {
                // Fallback, falls die Struktur anders ist
                this.getChildren().addFirst(timerLabel);
            }

            // Timer initialisieren
            secondsRemaining = Variables.timerSeconds; // Zeit aus globalen Variablen holen
            timerLabel.setText("Verbleibende Zeit: " + formatTime(secondsRemaining));

            timer = new Timeline(new KeyFrame(Duration.seconds(1), _ -> {
                secondsRemaining--;
                timerLabel.setText("Verbleibende Zeit: " + formatTime(secondsRemaining));

                // Wenn die Zeit abgelaufen ist
                if (secondsRemaining <= 0) {
                    timer.stop(); // Timer stoppen
                    Platform.runLater(() -> showLostDialog("Die Zeit ist abgelaufen! Das gesuchte Wort war: " + targetWord)); // Dialog für verlorenes Spiel anzeigen
                }
            }));
            timer.setCycleCount(Timeline.INDEFINITE); // Endlos wiederholen
            timer.play(); // Timer starten
        }

        // Methode zum Formatieren der Zeit als mm:ss
        private String formatTime(int totalSeconds) {
            if (totalSeconds < 0) totalSeconds = 0; // Negative Zeit verhindern
            int minutes = totalSeconds / 60;
            int seconds = totalSeconds % 60;
            return String.format("%02d:%02d", minutes, seconds); // Formatierung mit führenden Nullen
        }

        private void showWonDialog(String targetWord, int rowsUsed) {
            if (timer != null) {
                timer.stop(); // Timer anhalten bei Gewinn
            }
            Alert alert = getAlert("Gewonnen!", "Glückwunsch! Du hast das Wort '" + targetWord + "' in " + rowsUsed + (rowsUsed == 1 ? " Versuch" : " Versuchen") + " erraten.");

            // Dialog in einer separaten UI-Aktualisierung anzeigen
            Platform.runLater(() -> {
                alert.showAndWait();
                // Nach dem Schließen des Dialogs zum Hauptmenü zurückkehren
                uiReference.showMainMenu(parentStage);
            });
        }

        // Hilfsmethode zum Erstellen eines Standard-Alert-Dialogs
        private Alert getAlert(String title, String content) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null); // Keinen Header-Text
            alert.setContentText(content);

            // Icon für den Dialog setzen (optional)
            Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
            setStageIcon(alertStage); // Wiederverwenden der Icon-Setz-Methode

            return alert;
        }

        // Dialog anzeigen, wenn das Spiel verloren wurde (keine Versuche mehr oder Zeit abgelaufen)
        private void showLostDialog(String message) {
            if (timer != null) {
                timer.stop();
            }

            Alert alert = getAlert("Verloren!", message);
            Platform.runLater(() -> {
                alert.showAndWait();
                uiReference.showMainMenu(parentStage);
            });
        }
    }
}
