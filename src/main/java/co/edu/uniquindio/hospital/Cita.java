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
    private EstadoCita estado;
    private String observaciones; // Campo opcional
    private String diagnostico;   // Campo opcional

    // Constructor privado - solo accesible desde el Builder
    private Cita(CitaBuilder builder) {
        this.id = builder.id;
        this.paciente = builder.paciente;
        this.medico = builder.medico;
        this.especialidad = builder.especialidad;
        this.fecha = builder.fecha;
        this.hora = builder.hora;
        this.precio = builder.precio;
        this.motivo = builder.motivo;
        this.estado = builder.estado;
        this.observaciones = builder.observaciones;
        this.diagnostico = builder.diagnostico;
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

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
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

    // ==================== PATRÓN BUILDER ====================

    /**
     * PATRÓN CREACIONAL: BUILDER
     *
     * Clase Builder estática interna para construir objetos Cita de manera flexible
     * Permite crear citas con diferentes combinaciones de atributos opcionales
     */
    public static class CitaBuilder {
        // Atributos obligatorios
        private final String id;
        private final Paciente paciente;
        private final Medico medico;
        private final LocalDate fecha;
        private final LocalTime hora;

        // Atributos opcionales con valores por defecto
        private Especialidad especialidad;
        private double precio = 0.0;
        private String motivo = "";
        private EstadoCita estado = EstadoCita.PROGRAMADA;
        private String observaciones = "";
        private String diagnostico = "";

        /**
         * Constructor del Builder con parámetros obligatorios
         */
        public CitaBuilder(String id, Paciente paciente, Medico medico,
                           LocalDate fecha, LocalTime hora) {
            this.id = id;
            this.paciente = paciente;
            this.medico = medico;
            this.fecha = fecha;
            this.hora = hora;
        }

        /**
         * Métodos para establecer atributos opcionales (fluent interface)
         */
        public CitaBuilder especialidad(Especialidad especialidad) {
            this.especialidad = especialidad;
            return this;
        }

        public CitaBuilder precio(double precio) {
            this.precio = precio;
            return this;
        }

        public CitaBuilder motivo(String motivo) {
            this.motivo = motivo;
            return this;
        }

        public CitaBuilder estado(EstadoCita estado) {
            this.estado = estado;
            return this;
        }

        public CitaBuilder observaciones(String observaciones) {
            this.observaciones = observaciones;
            return this;
        }

        public CitaBuilder diagnostico(String diagnostico) {
            this.diagnostico = diagnostico;
            return this;
        }

        /**
         * Método para construir el objeto Cita
         * Aplica validaciones antes de crear la instancia
         */
        public Cita build() {
            // Validaciones
            validarCita();

            // Si no se especifica especialidad, tomar la del médico
            if (this.especialidad == null && this.medico != null) {
                this.especialidad = this.medico.getEspecialidad();
            }

            return new Cita(this);
        }

        /**
         * Validaciones de la cita antes de construirla
         */
        private void validarCita() {
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("El ID de la cita es obligatorio");
            }
            if (paciente == null) {
                throw new IllegalArgumentException("El paciente es obligatorio");
            }
            if (medico == null) {
                throw new IllegalArgumentException("El médico es obligatorio");
            }
            if (fecha == null) {
                throw new IllegalArgumentException("La fecha es obligatoria");
            }
            if (hora == null) {
                throw new IllegalArgumentException("La hora es obligatoria");
            }
            if (precio < 0) {
                throw new IllegalArgumentException("El precio no puede ser negativo");
            }
        }
    }

    /**
     * Método estático para obtener un nuevo Builder
     */
    public static CitaBuilder builder(String id, Paciente paciente, Medico medico,
                                      LocalDate fecha, LocalTime hora) {
        return new CitaBuilder(id, paciente, medico, fecha, hora);
    }
}