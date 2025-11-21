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

public class DashboardMedicoController {

    @FXML
    private Button btnMisCitas;
    @FXML
    private Button btnMisPacientes;
    @FXML
    private Button btnHistorialCitas;
    @FXML
    private Button btnCerrarSesion;
    @FXML
    private AnchorPane contentArea;
    @FXML
    private Label lblNombreMedico;

    @FXML
    void initialize() {
        System.out.println("=== INICIALIZANDO DASHBOARD MÉDICO ===");
        lblNombreMedico.setText("Dr. Médico");
    }

    @FXML
    void onMisCitas(ActionEvent event) {
        System.out.println("Mis Citas clickeado");
        // CORRECCIÓN: Agregar la ruta completa al FXML
        cargarVistaEnContentArea("/co/edu/uniquindio/hospital/CitasMedicas.fxml");
    }

    @FXML
    void onMisPacientes(ActionEvent event) {
        System.out.println("Mis Pacientes clickeado");
        cargarVistaProximamente("Mis Pacientes - Próximamente");
    }

    @FXML
    void onHistorialCitas(ActionEvent event) {
        System.out.println("Historial de Citas clickeado");
        cargarVistaProximamente("Historial de Citas - Próximamente");
    }

    @FXML
    void onCerrarSesion(ActionEvent event) {
        try {
            // Cargar la ventana de login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/edu/uniquindio/hospital/login.fxml"));
            Parent root = loader.load();

            // Obtener el stage actual y cambiar la escena
            Stage stage = (Stage) btnCerrarSesion.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login - Hospital");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cerrar sesión", Alert.AlertType.ERROR);
        }
    }

    /**
     * MÉTODO CORREGIDO: Carga una vista FXML en el área de contenido central
     */
    private void cargarVistaEnContentArea(String fxmlPath) {
        try {
            System.out.println("🔍 Intentando cargar: " + fxmlPath);

            // CORRECCIÓN: Verificar que la ruta no sea null
            if (fxmlPath == null || fxmlPath.trim().isEmpty()) {
                throw new IllegalArgumentException("La ruta del FXML no puede ser nula o vacía");
            }

            // Cargar el archivo FXML usando getResource
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(fxmlPath));

            // ALTERNATIVA: También puedes usar este método
            // FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));

            // Verificar que el recurso existe
            if (loader.getLocation() == null) {
                throw new IOException("No se encontró el archivo FXML: " + fxmlPath);
            }

            Node vista = loader.load();

            // Limpiar el área de contenido
            contentArea.getChildren().clear();

            // Agregar la nueva vista
            contentArea.getChildren().add(vista);

            // Hacer que la vista ocupe todo el espacio disponible
            AnchorPane.setTopAnchor(vista, 0.0);
            AnchorPane.setBottomAnchor(vista, 0.0);
            AnchorPane.setLeftAnchor(vista, 0.0);
            AnchorPane.setRightAnchor(vista, 0.0);

            System.out.println("✅ Vista cargada exitosamente: " + fxmlPath);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("❌ Error al cargar vista: " + fxmlPath);
            System.err.println("❌ Detalle del error: " + e.getMessage());
            mostrarAlerta("Error",
                    "No se pudo cargar la vista: " + fxmlPath + "\n\nVerifique que el archivo existe en resources.",
                    Alert.AlertType.ERROR);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            mostrarAlerta("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Carga una vista temporal de "Próximamente"
     */
    private void cargarVistaProximamente(String mensaje) {
        // Limpiar el área de contenido
        contentArea.getChildren().clear();

        // Crear un label con el mensaje
        Label label = new Label(mensaje);
        label.setStyle("-fx-font-size: 24px; -fx-text-fill: #424242;");
        label.setLayoutX(300);
        label.setLayoutY(300);

        // Agregar el label al contentArea
        contentArea.getChildren().add(label);
    }

    /**
     * Muestra un cuadro de diálogo de alerta
     */
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}