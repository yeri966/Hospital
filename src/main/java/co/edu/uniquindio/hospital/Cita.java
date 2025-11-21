package co.edu.uniquindio.hospital;

import java.time.LocalDate;
import java.time.LocalTime;

public class Cita {
    private String id;
    private Paciente paciente;
    private Medico medico;
    private Especialidad especialidad;
    private LocalDate fecha;
    private LocalTime hora;
    private double precio;
    private String motivo;
    private EstadoCita estado; // NUEVO ATRIBUTO

    // Constructor sin estado (por defecto PROGRAMADA)
    public Cita(String id, Paciente paciente, Medico medico, Especialidad especialidad,
                LocalDate fecha, LocalTime hora, double precio, String motivo) {
        this.id = id;
        this.paciente = paciente;
        this.medico = medico;
        this.especialidad = especialidad;
        this.fecha = fecha;
        this.hora = hora;
        this.precio = precio;
        this.motivo = motivo;
        this.estado = EstadoCita.PROGRAMADA; // Estado por defecto
    }

    // Constructor completo con estado
    public Cita(String id, Paciente paciente, Medico medico, Especialidad especialidad,
                LocalDate fecha, LocalTime hora, double precio, String motivo, EstadoCita estado) {
        this.id = id;
        this.paciente = paciente;
        this.medico = medico;
        this.especialidad = especialidad;
        this.fecha = fecha;
        this.hora = hora;
        this.precio = precio;
        this.motivo = motivo;
        this.estado = estado;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    public Especialidad getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(Especialidad especialidad) {
        this.especialidad = especialidad;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public EstadoCita getEstado() {
        return estado;
    }

    public void setEstado(EstadoCita estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Cita{" +
                "id='" + id + '\'' +
                ", paciente=" + (paciente != null ? paciente.getNombre() : "null") +
                ", medico=" + (medico != null ? medico.getNombre() : "null") +
                ", especialidad=" + especialidad +
                ", fecha=" + fecha +
                ", hora=" + hora +
                ", precio=" + precio +
                ", estado=" + estado +
                '}';
    }
}