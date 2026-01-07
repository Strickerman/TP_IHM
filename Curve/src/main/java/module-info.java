module ensisa.tp_note {
    requires javafx.controls;
    requires javafx.fxml;


    opens ensisa to javafx.fxml;
    exports ensisa;
    exports ensisa.commands;
    opens ensisa.commands to javafx.fxml;
}