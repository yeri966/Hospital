package co.edu.uniquindio.hospital.structural.facade;

import co.edu.uniquindio.hospital.*;
import co.edu.uniquindio.hospital.creational.singleton.Hospital;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * PATRÓN ESTRUCTURAL: FACADE
 *
 * Esta clase proporciona una interfaz simplificada para todas las operaciones
 * relacionadas con la gestión de citas médicas.
 *
 * Beneficios:
 * - Simplifica el acceso a un subsistema complejo
 * - Reduce el acoplamiento entre los clientes y el subsistema
 * - Centraliza la lógica de negocio de citas en un solo lugar
 * - Facilita el mantenimiento y las pruebas
 */
public class CitaFacade {

    private final Hospital hospital;

    // ==================== CONSTRUCTOR ====================

    public CitaFacade() {
        this.hospital = Hospital.getInstance();
    }

    // ==================== OPERACIONES CRUD ====================

    /**
     * Crea una nueva cita médica con todas las validaciones necesarias
     *
     * @return La cita creada o null si hay errores
     * @throws CitaException si hay problemas con la creación
     */
    public Cita crearCita(String id, Paciente paciente, Medico medico,
                          Especialidad especialidad, LocalDate fecha,
                          LocalTime hora, double precio, String motivo) throws CitaException {

        // Validar datos obligatorios
        validarDatosObligatorios(paciente, medico, fecha, hora);

        // Verificar disponibilidad del médico
        if (!verificarDisponibilidadMedico(medico, fecha, hora)) {
            throw new CitaException("El médico no está disponible en ese horario");
        }

        // Verificar que la fecha no sea pasada
        if (fecha.isBefore(LocalDate.now())) {
            throw new CitaException("No se pueden programar citas en fechas pasadas");
        }

        // Verificar que el médico esté activo
        if (!medico.isDisponible()) {
            throw new CitaException("El médico no está disponible para atender citas");
        }

        // Crear la cita usando el patrón Builder
        Cita nuevaCita = Cita.builder(id, paciente, medico, fecha, hora)
                .especialidad(especialidad != null ? especialidad : medico.getEspecialidad())
                .precio(precio)
                .motivo(motivo)
                .estado(EstadoCita.PROGRAMADA)
                .build();

        // Agregar al sistema
        hospital.addCita(nuevaCita);

        System.out.println("✅ [FACADE] Cita creada exitosamente: " + id);
        return nuevaCita;
    }

    /**
     * Actualiza una cita existente
     */
    public Cita actualizarCita(Cita cita, Paciente paciente, Medico medico,
                               Especialidad especialidad, LocalDate fecha,
                               LocalTime hora, double precio, String motivo) throws CitaException {

        if (cita == null) {
            throw new CitaException("La cita no puede ser nula");
        }

        // Validar datos obligatorios
        validarDatosObligatorios(paciente, medico, fecha, hora);

        // Verificar disponibilidad (excluyendo la cita actual)
        if (!verificarDisponibilidadParaActualizacion(cita.getId(), medico, fecha, hora)) {
            throw new CitaException("El médico ya tiene otra cita en ese horario");
        }

        // Actualizar datos
        cita.setPaciente(paciente);
        cita.setMedico(medico);
        cita.setEspecialidad(especialidad);
        cita.setFecha(fecha);
        cita.setHora(hora);
        cita.setPrecio(precio);
        cita.setMotivo(motivo);

        hospital.updateCita(cita);

        System.out.println("✅ [FACADE] Cita actualizada: " + cita.getId());
        return cita;
    }

    /**
     * Elimina una cita del sistema
     */
    public boolean eliminarCita(String citaId) throws CitaException {
        Cita cita = hospital.buscarCitaPorId(citaId);

        if (cita == null) {
            throw new CitaException("No se encontró la cita con ID: " + citaId);
        }

        hospital.deleteCita(citaId);
        System.out.println("✅ [FACADE] Cita eliminada: " + citaId);
        return true;
    }

    // ==================== CAMBIOS DE ESTADO ====================

    /**
     * Marca una cita como atendida
     */
    public Cita atenderCita(Cita cita) throws CitaException {
        validarCitaParaCambioEstado(cita);

        if (cita.getEstado() == EstadoCita.ATENDIDA) {
            throw new CitaException("La cita ya fue atendida");
        }

        if (cita.getEstado() == EstadoCita.CANCELADA) {
            throw new CitaException("No se puede atender una cita cancelada");
        }

        cita.setEstado(EstadoCita.ATENDIDA);
        hospital.updateCita(cita);

        System.out.println("✅ [FACADE] Cita atendida: " + cita.getId());
        return cita;
    }

    /**
     * Cancela una cita programada
     */
    public Cita cancelarCita(Cita cita) throws CitaException {
        validarCitaParaCambioEstado(cita);

        if (cita.getEstado() == EstadoCita.CANCELADA) {
            throw new CitaException("La cita ya está cancelada");
        }

        if (cita.getEstado() == EstadoCita.ATENDIDA) {
            throw new CitaException("No se puede cancelar una cita ya atendida");
        }

        cita.setEstado(EstadoCita.CANCELADA);
        hospital.updateCita(cita);

        System.out.println("✅ [FACADE] Cita cancelada: " + cita.getId());
        return cita;
    }

    /**
     * Agrega diagnóstico y observaciones a una cita
     */
    public Cita agregarDiagnostico(Cita cita, String diagnostico,
                                   String observaciones) throws CitaException {
        if (cita == null) {
            throw new CitaException("La cita no puede ser nula");
        }

        cita.setDiagnostico(diagnostico);
        cita.setObservaciones(observaciones);

        // Si se agrega diagnóstico, marcar como atendida
        if (cita.getEstado() == EstadoCita.PROGRAMADA) {
            cita.setEstado(EstadoCita.ATENDIDA);
        }

        hospital.updateCita(cita);

        System.out.println("✅ [FACADE] Diagnóstico agregado a cita: " + cita.getId());
        return cita;
    }

    // ==================== CONSULTAS ====================

    /**
     * Obtiene todas las citas del sistema
     */
    public List<Cita> obtenerTodasLasCitas() {
        return hospital.getListCitas();
    }

    /**
     * Obtiene las citas de un médico específico
     */
    public List<Cita> obtenerCitasPorMedico(Medico medico) {
        return hospital.getListCitas().stream()
                .filter(c -> c.getMedico() != null)
                .filter(c -> c.getMedico().getId().equals(medico.getId()))
                .sorted((c1, c2) -> {
                    int fechaComp = c1.getFecha().compareTo(c2.getFecha());
                    return fechaComp != 0 ? fechaComp : c1.getHora().compareTo(c2.getHora());
                })
                .collect(Collectors.toList());
    }

    /**
     * Obtiene las citas de un paciente específico
     */
    public List<Cita> obtenerCitasPorPaciente(Paciente paciente) {
        return hospital.getListCitas().stream()
                .filter(c -> c.getPaciente() != null)
                .filter(c -> c.getPaciente().getId().equals(paciente.getId()))
                .sorted((c1, c2) -> c1.getFecha().compareTo(c2.getFecha()))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene las citas de una fecha específica
     */
    public List<Cita> obtenerCitasPorFecha(LocalDate fecha) {
        return hospital.getListCitas().stream()
                .filter(c -> c.getFecha().equals(fecha))
                .sorted((c1, c2) -> c1.getHora().compareTo(c2.getHora()))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene las citas por estado
     */
    public List<Cita> obtenerCitasPorEstado(EstadoCita estado) {
        return hospital.getListCitas().stream()
                .filter(c -> c.getEstado() == estado)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene las citas programadas para hoy
     */
    public List<Cita> obtenerCitasDeHoy() {
        return obtenerCitasPorFecha(LocalDate.now());
    }

    /**
     * Obtiene las próximas citas (futuras) de un médico
     */
    public List<Cita> obtenerProximasCitasMedico(Medico medico) {
        LocalDate hoy = LocalDate.now();
        return obtenerCitasPorMedico(medico).stream()
                .filter(c -> !c.getFecha().isBefore(hoy))
                .filter(c -> c.getEstado() == EstadoCita.PROGRAMADA)
                .collect(Collectors.toList());
    }

    /**
     * Busca una cita por su ID
     */
    public Cita buscarCitaPorId(String id) {
        return hospital.buscarCitaPorId(id);
    }

    // ==================== ESTADÍSTICAS ====================

    /**
     * Cuenta el total de citas de un médico
     */
    public long contarCitasMedico(Medico medico) {
        return obtenerCitasPorMedico(medico).size();
    }

    /**
     * Cuenta las citas de hoy de un médico
     */
    public long contarCitasHoyMedico(Medico medico) {
        LocalDate hoy = LocalDate.now();
        return obtenerCitasPorMedico(medico).stream()
                .filter(c -> c.getFecha().equals(hoy))
                .count();
    }

    /**
     * Cuenta las citas pendientes de un médico
     */
    public long contarCitasPendientesMedico(Medico medico) {
        return obtenerCitasPorMedico(medico).stream()
                .filter(c -> c.getEstado() == EstadoCita.PROGRAMADA)
                .count();
    }

    // ==================== VALIDACIONES PRIVADAS ====================

    private void validarDatosObligatorios(Paciente paciente, Medico medico,
                                          LocalDate fecha, LocalTime hora) throws CitaException {
        if (paciente == null) {
            throw new CitaException("El paciente es obligatorio");
        }
        if (medico == null) {
            throw new CitaException("El médico es obligatorio");
        }
        if (fecha == null) {
            throw new CitaException("La fecha es obligatoria");
        }
        if (hora == null) {
            throw new CitaException("La hora es obligatoria");
        }
    }

    private void validarCitaParaCambioEstado(Cita cita) throws CitaException {
        if (cita == null) {
            throw new CitaException("La cita no puede ser nula");
        }
    }

    /**
     * Verifica si el médico está disponible en la fecha y hora indicadas
     */
    public boolean verificarDisponibilidadMedico(Medico medico, LocalDate fecha, LocalTime hora) {
        return hospital.getListCitas().stream()
                .filter(c -> c.getMedico().getId().equals(medico.getId()))
                .filter(c -> c.getFecha().equals(fecha))
                .filter(c -> c.getHora().equals(hora))
                .filter(c -> c.getEstado() != EstadoCita.CANCELADA)
                .findAny()
                .isEmpty();
    }

    /**
     * Verifica disponibilidad excluyendo una cita específica (para actualizaciones)
     */
    private boolean verificarDisponibilidadParaActualizacion(String citaIdExcluir,
                                                             Medico medico, LocalDate fecha, LocalTime hora) {
        return hospital.getListCitas().stream()
                .filter(c -> !c.getId().equals(citaIdExcluir))
                .filter(c -> c.getMedico().getId().equals(medico.getId()))
                .filter(c -> c.getFecha().equals(fecha))
                .filter(c -> c.getHora().equals(hora))
                .filter(c -> c.getEstado() != EstadoCita.CANCELADA)
                .findAny()
                .isEmpty();
    }

    // ==================== UTILIDADES ====================

    /**
     * Genera un ID único para una nueva cita
     */
    public String generarIdCita() {
        int count = hospital.getListCitas().size();
        return String.format("CIT%04d", count + 1);
    }

    /**
     * Obtiene todos los médicos disponibles por especialidad
     */
    public List<Medico> obtenerMedicosPorEspecialidad(Especialidad especialidad) {
        return hospital.getMedicosPorEspecialidad(especialidad);
    }

    /**
     * Obtiene todos los pacientes del sistema
     */
    public List<Paciente> obtenerTodosPacientes() {
        return hospital.getTodosPacientes();
    }

    /**
     * Obtiene todos los médicos del sistema
     */
    public List<Medico> obtenerTodosMedicos() {
        return hospital.getTodosMedicos();
    }
}