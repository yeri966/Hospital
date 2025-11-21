// src/main/java/co/edu/uniquindio/hospital/controllers/RegistroMedicoController.java
package co.edu.uniquindio.hospital.controllers;

import co.edu.uniquindio.hospital.*;
import co.edu.uniquindio.hospital.creational.singleton.Hospital;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.regex.Pattern;

public class RegistroMedicoController {

    private Hospital hospital = Hospital.getInstance();

    @FXML private TextField txtNombre;
    @FXML private TextField txtDocumento;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtLicenciaMedica;
    @FXML private ComboBox<Especialidad> cmbEspecialidad;
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtContrasenia;
    @FXML private Button btnRegistrar;
    @FXML private Button btnCancelar;

    @FXML
    void initialize() {
        cmbEspecialidad.setItems(FXCollections.observableArrayList(Especialidad.values()));
    }

    @FXML
    void onRegistrar(ActionEvent event) {
        if (!validarCampos()) {
            return;
        }

        String id = generarIdMedico();
        String usuario = txtUsuario.getText().trim();

        // Validar que el usuario no exista
        if (usuarioExiste(usuario)) {
            mostrarAlerta("Usuario Existente", "El nombre de usuario ya está en uso. Por favor elija otro", Alert.AlertType.WARNING);
            return;
        }

        UserAccount userAccount = new UserAccount(usuario, txtContrasenia.getText(), null, TipoUsuario.MEDICO);

        Medico nuevoMedico = new Medico(
                id,
                txtDocumento.getText().trim(),
                txtNombre.getText().trim(),
                txtCorreo.getText().trim(),
                txtTelefono.getText().trim(),
                userAccount,
                cmbEspecialidad.getValue(),
                txtLicenciaMedica.getText().trim(),
                true
        );

        userAccount.setPerson(nuevoMedico);

        hospital.agregarPersona(nuevoMedico);

        mostrarAlerta("Registro Exitoso", "¡Médico registrado exitosamente! Ya puede iniciar sesión", Alert.AlertType.INFORMATION);

        cerrarVentana();
    }

    @FXML
    void onCancelar(ActionEvent event) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Cancelación");
        confirmacion.setHeaderText("¿Está seguro que desea cancelar?");
        confirmacion.setContentText("Los datos ingresados se perderán");

        if (confirmacion.showAndWait().get() == ButtonType.OK) {
            cerrarVentana();
        }
    }

    private boolean validarCampos() {
        if (txtNombre.getText().trim().isEmpty()) {
            mostrarAlerta("Campos Incompletos", "Debe ingresar el nombre", Alert.AlertType.WARNING);
            return false;
        }

        if (txtDocumento.getText().trim().isEmpty()) {
            mostrarAlerta("Campos Incompletos", "Debe ingresar el documento", Alert.AlertType.WARNING);
            return false;
        }

        if (txtCorreo.getText().trim().isEmpty() || !validarCorreo(txtCorreo.getText())) {
            mostrarAlerta("Correo Inválido", "Por favor ingrese un correo electrónico válido", Alert.AlertType.WARNING);
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

        if (txtUsuario.getText().trim().isEmpty()) {
            mostrarAlerta("Campos Incompletos", "Debe ingresar un nombre de usuario", Alert.AlertType.WARNING);
            return false;
        }

        if (txtContrasenia.getText().length() < 4) {
            mostrarAlerta("Contraseña Débil", "La contraseña debe tener al menos 4 caracteres", Alert.AlertType.WARNING);
            return false;
        }

        return true;
    }

    private boolean validarCorreo(String correo) {
        String regexCorreo = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        Pattern pattern = Pattern.compile(regexCorreo);
        return pattern.matcher(correo).matches();
    }

    private boolean usuarioExiste(String usuario) {
        return hospital.getListPersonas().stream()
                .anyMatch(person -> person.getUserAccount() != null &&
                        person.getUserAccount().getUsuario().equalsIgnoreCase(usuario));
    }

    private String generarIdMedico() {
        int numeroMedicos = (int) hospital.getListPersonas().stream()
                .filter(person -> person instanceof Medico)
                .count();
        return String.format("MED%03d", numeroMedicos + 1);
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}