package src;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import javafx.application.Platform;

public class import_list {
    private final variables var;

    public import_list(variables var) {
        this.var = var;
    }

    public void setFilePath(String filePath_new) {
        var.filePath = filePath_new;
        System.out.println("Pfad: " + var.filePath);
    }

    public void import_list() {
        Path path = Paths.get(var.filePath);

        try {
            if (Files.exists(path) && Files.isReadable(path)) {
                String content = new String(Files.readAllBytes(path));

                content = content.replace("ö", "oe")
                        .replace("Ö", "Oe")
                        .replace("ä", "ae")
                        .replace("Ä", "Ae")
                        .replace("ü", "ue")
                        .replace("Ü", "Ue")
                        .replace("ß", "ss");

                String[] words = content.split("\\W+");
                var.fiveLetterWords = new ArrayList<>();

                for (String word : words) {
                    word = word.toLowerCase();
                    if (word.length() == 5) {
                        var.fiveLetterWords.add(word);
                    }
                }
            }

        } catch (AccessDeniedException e) {

        }
        catch (IOException e) {
            e.printStackTrace();
        }

        if (var.fiveLetterWords.isEmpty()) {
            Platform.runLater(() -> {
                ui.noFiveLetterWords();
            });
        }
    }
}