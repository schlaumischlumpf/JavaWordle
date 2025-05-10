package ressource;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

/**
 * ButtonAnimations-Klasse für visuelle Effekte bei Buttons
 * Implementiert einen rotierenden Rahmen beim Hover über Buttons
 */
public class Animation {

    private static int INDEFINITE;

    /**
     * Wendet den rotierenden Rahmen-Effekt auf einen einzelnen Button an
     *
     * @param button Der Button, auf den der Effekt angewendet werden soll
     */
    public static void applyRotatingBorderEffect(Button button) {
        // Erstellen eines Container-StackPane mit dem eigentlichen Button und einem Rahmen
        StackPane buttonContainer = new StackPane();
        Rectangle border = new Rectangle();

        // Rahmen konfigurieren
        border.setFill(Color.TRANSPARENT);
        border.setStroke(Color.web("#3366cc"));
        border.setStrokeWidth(2);
        border.setArcWidth(8);
        border.setArcHeight(8);

        // Rotationsanimation erstellen
        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(2), border);
        rotateTransition.setAxis(Rotate.Z_AXIS);
        rotateTransition.setByAngle(360);
        rotateTransition.setCycleCount(Animation.INDEFINITE);
        rotateTransition.setInterpolator(javafx.animation.Interpolator.LINEAR);

        // Farbwechsel-Animation für den Rahmen
        Timeline colorTimeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(border.strokeProperty(), Color.web("#3366cc"))),
                new KeyFrame(Duration.seconds(0.5),
                        new KeyValue(border.strokeProperty(), Color.web("#ff6600"))),
                new KeyFrame(Duration.seconds(1.0),
                        new KeyValue(border.strokeProperty(), Color.web("#cc33ff"))),
                new KeyFrame(Duration.seconds(2.0),
                        new KeyValue(border.strokeProperty(), Color.web("#3366cc")))
        );
        colorTimeline.setCycleCount(Animation.INDEFINITE);

        // Event-Handler für Mouseover
        button.setOnMouseEntered(e -> {
            // Aktualisiere die Größe des Rahmens, falls sich die Button-Größe ändert
            border.setWidth(button.getWidth() + 4);
            border.setHeight(button.getHeight() + 4);
            rotateTransition.play();
            colorTimeline.play();
            border.setOpacity(1); // Rahmen sichtbar machen
        });

        button.setOnMouseExited(e -> {
            rotateTransition.stop();
            colorTimeline.stop();
            border.setOpacity(0); // Rahmen ausblenden
        });

        // Rahmen anfangs unsichtbar machen
        border.setOpacity(0);

        // Layout zusammenbauen
        buttonContainer.getChildren().addAll(border, button);

        // Ersetze den Button in seinem Parent mit dem neuen Container
        if (button.getParent() instanceof Pane parentPane) {
            int buttonIndex = parentPane.getChildren().indexOf(button);
            parentPane.getChildren().remove(button);
            parentPane.getChildren().add(buttonIndex, buttonContainer);
        }
    }

    /**
     * Wendet den rotierenden Rahmen-Effekt auf alle Buttons an
     *
     * @param root Das Wurzelelement des Szenegraphen
     */
    public static void setupRotatingButtonBorders(Node root) {
        findButtonsAndApplyEffect(root);
    }

    /**
     * Rekursive Hilfsmethode zum Finden und Bearbeiten aller Buttons im Szenegraphen
     *
     * @param node Aktueller Knoten im Szenegraphen
     */
    private static void findButtonsAndApplyEffect(Node node) {
        if (node instanceof Button button) {
            applyRotatingBorderEffect(button);
        } else if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                findButtonsAndApplyEffect(child);
            }
        }
    }
}