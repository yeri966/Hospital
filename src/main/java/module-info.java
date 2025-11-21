module co.edu.uniquindio.hospital {
    requires javafx.controls;
    requires javafx.fxml;

    opens co.edu.uniquindio.hospital to javafx.fxml, javafx.base;
    opens co.edu.uniquindio.hospital.controllers to javafx.fxml;
    opens co.edu.uniquindio.hospital.creational.singleton to javafx.fxml;

    exports co.edu.uniquindio.hospital;
    exports co.edu.uniquindio.hospital.controllers;
    exports co.edu.uniquindio.hospital.creational.singleton;
}