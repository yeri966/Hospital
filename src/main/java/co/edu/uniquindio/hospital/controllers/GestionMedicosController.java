package co.edu.uniquindio.hospital.controllers;

import co.edu.uniquindio.hospital.*;
import co.edu.uniquindio.hospital.creational.singleton.Hospital;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;
import java.util.stream.Collectors;

public class GestionMedicosController {

    private Hospital hospital = Hospital.getInstance();
    private Medico medicoSeleccionado = null;

    @FXML private TextField txtId;
    @FXML private TextField txtDocumento;
    @FXML private TextField txtNombre;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtLicenciaMedica;
    @FXML private ComboBox<Especialidad> cmbEspecialidad;
    @FXML private CheckBox chkDisponible;

    @FXML private Button btnAgregar;
    @FXML private Button btnActualizar;
    @FXML private Button btnLimpiar;
    @FXML private Button btnEliminar;

    @FXML private TableView<Medico> tablaMedicos;
    @FXML private TableColumn<Medico, String> colId;
    @FXML private TableColumn<Medico, String> colDocumento;
    @FXML private TableColumn<Medico, String> colNombre;
    @FXML private TableColumn<Medico, String> colCorreo;
    @FXML private TableColumn<Medico, String> colTelefono;
    @FXML private TableColumn<Medico, String> colEspecialidad;
    @FXML private TableColumn<Medico, String> colDisponible;

    @FXML
    void initialize() {
        configurarComboBoxes();
        configurarTabla();
        cargarMedicos();
        configurarSeleccionTabla();
        btnActualizar.setDisable(true);
        chkDisponible.setSelected(true);
    }

    private void configurarComboBoxes() {
        cmbEspecialidad.setItems(FXCollections.observableArrayList(Especialidad.values()));
    }

    private void configurarTabla() {
        colId.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getId()));

        colDocumento.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDocumento()));

        colNombre.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getNombre()));

        colCorreo.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCorreo()));

        colTelefono.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTelefono()));

        colEspecialidad.setCellValueFactory(cellData -> {
            Especialidad esp = cellData.getValue().getEspecialidad();
            return new SimpleStringProperty(esp != null ? esp.toString() : "");
        });

        colDisponible.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().isDisponible() ? "Sí" : "No"));

        // Aplicar estilo a la columna de disponibilidad
        colDisponible.setCellFactory(column -> new TableCell<Medico, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equals("Sí")) {
                        setStyle("-fx-text-fill: #2e7d32; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #c62828; -fx-font-weight: bold;");
                    }
                }
            }
        });
    }

    private void configurarSeleccionTabla() {
        tablaMedicos.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        medicoSeleccionado = newValue;
                        cargarMedicoEnFormulario(newValue);
                        btnActualizar.setDisable(false);
                        btnAgregar.setDisable(true);
                    }
                }
        );
    }

    private void cargarMedicoEnFormulario(Medico medico) {
        txtId.setText(medico.getId());
        txtDocumento.setText(medico.getDocumento());
        txtNombre.setText(medico.getNombre());
        txtCorreo.setText(medico.getCorreo());
        txtTelefono.setText(medico.getTelefono());
        txtLicenciaMedica.setText(medico.getLicenciaMedica());
        cmbEspecialidad.setValue(medico.getEspecialidad());
        chkDisponible.setSelected(medico.isDisponible());
    }

    @FXML
    void onAgregar(ActionEvent event) {
        if (!validarCampos()) {
            return;
        }

        // Validar que el documento no exista
        if (documentoExiste(txtDocumento.getText().trim())) {
            mostrarAlerta("Documento Duplicado",
                    "Ya existe un médico con este documento",
                    Alert.AlertType.WARNING);
            return;
        }

        String id = generarIdMedico();

        // Crear UserAccount para el médico
        String usuario = generarUsuario(txtNombre.getText().trim());
        UserAccount userAccount = new UserAccount(usuario, "1234", null, TipoUsuario.MEDICO);

        Medico nuevoMedico = new Medico(
                id,
                txtDocumento.getText().trim(),
                txtNombre.getText().trim(),
                txtCorreo.getText().trim(),
                txtTelefono.getText().trim(),
                userAccount,
                cmbEspecialidad.getValue(),
                txtLicenciaMedica.getText().trim(),
                chkDisponible.isSelected()
        );

        // Establecer relación bidireccional
        userAccount.setPerson(nuevoMedico);

        hospital.agregarPersona(nuevoMedico);

        mostrarAlerta("Éxito",
                "Médico agregado correctamente\nUsuario: " + usuario + "\nContraseña: 1234",
                Alert.AlertType.INFORMATION);

        cargarMedicos();
        limpiarFormulario();
    }

    @FXML
    void onActualizar(ActionEvent event) {
        if (medicoSeleccionado == null) {
            mostrarAlerta("Error", "Debe seleccionar un médico", Alert.AlertType.WARNING);
            return;
        }

        if (!validarCampos()) {
            return;
        }

        // Validar documento duplicado (excepto el mismo médico)
        if (documentoExisteParaOtro(txtDocumento.getText().trim(), medicoSeleccionado.getId())) {
            mostrarAlerta("Documento Duplicado",
                    "Ya existe otro médico con este documento",
                    Alert.AlertType.WARNING);
            return;
        }

        medicoSeleccionado.setDocumento(txtDocumento.getText().trim());
        medicoSeleccionado.setNombre(txtNombre.getText().trim());
        medicoSeleccionado.setCorreo(txtCorreo.getText().trim());
        medicoSeleccionado.setTelefono(txtTelefono.getText().trim());
        medicoSeleccionado.setLicenciaMedica(txtLicenciaMedica.getText().trim());
        medicoSeleccionado.setEspecialidad(cmbEspecialidad.getValue());
        medicoSeleccionado.setDisponible(chkDisponible.isSelected());

        hospital.actualizarPersona(medicoSeleccionado);
        mostrarAlerta("Éxito", "Médico actualizado correctamente", Alert.AlertType.INFORMATION);
        cargarMedicos();
        limpiarFormulario();
    }

    @FXML
    void onEliminar(ActionEvent event) {
        Medico selected = tablaMedicos.getSelectionModel().getSelectedItem();

        if (selected == null) {
            mostrarAlerta("Error", "Debe seleccionar un médico", Alert.AlertType.WARNING);
            return;
        }

        // Verificar si el médico tiene citas asignadas
        long citasAsignadas = hospital.getListCitas().stream()
                .filter(cita -> cita.getMedico() != null &&
                        cita.getMedico().getId().equals(selected.getId()))
                .count();

        if (citasAsignadas > 0) {
            mostrarAlerta("No se puede eliminar",
                    "Este médico tiene " + citasAsignadas + " cita(s) asignada(s)",
                    Alert.AlertType.WARNING);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("¿Está seguro que desea eliminar este médico?");
        confirmacion.setContentText(selected.getNombre() + " - " + selected.getEspecialidad());

        if (confirmacion.showAndWait().get() == ButtonType.OK) {
            hospital.eliminarPersona(selected);
            mostrarAlerta("Éxito", "Médico eliminado correctamente", Alert.AlertType.INFORMATION);
            cargarMedicos();
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
        txtLicenciaMedica.clear();
        cmbEspecialidad.setValue(null);
        chkDisponible.setSelected(true);
        medicoSeleccionado = null;
        tablaMedicos.getSelectionModel().clearSelection();
        btnAgregar.setDisable(false);
        btnActualizar.setDisable(true);
        txtId.setText(generarIdMedico());
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

        if (!txtCorreo.getText().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            mostrarAlerta("Correo Inválido", "El formato del correo no es válido", Alert.AlertType.WARNING);
            return false;
        }

        if (txtTelefono.getText().trim().isEmpty()) {
            mostrarAlerta("Campos Incompletos", "Debe ingresar el teléfono", Alert.AlertType.WARNING);
            return false;
        }

        if (txtLicenciaMedica.getText().trim().isEmpty()) {
            mostrarAlerta("Campos Incompletos", "Debe ingresar la licencia médica", Alert.AlertType.WARNING);
            return false;
        }

        if (cmbEspecialidad.getValue() == null) {
            mostrarAlerta("Campos Incompletos", "Debe seleccionar una especialidad", Alert.AlertType.WARNING);
            return false;
        }

        return true;
    }

    private void cargarMedicos() {
        List<Medico> medicos = hospital.getListPersonas().stream()
                .filter(person -> person instanceof Medico)
                .map(person -> (Medico) person)
                .collect(Collectors.toList());

        ObservableList<Medico> medicosList = FXCollections.observableArrayList(medicos);
        tablaMedicos.setItems(medicosList);

        if (txtId.getText().isEmpty()) {
            txtId.setText(generarIdMedico());
        }
    }

    private String generarIdMedico() {
        int count = (int) hospital.getListPersonas().stream()
                .filter(person -> person instanceof Medico)
                .count();
        return String.format("MED%03d", count + 1);
    }

    private String generarUsuario(String nombreCompleto) {
        String[] partes = nombreCompleto.toLowerCase().split(" ");
        String usuario = partes[0];

        // Si el usuario ya existe, agregar un número
        int contador = 1;
        String usuarioFinal = usuario;
        while (usuarioExiste(usuarioFinal)) {
            usuarioFinal = usuario + contador;
            contador++;
        }

        return usuarioFinal;
    }

    private boolean usuarioExiste(String usuario) {
        return hospital.getListPersonas().stream()
                .anyMatch(person -> person.getUserAccount() != null &&
                        person.getUserAccount().getUsuario().equalsIgnoreCase(usuario));
    }

    private boolean documentoExiste(String documento) {
        return hospital.getListPersonas().stream()
                .filter(person -> person instanceof Medico)
                .anyMatch(person -> person.getDocumento().equals(documento));
    }

    private boolean documentoExisteParaOtro(String documento, String idActual) {
        return hospital.getListPersonas().stream()
                .filter(person -> person instanceof Medico)
                .filter(person -> !person.getId().equals(idActual))
                .anyMatch(person -> person.getDocumento().equals(documento));
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}