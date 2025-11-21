package co.edu.uniquindio.hospital.creational.singleton;

import co.edu.uniquindio.hospital.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Hospital {
    private static Hospital instance;
    private Person usuarioActivo;
    private ArrayList<Person> listPersonas;
    private ArrayList<Cita> listCitas;

    private Hospital() {
        listPersonas = new ArrayList<>();
        listCitas = new ArrayList<>();
        cargarDatosPrueba();
    }

    public static Hospital getInstance() {
        if (instance == null) {
            instance = new Hospital();
        }
        return instance;
    }

    private void cargarDatosPrueba() {
        // Crear Admin único
        UserAccount userAdmin = new UserAccount("admin", "admin", null, TipoUsuario.ADMINISTRADOR);
        Admin admin = new Admin("ADM001", "1000000", "Administrador Sistema",
                "admin@hospital.com", "3001234567", userAdmin, "Administrador General");
        admin.setUserAccount(userAdmin);
        userAdmin.setPerson(admin);
        agregarPersona(admin);

        System.out.println("=== DATOS DE PRUEBA CARGADOS ===");
        System.out.println("Total Personas: " + listPersonas.size());
        System.out.println("Total Citas: " + listCitas.size());
    }

    // Métodos CRUD para Personas
    public void agregarPersona(Person person) {
        listPersonas.add(person);
    }

    public void eliminarPersona(Person person) {
        listPersonas.remove(person);
    }

    public void actualizarPersona(Person person) {
        for (int i = 0; i < listPersonas.size(); i++) {
            if (listPersonas.get(i).getId().equals(person.getId())) {
                listPersonas.set(i, person);
                break;
            }
        }
    }

    // Métodos CRUD para Citas
    public void agregarCita(Cita cita) {
        listCitas.add(cita);
    }

    public void eliminarCita(String citaId) {
        listCitas.removeIf(cita -> cita.getId().equals(citaId));
    }

    public void actualizarCita(Cita cita) {
        for (int i = 0; i < listCitas.size(); i++) {
            if (listCitas.get(i).getId().equals(cita.getId())) {
                listCitas.set(i, cita);
                break;
            }
        }
    }

    // Validación de usuario
    public Person validarUsuario(String usuario, String contrasenia) {
        for (Person person : listPersonas) {
            UserAccount cuenta = person.getUserAccount();
            if (cuenta != null && cuenta.getUsuario().equals(usuario) &&
                    cuenta.getContrasenia().equals(contrasenia)) {
                usuarioActivo = person;
                return person;
            }
        }
        return null;
    }

    // Obtener médicos por especialidad
    public List<Medico> getMedicosPorEspecialidad(Especialidad especialidad) {
        return listPersonas.stream()
                .filter(person -> person instanceof Medico)
                .map(person -> (Medico) person)
                .filter(medico -> medico.getEspecialidad() == especialidad)
                .filter(Medico::isDisponible)
                .collect(Collectors.toList());
    }

    // Obtener citas de un médico específico
    public List<Cita> getCitasPorMedico(Medico medico) {
        return listCitas.stream()
                .filter(cita -> cita.getMedico() != null &&
                        cita.getMedico().getId().equals(medico.getId()))
                .collect(Collectors.toList());
    }

    public ArrayList<Person> getListPersonas() { return listPersonas; }
    public ArrayList<Cita> getListCitas() { return listCitas; }
    public Person getUsuarioActivo() { return usuarioActivo; }
    public void setUsuarioActivo(Person usuarioActivo) { this.usuarioActivo = usuarioActivo; }
}
