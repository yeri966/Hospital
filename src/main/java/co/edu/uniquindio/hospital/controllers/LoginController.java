package co.edu.uniquindio.hospital.controllers;

import co.edu.uniquindio.hospital.*;
import co.edu.uniquindio.hospital.creational.singleton.Hospital;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    private Hospital hospital = Hospital.getInstance();

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtContrasenia;
    @FXML private Button btnIngresar;
    @FXML private Button btnSalir;
    @FXML private Hyperlink hylRegistrarse;

    @FXML
    void onIngresar(ActionEvent event) {
        String usuario = txtUsuario.getText().trim();
        String contrasenia = txtContrasenia.getText();

        if (usuario.isEmpty() || contrasenia.isEmpty()) {
            mostrarAlerta("Campos vacíos", "Por favor ingrese usuario y contraseña", Alert.AlertType.WARNING);
            return;
        }

        Person personaEncontrada = hospital.validarUsuario(usuario, contrasenia);

        if (personaEncontrada != null) {
            String fxml = "";
            String titulo = "";

            if (personaEncontrada instanceof Admin) {
                fxml = "/co/edu/uniquindio/hospital/dashboardAdmin.fxml";
                titulo = "Dashboard Administrador - Hospital";
            } else if (personaEncontrada instanceof Medico) {
                fxml = "/co/edu/uniquindio/hospital/dashboardMedico.fxml";
                titulo = "Dashboard Médico - Hospital";
            }

            abrirVentana(fxml, titulo);
        } else {
            mostrarAlerta("Error de autenticación", "Usuario o contraseña incorrectos", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void onSalir(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    void onRegistrarse(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/edu/uniquindio/hospital/RegistroMedico.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Registro de Médico - Hospital");
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir la ventana de registro", Alert.AlertType.ERROR);
        }
    }

    private void abrirVentana(String fxml, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(titulo);
            stage.show();

            txtUsuario.getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir la ventana", Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}