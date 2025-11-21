package co.edu.uniquindio.hospital.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardAdminController {

    @FXML private Label lblNombreUsuario;
    @FXML private Button btnPacientes;
    @FXML private Button btnMedicos;
    @FXML private Button btnCitas;
    @FXML private Button btnCerrarSesion;
    @FXML private AnchorPane contentArea;

    private Button botonActivo = null;

    @FXML
    void initialize() {
        lblNombreUsuario.setText("Administrador");
        aplicarEfectoHover(btnPacientes);
        aplicarEfectoHover(btnMedicos);
        aplicarEfectoHover(btnCitas);
        aplicarEfectoHover(btnCerrarSesion);
    }

    @FXML
    void onPacientes(ActionEvent event) {
        cambiarEstiloBotonActivo(btnPacientes);
        cargarVistaEnContentArea("/co/edu/uniquindio/hospital/GestionPacientes.fxml");
    }

    @FXML
    void onMedicos(ActionEvent event) {
        cambiarEstiloBotonActivo(btnMedicos);
        cargarVistaEnContentArea("/co/edu/uniquindio/hospital/GestionMedicos.fxml");
    }

    @FXML
    void onCitas(ActionEvent event) {
        cambiarEstiloBotonActivo(btnCitas);
        cargarVistaEnContentArea("/co/edu/uniquindio/hospital/GestionCitas.fxml");
    }

    @FXML
    void onCerrarSesion(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/edu/uniquindio/hospital/Login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnCerrarSesion.getScene().getWindow();
            stage.setScene(new Scene(root, 500, 400));
            stage.setTitle("Login - Hospital");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cerrar sesión", Alert.AlertType.ERROR);
        }
    }

    private void cambiarEstiloBotonActivo(Button boton) {
        if (botonActivo != null && botonActivo != btnCerrarSesion) {
            botonActivo.setStyle("-fx-background-color: transparent; -fx-text-fill: #757575; -fx-font-size: 14px; -fx-cursor: hand; -fx-alignment: center-left;");
        }

        if (boton != btnCerrarSesion) {
            boton.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #424242; -fx-font-size: 14px; -fx-cursor: hand; -fx-alignment: center-left;");
            botonActivo = boton;
        }
    }

    private void aplicarEfectoHover(Button boton) {
        String estiloOriginal = boton.getStyle();

        boton.setOnMouseEntered(e -> {
            if (boton != botonActivo && boton != btnCerrarSesion) {
                boton.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #424242; -fx-font-size: 14px; -fx-cursor: hand; -fx-alignment: center-left;");
            }
        });

        boton.setOnMouseExited(e -> {
            if (boton != botonActivo && boton != btnCerrarSesion) {
                boton.setStyle(estiloOriginal);
            }
        });
    }

    private void cargarVistaEnContentArea(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node vista = loader.load();

            contentArea.getChildren().clear();
            contentArea.getChildren().add(vista);

            AnchorPane.setTopAnchor(vista, 0.0);
            AnchorPane.setBottomAnchor(vista, 0.0);
            AnchorPane.setLeftAnchor(vista, 0.0);
            AnchorPane.setRightAnchor(vista, 0.0);

            System.out.println("Vista cargada: " + fxmlPath);
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la vista: " + fxmlPath, Alert.AlertType.ERROR);
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