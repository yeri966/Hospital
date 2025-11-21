module co.edu.uniquindio.hospital {
    requires javafx.controls;
    requires javafx.fxml;


    opens co.edu.uniquindio.hospital to javafx.fxml;
    exports co.edu.uniquindio.hospital;
    exports co.edu.uniquindio.hospital.creational.singleton;
    opens co.edu.uniquindio.hospital.creational.singleton to javafx.fxml;
}