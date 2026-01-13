package ressource;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.List;
import java.util.function.Consumer;

public class Debug {
    private static final List<Character> usedLetters = new ArrayList<>();
    private static final String STATS_FILE = "ressource/debug_stats.txt";
    private static final Map<String, Integer> statistics = new HashMap<>();
    private static JFrame debugFrame;
    private static JLabel wordLabel;
    private static JLabel lettersLabel;
    private static JLabel timerLabel;
    private static Consumer<String> wordChangeCallback;
    private static Runnable restartGameCallback;
    private static Runnable undoLastInputCallback;
    private static Timer timer;
    private static int seconds = 0;
    private static JCheckBox showWordCheckBox;
    private static String currentTargetWord;

    /**
     * Zeigt das Debug-Fenster an oder erstellt es, wenn der Debug-Modus aktiviert ist
     */
    public static void showDebugWindow(String targetWord, Consumer<String> changeWordCallback, Runnable restartCallback, Runnable undoCallback) {
        if (!Variables.debugMode) return;

        wordChangeCallback = changeWordCallback;
        restartGameCallback = restartCallback;
        undoLastInputCallback = undoCallback;
        currentTargetWord = targetWord;

        if (debugFrame == null) {
            SwingUtilities.invokeLater(() -> createDebugWindow(targetWord));
        } else {
            SwingUtilities.invokeLater(() -> {
                updateDebugInfo(targetWord);
                if (!debugFrame.isVisible()) {
                    debugFrame.setVisible(true);
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

    private static void setFrameIcon(JFrame frame) {
        try {
            ImageIcon icon = new ImageIcon(Objects.requireNonNull(Debug.class.getResource("/ressource/logo.png")));
            frame.setIconImage(icon.getImage());
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

        timer = new Timer(1000, e -> {
            seconds++;
            updateTimerLabel();
        });
        timer.start();
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
        resetUsedLetters();
        currentTargetWord = targetWord;

        debugFrame = new JFrame("Wordle Debug-Tool");
        setFrameIcon(debugFrame);
        debugFrame.setSize(400, 300);
        debugFrame.setMinimumSize(new Dimension(400, 300));

        JTabbedPane tabPane = new JTabbedPane();

        // Tab 1: Spielinfo
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        wordLabel = new JLabel("Lösungswort: *****");
        wordLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        wordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        showWordCheckBox = new JCheckBox("Lösungswort anzeigen");
        showWordCheckBox.setSelected(false);
        showWordCheckBox.addActionListener(e -> updateWordVisibility());
        showWordCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        lettersLabel = new JLabel("Genutzte Buchstaben: ");
        lettersLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lettersLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        timerLabel = new JLabel("Spielzeit: 0:00");
        timerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        timerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JButton restartButton = new JButton("Spiel neu starten");
        JButton undoButton = new JButton("Letzte Eingabe löschen");

        restartButton.addActionListener(e -> {
            resetUsedLetters();
            startTimer();
            incrementStat("restarts");
            if (restartGameCallback != null) {
                restartGameCallback.run();
            }
        });

        undoButton.addActionListener(e -> {
            if (undoLastInputCallback != null) {
                undoLastInputCallback.run();
                incrementStat("undos");
            }
        });

        buttonPanel.add(restartButton);
        buttonPanel.add(undoButton);

        infoPanel.add(wordLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(showWordCheckBox);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(lettersLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(timerLabel);
        infoPanel.add(Box.createVerticalStrut(15));
        infoPanel.add(buttonPanel);
        infoPanel.add(Box.createVerticalGlue());

        tabPane.addTab("Spielinfo", infoPanel);

        // Tab 2: Wort ändern
        JPanel changeWordPanel = new JPanel();
        changeWordPanel.setLayout(new BoxLayout(changeWordPanel, BoxLayout.Y_AXIS));
        changeWordPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel instructionLabel = new JLabel("Neues Lösungswort (5 Buchstaben):");
        instructionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextField wordField = new JTextField();
        wordField.setMaximumSize(new Dimension(200, 25));
        wordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton changeWordButton = new JButton("Wort ändern");
        changeWordButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        changeWordButton.addActionListener(e -> {
            String newWord = wordField.getText().toUpperCase().trim();
            if (newWord.length() == 5) {
                if (wordChangeCallback != null) {
                    wordChangeCallback.accept(newWord);
                    wordLabel.setText("Lösungswort: " + (showWordCheckBox.isSelected() ? newWord : "*****"));
                    currentTargetWord = newWord;
                    wordField.setText("");
                    incrementStat("wordChanges");
                }
            } else {
                JOptionPane.showMessageDialog(debugFrame,
                        "Das Wort muss genau 5 Buchstaben lang sein.",
                        "Fehler",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        changeWordPanel.add(instructionLabel);
        changeWordPanel.add(Box.createVerticalStrut(10));
        changeWordPanel.add(wordField);
        changeWordPanel.add(Box.createVerticalStrut(10));
        changeWordPanel.add(changeWordButton);
        changeWordPanel.add(Box.createVerticalGlue());

        tabPane.addTab("Wort ändern", changeWordPanel);

        // Tab 3: Wörterbuch
        JPanel dictionaryPanel = new JPanel();
        dictionaryPanel.setLayout(new BoxLayout(dictionaryPanel, BoxLayout.Y_AXIS));
        dictionaryPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel dictionaryLabel = new JLabel("Wörterbuch-Prüfung");
        dictionaryLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        dictionaryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField wordCheckField = new JTextField();
        wordCheckField.setMaximumSize(new Dimension(200, 25));
        wordCheckField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton checkWordButton = new JButton("Wort prüfen");
        checkWordButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextArea resultArea = new JTextArea(5, 20);
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        JScrollPane resultScroll = new JScrollPane(resultArea);
        resultScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        resultScroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        checkWordButton.addActionListener(e -> {
            String word = wordCheckField.getText().trim();
            if (!word.isEmpty()) {
                resultArea.setText("Prüfe Wort '" + word + "'...");
                boolean isValid = checkWordValidity(word);
                resultArea.setText("Das Wort '" + word + "' ist " +
                        (isValid ? "gültig ✓" : "ungültig ✗"));
                incrementStat("wordChecks");
            }
        });

        dictionaryPanel.add(dictionaryLabel);
        dictionaryPanel.add(Box.createVerticalStrut(10));
        dictionaryPanel.add(wordCheckField);
        dictionaryPanel.add(Box.createVerticalStrut(10));
        dictionaryPanel.add(checkWordButton);
        dictionaryPanel.add(Box.createVerticalStrut(10));
        dictionaryPanel.add(resultScroll);
        dictionaryPanel.add(Box.createVerticalGlue());

        tabPane.addTab("Wörterbuch", dictionaryPanel);

        // Tab 4: Statistik
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel statsHeader = new JLabel("Debug-Tool Statistiken");
        statsHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statsHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea statsArea = new JTextArea(8, 20);
        statsArea.setEditable(false);
        JScrollPane statsScroll = new JScrollPane(statsArea);
        statsScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        statsScroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton refreshStatsButton = new JButton("Statistiken aktualisieren");
        refreshStatsButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        refreshStatsButton.addActionListener(e -> {
            loadStatistics();
            String sb = "Neustarts: " + statistics.getOrDefault("restarts", 0) + "\n" +
                    "Rückgängig gemachte Eingaben: " + statistics.getOrDefault("undos", 0) + "\n" +
                    "Wortänderungen: " + statistics.getOrDefault("wordChanges", 0) + "\n" +
                    "Wortprüfungen: " + statistics.getOrDefault("wordChecks", 0) + "\n" +
                    "Debug-Sitzungen: " + statistics.getOrDefault("sessions", 0) + "\n";
            statsArea.setText(sb);
        });

        JButton resetStatsButton = new JButton("Statistiken zurücksetzen");
        resetStatsButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        resetStatsButton.addActionListener(e -> {
            statistics.clear();
            saveStatistics();
            statsArea.setText("Alle Statistiken wurden zurückgesetzt.");
        });

        statsPanel.add(statsHeader);
        statsPanel.add(Box.createVerticalStrut(10));
        statsPanel.add(statsScroll);
        statsPanel.add(Box.createVerticalStrut(10));
        statsPanel.add(refreshStatsButton);
        statsPanel.add(Box.createVerticalStrut(5));
        statsPanel.add(resetStatsButton);
        statsPanel.add(Box.createVerticalGlue());

        tabPane.addTab("Statistik", statsPanel);

        // Tab 5: Hilfe & Tipps
        JPanel hintPanel = new JPanel();
        hintPanel.setLayout(new BoxLayout(hintPanel, BoxLayout.Y_AXIS));
        hintPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel hintLabel = new JLabel("Hilfe zum Lösungswort:");
        hintLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        hintLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton showFirstLetterButton = new JButton("Ersten Buchstaben anzeigen");
        showFirstLetterButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JButton analyzeWordButton = new JButton("Wortanalyse anzeigen");
        analyzeWordButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea hintTextArea = new JTextArea(5, 20);
        hintTextArea.setEditable(false);
        hintTextArea.setLineWrap(true);
        hintTextArea.setWrapStyleWord(true);
        JScrollPane hintScroll = new JScrollPane(hintTextArea);
        hintScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        hintScroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        showFirstLetterButton.addActionListener(e -> {
            if (currentTargetWord != null && !currentTargetWord.isEmpty()) {
                hintTextArea.setText("Der erste Buchstabe des Lösungswortes ist: " + currentTargetWord.charAt(0));
            }
        });

        analyzeWordButton.addActionListener(e -> {
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

        hintPanel.add(hintLabel);
        hintPanel.add(Box.createVerticalStrut(10));
        hintPanel.add(showFirstLetterButton);
        hintPanel.add(Box.createVerticalStrut(5));
        hintPanel.add(analyzeWordButton);
        hintPanel.add(Box.createVerticalStrut(10));
        hintPanel.add(hintScroll);
        hintPanel.add(Box.createVerticalGlue());

        tabPane.addTab("Hilfe & Tipps", hintPanel);

        debugFrame.add(tabPane);
        debugFrame.setAlwaysOnTop(true);

        debugFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                debugFrame.setVisible(false);
            }
        });

        incrementStat("sessions");
        startTimer();
        refreshStatsButton.doClick();

        debugFrame.setLocationRelativeTo(null);
        debugFrame.setVisible(true);
    }

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
