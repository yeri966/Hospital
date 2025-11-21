package co.edu.uniquindio.hospital;

public class Medico extends Person {
    private Especialidad especialidad;
    private String licenciaMedica;
    private boolean disponible;

    public Medico(String id, String documento, String nombre, String correo,
                  String telefono, UserAccount userAccount, Especialidad especialidad,
                  String licenciaMedica, boolean disponible) {
        super(id, documento, nombre, correo, telefono, userAccount);
        this.especialidad = especialidad;
        this.licenciaMedica = licenciaMedica;
        this.disponible = disponible;
    }

    public Especialidad getEspecialidad() { return especialidad; }
    public void setEspecialidad(Especialidad especialidad) { this.especialidad = especialidad; }

    public String getLicenciaMedica() { return licenciaMedica; }
    public void setLicenciaMedica(String licenciaMedica) { this.licenciaMedica = licenciaMedica; }

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
}