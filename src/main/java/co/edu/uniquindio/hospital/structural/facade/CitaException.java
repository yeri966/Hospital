package co.edu.uniquindio.hospital.structural.facade;

/**
 * Excepción personalizada para errores relacionados con la gestión de citas
 * Utilizada por el CitaFacade para reportar errores de validación y operaciones
 */
public class CitaException extends Exception {

    public CitaException(String mensaje) {
        super(mensaje);
    }

    public CitaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}