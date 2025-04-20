package src;
import java.util.*;


public class Words {
    Variables var = new Variables();
    public void chooseSecretWord() {
        Random rand = new Random();
        int randomIndex = rand.nextInt(var.fiveLetterWords.size());
        var.secretWord = var.fiveLetterWords.get(randomIndex);
    }

    public void checkUserInput() {
        if (var.userInput == null || var.userInput.length() != 5) {
            return; // Eingabe ist leer oder hat nicht die Länge 5
        }
        var.userInput = var.userInput.toUpperCase();
        if (var.userInput.contains(" ") || var.userInput.contains("Ä") || var.userInput.contains("Ö") || var.userInput.contains("Ü") || var.userInput.contains("ß")) {
            return; // Eingabe enthält Leerzeichen oder Umlaute
        }
        if (var.userInput.contains("0") || var.userInput.contains("1") || var.userInput.contains("2") || var.userInput.contains("3") || var.userInput.contains("4") || var.userInput.contains("5") || var.userInput.contains("6") || var.userInput.contains("7") || var.userInput.contains("8") || var.userInput.contains("9")) {
            return; // Eingabe enthält Zahlen
        }
        processUserInput();
    }

    public void processUserInput() {
        Variables var = new Variables();
        /*
        for(int i = 1; i < var.maxAttempts; i++) {
            char guessChar = var.userInput.charAt(i);

        */
    }
}
