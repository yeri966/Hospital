package co.edu.uniquindio.hospital;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Cita {
    private String id;
    private Paciente paciente;
    private Medico medico;
    private LocalDate fecha;
    private LocalTime hora;
    private double precio;
    private Especialidad especialidad;
    private String motivo;
    private String estado; // "PROGRAMADA", "CANCELADA", "COMPLETADA"

    public Cita() {
        this.estado = "PROGRAMADA";
    }

    public Cita(String id, Paciente paciente, Medico medico, LocalDate fecha,
                LocalTime hora, double precio, Especialidad especialidad, String motivo) {
        this.id = id;
        this.paciente = paciente;
        this.medico = medico;
        this.fecha = fecha;
        this.hora = hora;
        this.precio = precio;
        this.especialidad = especialidad;
        this.motivo = motivo;
        this.estado = "PROGRAMADA";
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }

    public Medico getMedico() { return medico; }
    public void setMedico(Medico medico) { this.medico = medico; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public Especialidad getEspecialidad() { return especialidad; }
    public void setEspecialidad(Especialidad especialidad) { this.especialidad = especialidad; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getFechaHoraFormateada() {
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm");
        return fecha.format(formatoFecha) + " - " + hora.format(formatoHora);
    }

    @Override
    public String toString() {
        return "Cita{" +
                "id='" + id + '\'' +
                ", paciente=" + (paciente != null ? paciente.getNombre() : "null") +
                ", medico=" + (medico != null ? medico.getNombre() : "null") +
                ", fecha=" + fecha +
                ", hora=" + hora +
                ", precio=" + precio +
                ", especialidad=" + especialidad +
                ", estado='" + estado + '\'' +
                '}';
    }
}