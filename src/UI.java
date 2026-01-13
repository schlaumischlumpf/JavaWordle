// UI.java
// Stand: 28.05.2025
// Autoren: Lennart und Moritz
// Konvertiert zu Swing

package src;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import ressource.*;

// Diese Klasse ist für die Benutzeroberfläche des Spiels verantwortlich.
// Sie enthält Methoden zum Erstellen und Verwalten der verschiedenen Anwendungsfenster,
// einschließlich des Hauptmenüs, des Einstellungsmenüs und des Spiels.
public class UI {
    // Boolean, damit die Einstellungen nicht beim Starten der Anwendung geöffnet werden
    public static boolean openSettingsOnStart = false;
    
    // Hauptfenster der Anwendung
    private JFrame mainFrame;
    
    // Container für die Elemente im Hauptfenster
    private JPanel contentPanel;
    
    // Dedizierter Thread-Pool für das Vorladen von Wörtern im Hintergrund
    private final ExecutorService preloadService = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "VorladeThread");
        t.setDaemon(true);
        return t;
    });
    
    // Variablen für das Precaching (vorladen von Wörtern für bessere Performance)
    public String preloadedTargetWord;
    public int preloadedGameType = 1; // Standard: Normal-Modus
    
    // Button group für die Radiobuttons im Hauptmenü
    ButtonGroup modeButtonGroup;
    
    // Methode zum Setzen des Icons in der Titelleiste des Fensters
    private void setFrameIcon(JFrame frame) {
        try {
            ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/ressource/logo.png")));
            frame.setIconImage(icon.getImage());
        } catch (Exception e) {
            System.err.println("Logo konnte nicht geladen werden: " + e.getMessage());
        }
    }
    
    // Startmethode der Swing-Anwendung
    public void start() {
        // Frame erstellen
        mainFrame = new JFrame("Wordle");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(600, 400);
        mainFrame.setMinimumSize(new Dimension(600, 400));
        
        // Content Panel erstellen
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        mainFrame.setContentPane(contentPanel);
        
        // Icon asynchron laden
        SwingUtilities.invokeLater(() -> setFrameIcon(mainFrame));
        
        // Prüfen, ob Einstellungsmenü direkt geöffnet werden soll
        if (openSettingsOnStart) {
            showSettingsMenu();
        } else {
            showMainMenu();
        }
        
        // Frame zentrieren und anzeigen
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
        
        // Optimiertes Vorladen der Wortliste im Hintergrund
        new Thread(Wortliste::getRandomWord).start();
    }
    
    // Methode zum Erstellen und Anzeigen des Hauptmenüs
    private void showMainMenu() {
        // Hauptmenü leeren
        contentPanel.removeAll();
        
        // Titel des Fensters setzen
        mainFrame.setTitle("Hauptmenü");
        
        // Panel mit vertikaler Anordnung
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Label für Spielmodusauswahl
        JLabel auswahlLabel = new JLabel("Wähle einen Spielmodus");
        auswahlLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        auswahlLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Buttons für Spielstart und Einstellungsmenü
        JButton btnConfirm = new JButton("Spiel mit diesem Modus starten");
        btnConfirm.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JButton openSettings = new JButton("Einstellungen");
        openSettings.setPreferredSize(new Dimension(150, 30));
        openSettings.setMaximumSize(new Dimension(150, 30));
        openSettings.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Radiobuttons für die drei Spielmodi
        JRadioButton rbModus1 = new JRadioButton("Normal");
        JRadioButton rbModus2 = new JRadioButton("Schwer");
        JRadioButton rbModus3 = new JRadioButton("Challenge");
        
        // ButtonGroup für die Radiobuttons
        modeButtonGroup = new ButtonGroup();
        modeButtonGroup.add(rbModus1);
        modeButtonGroup.add(rbModus2);
        modeButtonGroup.add(rbModus3);
        rbModus1.setSelected(true); // Standardmäßig Normal-Modus
        
        // Listener für Radiobuttons
        rbModus1.addActionListener(e -> preloadTargetWord(1));
        rbModus2.addActionListener(e -> preloadTargetWord(2));
        rbModus3.addActionListener(e -> preloadTargetWord(3));
        
        // Initial vorladen
        preloadTargetWord(1);
        
        // Aktion bei Klick auf Startbutton
        btnConfirm.addActionListener(e -> {
            if (rbModus1.isSelected()) {
                prepareAndStartNewGame(1);
            } else if (rbModus2.isSelected()) {
                prepareAndStartNewGame(2);
            } else if (rbModus3.isSelected()) {
                prepareAndStartNewGame(3);
            }
        });
        
        // Öffnen des Einstellungsmenüs
        openSettings.addActionListener(e -> showSettingsMenu());
        
        // Panel für Radiobuttons
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 0));
        radioPanel.add(rbModus1);
        radioPanel.add(rbModus2);
        radioPanel.add(rbModus3);
        radioPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Elemente zum Panel hinzufügen
        menuPanel.add(Box.createVerticalGlue());
        menuPanel.add(auswahlLabel);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        menuPanel.add(radioPanel);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        menuPanel.add(btnConfirm);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 70)));
        menuPanel.add(openSettings);
        menuPanel.add(Box.createVerticalGlue());
        
        contentPanel.add(menuPanel);
        
        // Frame aktualisieren
        mainFrame.setSize(600, 500);
        mainFrame.setMinimumSize(new Dimension(600, 500));
        mainFrame.setLocationRelativeTo(null);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    // Methode zum Vorladen des Zielworts
    private void preloadTargetWord(int gameType) {
        preloadedGameType = gameType;
        preloadService.submit(() -> {
            Variables.resetTargetWord();
            preloadedTargetWord = Wortliste.getRandomWord();
        });
    }
    
    // Verbesserte Methode zum Anzeigen des Spielbildschirms
    private void showGameScreen() {
        mainFrame.setTitle("Wordle");
        mainFrame.setSize(700, 800);
        mainFrame.setMinimumSize(new Dimension(700, 800));
        
        // Content Panel leeren
        contentPanel.removeAll();
        
        // Game Layout erstellen
        JPanel gameLayout = new JPanel();
        gameLayout.setLayout(new BoxLayout(gameLayout, BoxLayout.Y_AXIS));
        gameLayout.setBorder(new EmptyBorder(22, 10, 10, 10));
        
        // Lade-Indikator während Initialisierung
        JLabel loadingLabel = new JLabel("Spiel wird geladen...");
        loadingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        gameLayout.add(Box.createVerticalGlue());
        gameLayout.add(loadingLabel);
        gameLayout.add(Box.createVerticalGlue());
        
        contentPanel.add(gameLayout);
        contentPanel.revalidate();
        contentPanel.repaint();
        
        // Spielfeld in Hintergrund-Thread laden
        new Thread(() -> {
            GameFieldWithCheck gameField = getGameFieldWithCheck();
            
            // UI nach Initialisierung aktualisieren
            SwingUtilities.invokeLater(() -> {
                gameLayout.removeAll();
                
                JButton btnBackToMenu = new JButton("Zurück zum Hauptmenü");
                btnBackToMenu.addActionListener(e -> showMainMenu());
                btnBackToMenu.setAlignmentX(Component.CENTER_ALIGNMENT);
                
                gameField.setAlignmentX(Component.CENTER_ALIGNMENT);
                
                gameLayout.add(Box.createVerticalStrut(10));
                gameLayout.add(gameField);
                gameLayout.add(Box.createVerticalStrut(10));
                gameLayout.add(btnBackToMenu);
                gameLayout.add(Box.createVerticalGlue());
                
                gameLayout.revalidate();
                gameLayout.repaint();
                
                // Fokus auf Frame setzen für Tastatureingabe
                mainFrame.requestFocus();
                
                // Nächstes Wort vorladen
                preloadNextWord();
            });
        }).start();
        
        mainFrame.setLocationRelativeTo(null);
    }
    
    private GameFieldWithCheck getGameFieldWithCheck() {
        String targetWord = preloadedTargetWord;
        GameFieldWithCheck gameField;
        
        switch (preloadedGameType) {
            case 2 -> gameField = new GameFieldWithCheck(targetWord, this, 4, false); // Schwer: 4 Zeilen
            case 3 -> gameField = new GameFieldWithCheck(targetWord, this, 6, true);  // Challenge: mit Timer
            default -> gameField = new GameFieldWithCheck(targetWord, this, 6, false); // Normal: 6 Zeilen
        }
        
        return gameField;
    }
    
    // Methode zum Vorladen des nächsten Worts
    private void preloadNextWord() {
        preloadService.submit(() -> {
            Variables.resetTargetWord();
            preloadedTargetWord = Wortliste.getRandomWord();
        });
    }
    
    // Methode zum Anzeigen des Einstellungsmenüs
    public void showSettingsMenu() {
        contentPanel.removeAll();
        
        // Ladebildschirm
        JPanel loadingPanel = new JPanel();
        loadingPanel.setLayout(new BoxLayout(loadingPanel, BoxLayout.Y_AXIS));
        
        JLabel loadingLabel = new JLabel("Einstellungen werden geladen...");
        loadingLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loadingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        loadingPanel.add(Box.createVerticalGlue());
        loadingPanel.add(loadingLabel);
        loadingPanel.add(Box.createVerticalGlue());
        
        contentPanel.add(loadingPanel);
        contentPanel.revalidate();
        contentPanel.repaint();
        
        // Verzögerung für visuelles Feedback
        Timer delay = new Timer(300, e -> {
            contentPanel.removeAll();
            mainFrame.setTitle("Einstellungen");
            mainFrame.setSize(600, 700);
            
            // Settings Panel
            JPanel settingsPanel = new JPanel();
            settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
            settingsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
            
            // Überschrift
            JLabel titleLabel = new JLabel("Einstellungen");
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // Allgemeine Einstellungen
            JLabel settingsLabel = new JLabel("Allgemeine Einstellungen");
            settingsLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            settingsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // Debug-Modus Checkbox
            JCheckBox debugModeCheckBox = new JCheckBox("Debug-Modus aktivieren");
            debugModeCheckBox.setSelected(Variables.debugMode);
            debugModeCheckBox.addActionListener(event -> Variables.debugMode = debugModeCheckBox.isSelected());
            debugModeCheckBox.setAlignmentX(Component.CENTER_ALIGNMENT);
            debugModeCheckBox.setToolTipText("Wenn der Debug-Modus aktiviert ist, öffnet sich das Debug-Tool");
            
            // Timer-Einstellungen
            JLabel sliderLabel = new JLabel("Timer-Einstellung für den Challenge-Modus");
            sliderLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            sliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // Slider
            JSlider setTimerValue = new JSlider(30, 360, Variables.timerSeconds);
            setTimerValue.setMajorTickSpacing(30);
            setTimerValue.setMinorTickSpacing(0);
            setTimerValue.setSnapToTicks(true);
            setTimerValue.setPaintTicks(true);
            setTimerValue.setPreferredSize(new Dimension(350, 50));
            setTimerValue.setMaximumSize(new Dimension(400, 50));
            
            // Slider mit Labels
            JPanel sliderPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
            sliderPanel.add(new JLabel("30s"));
            sliderPanel.add(setTimerValue);
            sliderPanel.add(new JLabel("360s"));
            sliderPanel.setMaximumSize(new Dimension(500, 60));
            
            // Slider Value Label
            JLabel sliderValueLabel = new JLabel("Eingestellte Timerzeit: " + Variables.timerSeconds + " Sekunden");
            sliderValueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            setTimerValue.addChangeListener(evt -> {
                int value = setTimerValue.getValue();
                sliderValueLabel.setText("Eingestellte Timerzeit: " + value + " Sekunden");
                Variables.timerSeconds = value;
            });
            
            // Theme-Einstellungen
            JLabel themeLabel = new JLabel("Design-Einstellungen");
            themeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            themeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // Theme-Optionen
            JPanel themePanel = createThemeSelectionPanel();
            themePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // Zurück-Button
            JButton btnBack = new JButton("Zurück zum Hauptmenü");
            btnBack.addActionListener(event -> showMainMenu());
            btnBack.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // Alles zum Settings Panel hinzufügen
            settingsPanel.add(titleLabel);
            settingsPanel.add(Box.createVerticalStrut(20));
            settingsPanel.add(settingsLabel);
            settingsPanel.add(Box.createVerticalStrut(10));
            settingsPanel.add(debugModeCheckBox);
            settingsPanel.add(Box.createVerticalStrut(20));
            settingsPanel.add(sliderLabel);
            settingsPanel.add(Box.createVerticalStrut(10));
            settingsPanel.add(sliderPanel);
            settingsPanel.add(sliderValueLabel);
            settingsPanel.add(Box.createVerticalStrut(20));
            settingsPanel.add(themeLabel);
            settingsPanel.add(Box.createVerticalStrut(10));
            settingsPanel.add(themePanel);
            settingsPanel.add(Box.createVerticalStrut(30));
            settingsPanel.add(btnBack);
            
            contentPanel.add(settingsPanel);
            mainFrame.setLocationRelativeTo(null);
            contentPanel.revalidate();
            contentPanel.repaint();
        });
        delay.setRepeats(false);
        delay.start();
    }
    
    // Methode zum Erstellen der Theme-Auswahl
    private JPanel createThemeSelectionPanel() {
        JPanel themePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        
        ButtonGroup themeGroup = new ButtonGroup();
        
        // Theme-Optionen erstellen
        JPanel lightTheme = createThemeButton("Hell", new Color(240, 240, 240), new Color(30, 30, 30), themeGroup);
        JPanel darkTheme = createThemeButton("Dunkel", new Color(30, 30, 30), new Color(230, 230, 230), themeGroup);
        JPanel mintTheme = createThemeButton("Mint", new Color(200, 255, 220), new Color(30, 30, 30), themeGroup);
        JPanel randomTheme = createThemeButton("Zufall",
                new Color(Variables.random.nextInt(256), Variables.random.nextInt(256), Variables.random.nextInt(256)),
                new Color(Variables.random.nextInt(256), Variables.random.nextInt(256), Variables.random.nextInt(256)),
                themeGroup);
        
        themePanel.add(lightTheme);
        themePanel.add(darkTheme);
        themePanel.add(mintTheme);
        themePanel.add(randomTheme);
        
        return themePanel;
    }
    
    // Methode zum Erstellen eines Theme-Buttons
    private JPanel createThemeButton(String themeName, Color bgColor, Color textColor, ButtonGroup group) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        // Farbvorschau erstellen
        JPanel colorPreview = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Oberes Dreieck (Hintergrundfarbe)
                int[] xPoints1 = {0, 60, 60};
                int[] yPoints1 = {0, 0, 60};
                g.setColor(bgColor);
                g.fillPolygon(xPoints1, yPoints1, 3);
                
                // Unteres Dreieck (Textfarbe)
                int[] xPoints2 = {0, 0, 60};
                int[] yPoints2 = {0, 60, 60};
                g.setColor(textColor);
                g.fillPolygon(xPoints2, yPoints2, 3);
                
                // Rahmen
                g.setColor(Color.BLACK);
                g.drawRect(0, 0, 59, 59);
            }
        };
        colorPreview.setPreferredSize(new Dimension(60, 60));
        colorPreview.setMinimumSize(new Dimension(60, 60));
        colorPreview.setMaximumSize(new Dimension(60, 60));
        
        // Name Label
        JLabel nameLabel = new JLabel(themeName);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // RadioButton
        JRadioButton radioButton = new JRadioButton();
        radioButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        group.add(radioButton);
        
        // Aktion für Theme-Anwendung
        radioButton.addActionListener(e -> applyTheme(themeName));
        
        // Aktuelles Theme vorauswählen
        if (Variables.currentTheme != null) {
            if ((themeName.equals("Dunkel") && Variables.currentTheme.equals("dark")) ||
                (themeName.equals("Hell") && Variables.currentTheme.equals("light")) ||
                (themeName.equals("Mint") && Variables.currentTheme.equals("mint")) ||
                (themeName.equals("Zufall") && Variables.currentTheme.equals("random"))) {
                radioButton.setSelected(true);
            }
        } else if (themeName.equals("Hell")) {
            radioButton.setSelected(true);
        }
        
        // Panel zusammenbauen
        panel.add(colorPreview);
        panel.add(nameLabel);
        panel.add(radioButton);
        
        // Klickbar machen
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                radioButton.setSelected(true);
                applyTheme(themeName);
            }
        });
        
        return panel;
    }
    
    // Methode zum Anwenden eines Themes
    private void applyTheme(String themeName) {
        switch (themeName) {
            case "Dunkel" -> {
                contentPanel.setBackground(new Color(30, 30, 30));
                setComponentColors(contentPanel, new Color(30, 30, 30), new Color(230, 230, 230));
                Variables.currentTheme = "dark";
            }
            case "Hell" -> {
                contentPanel.setBackground(new Color(240, 240, 240));
                setComponentColors(contentPanel, new Color(240, 240, 240), new Color(30, 30, 30));
                Variables.currentTheme = "light";
            }
            case "Mint" -> {
                contentPanel.setBackground(new Color(200, 255, 220));
                setComponentColors(contentPanel, new Color(200, 255, 220), new Color(30, 30, 30));
                Variables.currentTheme = "mint";
            }
            case "Zufall" -> {
                Color bgColor = new Color(Variables.random.nextInt(256), Variables.random.nextInt(256), Variables.random.nextInt(256));
                Color textColor = new Color(Variables.random.nextInt(256), Variables.random.nextInt(256), Variables.random.nextInt(256));
                contentPanel.setBackground(bgColor);
                setComponentColors(contentPanel, bgColor, textColor);
                Variables.currentTheme = "random";
            }
        }
        contentPanel.repaint();
    }
    
    // Hilfsmethode zum rekursiven Setzen von Farben
    private void setComponentColors(Container container, Color bgColor, Color fgColor) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JLabel label) {
                label.setForeground(fgColor);
            } else if (comp instanceof Container) {
                if (!(comp instanceof JButton) && !(comp instanceof JTextField) && !(comp instanceof JCheckBox) && !(comp instanceof JRadioButton)) {
                    comp.setBackground(bgColor);
                }
                setComponentColors((Container) comp, bgColor, fgColor);
            }
        }
    }
    
    // Methode zum Vorbereiten und Starten eines neuen Spiels
    private void prepareAndStartNewGame(int gameType) {
        contentPanel.removeAll();
        
        // Ladebildschirm
        JPanel loadingPanel = new JPanel();
        loadingPanel.setLayout(new BoxLayout(loadingPanel, BoxLayout.Y_AXIS));
        
        JLabel loadingLabel = new JLabel("Spiel wird vorbereitet ...");
        loadingLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loadingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        loadingPanel.add(Box.createVerticalGlue());
        loadingPanel.add(loadingLabel);
        loadingPanel.add(Box.createVerticalGlue());
        
        contentPanel.add(loadingPanel);
        contentPanel.revalidate();
        contentPanel.repaint();
        
        // Spielvorbereitung in separatem Thread
        new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            preloadedGameType = gameType;
            if (preloadedTargetWord == null) {
                Variables.resetTargetWord();
                preloadedTargetWord = Wortliste.getRandomWord();
            }
            
            SwingUtilities.invokeLater(this::showGameScreen);
        }).start();
    }
    
    // Innere Klasse GameFieldWithCheck für die Spiellogik
    private class GameFieldWithCheck extends GameField {
        private final UI uiReference;
        private final boolean withTimer;
        private final Set<Character> incorrectLetters = new HashSet<>();
        private final Map<Character, JButton> keyboardButtons = new HashMap<>();
        private String targetWord;
        private Timer timer;
        private JLabel timerLabel;
        private int secondsRemaining;
        private JLabel[][] cellsCache;
        
        public GameFieldWithCheck(String targetWord, UI uiReference, int rows, boolean withTimer) {
            super(rows);
            this.targetWord = targetWord.toUpperCase();
            this.uiReference = uiReference;
            this.withTimer = withTimer;
            
            // Virtuelle Tastatur erstellen
            createKeyboard();
            
            // Tastatureingabe über physische Tastatur
            KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
                @Override
                public boolean dispatchKeyEvent(KeyEvent e) {
                    if (e.getID() == KeyEvent.KEY_PRESSED) {
                        if (timer == null || timer.isRunning()) {
                            int code = e.getKeyCode();
                            
                            if (code == KeyEvent.VK_ENTER) {
                                if (currentCol == 5) {
                                    e.consume();
                                    submitCurrentInput();
                                    return true;
                                }
                            } else if (code == KeyEvent.VK_BACK_SPACE || code == KeyEvent.VK_DELETE) {
                                e.consume();
                                removeLetter();
                                return true;
                            } else {
                                char keyChar = e.getKeyChar();
                                if (Character.isLetter(keyChar)) {
                                    e.consume();
                                    addLetter(String.valueOf(keyChar).toUpperCase());
                                    return true;
                                }
                            }
                        }
                    }
                    return false;
                }
            });
            
            // Timer für Challenge-Modus
            if (withTimer) {
                setupTimer();
            }
            
            // Debug-Fenster anzeigen
            if (Variables.debugMode) {
                Debug.showDebugWindow(targetWord,
                        newWord -> this.targetWord = newWord,
                        () -> uiReference.showGameScreen(),
                        () -> {
                            if (currentRow > 0) {
                                currentRow--;
                                currentCol = 0;
                                for (int col = 0; col < 5; col++) {
                                    setLetter(currentRow, col, "");
                                    setCellColor(currentRow, col, Color.WHITE);
                                }
                                highlightCurrentCell();
                            }
                        }
                );
            }
        }
        
        private void highlightCurrentCell() {
            Color defaultBorder = new Color(211, 214, 218);
            Color highlightBorder = new Color(153, 155, 158);
            
            for (int col = 0; col < 5; col++) {
                JLabel cell = getCellAt(currentRow, col);
                if (cell != null) {
                    LineBorder currentBorder = (LineBorder) cell.getBorder();
                    Color borderColor = currentBorder.getLineColor();
                    
                    if (!borderColor.equals(CheckAlgo.COLOR_GRAY) &&
                        !borderColor.equals(CheckAlgo.COLOR_YELLOW) &&
                        !borderColor.equals(CheckAlgo.COLOR_GREEN)) {
                        if (col != currentCol) {
                            cell.setBorder(new LineBorder(defaultBorder, 2));
                        }
                    }
                }
            }
            
            if (currentCol < 5 && currentRow < rows) {
                JLabel cell = getCellAt(currentRow, currentCol);
                if (cell != null) {
                    LineBorder currentBorder = (LineBorder) cell.getBorder();
                    Color borderColor = currentBorder.getLineColor();
                    
                    if (!borderColor.equals(CheckAlgo.COLOR_GRAY) &&
                        !borderColor.equals(CheckAlgo.COLOR_YELLOW) &&
                        !borderColor.equals(CheckAlgo.COLOR_GREEN)) {
                        cell.setBorder(new LineBorder(highlightBorder, 2));
                    }
                }
            }
        }
        
        private String getLetterAt(int row, int col) {
            try {
                JLabel cell = getCellAt(row, col);
                return cell != null ? cell.getText() : "";
            } catch (Exception e) {
                return "";
            }
        }
        
        private JLabel getCellAt(int row, int col) {
            if (cellsCache == null) {
                try {
                    java.lang.reflect.Field cellsField = GameField.class.getDeclaredField("cells");
                    cellsField.setAccessible(true);
                    cellsCache = (JLabel[][]) cellsField.get(this);
                } catch (Exception e) {
                    System.err.println("Fehler beim Zugriff auf cells: " + e.getMessage());
                    return null;
                }
            }
            
            try {
                return cellsCache[row][col];
            } catch (Exception e) {
                System.err.println("Fehler beim Zugriff auf Zelle: " + e.getMessage());
                return null;
            }
        }
        
        private void createKeyboard() {
            JPanel keyboardContainer = new JPanel();
            keyboardContainer.setLayout(new BoxLayout(keyboardContainer, BoxLayout.Y_AXIS));
            keyboardContainer.setBorder(new EmptyBorder(22, 0, 0, 0));
            
            // Zeile 1
            JPanel row1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 7, 0));
            String[] row1Keys = {"Q", "W", "E", "R", "T", "Z", "U", "I", "O", "P", "Ü"};
            for (String key : row1Keys) {
                JButton btn = createKeyButton(key);
                row1.add(btn);
                keyboardButtons.put(key.charAt(0), btn);
            }
            
            // Zeile 2
            JPanel row2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 7, 0));
            String[] row2Keys = {"A", "S", "D", "F", "G", "H", "J", "K", "L", "Ö", "Ä"};
            for (String key : row2Keys) {
                JButton btn = createKeyButton(key);
                row2.add(btn);
                keyboardButtons.put(key.charAt(0), btn);
            }
            
            // Zeile 3
            JPanel row3 = new JPanel(new FlowLayout(FlowLayout.CENTER, 7, 0));
            JButton enterButton = createKeyButton("ENTER");
            enterButton.setPreferredSize(new Dimension(70, 40));
            row3.add(enterButton);
            
            String[] row3Keys = {"Y", "X", "C", "V", "B", "N", "M"};
            for (String key : row3Keys) {
                JButton btn = createKeyButton(key);
                row3.add(btn);
                keyboardButtons.put(key.charAt(0), btn);
            }
            
            JButton backspaceButton = createKeyButton("⌫");
            row3.add(backspaceButton);
            
            keyboardContainer.add(row1);
            keyboardContainer.add(Box.createVerticalStrut(10));
            keyboardContainer.add(row2);
            keyboardContainer.add(Box.createVerticalStrut(10));
            keyboardContainer.add(row3);
            
            // Event-Handler
            ActionListener keyboardHandler = e -> {
                JButton source = (JButton) e.getSource();
                String text = source.getText();
                
                if (text.equals("ENTER")) {
                    if (currentCol == 5) {
                        submitCurrentInput();
                    }
                } else if (text.length() == 1 && !text.equals("⌫")) {
                    if (!withTimer || !incorrectLetters.contains(text.charAt(0))) {
                        addLetter(text);
                    }
                }
            };
            
            for (JButton btn : keyboardButtons.values()) {
                btn.addActionListener(keyboardHandler);
            }
            enterButton.addActionListener(keyboardHandler);
            backspaceButton.addActionListener(e -> removeLetter());
            
            this.add(keyboardContainer);
        }
        
        private JButton createKeyButton(String text) {
            JButton button = new JButton(text);
            button.setPreferredSize(new Dimension(40, 40));
            button.setFont(new Font("Arial", Font.BOLD, 12));
            button.setBackground(new Color(211, 214, 218));
            button.setForeground(Color.BLACK);
            button.setFocusPainted(false);
            return button;
        }
        
        private void updateKeyboard(String input, Color[] colors) {
            for (int i = 0; i < input.length(); i++) {
                char c = input.charAt(i);
                JButton keyButton = keyboardButtons.get(c);
                if (keyButton == null) continue;
                
                Color currentBg = keyButton.getBackground();
                if (currentBg.equals(CheckAlgo.COLOR_GREEN)) continue;
                
                if (colors[i].equals(CheckAlgo.COLOR_GREEN)) {
                    keyButton.setBackground(CheckAlgo.COLOR_GREEN);
                    keyButton.setForeground(Color.WHITE);
                } else if (colors[i].equals(CheckAlgo.COLOR_YELLOW) && !currentBg.equals(CheckAlgo.COLOR_GREEN)) {
                    keyButton.setBackground(CheckAlgo.COLOR_YELLOW);
                    keyButton.setForeground(Color.WHITE);
                } else if (colors[i].equals(CheckAlgo.COLOR_GRAY) && !currentBg.equals(CheckAlgo.COLOR_GREEN) && !currentBg.equals(CheckAlgo.COLOR_YELLOW)) {
                    keyButton.setBackground(CheckAlgo.COLOR_GRAY);
                    keyButton.setForeground(Color.WHITE);
                }
            }
        }
        
        private boolean checkInput(String input) {
            if (input == null || input.length() != 5) {
                return false;
            }
            
            String normalizedInput = input.toUpperCase();
            return Wortliste.isInWordList(normalizedInput);
        }
        
        private void processValidInput(String normalizedInput) {
            final int rowToCheck = currentRow - 1;
            
            Color[] colors = CheckAlgo.checkWord(normalizedInput, targetWord);
            boolean allCorrect = true;
            for (Color color : colors) {
                if (!color.equals(CheckAlgo.COLOR_GREEN)) {
                    allCorrect = false;
                    break;
                }
            }
            
            // Animation für Zellen
            Timer animationTimer = new Timer(200, null);
            final int[] currentCol = {0};
            final boolean finalAllCorrect = allCorrect;
            
            animationTimer.addActionListener(e -> {
                if (currentCol[0] < 5) {
                    animateCellReveal(rowToCheck, currentCol[0], colors[currentCol[0]]);
                    currentCol[0]++;
                } else {
                    animationTimer.stop();
                    
                    // Tastatur aktualisieren
                    updateKeyboard(normalizedInput, colors);
                    
                    // Challenge-Modus: falsche Buchstaben merken
                    if (withTimer) {
                        for (int i = 0; i < normalizedInput.length(); i++) {
                            if (colors[i].equals(CheckAlgo.COLOR_GRAY)) {
                                incorrectLetters.add(normalizedInput.charAt(i));
                            }
                        }
                    }
                    
                    // Gewinn/Verlust prüfen
                    if (finalAllCorrect) {
                        showWonDialog(targetWord, rowToCheck + 1);
                    } else if (rowToCheck + 1 >= rows) {
                        showLostDialog("Leider verloren! Das Wort war: " + targetWord);
                    }
                }
            });
            animationTimer.setInitialDelay(200);
            animationTimer.start();
        }
        
        private void animateCellReveal(int row, int col, Color color) {
            JLabel cell = getCellAt(row, col);
            if (cell != null) {
                // Einfache Farbanimation ohne komplexe 3D-Rotation
                Timer colorTimer = new Timer(250, e -> {
                    cell.setBackground(color);
                    cell.setBorder(new LineBorder(color, 2));
                    cell.setForeground(Color.WHITE);
                });
                colorTimer.setRepeats(false);
                colorTimer.start();
            }
        }
        
        @Override
        public void addLetter(String letter) {
            if (withTimer && incorrectLetters.contains(letter.charAt(0))) {
                return;
            }
            
            if (currentCol < 5 && currentRow < rows) {
                setLetter(currentRow, currentCol, letter);
                currentCol++;
                highlightCurrentCell();
            }
        }
        
        @Override
        public void removeLetter() {
            if (currentCol > 0 && currentRow < rows) {
                currentCol--;
                setLetter(currentRow, currentCol, "");
                highlightCurrentCell();
            }
        }
        
        public void submitCurrentInput() {
            if (currentCol == 5 && currentRow < rows) {
                StringBuilder sb = new StringBuilder();
                for (int col = 0; col < 5; col++) {
                    sb.append(getLetterAt(currentRow, col));
                }
                String input = sb.toString();
                
                if (checkInput(input)) {
                    currentRow++;
                    currentCol = 0;
                    processValidInput(input);
                }
                
                highlightCurrentCell();
            }
        }
        
        private void setupTimer() {
            timerLabel = new JLabel();
            timerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
            timerLabel.setBorder(new EmptyBorder(5, 0, 10, 0));
            
            // Timer Label an erster Stelle einfügen
            this.add(timerLabel, 0);
            
            secondsRemaining = Variables.timerSeconds;
            timerLabel.setText("Verbleibende Zeit: " + formatTime(secondsRemaining));
            
            timer = new Timer(1000, e -> {
                secondsRemaining--;
                timerLabel.setText("Verbleibende Zeit: " + formatTime(secondsRemaining));
                
                if (secondsRemaining <= 0) {
                    timer.stop();
                    SwingUtilities.invokeLater(() -> showLostDialog("Die Zeit ist abgelaufen! Das gesuchte Wort war: " + targetWord));
                }
            });
            timer.start();
        }
        
        private String formatTime(int totalSeconds) {
            if (totalSeconds < 0) totalSeconds = 0;
            int minutes = totalSeconds / 60;
            int seconds = totalSeconds % 60;
            return String.format("%02d:%02d", minutes, seconds);
        }
        
        private void showWonDialog(String targetWord, int rowsUsed) {
            if (timer != null) {
                timer.stop();
            }
            
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(mainFrame,
                        "Glückwunsch! Du hast das Wort '" + targetWord + "' in " + rowsUsed + (rowsUsed == 1 ? " Versuch" : " Versuchen") + " erraten.",
                        "Gewonnen!",
                        JOptionPane.INFORMATION_MESSAGE);
                uiReference.showMainMenu();
            });
        }
        
        private void showLostDialog(String message) {
            if (timer != null) {
                timer.stop();
            }
            
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(mainFrame,
                        message,
                        "Verloren!",
                        JOptionPane.INFORMATION_MESSAGE);
                uiReference.showMainMenu();
            });
        }
    }
}
