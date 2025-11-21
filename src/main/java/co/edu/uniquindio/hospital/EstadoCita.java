package co.edu.uniquindio.hospital;

public enum EstadoCita {
    PROGRAMADA,
    ATENDIDA,
    CANCELADA;

    @Override
    public String toString() {
        switch (this) {
            case PROGRAMADA:
                return "Programada";
            case ATENDIDA:
                return "Atendida";
            case CANCELADA:
                return "Cancelada";
            default:
                return name();
        }
    }
}