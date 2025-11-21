package co.edu.uniquindio.hospital;

import java.time.LocalDate;

public class Paciente extends Person {
    private LocalDate fechaNacimiento;
    private String direccion;
    private String genero;

    public Paciente(String id, String documento, String nombre, String correo,
                    String telefono, UserAccount userAccount, LocalDate fechaNacimiento,
                    String direccion, String genero) {
        super(id, documento, nombre, correo, telefono, userAccount);
        this.fechaNacimiento = fechaNacimiento;
        this.direccion = direccion;
        this.genero = genero;
    }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public int getEdad() {
        return LocalDate.now().getYear() - fechaNacimiento.getYear();
    }
}