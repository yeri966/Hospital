package co.edu.uniquindio.hospital.controllers;

import co.edu.uniquindio.hospital.*;
import co.edu.uniquindio.hospital.creational.singleton.Hospital;
import co.edu.uniquindio.hospital.structural.facade.CitaFacade;
import co.edu.uniquindio.hospital.structural.facade.CitaException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Controlador para las citas del médico
 * UTILIZA PATRÓN FACADE para simplificar las operaciones
 */
public class CitasMedicasController {

    // ==================== USO DEL PATRÓN FACADE ====================
    private CitaFacade citaFacade = new CitaFacade();
    private Hospital hospital = Hospital.getInstance();
    private Medico medicoActual;

    @FXML private TableView<Cita> tablaCitas;
    @FXML private TableColumn<Cita, String> colId;
    @FXML private TableColumn<Cita, String> colFecha;
    @FXML private TableColumn<Cita, String> colHora;
    @FXML private TableColumn<Cita, String> colPaciente;
    @FXML private TableColumn<Cita, String> colDocumento;
    @FXML private TableColumn<Cita, String> colMotivo;
    @FXML private TableColumn<Cita, String> colEstado;

    @FXML private Button btnAtenderCita;
    @FXML private Button btnVerDetalles;
    @FXML private Button btnCancelarCita;
    @FXML private Button btnAgregarDiagnostico;

    @FXML
    void initialize() {
        System.out.println("=== INICIALIZANDO MIS CITAS CON FACADE ===");

        obtenerMedicoActual();

        if (medicoActual == null) {
            mostrarAlerta("Error", "No se pudo identificar al médico actual", Alert.AlertType.ERROR);
            return;
        }

        configurarTabla();
        cargarCitasMedico();

        System.out.println("=== INICIALIZACIÓN CON FACADE COMPLETA ===");
    }

    private void obtenerMedicoActual() {
        Person usuarioActivo = hospital.getUsuarioActivo();
        if (usuarioActivo instanceof Medico) {
            medicoActual = (Medico) usuarioActivo;
            System.out.println("✅ Médico identificado: " + medicoActual.getNombre());
        } else {
            System.err.println("❌ El usuario activo no es un médico");
        }
    }

    private void configurarTabla() {
        colId.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getId()));

        colFecha.setCellValueFactory(cd -> {
            LocalDate f = cd.getValue().getFecha();
            return new SimpleStringProperty(f != null ?
                    f.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "");
        });

        colHora.setCellValueFactory(cd -> {
            if (cd.getValue().getHora() != null) {
                return new SimpleStringProperty(
                        cd.getValue().getHora().format(DateTimeFormatter.ofPattern("HH:mm")));
            }
            return new SimpleStringProperty("");
        });

        colPaciente.setCellValueFactory(cd -> {
            Paciente p = cd.getValue().getPaciente();
            return new SimpleStringProperty(p != null ? p.getNombre() : "");
        });

        colDocumento.setCellValueFactory(cd -> {
            Paciente p = cd.getValue().getPaciente();
            return new SimpleStringProperty(p != null ? p.getDocumento() : "");
        });

        colMotivo.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getMotivo()));

        colEstado.setCellValueFactory(cd -> {
            EstadoCita e = cd.getValue().getEstado();
            return new SimpleStringProperty(e != null ? e.toString() : "");
        });

        // Estilo para estado
        colEstado.setCellFactory(col -> new TableCell<Cita, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item) {
                        case "Programada": setStyle("-fx-text-fill: #2196F3; -fx-font-weight: bold;"); break;
                        case "Atendida": setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;"); break;
                        case "Cancelada": setStyle("-fx-text-fill: #F44336; -fx-font-weight: bold;"); break;
                        default: setStyle("-fx-text-fill: #757575; -fx-font-weight: bold;");
                    }
                }
            }
        });
    }

    /**
     * ==================== CARGAR CITAS USANDO FACADE ====================
     */
    private void cargarCitasMedico() {
        System.out.println("\n=== CARGANDO CITAS CON FACADE ===");

        if (medicoActual == null) {
            System.err.println("❌ No hay médico actual");
            return;
        }

        // ✨ USAR FACADE PARA OBTENER CITAS DEL MÉDICO
        List<Cita> citasMedico = citaFacade.obtenerCitasPorMedico(medicoActual);

        ObservableList<Cita> citasObservable = FXCollections.observableArrayList(citasMedico);
        tablaCitas.setItems(citasObservable);

        System.out.println("✅ [FACADE] Total citas del médico: " + citasMedico.size());
    }

    /**
     * ==================== ATENDER CITA USANDO FACADE ====================
     */
    @FXML
    void onAtenderCita(ActionEvent event) {
        Cita citaSeleccionada = tablaCitas.getSelectionModel().getSelectedItem();

        if (citaSeleccionada == null) {
            mostrarAlerta("Selección requerida", "Debe seleccionar una cita", Alert.AlertType.WARNING);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar Atención");
        confirm.setHeaderText("¿Marcar esta cita como atendida?");
        confirm.setContentText("Paciente: " + citaSeleccionada.getPaciente().getNombre());

        if (confirm.showAndWait().get() == ButtonType.OK) {
            try {
                // ✨ USAR FACADE PARA ATENDER CITA
                citaFacade.atenderCita(citaSeleccionada);

                mostrarAlerta("Éxito", "✨ Cita atendida usando FACADE", Alert.AlertType.INFORMATION);
                tablaCitas.refresh();

            } catch (CitaException e) {
                mostrarAlerta("Error", e.getMessage(), Alert.AlertType.WARNING);
            }
        }
    }

    @FXML
    void onVerDetalles(ActionEvent event) {
        Cita citaSeleccionada = tablaCitas.getSelectionModel().getSelectedItem();

        if (citaSeleccionada == null) {
            mostrarAlerta("Selección requerida", "Debe seleccionar una cita", Alert.AlertType.WARNING);
            return;
        }

        mostrarDetallesCita(citaSeleccionada);
    }

    /**
     * ==================== CANCELAR CITA USANDO FACADE ====================
     */
    @FXML
    void onCancelarCita(ActionEvent event) {
        Cita citaSeleccionada = tablaCitas.getSelectionModel().getSelectedItem();

        if (citaSeleccionada == null) {
            mostrarAlerta("Selección requerida", "Debe seleccionar una cita", Alert.AlertType.WARNING);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar Cancelación");
        confirm.setHeaderText("¿Cancelar esta cita?");
        confirm.setContentText("Paciente: " + citaSeleccionada.getPaciente().getNombre());

        if (confirm.showAndWait().get() == ButtonType.OK) {
            try {
                // ✨ USAR FACADE PARA CANCELAR CITA
                citaFacade.cancelarCita(citaSeleccionada);

                mostrarAlerta("Éxito", "✨ Cita cancelada usando FACADE", Alert.AlertType.INFORMATION);
                tablaCitas.refresh();

            } catch (CitaException e) {
                mostrarAlerta("Error", e.getMessage(), Alert.AlertType.WARNING);
            }
        }
    }

    /**
     * ==================== AGREGAR DIAGNÓSTICO USANDO FACADE ====================
     */
    @FXML
    void onAgregarDiagnostico(ActionEvent event) {
        Cita citaSeleccionada = tablaCitas.getSelectionModel().getSelectedItem();

        if (citaSeleccionada == null) {
            mostrarAlerta("Selección requerida", "Debe seleccionar una cita", Alert.AlertType.WARNING);
            return;
        }

        // Crear diálogo
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Agregar Diagnóstico");
        dialog.setHeaderText("Diagnóstico para: " + citaSeleccionada.getPaciente().getNombre());

        ButtonType btnGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardar, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextArea txtDiagnostico = new TextArea();
        txtDiagnostico.setPromptText("Diagnóstico médico...");
        txtDiagnostico.setPrefRowCount(5);
        txtDiagnostico.setPrefColumnCount(40);
        if (citaSeleccionada.getDiagnostico() != null) {
            txtDiagnostico.setText(citaSeleccionada.getDiagnostico());
        }

        TextArea txtObservaciones = new TextArea();
        txtObservaciones.setPromptText("Observaciones adicionales...");
        txtObservaciones.setPrefRowCount(3);
        txtObservaciones.setPrefColumnCount(40);
        if (citaSeleccionada.getObservaciones() != null) {
            txtObservaciones.setText(citaSeleccionada.getObservaciones());
        }

        grid.add(new Label("Diagnóstico:"), 0, 0);
        grid.add(txtDiagnostico, 0, 1);
        grid.add(new Label("Observaciones:"), 0, 2);
        grid.add(txtObservaciones, 0, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> btn == btnGuardar ? txtDiagnostico.getText() : null);

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(diagnostico -> {
            try {
                // ✨ USAR FACADE PARA AGREGAR DIAGNÓSTICO
                citaFacade.agregarDiagnostico(
                        citaSeleccionada,
                        diagnostico,
                        txtObservaciones.getText()
                );

                mostrarAlerta("Éxito", "✨ Diagnóstico guardado usando FACADE", Alert.AlertType.INFORMATION);
                tablaCitas.refresh();

            } catch (CitaException e) {
                mostrarAlerta("Error", e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    private void mostrarDetallesCita(Cita cita) {
        StringBuilder detalle = new StringBuilder();
        detalle.append("📋 DETALLE DE LA CITA\n\n");
        detalle.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
        detalle.append("ID: ").append(cita.getId()).append("\n");
        detalle.append("Estado: ").append(cita.getEstado()).append("\n\n");

        detalle.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        detalle.append("PACIENTE:\n");
        if (cita.getPaciente() != null) {
            detalle.append("  • Nombre: ").append(cita.getPaciente().getNombre()).append("\n");
            detalle.append("  • Documento: ").append(cita.getPaciente().getDocumento()).append("\n");
            detalle.append("  • Teléfono: ").append(cita.getPaciente().getTelefono()).append("\n");
        }

        detalle.append("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        detalle.append("CITA:\n");
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm");
        detalle.append("  • Fecha: ").append(cita.getFecha().format(df)).append("\n");
        detalle.append("  • Hora: ").append(cita.getHora().format(tf)).append("\n");
        detalle.append("  • Especialidad: ").append(cita.getEspecialidad()).append("\n");
        detalle.append("  • Motivo: ").append(cita.getMotivo()).append("\n");
        detalle.append("  • Precio: $").append(String.format("%,.0f", cita.getPrecio())).append("\n");

        if (cita.getDiagnostico() != null && !cita.getDiagnostico().isEmpty()) {
            detalle.append("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            detalle.append("DIAGNÓSTICO:\n").append(cita.getDiagnostico()).append("\n");
        }

        if (cita.getObservaciones() != null && !cita.getObservaciones().isEmpty()) {
            detalle.append("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            detalle.append("OBSERVACIONES:\n").append(cita.getObservaciones()).append("\n");
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detalles de la Cita");
        alert.setHeaderText(null);
        alert.setContentText(detalle.toString());
        alert.getDialogPane().setPrefWidth(500);
        alert.showAndWait();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}