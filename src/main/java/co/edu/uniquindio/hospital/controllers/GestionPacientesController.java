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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class GestionPacientesController {

    private Hospital hospital = Hospital.getInstance();
    private Paciente pacienteSeleccionado = null;

    @FXML private TextField txtId;
    @FXML private TextField txtDocumento;
    @FXML private TextField txtNombre;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtTelefono;
    @FXML private DatePicker dpFechaNacimiento;
    @FXML private TextField txtDireccion;
    @FXML private ComboBox<String> cmbGenero;

    @FXML private Button btnAgregar;
    @FXML private Button btnActualizar;
    @FXML private Button btnLimpiar;
    @FXML private Button btnEliminar;

    @FXML private TableView<Paciente> tablaPacientes;
    @FXML private TableColumn<Paciente, String> colId;
    @FXML private TableColumn<Paciente, String> colDocumento;
    @FXML private TableColumn<Paciente, String> colNombre;
    @FXML private TableColumn<Paciente, String> colCorreo;
    @FXML private TableColumn<Paciente, String> colTelefono;
    @FXML private TableColumn<Paciente, String> colFechaNacimiento;
    @FXML private TableColumn<Paciente, String> colGenero;

    @FXML
    void initialize() {
        configurarComboBoxes();
        configurarTabla();
        cargarPacientes();
        configurarSeleccionTabla();
        btnActualizar.setDisable(true);
    }

    private void configurarComboBoxes() {
        cmbGenero.setItems(FXCollections.observableArrayList("Masculino", "Femenino", "Otro"));
    }

    private void configurarTabla() {
        colId.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId()));
        colDocumento.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDocumento()));
        colNombre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));
        colCorreo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCorreo()));
        colTelefono.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTelefono()));

        colFechaNacimiento.setCellValueFactory(cellData -> {
            LocalDate fecha = cellData.getValue().getFechaNacimiento();
            String fechaStr = fecha != null ? fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
            return new SimpleStringProperty(fechaStr);
        });

        colGenero.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGenero()));
    }

    private void configurarSeleccionTabla() {
        tablaPacientes.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        pacienteSeleccionado = newValue;
                        cargarPacienteEnFormulario(newValue);
                        btnActualizar.setDisable(false);
                        btnAgregar.setDisable(true);
                    }
                }
        );
    }

    private void cargarPacienteEnFormulario(Paciente paciente) {
        txtId.setText(paciente.getId());
        txtDocumento.setText(paciente.getDocumento());
        txtNombre.setText(paciente.getNombre());
        txtCorreo.setText(paciente.getCorreo());
        txtTelefono.setText(paciente.getTelefono());
        dpFechaNacimiento.setValue(paciente.getFechaNacimiento());
        txtDireccion.setText(paciente.getDireccion());
        cmbGenero.setValue(paciente.getGenero());
    }

    @FXML
    void onAgregar(ActionEvent event) {
        if (!validarCampos()) {
            return;
        }

        String id = generarIdPaciente();

        Paciente nuevoPaciente = new Paciente(
                id,
                txtDocumento.getText().trim(),
                txtNombre.getText().trim(),
                txtCorreo.getText().trim(),
                txtTelefono.getText().trim(),
                null,
                dpFechaNacimiento.getValue(),
                txtDireccion.getText().trim(),
                cmbGenero.getValue()
        );

        hospital.agregarPersona(nuevoPaciente);
        mostrarAlerta("Éxito", "Paciente agregado correctamente", Alert.AlertType.INFORMATION);
        cargarPacientes();
        limpiarFormulario();
    }

    @FXML
    void onActualizar(ActionEvent event) {
        if (pacienteSeleccionado == null) {
            mostrarAlerta("Error", "Debe seleccionar un paciente", Alert.AlertType.WARNING);
            return;
        }

        if (!validarCampos()) {
            return;
        }

        pacienteSeleccionado.setDocumento(txtDocumento.getText().trim());
        pacienteSeleccionado.setNombre(txtNombre.getText().trim());
        pacienteSeleccionado.setCorreo(txtCorreo.getText().trim());
        pacienteSeleccionado.setTelefono(txtTelefono.getText().trim());
        pacienteSeleccionado.setFechaNacimiento(dpFechaNacimiento.getValue());
        pacienteSeleccionado.setDireccion(txtDireccion.getText().trim());
        pacienteSeleccionado.setGenero(cmbGenero.getValue());

        hospital.actualizarPersona(pacienteSeleccionado);
        mostrarAlerta("Éxito", "Paciente actualizado correctamente", Alert.AlertType.INFORMATION);
        cargarPacientes();
        limpiarFormulario();
    }

    @FXML
    void onEliminar(ActionEvent event) {
        Paciente selected = tablaPacientes.getSelectionModel().getSelectedItem();

        if (selected == null) {
            mostrarAlerta("Error", "Debe seleccionar un paciente", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("¿Está seguro que desea eliminar este paciente?");
        confirmacion.setContentText(selected.getNombre());

        if (confirmacion.showAndWait().get() == ButtonType.OK) {
            hospital.eliminarPersona(selected);
            mostrarAlerta("Éxito", "Paciente eliminado correctamente", Alert.AlertType.INFORMATION);
            cargarPacientes();
            limpiarFormulario();
        }
    }

    @FXML
    void onLimpiar(ActionEvent event) {
        limpiarFormulario();
    }

    private void limpiarFormulario() {
        txtId.clear();
        txtDocumento.clear();
        txtNombre.clear();
        txtCorreo.clear();
        txtTelefono.clear();
        dpFechaNacimiento.setValue(null);
        txtDireccion.clear();
        cmbGenero.setValue(null);
        pacienteSeleccionado = null;
        tablaPacientes.getSelectionModel().clearSelection();
        btnAgregar.setDisable(false);
        btnActualizar.setDisable(true);
        txtId.setText(generarIdPaciente());
    }

    private boolean validarCampos() {
        if (txtDocumento.getText().trim().isEmpty()) {
            mostrarAlerta("Campos Incompletos", "Debe ingresar el documento", Alert.AlertType.WARNING);
            return false;
        }

        if (txtNombre.getText().trim().isEmpty()) {
            mostrarAlerta("Campos Incompletos", "Debe ingresar el nombre", Alert.AlertType.WARNING);
            return false;
        }

        if (txtCorreo.getText().trim().isEmpty()) {
            mostrarAlerta("Campos Incompletos", "Debe ingresar el correo", Alert.AlertType.WARNING);
            return false;
        }

        if (txtTelefono.getText().trim().isEmpty()) {
            mostrarAlerta("Campos Incompletos", "Debe ingresar el teléfono", Alert.AlertType.WARNING);
            return false;
        }

        if (dpFechaNacimiento.getValue() == null) {
            mostrarAlerta("Campos Incompletos", "Debe seleccionar la fecha de nacimiento", Alert.AlertType.WARNING);
            return false;
        }

        if (txtDireccion.getText().trim().isEmpty()) {
            mostrarAlerta("Campos Incompletos", "Debe ingresar la dirección", Alert.AlertType.WARNING);
            return false;
        }

        if (cmbGenero.getValue() == null) {
            mostrarAlerta("Campos Incompletos", "Debe seleccionar el género", Alert.AlertType.WARNING);
            return false;
        }

        return true;
    }

    private void cargarPacientes() {
        List<Paciente> pacientes = hospital.getListPersonas().stream()
                .filter(person -> person instanceof Paciente)
                .map(person -> (Paciente) person)
                .collect(Collectors.toList());

        ObservableList<Paciente> pacientesList = FXCollections.observableArrayList(pacientes);
        tablaPacientes.setItems(pacientesList);

        if (txtId.getText().isEmpty()) {
            txtId.setText(generarIdPaciente());
        }
    }

    private String generarIdPaciente() {
        int count = (int) hospital.getListPersonas().stream()
                .filter(person -> person instanceof Paciente)
                .count();
        return String.format("PAC%03d", count + 1);
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}