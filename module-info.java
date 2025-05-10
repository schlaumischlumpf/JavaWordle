module Wordle {
    requires javafx.controls;
    requires javafx.fxml;

    exports src;
    exports ressource;
    opens src to javafx.fxml;
    opens ressource to javafx.fxml;
}