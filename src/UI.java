// UI.java
// Stand: 23.04.2025
// Autoren: Lennart und Moritz

package src;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;
import ressource.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static ressource.Variables.disableDuplicateLetters;

/*
    Über diese Klasse:
    Die UI-Klasse ist für die Benutzeroberfläche des Spiels verantwortlich.
    Sie enthält Methoden zum Erstellen und Verwalten der verschiedenen Anwendungsfenster,
    einschließlich des Hauptmenüs, des Einstellungsmenüs und des Spiels.
*/

public class UI extends Application {
    // Boolean, damit die Einstellungen nicht beim Starten der Anwendung geöffnet werden
    public static boolean openSettingsOnStart = false;

    // Erstellung einer ToggleGroup
    ToggleGroup tg;

    // Variablen für das Precaching
    public String preloadedTargetWord;
    public int preloadedGameType = 1; // Standard: Normal-Modus

    // Container für die vertikale Anordnung der Elemente
    private static VBox rootNode;

    // Icon für die Anwendung oben im Fenster
    private void setStageIcon(Stage stage) {
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/ressource/logo.png")));
        stage.getIcons().add(icon);
    }

    // Startmethode der JavaFX-Anwendung
    @Override
    public void start(Stage stage) {
        // Fenstertitel
        stage.setTitle("Wordle");

        // Vertikaler Layout-Container mit einem Abstand von 10px
        rootNode = new VBox(10);

        // Padding für den Container; Anordnung im Center der Stage
        rootNode.setAlignment(Pos.CENTER);

        // Neue Scene mit dem Container und einer Breite von 600px und Höhe von 400px
        Scene scene = new Scene(rootNode, 600, 400);

        // Hinzufügen der Scene zur Stage
        stage.setScene(scene);

        // Icon wird hier für die Stage festgelegt
        setStageIcon(stage);

        // Prüfen, ob Einstellungsmenü direkt geöffnet werden soll; true = öffnen, false = Wordle direkt starten
        if (openSettingsOnStart) {
            showSettingsMenu(stage);
        } else {
            showMainMenu(stage);
        }

        // Die Stage wird geöffnet
        stage.show();
    }

    // Methode zum Erstellen des Hauptmenüs
    private void showMainMenu(Stage stage) {
        // Hauptmenü leeren
        rootNode.getChildren().clear();

        // Titel des Fensters
        stage.setTitle("Hauptmenü");

        // Label für Spielmodusauswahl
        Label auswahl = new Label("Wähle einen Spielmodus");

        // Buttons für Spielstart und Einstellungsmenü
        Button btnConfirm = new Button("Spiel mit diesem Modus starten");
        Button openSettings = new Button("Einstellungen");

        // Breite des Einstellungsbuttons auf 150px
        openSettings.setPrefWidth(150);

        // Erstellung von Radiobuttons für die drei Spielmodi
        RadioButton rbModus1 = new RadioButton("Normal");
        RadioButton rbModus2 = new RadioButton("Schwer");
        RadioButton rbModus3 = new RadioButton("Challenge");

        // Festlegung der Fenstergröße
        stage.setHeight(450);
        stage.setWidth(500);

        // Minimale Fenstergröße soll der festgelegten Größe entsprechen
        stage.setMinHeight(450);
        stage.setMinWidth(500);

        // Neue ToggleGroup für die Radiobuttons
        tg = new ToggleGroup();

        // Zuweisung der ToggleGroup zu den Radiobuttons
        rbModus1.setToggleGroup(tg);
        rbModus2.setToggleGroup(tg);
        rbModus3.setToggleGroup(tg);
        rbModus1.setSelected(true);

        // Listener für Radiobuttons, die das Zielwort vorladen
        rbModus1.setOnAction(_ -> preloadTargetWord(1));
        rbModus2.setOnAction(_ -> preloadTargetWord(2));
        rbModus3.setOnAction(_ -> preloadTargetWord(3));

        // Da rbModus1 standardmäßig ausgewählt ist, initial vorladen
        preloadTargetWord(1);

        // Aktion bei Klick auf den Startbutton
        btnConfirm.setOnAction(_ -> {
            // Speicherung des ausgewählten Radiobuttons
            RadioButton rb = (RadioButton) tg.getSelectedToggle();
            switch (rb.getText()) {
                case "Normal" -> preloadedGameType = 1;
                case "Schwer" -> preloadedGameType = 2;
                case "Challenge" -> preloadedGameType = 3;
            }

            // Zum Spielbildschirm wechseln - der vorgeladene Wert wird verwendet
            showGameScreen(stage);
        });

        // Öffnen des Einstellungsmenüs bei Klick auf Einstellungsmenü
        openSettings.setOnAction(_ -> showSettingsMenu(stage));

        // Horizontale Anordnung der Radiobuttons in horizontalem Layoutcontainer
        HBox radioButtons = new HBox(25, rbModus1, rbModus2, rbModus3);

        // Anordnung der Radiobuttons in Fenstermitte
        radioButtons.setAlignment(Pos.CENTER);

        // 50px Abstand über dem Einstellungsbutton
        VBox.setMargin(openSettings, new Insets(50, 0, 0, 0));

        // Vertikale Abstandshalter erstellen
        Region spacer1 = new Region();
        spacer1.setPrefHeight(5);
        Region spacer2 = new Region();
        spacer2.setPrefHeight(5);
        Region spacer3 = new Region();
        spacer3.setPrefHeight(20);

        // Elemente zum rootNode hinzufügen
        rootNode.getChildren().addAll(
                auswahl,
                spacer1,
                radioButtons,
                spacer2,
                btnConfirm,
                spacer3,
                openSettings
        );

        // Fenster zentrieren
        stage.centerOnScreen();
    }

    private void preloadTargetWord(int gameType) {
        preloadedGameType = gameType;
        Variables.resetTargetWord();
        preloadedTargetWord = Wortliste.getRandomWord();
    }

    private void showGameScreen(Stage stage) {
        // UI-Update-Logik verzögern durch Platform.runLater
        Platform.runLater(() -> {
            // Fenstereigenschaften setzen
            stage.setTitle("Wordle");
            stage.setWidth(500);
            stage.setHeight(700);
            stage.setMinWidth(500);
            stage.setMinHeight(700);
            setStageIcon(stage);

            //Debug-Label zurücksetzen
            Debug.hideDebugInfo(rootNode);

            // UI vorbereiten
            rootNode.getChildren().clear();
            VBox gameLayout = new VBox(20);
            gameLayout.setAlignment(Pos.CENTER);

            // Vorgeladenes Wort und Spieltyp verwenden, statt neu zu generieren
            String targetWord = preloadedTargetWord;
            GameFieldWithCheck gameField = switch (preloadedGameType) {
                case 2 -> new GameFieldWithCheck(targetWord, stage, this, 4, false, rootNode);
                case 3 -> new GameFieldWithCheck(targetWord, stage, this, 6, true, rootNode);
                default -> new GameFieldWithCheck(targetWord, stage, this, 6, false, rootNode);
            };

            Button btnBackToMenu = new Button("Zurück zum Hauptmenü");
            btnBackToMenu.setOnAction(_ -> showMainMenu(stage));

            gameLayout.getChildren().addAll(gameField, btnBackToMenu);
            rootNode.getChildren().add(gameLayout);
            stage.centerOnScreen();

            // Sofort neues Wort für nächste Runde vorladen
            preloadNextWord();
        });
    }

    // Methode zum Vorladen des nächsten Worts im Hintergrund
    private void preloadNextWord() {
        Thread preloadThread = new Thread(() -> {
            // Im Hintergrund neues Wort für nächste Runde vorbereiten
            Variables.resetTargetWord();
            preloadedTargetWord = Wortliste.getRandomWord();
        });
        preloadThread.setDaemon(true);
        preloadThread.start();
    }

    public void showSettingsMenu(Stage stage) {
        // Hauptmenü leeren
        rootNode.getChildren().clear();
        stage.setTitle("Einstellungen");

        Label settingsLabel = new Label("Allgemeine Einstellungen");
        settingsLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        CheckBox debugModeCheckBox = new CheckBox("Debug-Modus aktivieren");
        debugModeCheckBox.setSelected(Variables.debugMode);
        debugModeCheckBox.setOnAction(_ -> Variables.debugMode = debugModeCheckBox.isSelected());

        CheckBox disableDuplicatesCheckBox = new CheckBox("Doppelte Buchstaben deaktivieren (nicht verfügbar)");
        disableDuplicatesCheckBox.setSelected(disableDuplicateLetters);
        disableDuplicatesCheckBox.setOnAction(_ -> disableDuplicateLetters = disableDuplicatesCheckBox.isSelected());

        // Label über dem Slider
        Label sliderLabel = new Label("Timereinstellung für den Challenge-Modus");
        sliderLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        // Slider, mit dem man die Timerzeit einstellen kann
        Slider setTimerValue = new Slider(30, 360,210);
        setTimerValue.setMajorTickUnit(30); // Schrittweite
        setTimerValue.setMinorTickCount(0);
        setTimerValue.setSnapToTicks(true);
        setTimerValue.setPrefWidth(350); // Der Slider soll etwa 70-80 % des Fensters einnehmen
        setTimerValue.setShowTickMarks(true);
        setTimerValue.setShowTickLabels(false);

        // Anzeigeeinstellungen für den Slider

        // Nur die Min- und Max-Werte anzeigen auf dem Slider
        HBox sliderBox = new HBox(10);
        sliderBox.setAlignment(Pos.CENTER);
        Label minLabel = new Label("30s");
        Label maxLabel = new Label("360s");
        sliderBox.getChildren().addAll(minLabel, setTimerValue, maxLabel);

        // Label für die Werte
        Label sliderValueLabel = new Label("Eingestellte Timerzeit: 210 Sekunden");

        // Wert-Listener für den Sliderwert
        setTimerValue.valueProperty().addListener((_, _, newValue) -> {
            int timerValue = newValue.intValue();
            sliderValueLabel.setText("Eingestellte Timerzeit: " + timerValue + " Sekunden");

            // Nun soll die eingestellte Zeit in variables gespeichert werden
            Variables.timerSeconds = timerValue;
        });

        Button btnBack = new Button("Zurück zum Hauptmenü");
        btnBack.setOnAction(_ -> showMainMenu(stage));

        VBox settingsContent = new VBox(20);
        settingsContent.setAlignment(Pos.CENTER);

        Label lableTitle = new Label("Einstellungen");
        lableTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));

        Region spacer5 = new Region();
        spacer5.setPrefHeight(5);
        Region spacer10 = new Region();
        spacer10.setPrefHeight(10);


        // Alle UI-Elemente zum Settings-Container hinzufügen
        settingsContent.getChildren().addAll(
                settingsLabel,
                debugModeCheckBox,
                disableDuplicatesCheckBox,
                spacer10,
                sliderLabel,
                sliderBox,
                sliderValueLabel,
                spacer5,  // 5px Abstand
                btnBack
        );


        rootNode.getChildren().add(settingsContent);
        stage.centerOnScreen();
    }

    // Eigene GameField-Klasse mit Überprüfungslogik
    private static class GameFieldWithCheck extends GameField {
        private String targetWord; // Zielwort
        private final Stage parentStage;  // Referenz auf das Hauptfenster
        private final UI uiReference;     // Referenz auf die UI-Klasse
        private final boolean withTimer; // Boolean für Timer
        private final Set<Character> incorrectLetters = new HashSet<>();
        private int currentRow = 0; // Aktuelle Zeile; wird bei jedem Versuch erhöht, beginnt mit 0
        private final VBox rootNode; // Referenz auf den VBox-Containers
        private Timeline timer; // Timer-Objekt
        private Label timerLabel; // Label für den Timer
        private int secondsRemaining; // verbleibende Sekunden



        public GameFieldWithCheck(String targetWord, Stage stage, UI uiReference, int rows, boolean withTimer, VBox rootNode) {
            super(rows);
            this.targetWord = targetWord.toUpperCase();
            this.parentStage = stage;
            this.uiReference = uiReference;
            this.withTimer = withTimer;
            this.rootNode = rootNode;

            // TextField-Listener überschreiben
            TextField inputField = getInputField();
            Button submitButton = (Button) ((HBox) this.getChildren().get(1)).getChildren().get(1);

            // Timer hinzufügen, falls Modus 3 aktiv ist
            if (withTimer) {
                setupTimer();

                // Bei Modus 3: Eingabe-Validierung für falsche Buchstaben hinzufügen
                inputField.textProperty().addListener((_, oldValue, newValue) -> {
                    if (newValue.length() > oldValue.length()) {
                        // Prüfen, ob der neueste Buchstabe falsch ist
                        char lastChar = Character.toUpperCase(newValue.charAt(newValue.length() - 1));
                        if (incorrectLetters.contains(lastChar)) {
                            // Falschen Buchstaben entfernen
                            inputField.setText(oldValue);
                        }
                    }
                });
            }

            // Input-Handler überschreiben
            submitButton.setOnAction(_ -> checkInput(inputField.getText()));
            inputField.setOnAction(_ -> checkInput(inputField.getText()));
            Debug.showDebugWindow(targetWord,
                // Callback zum Ändern des Zielworts
                newWord -> this.targetWord = newWord,
                // Callback zum Neustarten des Spiels
                () -> uiReference.showGameScreen(parentStage),
                // Callback zum Löschen der letzten Eingabe
                () -> {
                    if (currentRow > 0) {
                        currentRow--;
                        // Zellen der aktuellen Zeile zurücksetzen
                        for (int col = 0; col < 5; col++) {
                            setLetter(currentRow, col, "");
                            setCellColor(currentRow, col, Color.WHITE);
                        }
                    }
                }
            );
        }

        private void setupTimer() {
            secondsRemaining = Variables.timerSeconds;
            int minutes = secondsRemaining / 60;
            int seconds = secondsRemaining % 60;

            // Label mit dem korrekten initialen Wert
            timerLabel = new Label(String.format("Verbleibende Zeit: %d:%02d", minutes, seconds));
            timerLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
            timerLabel.setTextFill(Color.BLACK);

            // Timer am Anfang des VBox einfügen
            this.getChildren().addFirst(timerLabel); // Timer-Label an den Anfang der VBox setzen

            timer = new Timeline(
                    new KeyFrame(Duration.seconds(1), _ -> {
                        secondsRemaining--;
                        int currentMinutes = secondsRemaining / 60; // Variablenname geändert
                        int currentSeconds = secondsRemaining % 60; // Variablenname geändert
                        timerLabel.setText(String.format("Verbleibende Zeit: %d:%02d", currentMinutes, currentSeconds)); // Formatierung der Zeit im Label

                        // Wenn die Zeit abgelaufen ist, soll das Spiel beendet werden
                        if (secondsRemaining <= 0) {
                            timer.stop(); // Timer wird beendet
                            showResultDialog(false); // Da das Wort nicht erraten wurde, wird false übergeben
                        }
                    })
            );

            timer.setCycleCount(Timeline.INDEFINITE); // Timer läuft unendlich oft
            timer.play(); // Timer starten
        }

        private void checkInput(String input) {
            // Prüfen auf Debug-Kommando
            if (Debug.isDebugInput(input)) {
                // Hier wird das Debug-Fenster geöffnet statt des Labels
                Debug.showDebugWindow(targetWord,
                        // Callback zum Ändern des Zielworts
                        newWord -> this.targetWord = newWord,
                        // Callback zum Neustarten des Spiels
                        () -> uiReference.showGameScreen(parentStage)
                );
                TextField inputField = getInputField();
                inputField.clear();
                return;
            }

            // Lokale Kopie der Eingabe erstellen
            final String normalizedInput = input.toUpperCase().trim();

            if (normalizedInput.length() == 5 && currentRow < getRows()) {
                // Textfeld sofort leeren für besseres Feedback
                TextField inputField = getInputField();
                inputField.clear();

                // Logik in Platform.runLater auslagern, um UI-Thread nicht zu blockieren
                Platform.runLater(() -> {
                    // Buchstaben setzen
                    for (int col = 0; col < 5; col++) {
                        setLetter(currentRow, col, String.valueOf(normalizedInput.charAt(col)));
                        // Verwendete Buchstaben zum Debug-Fenster hinzufügen
                        Debug.addUsedLetter(normalizedInput.charAt(col));
                    }

                    // Wort überprüfen und Farben setzen
                    Color[] colors = CheckAlgo.pruefeWort(normalizedInput, targetWord);
                    for (int col = 0; col < 5; col++) {
                        setCellColor(currentRow, col, colors[col]);

                        // Bei Modus 3: Falsche Buchstaben für zukünftige Eingaben blockieren
                        if (withTimer && colors[col] == Color.GRAY) {
                            incorrectLetters.add(normalizedInput.charAt(col));
                        }
                    }

                    // Prüfen, ob das Wort erraten wurde
                    boolean allGreen = true;
                    for (Color color : colors) {
                        if (!color.equals(Color.GREEN)) {
                            allGreen = false;
                            break;
                        }
                    }

                    if (allGreen) {
                        if (withTimer && timer != null) {
                            timer.stop();
                        }
                        showResultDialog(true);
                    } else if (currentRow == getRows() - 1) {
                        if (withTimer && timer != null) {
                            timer.stop();
                        }
                        showResultDialog(false);
                    } else {
                        currentRow++;
                    }
                });
            }
        }

        private void showResultDialog(boolean won) {
            // Timer stoppen, falls aktiv
            if (withTimer && timer != null) {
                timer.stop();
            }

            // Debug-Fenster schließen, wenn Spiel beendet
            Debug.closeDebugWindow();

            // Hauptfenster für die Ergebnisanzeige umgestalten
            rootNode.getChildren().clear();
            rootNode.setAlignment(Pos.CENTER);
            rootNode.setPadding(new Insets(20));

            Label resultLabel = new Label(won ?
                    "Herzlichen Glückwunsch!\nDu hast das gesuchte Wort erraten!" :
                    "Schade! Das gesuchte Wort war: " + targetWord);
            resultLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
            resultLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
            resultLabel.setAlignment(Pos.CENTER);

            Button newGameButton = new Button("Neues Spiel");
            Button mainMenuButton = new Button("Zurück zum Hauptmenü");

            HBox buttonBox = new HBox(20);
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.getChildren().addAll(newGameButton, mainMenuButton);

            // Buttons stylen
            newGameButton.setPrefWidth(150);
            mainMenuButton.setPrefWidth(150);

            newGameButton.setOnAction(_ -> uiReference.showGameScreen(parentStage));

            mainMenuButton.setOnAction(_ -> uiReference.showMainMenu(parentStage));

            // Vertikalen Abstand hinzufügen
            VBox.setMargin(buttonBox, new Insets(30, 0, 0, 0));

            rootNode.getChildren().addAll(resultLabel, buttonBox);
        }
    }
}