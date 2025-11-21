package co.edu.uniquindio.hospital.controllers;

import co.edu.uniquindio.hospital.*;
import co.edu.uniquindio.hospital.creational.singleton.Hospital;
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
import java.util.stream.Collectors;

public class CitasMedicasController {

    private Hospital hospital = Hospital.getInstance();
    private Medico medicoActual;
    private ObservableList<Cita> todasLasCitas = FXCollections.observableArrayList();

    @FXML private TableView<Cita> tablaCitas;
    @FXML private TableColumn<Cita, String> colId;
    @FXML private TableColumn<Cita, String> colFecha;
    @FXML private TableColumn<Cita, String> colHora;
    @FXML private TableColumn<Cita, String> colPaciente;
    @FXML private TableColumn<Cita, String> colDocumento;
    @FXML private TableColumn<Cita, String> colMotivo;
    @FXML private TableColumn<Cita, String> colEstado;
    @FXML private TableColumn<Cita, String> colAcciones;

    @FXML private Label lblCitasProgramadas;
    @FXML private Label lblCitasAtendidas;
    @FXML private Label lblCitasHoy;

    @FXML private ComboBox<String> cmbFiltroEstado;
    @FXML private DatePicker dpFiltroFecha;
    @FXML private Button btnFiltrar;
    @FXML private Button btnLimpiarFiltros;

    @FXML private Button btnAtenderCita;
    @FXML private Button btnVerDetalles;
    @FXML private Button btnCancelarCita;
    @FXML private Button btnAgregarDiagnostico;

    @FXML
    void initialize() {
        System.out.println("=== INICIALIZANDO MIS CITAS MÉDICO ===");

        // Obtener el médico actual del sistema
        obtenerMedicoActual();

        if (medicoActual == null) {
            mostrarAlerta("Error",
                    "No se pudo identificar al médico actual",
                    Alert.AlertType.ERROR);
            return;
        }

        configurarTabla();
        configurarFiltros();
        cargarCitasMedico();
        actualizarEstadisticas();

        System.out.println("=== INICIALIZACIÓN COMPLETA ===");
    }

    /**
     * Obtiene el médico actualmente logueado
     */
    private void obtenerMedicoActual() {
        Person usuarioActivo = hospital.getUsuarioActivo();

        if (usuarioActivo instanceof Medico) {
            medicoActual = (Medico) usuarioActivo;
            System.out.println("✅ Médico identificado: " + medicoActual.getNombre());
        } else {
            System.err.println("❌ El usuario activo no es un médico");
        }
    }

    /**
     * Configura las columnas de la tabla
     */
    private void configurarTabla() {
        colId.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getId()));

        colFecha.setCellValueFactory(cellData -> {
            LocalDate fecha = cellData.getValue().getFecha();
            if (fecha != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                return new SimpleStringProperty(fecha.format(formatter));
            }
            return new SimpleStringProperty("");
        });

        colHora.setCellValueFactory(cellData -> {
            if (cellData.getValue().getHora() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                return new SimpleStringProperty(cellData.getValue().getHora().format(formatter));
            }
            return new SimpleStringProperty("");
        });

        colPaciente.setCellValueFactory(cellData -> {
            Paciente paciente = cellData.getValue().getPaciente();
            return new SimpleStringProperty(paciente != null ? paciente.getNombre() : "");
        });

        colDocumento.setCellValueFactory(cellData -> {
            Paciente paciente = cellData.getValue().getPaciente();
            return new SimpleStringProperty(paciente != null ? paciente.getDocumento() : "");
        });

        colMotivo.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getMotivo()));

        colEstado.setCellValueFactory(cellData -> {
            EstadoCita estado = cellData.getValue().getEstado();
            return new SimpleStringProperty(estado != null ? estado.toString() : "");
        });

        // Aplicar estilos a la columna de estado
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
                        case "Programada":
                            setStyle("-fx-text-fill: #2196F3; -fx-font-weight: bold;");
                            break;
                        case "Atendida":
                            setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
                            break;
                        case "Cancelada":
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
     * Configura los filtros
     */
    private void configurarFiltros() {
        // Configurar ComboBox de estados
        ObservableList<String> estados = FXCollections.observableArrayList(
                "Todos",
                "Programada",
                "Atendida",
                "Cancelada"
        );
        cmbFiltroEstado.setItems(estados);
        cmbFiltroEstado.setValue("Todos");
    }

    /**
     * Carga todas las citas asignadas al médico actual
     */
    private void cargarCitasMedico() {
        System.out.println("\n=== CARGANDO CITAS DEL MÉDICO ===");

        if (medicoActual == null) {
            System.err.println("❌ No hay médico actual");
            return;
        }

        // Filtrar citas del médico actual
        List<Cita> citasMedico = hospital.getListCitas().stream()
                .filter(cita -> cita.getMedico() != null)
                .filter(cita -> cita.getMedico().getId().equals(medicoActual.getId()))
                .sorted((c1, c2) -> {
                    // Ordenar por fecha y hora
                    int fechaComparison = c1.getFecha().compareTo(c2.getFecha());
                    if (fechaComparison != 0) return fechaComparison;
                    return c1.getHora().compareTo(c2.getHora());
                })
                .collect(Collectors.toList());

        todasLasCitas = FXCollections.observableArrayList(citasMedico);
        tablaCitas.setItems(todasLasCitas);

        System.out.println("✅ Total citas del médico: " + citasMedico.size());
        System.out.println("=== CITAS CARGADAS ===\n");
    }

    /**
     * Actualiza las estadísticas mostradas
     */
    private void actualizarEstadisticas() {
        if (medicoActual == null) return;

        long programadas = todasLasCitas.stream()
                .filter(cita -> cita.getEstado() == EstadoCita.PROGRAMADA)
                .count();

        long atendidas = todasLasCitas.stream()
                .filter(cita -> cita.getEstado() == EstadoCita.ATENDIDA)
                .count();

        long citasHoy = todasLasCitas.stream()
                .filter(cita -> cita.getFecha().equals(LocalDate.now()))
                .filter(cita -> cita.getEstado() != EstadoCita.CANCELADA)
                .count();

        lblCitasProgramadas.setText("Total: " + programadas);
        lblCitasAtendidas.setText("Total: " + atendidas);
        lblCitasHoy.setText("Hoy: " + citasHoy);
    }

    /**
     * Filtra las citas según los criterios seleccionados
     */
    @FXML
    void onFiltrar(ActionEvent event) {
        List<Cita> citasFiltradas = todasLasCitas.stream()
                .filter(this::filtrarPorEstado)
                .filter(this::filtrarPorFecha)
                .collect(Collectors.toList());

        tablaCitas.setItems(FXCollections.observableArrayList(citasFiltradas));
    }

    /**
     * Limpia todos los filtros
     */
    @FXML
    void onLimpiarFiltros(ActionEvent event) {
        cmbFiltroEstado.setValue("Todos");
        dpFiltroFecha.setValue(null);
        tablaCitas.setItems(todasLasCitas);
    }

    private boolean filtrarPorEstado(Cita cita) {
        String estadoSeleccionado = cmbFiltroEstado.getValue();
        if (estadoSeleccionado == null || estadoSeleccionado.equals("Todos")) {
            return true;
        }
        return cita.getEstado().toString().equals(estadoSeleccionado);
    }

    private boolean filtrarPorFecha(Cita cita) {
        LocalDate fechaFiltro = dpFiltroFecha.getValue();
        if (fechaFiltro == null) {
            return true;
        }
        return cita.getFecha().equals(fechaFiltro);
    }

    /**
     * Marca una cita como atendida
     */
    @FXML
    void onAtenderCita(ActionEvent event) {
        Cita citaSeleccionada = tablaCitas.getSelectionModel().getSelectedItem();

        if (citaSeleccionada == null) {
            mostrarAlerta("Selección requerida",
                    "Debe seleccionar una cita de la tabla",
                    Alert.AlertType.WARNING);
            return;
        }

        if (citaSeleccionada.getEstado() == EstadoCita.ATENDIDA) {
            mostrarAlerta("Cita ya atendida",
                    "Esta cita ya fue marcada como atendida",
                    Alert.AlertType.INFORMATION);
            return;
        }

        if (citaSeleccionada.getEstado() == EstadoCita.CANCELADA) {
            mostrarAlerta("Cita cancelada",
                    "No se puede atender una cita cancelada",
                    Alert.AlertType.WARNING);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Atención");
        confirmacion.setHeaderText("¿Marcar esta cita como atendida?");
        confirmacion.setContentText("Paciente: " + citaSeleccionada.getPaciente().getNombre());

        if (confirmacion.showAndWait().get() == ButtonType.OK) {
            citaSeleccionada.setEstado(EstadoCita.ATENDIDA);
            hospital.updateCita(citaSeleccionada);

            mostrarAlerta("Éxito",
                    "Cita marcada como atendida correctamente",
                    Alert.AlertType.INFORMATION);

            tablaCitas.refresh();
            actualizarEstadisticas();
        }
    }

    /**
     * Muestra los detalles completos de una cita
     */
    @FXML
    void onVerDetalles(ActionEvent event) {
        Cita citaSeleccionada = tablaCitas.getSelectionModel().getSelectedItem();

        if (citaSeleccionada == null) {
            mostrarAlerta("Selección requerida",
                    "Debe seleccionar una cita de la tabla",
                    Alert.AlertType.WARNING);
            return;
        }

        mostrarDetallesCita(citaSeleccionada);
    }

    /**
     * Cancela una cita programada
     */
    @FXML
    void onCancelarCita(ActionEvent event) {
        Cita citaSeleccionada = tablaCitas.getSelectionModel().getSelectedItem();

        if (citaSeleccionada == null) {
            mostrarAlerta("Selección requerida",
                    "Debe seleccionar una cita de la tabla",
                    Alert.AlertType.WARNING);
            return;
        }

        if (citaSeleccionada.getEstado() == EstadoCita.CANCELADA) {
            mostrarAlerta("Cita ya cancelada",
                    "Esta cita ya está cancelada",
                    Alert.AlertType.INFORMATION);
            return;
        }

        if (citaSeleccionada.getEstado() == EstadoCita.ATENDIDA) {
            mostrarAlerta("Cita atendida",
                    "No se puede cancelar una cita que ya fue atendida",
                    Alert.AlertType.WARNING);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Cancelación");
        confirmacion.setHeaderText("¿Está seguro que desea cancelar esta cita?");
        confirmacion.setContentText("Paciente: " + citaSeleccionada.getPaciente().getNombre());

        if (confirmacion.showAndWait().get() == ButtonType.OK) {
            citaSeleccionada.setEstado(EstadoCita.CANCELADA);
            hospital.updateCita(citaSeleccionada);

            mostrarAlerta("Éxito",
                    "Cita cancelada correctamente",
                    Alert.AlertType.INFORMATION);

            tablaCitas.refresh();
            actualizarEstadisticas();
        }
    }

    /**
     * Agrega un diagnóstico a una cita
     */
    @FXML
    void onAgregarDiagnostico(ActionEvent event) {
        Cita citaSeleccionada = tablaCitas.getSelectionModel().getSelectedItem();

        if (citaSeleccionada == null) {
            mostrarAlerta("Selección requerida",
                    "Debe seleccionar una cita de la tabla",
                    Alert.AlertType.WARNING);
            return;
        }

        // Crear diálogo para ingresar el diagnóstico
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Agregar Diagnóstico");
        dialog.setHeaderText("Ingrese el diagnóstico para el paciente: " +
                citaSeleccionada.getPaciente().getNombre());

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

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnGuardar) {
                return txtDiagnostico.getText();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(diagnostico -> {
            citaSeleccionada.setDiagnostico(diagnostico);
            citaSeleccionada.setObservaciones(txtObservaciones.getText());

            // Si se agrega diagnóstico, marcar como atendida
            if (citaSeleccionada.getEstado() == EstadoCita.PROGRAMADA) {
                citaSeleccionada.setEstado(EstadoCita.ATENDIDA);
            }

            hospital.updateCita(citaSeleccionada);

            mostrarAlerta("Éxito",
                    "Diagnóstico guardado correctamente",
                    Alert.AlertType.INFORMATION);

            tablaCitas.refresh();
            actualizarEstadisticas();
        });
    }

    /**
     * Muestra el detalle completo de una cita
     */
    private void mostrarDetallesCita(Cita cita) {
        StringBuilder detalle = new StringBuilder();
        detalle.append("📋 DETALLE DE LA CITA\n\n");
        detalle.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");

        detalle.append("ID: ").append(cita.getId()).append("\n");
        detalle.append("Estado: ").append(cita.getEstado()).append("\n\n");

        detalle.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
        detalle.append("PACIENTE:\n");
        if (cita.getPaciente() != null) {
            detalle.append("  • Nombre: ").append(cita.getPaciente().getNombre()).append("\n");
            detalle.append("  • Documento: ").append(cita.getPaciente().getDocumento()).append("\n");
            detalle.append("  • Teléfono: ").append(cita.getPaciente().getTelefono()).append("\n");
        }

        detalle.append("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
        detalle.append("CITA:\n");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        detalle.append("  • Fecha: ").append(cita.getFecha().format(dateFormatter)).append("\n");
        detalle.append("  • Hora: ").append(cita.getHora().format(timeFormatter)).append("\n");
        detalle.append("  • Especialidad: ").append(cita.getEspecialidad()).append("\n");
        detalle.append("  • Motivo: ").append(cita.getMotivo()).append("\n");
        detalle.append("  • Precio: $").append(String.format("%,.0f", cita.getPrecio())).append("\n");

        if (cita.getDiagnostico() != null && !cita.getDiagnostico().isEmpty()) {
            detalle.append("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
            detalle.append("DIAGNÓSTICO:\n");
            detalle.append(cita.getDiagnostico()).append("\n");
        }

        if (cita.getObservaciones() != null && !cita.getObservaciones().isEmpty()) {
            detalle.append("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
            detalle.append("OBSERVACIONES:\n");
            detalle.append(cita.getObservaciones()).append("\n");
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detalles de la Cita");
        alert.setHeaderText(null);
        alert.setContentText(detalle.toString());
        alert.getDialogPane().setPrefWidth(500);
        alert.showAndWait();
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