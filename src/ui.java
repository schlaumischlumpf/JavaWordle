package src;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ui extends Application {
    public static boolean openSettingsOnStart = false;
    static variables var = new variables();
    Label response;
    ToggleGroup tg;
    private VBox rootNode;

    private void setStageIcon(Stage stage) {
        try {
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/ressource/logo.png")));
            stage.getIcons().add(icon);
        } catch (Exception e) {
            if (debugMode) {
                System.err.println("Fehler beim Laden des Icons: " + e.getMessage());
            }
        }
    }

    public static void noFiveLetterWords() {
        Platform.runLater(() -> {
            Stage insertFilePath = new Stage();
            startFilePathError(insertFilePath);
        });
    }

    public static void startFilePathError(Stage insertFilePath) {
        insertFilePath.setTitle("Fehler bei der Pfadangabe");
        VBox rootNode = new VBox(10);
        rootNode.setAlignment(Pos.CENTER);
        Scene scene = new Scene(rootNode, 600, 300);
        insertFilePath.setScene(scene);

        Label error = new Label("Leider konnte keine Wortliste gefunden werden. Bitte überprüfe die Pfadangabe. Aktuell ist " + var.filePath + " eingetragen.");
        TextField filePath = new TextField();
        Button btnConfirmFilePath = new Button("Pfad bestätigen");
        btnConfirmFilePath.setOnAction(_ -> {
            import_list importer = new import_list(var);
            importer.setFilePath(filePath.getText());
            importer.import_list();
            insertFilePath.close();
        });

        rootNode.getChildren().addAll(error, filePath, btnConfirmFilePath);
        insertFilePath.show();
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Wordle");
        rootNode = new VBox(10);
        rootNode.setAlignment(Pos.CENTER);
        Scene scene = new Scene(rootNode, 600, 400);
        stage.setScene(scene);
        setStageIcon(stage);

        // Prüfen, ob Einstellungsmenü direkt geöffnet werden soll; true = öffnen, false = Wordle direkt starten
        if (openSettingsOnStart) {
            showSettingsMenu(stage);
        } else {
            showMainMenu(stage);
        }

        stage.show();
    }

    private void showMainMenu(Stage stage) {
        // Hauptmenü leeren
        rootNode.getChildren().clear();
        stage.setTitle("Auswahl der Spielmodi");

        Label auswahl = new Label("Wähle einen Spielmodus");
        response = new Label("Du hast noch keinen Spielmodus gewählt");
        Button btnConfirm = new Button("Spiel mit diesem Modus starten");
        Button openSettings = new Button("Einstellungen");
        openSettings.setPrefWidth(150);

        RadioButton rbModus1 = new RadioButton("Modus 1");
        RadioButton rbModus2 = new RadioButton("Modus 2");
        RadioButton rbModus3 = new RadioButton("Modus 3");

        stage.setHeight(400);
        stage.setWidth(500);
        stage.setMinHeight(400);
        stage.setMinWidth(500);

        tg = new ToggleGroup();
        rbModus1.setToggleGroup(tg);
        rbModus2.setToggleGroup(tg);
        rbModus3.setToggleGroup(tg);
        rbModus1.setSelected(true);

        btnConfirm.setOnAction(_ -> {
            try {
                RadioButton rb = (RadioButton) tg.getSelectedToggle();
                switch (rb.getText()) {
                    case "Modus 1":
                        var.gameType = 1;
                        break;
                    case "Modus 2":
                        var.gameType = 2;
                        break;
                    case "Modus 3":
                        var.gameType = 3;
                        break;
                }

                // Zum Spielbildschirm wechseln
                showGameScreen(stage);

            } catch (Exception e) {
                if (debugMode) {
                    System.err.println("Fehler bei der Auswahl des Spielmodus: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });

        openSettings.setOnAction(_ -> showSettingsMenu(stage));

        HBox radioButtons = new HBox(10, rbModus1, rbModus2, rbModus3);
        radioButtons.setAlignment(Pos.CENTER);
        VBox.setMargin(openSettings, new Insets(50, 0, 0, 0));
        rootNode.getChildren().addAll(auswahl, radioButtons, btnConfirm, response, openSettings);
        stage.centerOnScreen();
    }

    private void showGameScreen(Stage stage) {
        // Fenster vor dem UI-Update konfigurieren
        stage.setTitle("Wordle");
        stage.setWidth(500);
        stage.setHeight(700);
        stage.setMinWidth(500);
        stage.setMinHeight(700);
        setStageIcon(stage);

        // UI-Update-Logik verzögern
        Platform.runLater(() -> {
            rootNode.getChildren().clear();

            // Eigenes GameField mit Überprüfungslogik erstellen
            VBox gameLayout = new VBox(20);
            gameLayout.setAlignment(Pos.CENTER);

            // GameField erstellen basierend auf dem Spielmodus
            variables.resetTargetWord();

            String targetWord = ressource.wortliste.getRandomWord();
            GameFieldWithCheck gameField = switch (var.gameType) {
                case 2 -> new GameFieldWithCheck(targetWord, stage, this, 4, false);
                case 3 -> new GameFieldWithCheck(targetWord, stage, this, 6, true);
                default -> new GameFieldWithCheck(targetWord, stage, this, 6, false);
            };

            // Spielmodus-spezifische Einstellungen

            Button btnBackToMenu = new Button("Zurück zum Hauptmenü");
            btnBackToMenu.setOnAction(_ -> showMainMenu(stage));

            gameLayout.getChildren().addAll(gameField, btnBackToMenu);
            rootNode.getChildren().add(gameLayout);
        });
        stage.centerOnScreen();
    }

    public void showSettingsMenu(Stage stage) {
        // Hauptmenü leeren
        rootNode.getChildren().clear();
        stage.setTitle("Einstellungen");

        // Hier den Inhalt des Einstellungsmenüs erstellen (aus der settings-Klasse)
        CheckBox debugModeCheckBox = new CheckBox("Debug-Modus aktivieren");
        debugModeCheckBox.setSelected(debugMode);
        debugModeCheckBox.setOnAction(_ -> debugMode = debugModeCheckBox.isSelected());

        CheckBox disableDuplicatesCheckBox = new CheckBox("Doppelte Buchstaben deaktivieren (nicht verfügbar)");
        disableDuplicatesCheckBox.setSelected(disableDuplicateLetters);
        disableDuplicatesCheckBox.setOnAction(_ -> disableDuplicateLetters = disableDuplicatesCheckBox.isSelected());

        Button btnBack = new Button("Zurück zum Hauptmenü");
        btnBack.setOnAction(_ -> showMainMenu(stage));

        VBox settingsContent = new VBox(20);
        settingsContent.setAlignment(Pos.CENTER);
        Label lableTitle = new Label("Einstellungen");
        lableTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        settingsContent.getChildren().addAll(
                debugModeCheckBox,
                disableDuplicatesCheckBox,
                btnBack
        );

        rootNode.getChildren().add(settingsContent);
        stage.centerOnScreen();
    }

    // Eigene GameField-Klasse mit Überprüfungslogik
    private static class GameFieldWithCheck extends ressource.gameField {
        private final String targetWord;
        private int currentRow = 0;
        private final Stage parentStage;  // Referenz auf das Hauptfenster
        private final ui uiReference;     // Referenz auf die UI-Klasse
        private final boolean withTimer;
        private Timeline timer;
        private Label timerLabel;
        private int secondsRemaining;
        private final Set<Character> incorrectLetters = new HashSet<>();

        public GameFieldWithCheck(String targetWord, Stage stage, ui uiReference, int rows, boolean withTimer) {
            super(rows);
            this.targetWord = targetWord.toUpperCase();
            this.parentStage = stage;
            this.uiReference = uiReference;
            this.withTimer = withTimer;

            // TextField-Listener überschreiben
            TextField inputField = getInputField();
            Button submitButton = (Button) ((HBox) this.getChildren().get(1)).getChildren().get(1);

            // Timer hinzufügen, falls Modus 3 aktiv ist
            if (withTimer) {
                setupTimer();

                // Bei Modus 3: Eingabe-Validierung für falsche Buchstaben hinzufügen
                inputField.textProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue.length() > oldValue.length() && newValue.length() > 0) {
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
        }

        private void setupTimer() {
            // Timer für 3:30 Minuten (210 Sekunden)
            secondsRemaining = 10;
            timerLabel = new Label("Zeit übrig: 0:10");
            timerLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
            timerLabel.setTextFill(Color.BLACK);

            // Timer am Anfang des VBox einfügen
            this.getChildren().add(0, timerLabel);

            timer = new Timeline(
                new KeyFrame(Duration.seconds(1), _ -> {
                    secondsRemaining--;
                    int minutes = secondsRemaining / 60;
                    int seconds = secondsRemaining % 60;
                    timerLabel.setText(String.format("Zeit übrig: %d:%02d", minutes, seconds));

                    // Zeit abgelaufen
                    if (secondsRemaining <= 0) {
                        timer.stop();
                        showResultDialog(false);
                    }
                })
            );

            timer.setCycleCount(Timeline.INDEFINITE);
            timer.play();
        }

        private void checkInput(String input) {
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
                    }

                    // Wort überprüfen und Farben setzen
                    Color[] colors = ressource.checkAlgo.pruefeWort(normalizedInput, targetWord);
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

            // Hauptfenster für die Ergebnisanzeige umgestalten
            VBox gameLayout = (VBox) parentStage.getScene().getRoot();
            gameLayout.getChildren().clear();

            gameLayout.setAlignment(Pos.CENTER);
            gameLayout.setPadding(new Insets(20));

            Label resultLabel = new Label(won ?
                    "Herzlichen Glückwunsch! Du hast das gesuchte Wort erraten!" :
                    "Schade! Das gesuchte Wort war: " + targetWord);
            resultLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));

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

            gameLayout.getChildren().addAll(resultLabel, buttonBox);
        }
    }
}
