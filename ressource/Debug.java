package ressource;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.application.Platform;

import java.io.*;
import java.util.*;
import java.util.function.Consumer;


public class Debug {
    private static final List<Character> usedLetters = new ArrayList<>();
    private static final String STATS_FILE = "ressource/debug_stats.txt";
    private static final Map<String, Integer> statistics = new HashMap<>();
    private static Stage debugStage;
    private static Label wordLabel;
    private static Label lettersLabel;
    private static Label timerLabel;
    private static Consumer<String> wordChangeCallback;
    private static Runnable restartGameCallback;
    private static Runnable undoLastInputCallback;
    private static Timeline timer;
    private static int seconds = 0;
    private static CheckBox showWordCheckBox;
    private static String currentTargetWord;

    /**
     * Zeigt das Debug-Fenster an oder erstellt es, wenn der Debug-Modus aktiviert ist
     */
    public static void showDebugWindow(String targetWord, Consumer<String> changeWordCallback, Runnable restartCallback, Runnable undoCallback) {
        if (!Variables.debugMode) return;

        wordChangeCallback = changeWordCallback;
        restartGameCallback = restartCallback;
        undoLastInputCallback = undoCallback;

        // Store the target word so it can be used inside runLater
        currentTargetWord = targetWord;

        if (debugStage == null) {
            // Use Platform.runLater to ensure UI operations run on JavaFX Application Thread
            Platform.runLater(() -> createDebugWindow(targetWord));
        } else {
            Platform.runLater(() -> {
                updateDebugInfo(targetWord);
                if (!debugStage.isShowing()) {
                    debugStage.show();
                }
            });
        }
    }

    /**
     * Zurücksetzen der verwendeten Buchstaben
     */
    public static void resetUsedLetters() {
        usedLetters.clear();
        updateLettersLabel();
    }

    private static void updateLettersLabel() {
        if (lettersLabel != null) {
            // Liste kopieren und alphabetisch sortieren
            List<Character> sortedLetters = new ArrayList<>(usedLetters);
            Collections.sort(sortedLetters);

            StringBuilder sb = new StringBuilder("Genutzte Buchstaben: ");
            for (int i = 0; i < sortedLetters.size(); i++) {
                sb.append(sortedLetters.get(i));
                if (i < sortedLetters.size() - 1) {
                    sb.append(", ");
                }
            }
            lettersLabel.setText(sb.toString());
        }
    }

    private static void setStageIcon(Stage stage) {
        try {
            Image icon = new Image(Objects.requireNonNull(Debug.class.getResourceAsStream("/ressource/logo.png")));
            stage.getIcons().add(icon);
        } catch (Exception e) {
            System.err.println("Logo konnte nicht geladen werden: " + e.getMessage());
        }
    }

    private static void startTimer() {
        if (timer != null) {
            timer.stop();
        }

        seconds = 0;
        updateTimerLabel();

        timer = new Timeline(new KeyFrame(Duration.seconds(1), _ -> {
            seconds++;
            updateTimerLabel();
        }));
        timer.setCycleCount(Animation.INDEFINITE);
        timer.play();
    }

    private static void updateTimerLabel() {
        if (timerLabel != null) {
            int minutes = seconds / 60;
            int remainingSeconds = seconds % 60;
            timerLabel.setText(String.format("Spielzeit: %d:%02d", minutes, remainingSeconds));
        }
    }

    private static void loadStatistics() {
        statistics.clear();
        File file = new File(STATS_FILE);

        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(":");
                    if (parts.length == 2) {
                        statistics.put(parts[0], Integer.parseInt(parts[1]));
                    }
                }
            } catch (IOException e) {
                System.err.println("Fehler beim Laden der Statistiken: " + e.getMessage());
            }
        }
    }

    private static void saveStatistics() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(STATS_FILE))) {
            for (Map.Entry<String, Integer> entry : statistics.entrySet()) {
                writer.println(entry.getKey() + ":" + entry.getValue());
            }
        } catch (IOException e) {
            System.err.println("Fehler beim Speichern der Statistiken: " + e.getMessage());
        }
    }

    private static void incrementStat(String key) {
        loadStatistics();
        statistics.put(key, statistics.getOrDefault(key, 0) + 1);
        saveStatistics();
    }

    /**
     * Prüft, ob ein Wort gültig ist
     */
    private static boolean checkWordValidity(String word) {
        // Lokale Liste mit vielen deutschen Substantiven
        try {
            InputStream inputStream = Debug.class.getResourceAsStream("/ressource/wordlist.txt");
            if (inputStream != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.trim().equalsIgnoreCase(word)) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Fehler bei Wortprüfung: " + e.getMessage());
            return false;
        }
        return false;
    }

    // Methode, mit der das Lösungswort angezeigt/ausgeblendet werden kann
    private static void updateWordVisibility() {
        if (wordLabel != null && currentTargetWord != null) {
            if (showWordCheckBox.isSelected()) {
                wordLabel.setText("Lösungswort: " + currentTargetWord);
            } else {
                wordLabel.setText("Lösungswort: *****");
            }
        }
    }

    private static void createDebugWindow(String targetWord) {
        // Buchstaben zurücksetzen
        resetUsedLetters();
        currentTargetWord = targetWord;

        debugStage = new Stage();
        debugStage.setTitle("Wordle Debug-Tool");
        debugStage.initStyle(StageStyle.DECORATED);

        // Icon hinzufügen
        setStageIcon(debugStage);

        debugStage.setWidth(400);
        debugStage.setHeight(300);
        debugStage.setMinWidth(400);
        debugStage.setMinHeight(300);

        TabPane tabPane = new TabPane();

        // Tab 1: Spielinfo
        Tab infoTab = new Tab("Spielinfo");
        infoTab.setClosable(false);

        VBox infoContent = new VBox(10);
        infoContent.setPadding(new Insets(10));

        wordLabel = new Label("Lösungswort: *****");
        wordLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

        // Möglichkeit, Lösungswort mit Checkbox einzublenden
        showWordCheckBox = new CheckBox("Lösungswort anzeigen");
        showWordCheckBox.setSelected(false);
        showWordCheckBox.setOnAction(_ -> updateWordVisibility());

        lettersLabel = new Label("Genutzte Buchstaben: ");
        lettersLabel.setFont(Font.font("Segoe UI", 14));

        // Timer Label hinzufügen
        timerLabel = new Label("Spielzeit: 0:00");
        timerLabel.setFont(Font.font("Segoe UI", 14));

        // Button-Leiste mit Restart und Undo
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button restartButton = new Button("Spiel neu starten");
        restartButton.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(restartButton, Priority.ALWAYS);

        Button undoButton = new Button("Letzte Eingabe löschen");
        undoButton.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(undoButton, Priority.ALWAYS);

        buttonBox.getChildren().addAll(restartButton, undoButton);

        restartButton.setOnAction(_ -> {
            resetUsedLetters();
            startTimer(); // Timer neu starten
            incrementStat("restarts");
            if (restartGameCallback != null) {
                restartGameCallback.run();
            }
        });

        undoButton.setOnAction(_ -> {
            if (undoLastInputCallback != null) {
                undoLastInputCallback.run();
                incrementStat("undos");
            }
        });

        infoContent.getChildren().addAll(wordLabel, showWordCheckBox, lettersLabel, timerLabel, buttonBox);
        infoTab.setContent(infoContent);

        // Tab 2: Wort ändern
        Tab changeWordTab = new Tab("Wort ändern");
        changeWordTab.setClosable(false);

        VBox changeWordContent = new VBox(10);
        changeWordContent.setPadding(new Insets(10));

        Label instructionLabel = new Label("Neues Lösungswort (5 Buchstaben):");
        TextField wordField = new TextField();
        wordField.setMaxWidth(200);

        Button changeWordButton = getChangeWordButton(wordField);

        changeWordContent.getChildren().addAll(instructionLabel, wordField, changeWordButton);
        changeWordTab.setContent(changeWordContent);

        // Tab 3: Wörterbuch
        Tab dictionaryTab = new Tab("Wörterbuch");
        dictionaryTab.setClosable(false);

        VBox dictionaryContent = new VBox(10);
        dictionaryContent.setPadding(new Insets(10));

        Label dictionaryLabel = new Label("Wörterbuch-Prüfung");
        dictionaryLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        TextField wordCheckField = new TextField();
        wordCheckField.setPromptText("Wort eingeben");
        wordCheckField.setMaxWidth(200);

        Button checkWordButton = new Button("Wort prüfen");
        TextArea resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setPrefRowCount(5);
        resultArea.setWrapText(true);

        checkWordButton.setOnAction(_ -> {
            String word = wordCheckField.getText().trim();
            if (!word.isEmpty()) {
                resultArea.setText("Prüfe Wort '" + word + "'...");
                boolean isValid = checkWordValidity(word);
                resultArea.setText("Das Wort '" + word + "' ist " +
                        (isValid ? "gültig ✓" : "ungültig ✗"));

                incrementStat("wordChecks");
            }
        });

        dictionaryContent.getChildren().addAll(dictionaryLabel, wordCheckField, checkWordButton, resultArea);
        dictionaryTab.setContent(dictionaryContent);

        // Tab 4: Statistik
        Tab statsTab = new Tab("Statistik");
        statsTab.setClosable(false);

        VBox statsContent = new VBox(10);
        statsContent.setPadding(new Insets(10));

        Label statsHeader = new Label("Debug-Tool Statistiken");
        statsHeader.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        TextArea statsArea = new TextArea();
        statsArea.setEditable(false);
        statsArea.setPrefRowCount(8);

        Button refreshStatsButton = getRefreshStatsButton(statsArea);

        Button resetStatsButton = new Button("Statistiken zurücksetzen");
        resetStatsButton.setOnAction(_ -> {
            statistics.clear();
            saveStatistics();
            statsArea.setText("Alle Statistiken wurden zurückgesetzt.");
        });

        statsContent.getChildren().addAll(statsHeader, statsArea, refreshStatsButton, resetStatsButton);
        statsTab.setContent(statsContent);

        // Tab 5: Hilfe & Tipps
        Tab hintTab = new Tab("Hilfe & Tipps");
        hintTab.setClosable(false);

        VBox hintContent = new VBox(10);
        hintContent.setPadding(new Insets(10));

        Label hintLabel = new Label("Hilfe zum Lösungswort:");
        hintLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        Button showFirstLetterButton = new Button("Ersten Buchstaben anzeigen");
        Button analyzeWordButton = new Button("Wortanalyse anzeigen");

        TextArea hintTextArea = new TextArea();
        hintTextArea.setEditable(false);
        hintTextArea.setPrefRowCount(5);
        hintTextArea.setWrapText(true);

        showFirstLetterButton.setOnAction(_ -> {
            if (currentTargetWord != null && !currentTargetWord.isEmpty()) {
                hintTextArea.setText("Der erste Buchstabe des Lösungswortes ist: " + currentTargetWord.charAt(0));
            }
        });

        analyzeWordButton.setOnAction(_ -> {
            if (currentTargetWord != null && !currentTargetWord.isEmpty()) {
                int vowels = 0;
                for (char c : currentTargetWord.toCharArray()) {
                    if ("AEIOUÄÖÜ".indexOf(c) >= 0) {
                        vowels++;
                    }
                }
                hintTextArea.setText(String.format(
                        "Wortanalyse:\n- Länge: %d Buchstaben\n- Vokale: %d\n- Konsonanten: %d",
                        currentTargetWord.length(), vowels, currentTargetWord.length() - vowels
                ));
            }
        });

        hintContent.getChildren().addAll(hintLabel, showFirstLetterButton, analyzeWordButton, hintTextArea);
        hintTab.setContent(hintContent);

        tabPane.getTabs().addAll(infoTab, changeWordTab, dictionaryTab, statsTab, hintTab);

        Scene scene = new Scene(tabPane, 350, 350);
        debugStage.setScene(scene);
        debugStage.setAlwaysOnTop(true);

        debugStage.setOnCloseRequest(e -> {
            debugStage.hide();
            e.consume();
        });

        // Statistik für neue Sitzung erhöhen
        incrementStat("sessions");
        // Timer starten
        startTimer();
        // Refresh Statistiken
        refreshStatsButton.fire();

        debugStage.show();
    }

    private static Button getRefreshStatsButton(TextArea statsArea) {
        Button refreshStatsButton = new Button("Statistiken aktualisieren");
        refreshStatsButton.setOnAction(_ -> {
            loadStatistics();
            String sb = "Neustarts: " + statistics.getOrDefault("restarts", 0) + "\n" +
                    "Rückgängig gemachte Eingaben: " + statistics.getOrDefault("undos", 0) + "\n" +
                    "Wortänderungen: " + statistics.getOrDefault("wordChanges", 0) + "\n" +
                    "Wortprüfungen: " + statistics.getOrDefault("wordChecks", 0) + "\n" +
                    "Debug-Sitzungen: " + statistics.getOrDefault("sessions", 0) + "\n";
            statsArea.setText(sb);
        });
        return refreshStatsButton;
    }

    private static Button getChangeWordButton(TextField wordField) {
        Button changeWordButton = new Button("Wort ändern");
        changeWordButton.setOnAction(_ -> {
            String newWord = wordField.getText().toUpperCase().trim();
            if (newWord.length() == 5) {
                if (wordChangeCallback != null) {
                    wordChangeCallback.accept(newWord);
                    wordLabel.setText("Lösungswort: " + newWord);
                    wordField.clear();
                    incrementStat("wordChanges");
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Fehler");
                alert.setHeaderText("Ungültige Eingabe");
                alert.setContentText("Das Wort muss genau 5 Buchstaben lang sein.");
                alert.showAndWait();
            }
        });
        return changeWordButton;
    }

    // Aktualisierung der Debuginformationen
    public static void updateDebugInfo(String targetWord) {
        currentTargetWord = targetWord;
        if (wordLabel != null) {
            if (showWordCheckBox != null && showWordCheckBox.isSelected()) {
                wordLabel.setText("Lösungswort: " + targetWord);
            } else {
                wordLabel.setText("Lösungswort: *****");
            }
        }
    }

}