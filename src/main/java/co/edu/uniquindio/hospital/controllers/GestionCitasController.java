package co.edu.uniquindio.hospital.controllers;

import co.edu.uniquindio.hospital.*;
import co.edu.uniquindio.hospital.creational.singleton.Hospital;
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
import java.util.stream.Collectors;

public class GestionCitasController {

    private Hospital hospital = Hospital.getInstance();
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
        System.out.println("=== INICIALIZANDO CONTROLADOR GESTIÓN DE CITAS ===");
        configurarComboBoxes();
        configurarTabla();
        cargarCitas();
        configurarSeleccionTabla();
        btnActualizar.setDisable(true);

        // Configurar listener para especialidad -> filtrar médicos
        cmbEspecialidad.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                filtrarMedicosPorEspecialidad(newVal);
            }
        });

        System.out.println("=== INICIALIZACIÓN COMPLETA ===");
    }

    /**
     * Configura los ComboBoxes con los datos del sistema
     */
    private void configurarComboBoxes() {
        // ComboBox de Especialidades
        cmbEspecialidad.setItems(FXCollections.observableArrayList(Especialidad.values()));

        // ComboBox de Pacientes
        List<Paciente> pacientes = hospital.getListPersonas().stream()
                .filter(persona -> persona instanceof Paciente)
                .map(persona -> (Paciente) persona)
                .collect(Collectors.toList());
        cmbPaciente.setItems(FXCollections.observableArrayList(pacientes));

        // Configurar cómo se muestran los pacientes en el ComboBox
        cmbPaciente.setCellFactory(param -> new ListCell<Paciente>() {
            @Override
            protected void updateItem(Paciente item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNombre() + " - " + item.getDocumento());
                }
            }
        });
        cmbPaciente.setButtonCell(new ListCell<Paciente>() {
            @Override
            protected void updateItem(Paciente item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNombre() + " - " + item.getDocumento());
                }
            }
        });

        // ComboBox de Médicos (inicialmente todos)
        cargarTodosMedicos();

        // ComboBox de Horas disponibles
        configurarHorasDisponibles();
    }

    /**
     * Carga todos los médicos en el ComboBox
     */
    private void cargarTodosMedicos() {
        List<Medico> medicos = hospital.getListPersonas().stream()
                .filter(persona -> persona instanceof Medico)
                .map(persona -> (Medico) persona)
                .collect(Collectors.toList());
        cmbMedico.setItems(FXCollections.observableArrayList(medicos));

        // Configurar cómo se muestran los médicos
        cmbMedico.setCellFactory(param -> new ListCell<Medico>() {
            @Override
            protected void updateItem(Medico item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNombre() + " - " + item.getEspecialidad());
                }
            }
        });
        cmbMedico.setButtonCell(new ListCell<Medico>() {
            @Override
            protected void updateItem(Medico item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNombre() + " - " + item.getEspecialidad());
                }
            }
        });
    }

    /**
     * Filtra los médicos según la especialidad seleccionada
     */
    private void filtrarMedicosPorEspecialidad(Especialidad especialidad) {
        List<Medico> medicosFiltrados = hospital.getListPersonas().stream()
                .filter(persona -> persona instanceof Medico)
                .map(persona -> (Medico) persona)
                .filter(medico -> medico.getEspecialidad() == especialidad)
                .collect(Collectors.toList());

        cmbMedico.setItems(FXCollections.observableArrayList(medicosFiltrados));
        cmbMedico.setValue(null); // Limpiar selección anterior

        System.out.println("Médicos filtrados por " + especialidad + ": " + medicosFiltrados.size());
    }

    /**
     * Configura las horas disponibles (8:00 AM - 5:00 PM)
     */
    private void configurarHorasDisponibles() {
        List<String> horas = new ArrayList<>();
        for (int i = 8; i <= 17; i++) {
            horas.add(String.format("%02d:00", i));
            horas.add(String.format("%02d:30", i));
        }
        cmbHora.setItems(FXCollections.observableArrayList(horas));
    }

    /**
     * Configura las columnas de la tabla
     */
    private void configurarTabla() {
        colId.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getId()));

        colPaciente.setCellValueFactory(cellData -> {
            Paciente paciente = cellData.getValue().getPaciente();
            return new SimpleStringProperty(paciente != null ? paciente.getNombre() : "");
        });

        colMedico.setCellValueFactory(cellData -> {
            Medico medico = cellData.getValue().getMedico();
            return new SimpleStringProperty(medico != null ? medico.getNombre() : "");
        });

        colEspecialidad.setCellValueFactory(cellData -> {
            Especialidad especialidad = cellData.getValue().getEspecialidad();
            return new SimpleStringProperty(especialidad != null ? especialidad.toString() : "");
        });

        colFecha.setCellValueFactory(cellData -> {
            LocalDate fecha = cellData.getValue().getFecha();
            if (fecha != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                return new SimpleStringProperty(fecha.format(formatter));
            }
            return new SimpleStringProperty("");
        });

        colHora.setCellValueFactory(cellData -> {
            LocalTime hora = cellData.getValue().getHora();
            if (hora != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                return new SimpleStringProperty(hora.format(formatter));
            }
            return new SimpleStringProperty("");
        });

        colPrecio.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.format("$%,.0f", cellData.getValue().getPrecio())));

        colEstado.setCellValueFactory(cellData -> {
            EstadoCita estado = cellData.getValue().getEstado();
            return new SimpleStringProperty(estado != null ? estado.toString() : "");
        });

        // Aplicar estilo a la columna de estado
        colEstado.setCellFactory(column -> new TableCell<Cita, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item) {
                        case "PROGRAMADA":
                            setStyle("-fx-text-fill: #2196F3; -fx-font-weight: bold;");
                            break;
                        case "ATENDIDA":
                            setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
                            break;
                        case "CANCELADA":
                            setStyle("-fx-text-fill: #F44336; -fx-font-weight: bold;");
                            break;
                        default:
                            setStyle("-fx-text-fill: #757575; -fx-font-weight: bold;");
                    }
                }
            }
        });
    }

    /**
     * Configura la selección de la tabla
     */
    private void configurarSeleccionTabla() {
        tablaCitas.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        citaSeleccionada = newValue;
                        cargarCitaEnFormulario(newValue);
                        btnActualizar.setDisable(false);
                        btnAgregar.setDisable(true);
                    }
                }
        );
    }

    /**
     * Carga los datos de una cita en el formulario
     */
    private void cargarCitaEnFormulario(Cita cita) {
        txtId.setText(cita.getId());
        cmbEspecialidad.setValue(cita.getEspecialidad());
        cmbPaciente.setValue(cita.getPaciente());
        cmbMedico.setValue(cita.getMedico());
        dpFecha.setValue(cita.getFecha());

        if (cita.getHora() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            cmbHora.setValue(cita.getHora().format(formatter));
        }

        txtPrecio.setText(String.valueOf(cita.getPrecio()));
        txtMotivo.setText(cita.getMotivo());
    }

    /**
     * Agrega una nueva cita
     */
    @FXML
    void onAgregar(ActionEvent event) {
        if (!validarCampos()) {
            return;
        }

        try {
            // Generar ID único
            String id = generarIdCita();

            // Parsear la hora
            String horaString = cmbHora.getValue();
            LocalTime hora = LocalTime.parse(horaString, DateTimeFormatter.ofPattern("HH:mm"));

            // Parsear el precio
            double precio = Double.parseDouble(txtPrecio.getText().trim());

            // Crear nueva cita
            Cita nuevaCita = new Cita(
                    id,
                    cmbPaciente.getValue(),
                    cmbMedico.getValue(),
                    cmbEspecialidad.getValue(),
                    dpFecha.getValue(),
                    hora,
                    precio,
                    txtMotivo.getText().trim()
            );

            // Verificar disponibilidad
            if (!verificarDisponibilidad(nuevaCita)) {
                mostrarAlerta("Horario No Disponible",
                        "El médico ya tiene una cita programada en ese horario",
                        Alert.AlertType.WARNING);
                return;
            }

            // Agregar al sistema
            hospital.addCita(nuevaCita);

            mostrarAlerta("Éxito",
                    "Cita agregada correctamente",
                    Alert.AlertType.INFORMATION);

            cargarCitas();
            limpiarFormulario();

        } catch (NumberFormatException e) {
            mostrarAlerta("Error de Formato",
                    "El precio debe ser un número válido",
                    Alert.AlertType.ERROR);
        }
    }

    /**
     * Actualiza una cita existente
     */
    @FXML
    void onActualizar(ActionEvent event) {
        if (citaSeleccionada == null) {
            mostrarAlerta("Error",
                    "Debe seleccionar una cita de la tabla",
                    Alert.AlertType.WARNING);
            return;
        }

        if (!validarCampos()) {
            return;
        }

        try {
            // Parsear la hora
            String horaString = cmbHora.getValue();
            LocalTime hora = LocalTime.parse(horaString, DateTimeFormatter.ofPattern("HH:mm"));

            // Parsear el precio
            double precio = Double.parseDouble(txtPrecio.getText().trim());

            // Actualizar datos
            citaSeleccionada.setPaciente(cmbPaciente.getValue());
            citaSeleccionada.setMedico(cmbMedico.getValue());
            citaSeleccionada.setEspecialidad(cmbEspecialidad.getValue());
            citaSeleccionada.setFecha(dpFecha.getValue());
            citaSeleccionada.setHora(hora);
            citaSeleccionada.setPrecio(precio);
            citaSeleccionada.setMotivo(txtMotivo.getText().trim());

            // Verificar disponibilidad (excluyendo la cita actual)
            if (!verificarDisponibilidadActualizacion(citaSeleccionada)) {
                mostrarAlerta("Horario No Disponible",
                        "El médico ya tiene una cita programada en ese horario",
                        Alert.AlertType.WARNING);
                return;
            }

            hospital.updateCita(citaSeleccionada);

            mostrarAlerta("Éxito",
                    "Cita actualizada correctamente",
                    Alert.AlertType.INFORMATION);

            tablaCitas.refresh();
            cargarCitas();
            limpiarFormulario();

        } catch (NumberFormatException e) {
            mostrarAlerta("Error de Formato",
                    "El precio debe ser un número válido",
                    Alert.AlertType.ERROR);
        }
    }

    /**
     * Elimina una cita
     */
    @FXML
    void onEliminar(ActionEvent event) {
        Cita citaAEliminar = tablaCitas.getSelectionModel().getSelectedItem();

        if (citaAEliminar == null) {
            mostrarAlerta("Error",
                    "Debe seleccionar una cita de la tabla",
                    Alert.AlertType.WARNING);
            return;
        }

        // Confirmación
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("¿Está seguro que desea eliminar esta cita?");
        confirmacion.setContentText("Paciente: " + citaAEliminar.getPaciente().getNombre() +
                "\nMédico: " + citaAEliminar.getMedico().getNombre() +
                "\nFecha: " + citaAEliminar.getFecha());

        if (confirmacion.showAndWait().get() == ButtonType.OK) {
            hospital.deleteCita(citaAEliminar.getId());

            mostrarAlerta("Éxito",
                    "Cita eliminada correctamente",
                    Alert.AlertType.INFORMATION);

            cargarCitas();
            limpiarFormulario();
        }
    }

    /**
     * Limpia el formulario
     */
    @FXML
    void onLimpiar(ActionEvent event) {
        limpiarFormulario();
    }

    /**
     * Limpia todos los campos del formulario
     */
    private void limpiarFormulario() {
        txtId.clear();
        txtId.setText(generarIdCita());
        cmbEspecialidad.setValue(null);
        cmbPaciente.setValue(null);
        cmbMedico.setValue(null);
        cargarTodosMedicos(); // Restaurar lista completa de médicos
        dpFecha.setValue(null);
        cmbHora.setValue(null);
        txtPrecio.clear();
        txtMotivo.clear();

        citaSeleccionada = null;
        tablaCitas.getSelectionModel().clearSelection();
        btnAgregar.setDisable(false);
        btnActualizar.setDisable(true);
    }

    /**
     * Valida que todos los campos obligatorios estén llenos
     */
    private boolean validarCampos() {
        if (cmbEspecialidad.getValue() == null) {
            mostrarAlerta("Campos Incompletos",
                    "Debe seleccionar una especialidad",
                    Alert.AlertType.WARNING);
            cmbEspecialidad.requestFocus();
            return false;
        }

        if (cmbPaciente.getValue() == null) {
            mostrarAlerta("Campos Incompletos",
                    "Debe seleccionar un paciente",
                    Alert.AlertType.WARNING);
            cmbPaciente.requestFocus();
            return false;
        }

        if (cmbMedico.getValue() == null) {
            mostrarAlerta("Campos Incompletos",
                    "Debe seleccionar un médico",
                    Alert.AlertType.WARNING);
            cmbMedico.requestFocus();
            return false;
        }

        if (dpFecha.getValue() == null) {
            mostrarAlerta("Campos Incompletos",
                    "Debe seleccionar una fecha",
                    Alert.AlertType.WARNING);
            dpFecha.requestFocus();
            return false;
        }

        // Validar que la fecha no sea pasada
        if (dpFecha.getValue().isBefore(LocalDate.now())) {
            mostrarAlerta("Fecha Inválida",
                    "La fecha de la cita no puede ser anterior a hoy",
                    Alert.AlertType.WARNING);
            dpFecha.requestFocus();
            return false;
        }

        if (cmbHora.getValue() == null) {
            mostrarAlerta("Campos Incompletos",
                    "Debe seleccionar una hora",
                    Alert.AlertType.WARNING);
            cmbHora.requestFocus();
            return false;
        }

        if (txtPrecio.getText().trim().isEmpty()) {
            mostrarAlerta("Campos Incompletos",
                    "Debe ingresar el precio de la consulta",
                    Alert.AlertType.WARNING);
            txtPrecio.requestFocus();
            return false;
        }

        try {
            double precio = Double.parseDouble(txtPrecio.getText().trim());
            if (precio <= 0) {
                mostrarAlerta("Precio Inválido",
                        "El precio debe ser mayor a 0",
                        Alert.AlertType.WARNING);
                txtPrecio.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Precio Inválido",
                    "El precio debe ser un número válido",
                    Alert.AlertType.WARNING);
            txtPrecio.requestFocus();
            return false;
        }

        if (txtMotivo.getText().trim().isEmpty()) {
            mostrarAlerta("Campos Incompletos",
                    "Debe ingresar el motivo de la consulta",
                    Alert.AlertType.WARNING);
            txtMotivo.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Verifica que el médico esté disponible en el horario seleccionado
     */
    private boolean verificarDisponibilidad(Cita nuevaCita) {
        return hospital.getListCitas().stream()
                .filter(cita -> cita.getMedico().getId().equals(nuevaCita.getMedico().getId()))
                .filter(cita -> cita.getFecha().equals(nuevaCita.getFecha()))
                .filter(cita -> cita.getHora().equals(nuevaCita.getHora()))
                .filter(cita -> cita.getEstado() != EstadoCita.CANCELADA)
                .findAny()
                .isEmpty();
    }

    /**
     * Verifica disponibilidad excluyendo la cita que se está actualizando
     */
    private boolean verificarDisponibilidadActualizacion(Cita citaActualizada) {
        return hospital.getListCitas().stream()
                .filter(cita -> !cita.getId().equals(citaActualizada.getId())) // Excluir cita actual
                .filter(cita -> cita.getMedico().getId().equals(citaActualizada.getMedico().getId()))
                .filter(cita -> cita.getFecha().equals(citaActualizada.getFecha()))
                .filter(cita -> cita.getHora().equals(citaActualizada.getHora()))
                .filter(cita -> cita.getEstado() != EstadoCita.CANCELADA)
                .findAny()
                .isEmpty();
    }

    /**
     * Carga todas las citas en la tabla
     */
    private void cargarCitas() {
        System.out.println("\n=== CARGANDO CITAS ===");
        ObservableList<Cita> citas = FXCollections.observableArrayList(hospital.getListCitas());
        tablaCitas.setItems(citas);
        tablaCitas.refresh();
        System.out.println("Total citas: " + citas.size());
        System.out.println("=== CITAS CARGADAS ===\n");
    }

    /**
     * Genera un ID único para una nueva cita
     */
    private String generarIdCita() {
        int count = hospital.getListCitas().size();
        return String.format("CIT%04d", count + 1);
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