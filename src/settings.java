package src;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class settings {

    public static void startSettings() {
        Stage settingsStage = new Stage();
        settingsStage.setTitle("Einstellungen");

        VBox rootNode = new VBox(10);
        rootNode.setPadding(new Insets(15));
        rootNode.setAlignment(Pos.CENTER);

        // Checkboxen mit aktuellem Zustand initialisieren
        CheckBox debugMode = new CheckBox("Debug-Modus");
        debugMode.setSelected(variables.debugMode);

        CheckBox disableDuplicateLetters = new CheckBox("Doppelte Buchstaben deaktivieren");
        disableDuplicateLetters.setSelected(variables.disableDuplicateLetters);

        Button saveButton = new Button("Speichern");
        saveButton.setOnAction(_ -> {
            variables.debugMode = debugMode.isSelected();
            variables.disableDuplicateLetters = disableDuplicateLetters.isSelected();
            settingsStage.close();
        });

        rootNode.getChildren().addAll(
                debugMode,
                disableDuplicateLetters,
                saveButton
        );

        Scene scene = new Scene(rootNode, 300, 200);
        settingsStage.setScene(scene);
        settingsStage.show();
    }
}