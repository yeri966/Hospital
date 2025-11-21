package co.edu.uniquindio.hospital.controllers;

import co.edu.uniquindio.hospital.*;
import co.edu.uniquindio.hospital.structural.facade.CitaFacade;
import co.edu.uniquindio.hospital.structural.facade.CitaException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador para la gestión de citas
 * UTILIZA PATRÓN FACADE para simplificar las operaciones
 */
public class GestionCitasController {

    // ==================== USO DEL PATRÓN FACADE ====================
    private CitaFacade citaFacade = new CitaFacade();

    private Cita citaSeleccionada = null;

    @FXML private TextField txtId;
    @FXML private ComboBox<Especialidad> cmbEspecialidad;
    @FXML private ComboBox<Paciente> cmbPaciente;
    @FXML private ComboBox<Medico> cmbMedico;
    @FXML private DatePicker dpFecha;
    @FXML private ComboBox<String> cmbHora;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtMotivo;

    @FXML private Button btnAgregar;
    @FXML private Button btnActualizar;
    @FXML private Button btnLimpiar;
    @FXML private Button btnEliminar;

    @FXML private TableView<Cita> tablaCitas;
    @FXML private TableColumn<Cita, String> colId;
    @FXML private TableColumn<Cita, String> colPaciente;
    @FXML private TableColumn<Cita, String> colMedico;
    @FXML private TableColumn<Cita, String> colEspecialidad;
    @FXML private TableColumn<Cita, String> colFecha;
    @FXML private TableColumn<Cita, String> colHora;
    @FXML private TableColumn<Cita, String> colPrecio;
    @FXML private TableColumn<Cita, String> colEstado;

    @FXML
    void initialize() {
        System.out.println("=== INICIALIZANDO CONTROLADOR CON PATRÓN FACADE ===");
        configurarComboBoxes();
        configurarTabla();
        cargarCitas();
        configurarSeleccionTabla();
        btnActualizar.setDisable(true);
        txtId.setText(citaFacade.generarIdCita());

        // Listener para filtrar médicos por especialidad
        cmbEspecialidad.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                filtrarMedicosPorEspecialidad(newVal);
            }
        });

        System.out.println("=== INICIALIZACIÓN CON FACADE COMPLETA ===");
    }

    private void configurarComboBoxes() {
        // Especialidades
        cmbEspecialidad.setItems(FXCollections.observableArrayList(Especialidad.values()));

        // Pacientes - usando Facade
        List<Paciente> pacientes = citaFacade.obtenerTodosPacientes();
        cmbPaciente.setItems(FXCollections.observableArrayList(pacientes));
        configurarCellFactoryPaciente();

        // Médicos - usando Facade
        cargarTodosMedicos();

        // Horas disponibles
        configurarHorasDisponibles();
    }

    private void configurarCellFactoryPaciente() {
        cmbPaciente.setCellFactory(param -> new ListCell<Paciente>() {
            @Override
            protected void updateItem(Paciente item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre() + " - " + item.getDocumento());
            }
        });
        cmbPaciente.setButtonCell(new ListCell<Paciente>() {
            @Override
            protected void updateItem(Paciente item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre() + " - " + item.getDocumento());
            }
        });
    }

    private void cargarTodosMedicos() {
        List<Medico> medicos = citaFacade.obtenerTodosMedicos();
        cmbMedico.setItems(FXCollections.observableArrayList(medicos));
        configurarCellFactoryMedico();
    }

    private void configurarCellFactoryMedico() {
        cmbMedico.setCellFactory(param -> new ListCell<Medico>() {
            @Override
            protected void updateItem(Medico item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre() + " - " + item.getEspecialidad());
            }
        });
        cmbMedico.setButtonCell(new ListCell<Medico>() {
            @Override
            protected void updateItem(Medico item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre() + " - " + item.getEspecialidad());
            }
        });
    }

    private void filtrarMedicosPorEspecialidad(Especialidad especialidad) {
        List<Medico> medicosFiltrados = citaFacade.obtenerMedicosPorEspecialidad(especialidad);
        cmbMedico.setItems(FXCollections.observableArrayList(medicosFiltrados));
        cmbMedico.setValue(null);
        System.out.println("[FACADE] Médicos filtrados por " + especialidad + ": " + medicosFiltrados.size());
    }

    private void configurarHorasDisponibles() {
        List<String> horas = new ArrayList<>();
        for (int i = 8; i <= 17; i++) {
            horas.add(String.format("%02d:00", i));
            horas.add(String.format("%02d:30", i));
        }
        cmbHora.setItems(FXCollections.observableArrayList(horas));
    }

    private void configurarTabla() {
        colId.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getId()));

        colPaciente.setCellValueFactory(cd -> {
            Paciente p = cd.getValue().getPaciente();
            return new SimpleStringProperty(p != null ? p.getNombre() : "");
        });

        colMedico.setCellValueFactory(cd -> {
            Medico m = cd.getValue().getMedico();
            return new SimpleStringProperty(m != null ? m.getNombre() : "");
        });

        colEspecialidad.setCellValueFactory(cd -> {
            Especialidad e = cd.getValue().getEspecialidad();
            return new SimpleStringProperty(e != null ? e.toString() : "");
        });

        colFecha.setCellValueFactory(cd -> {
            LocalDate f = cd.getValue().getFecha();
            return new SimpleStringProperty(f != null ?
                    f.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "");
        });

        colHora.setCellValueFactory(cd -> {
            LocalTime h = cd.getValue().getHora();
            return new SimpleStringProperty(h != null ?
                    h.format(DateTimeFormatter.ofPattern("HH:mm")) : "");
        });

        colPrecio.setCellValueFactory(cd ->
                new SimpleStringProperty(String.format("$%,.0f", cd.getValue().getPrecio())));

        colEstado.setCellValueFactory(cd -> {
            EstadoCita e = cd.getValue().getEstado();
            return new SimpleStringProperty(e != null ? e.toString() : "");
        });

        // Estilo para columna estado
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

    private void configurarSeleccionTabla() {
        tablaCitas.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        citaSeleccionada = newVal;
                        cargarCitaEnFormulario(newVal);
                        btnActualizar.setDisable(false);
                        btnAgregar.setDisable(true);
                    }
                }
        );
    }

    private void cargarCitaEnFormulario(Cita cita) {
        txtId.setText(cita.getId());
        cmbEspecialidad.setValue(cita.getEspecialidad());
        cmbPaciente.setValue(cita.getPaciente());
        cmbMedico.setValue(cita.getMedico());
        dpFecha.setValue(cita.getFecha());
        if (cita.getHora() != null) {
            cmbHora.setValue(cita.getHora().format(DateTimeFormatter.ofPattern("HH:mm")));
        }
        txtPrecio.setText(String.valueOf(cita.getPrecio()));
        txtMotivo.setText(cita.getMotivo());
    }

    /**
     * ==================== AGREGAR CITA USANDO FACADE ====================
     */
    @FXML
    void onAgregar(ActionEvent event) {
        if (!validarCampos()) return;

        try {
            String id = citaFacade.generarIdCita();
            LocalTime hora = LocalTime.parse(cmbHora.getValue(), DateTimeFormatter.ofPattern("HH:mm"));
            double precio = Double.parseDouble(txtPrecio.getText().trim());

            // ✨ USAR FACADE PARA CREAR LA CITA
            Cita nuevaCita = citaFacade.crearCita(
                    id,
                    cmbPaciente.getValue(),
                    cmbMedico.getValue(),
                    cmbEspecialidad.getValue(),
                    dpFecha.getValue(),
                    hora,
                    precio,
                    txtMotivo.getText().trim()
            );

            mostrarAlerta("Éxito",
                    "✨ Cita creada exitosamente usando FACADE\nID: " + nuevaCita.getId(),
                    Alert.AlertType.INFORMATION);

            cargarCitas();
            limpiarFormulario();

        } catch (CitaException e) {
            mostrarAlerta("Error de Validación", e.getMessage(), Alert.AlertType.WARNING);
        } catch (NumberFormatException e) {
            mostrarAlerta("Error de Formato", "El precio debe ser un número válido", Alert.AlertType.ERROR);
        } catch (Exception e) {
            mostrarAlerta("Error", "Error inesperado: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    /**
     * ==================== ACTUALIZAR CITA USANDO FACADE ====================
     */
    @FXML
    void onActualizar(ActionEvent event) {
        if (citaSeleccionada == null) {
            mostrarAlerta("Error", "Debe seleccionar una cita", Alert.AlertType.WARNING);
            return;
        }

        if (!validarCampos()) return;

        try {
            LocalTime hora = LocalTime.parse(cmbHora.getValue(), DateTimeFormatter.ofPattern("HH:mm"));
            double precio = Double.parseDouble(txtPrecio.getText().trim());

            // ✨ USAR FACADE PARA ACTUALIZAR
            citaFacade.actualizarCita(
                    citaSeleccionada,
                    cmbPaciente.getValue(),
                    cmbMedico.getValue(),
                    cmbEspecialidad.getValue(),
                    dpFecha.getValue(),
                    hora,
                    precio,
                    txtMotivo.getText().trim()
            );

            mostrarAlerta("Éxito",
                    "✨ Cita actualizada usando FACADE",
                    Alert.AlertType.INFORMATION);

            cargarCitas();
            limpiarFormulario();

        } catch (CitaException e) {
            mostrarAlerta("Error de Validación", e.getMessage(), Alert.AlertType.WARNING);
        } catch (NumberFormatException e) {
            mostrarAlerta("Error de Formato", "El precio debe ser un número válido", Alert.AlertType.ERROR);
        } catch (Exception e) {
            mostrarAlerta("Error", "Error inesperado: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * ==================== ELIMINAR CITA USANDO FACADE ====================
     */
    @FXML
    void onEliminar(ActionEvent event) {
        Cita cita = tablaCitas.getSelectionModel().getSelectedItem();
        if (cita == null) {
            mostrarAlerta("Error", "Debe seleccionar una cita", Alert.AlertType.WARNING);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar Eliminación");
        confirm.setHeaderText("¿Eliminar esta cita?");
        confirm.setContentText("Paciente: " + cita.getPaciente().getNombre());

        if (confirm.showAndWait().get() == ButtonType.OK) {
            try {
                // ✨ USAR FACADE PARA ELIMINAR
                citaFacade.eliminarCita(cita.getId());

                mostrarAlerta("Éxito", "Cita eliminada usando FACADE", Alert.AlertType.INFORMATION);
                cargarCitas();
                limpiarFormulario();

            } catch (CitaException e) {
                mostrarAlerta("Error", e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    void onLimpiar(ActionEvent event) {
        limpiarFormulario();
    }

    private void limpiarFormulario() {
        txtId.setText(citaFacade.generarIdCita());
        cmbEspecialidad.setValue(null);
        cmbPaciente.setValue(null);
        cmbMedico.setValue(null);
        cargarTodosMedicos();
        dpFecha.setValue(null);
        cmbHora.setValue(null);
        txtPrecio.clear();
        txtMotivo.clear();
        citaSeleccionada = null;
        tablaCitas.getSelectionModel().clearSelection();
        btnAgregar.setDisable(false);
        btnActualizar.setDisable(true);
    }

    private boolean validarCampos() {
        if (cmbEspecialidad.getValue() == null) {
            mostrarAlerta("Campos Incompletos", "Seleccione especialidad", Alert.AlertType.WARNING);
            return false;
        }
        if (cmbPaciente.getValue() == null) {
            mostrarAlerta("Campos Incompletos", "Seleccione paciente", Alert.AlertType.WARNING);
            return false;
        }
        if (cmbMedico.getValue() == null) {
            mostrarAlerta("Campos Incompletos", "Seleccione médico", Alert.AlertType.WARNING);
            return false;
        }
        if (dpFecha.getValue() == null) {
            mostrarAlerta("Campos Incompletos", "Seleccione fecha", Alert.AlertType.WARNING);
            return false;
        }
        if (cmbHora.getValue() == null) {
            mostrarAlerta("Campos Incompletos", "Seleccione hora", Alert.AlertType.WARNING);
            return false;
        }
        if (txtPrecio.getText().trim().isEmpty()) {
            mostrarAlerta("Campos Incompletos", "Ingrese el precio", Alert.AlertType.WARNING);
            return false;
        }
        if (txtMotivo.getText().trim().isEmpty()) {
            mostrarAlerta("Campos Incompletos", "Ingrese el motivo", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    private void cargarCitas() {
        // ✨ USAR FACADE PARA OBTENER CITAS
        ObservableList<Cita> citas = FXCollections.observableArrayList(
                citaFacade.obtenerTodasLasCitas()
        );
        tablaCitas.setItems(citas);
        tablaCitas.refresh();
        System.out.println("[FACADE] Total citas cargadas: " + citas.size());
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}