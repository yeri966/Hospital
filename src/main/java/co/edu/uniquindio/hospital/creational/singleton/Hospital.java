package co.edu.uniquindio.hospital.creational.singleton;

import co.edu.uniquindio.hospital.*;

import java.time.LocalDate;
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

        // Crear Médico de prueba
        UserAccount userMedico = new UserAccount("medico", "medico", null, TipoUsuario.MEDICO);
        Medico medico = new Medico("MED001", "1000001", "Dr. Juan Pérez",
                "medico@hospital.com", "3001234568", userMedico, Especialidad.CARDIOLOGIA, "20211", true);
        medico.setUserAccount(userMedico);
        userMedico.setPerson(medico);
        agregarPersona(medico);

        // Crear Paciente de prueba
        UserAccount userPaciente = new UserAccount("paciente", "paciente", null, TipoUsuario.PACIENTE);
        Paciente paciente = new Paciente("PAC001", "1000002", "María López",
                "paciente@hospital.com", "3001234569",userMedico, LocalDate.of(2010,05,10),"Manzana 60","Femenino");
        paciente.setUserAccount(userPaciente);
        userPaciente.setPerson(paciente);
        agregarPersona(paciente);

        System.out.println("=== DATOS DE PRUEBA CARGADOS ===");
        System.out.println("Total Personas: " + listPersonas.size());
        System.out.println("Total Citas: " + listCitas.size());
    }

    // ==================== MÉTODOS CRUD PARA PERSONAS ====================

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

    // ==================== MÉTODOS CRUD PARA CITAS ====================

    /**
     * Agrega una nueva cita al sistema
     */
    public void addCita(Cita cita) {
        listCitas.add(cita);
    }

    /**
     * Actualiza una cita existente
     */
    public void updateCita(Cita cita) {
        for (int i = 0; i < listCitas.size(); i++) {
            if (listCitas.get(i).getId().equals(cita.getId())) {
                listCitas.set(i, cita);
                break;
            }
        }
    }

    /**
     * Elimina una cita por su ID
     */
    public void deleteCita(String citaId) {
        listCitas.removeIf(cita -> cita.getId().equals(citaId));
    }

    /**
     * Busca una cita por su ID
     */
    public Cita buscarCitaPorId(String citaId) {
        return listCitas.stream()
                .filter(cita -> cita.getId().equals(citaId))
                .findFirst()
                .orElse(null);
    }

    // ==================== MÉTODOS DE VALIDACIÓN ====================

    /**
     * Valida las credenciales de un usuario
     */
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

    // ==================== MÉTODOS DE CONSULTA ESPECÍFICOS ====================

    /**
     * Obtiene todos los médicos disponibles por especialidad
     */
    public List<Medico> getMedicosPorEspecialidad(Especialidad especialidad) {
        return listPersonas.stream()
                .filter(person -> person instanceof Medico)
                .map(person -> (Medico) person)
                .filter(medico -> medico.getEspecialidad() == especialidad)
                .filter(Medico::isDisponible)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todas las citas de un médico específico
     */
    public List<Cita> getCitasPorMedico(Medico medico) {
        return listCitas.stream()
                .filter(cita -> cita.getMedico() != null &&
                        cita.getMedico().getId().equals(medico.getId()))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todas las citas de un paciente específico
     */
    public List<Cita> getCitasPorPaciente(Paciente paciente) {
        return listCitas.stream()
                .filter(cita -> cita.getPaciente() != null &&
                        cita.getPaciente().getId().equals(paciente.getId()))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todos los médicos del sistema
     */
    public List<Medico> getTodosMedicos() {
        return listPersonas.stream()
                .filter(person -> person instanceof Medico)
                .map(person -> (Medico) person)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todos los pacientes del sistema
     */
    public List<Paciente> getTodosPacientes() {
        return listPersonas.stream()
                .filter(person -> person instanceof Paciente)
                .map(person -> (Paciente) person)
                .collect(Collectors.toList());
    }

    /**
     * Verifica si un médico está disponible en una fecha y hora específicas
     */
    public boolean verificarDisponibilidadMedico(Medico medico, java.time.LocalDate fecha, java.time.LocalTime hora) {
        return listCitas.stream()
                .filter(cita -> cita.getMedico().getId().equals(medico.getId()))
                .filter(cita -> cita.getFecha().equals(fecha))
                .filter(cita -> cita.getHora().equals(hora))
                .filter(cita -> cita.getEstado() != EstadoCita.CANCELADA)
                .findAny()
                .isEmpty();
    }

    // ==================== GETTERS Y SETTERS ====================

    public ArrayList<Person> getListPersonas() {
        return listPersonas;
    }

    public void setListPersonas(ArrayList<Person> listPersonas) {
        this.listPersonas = listPersonas;
    }

    public ArrayList<Cita> getListCitas() {
        return listCitas;
    }

    public void setListCitas(ArrayList<Cita> listCitas) {
        this.listCitas = listCitas;
    }

    public Person getUsuarioActivo() {
        return usuarioActivo;
    }

    public void setUsuarioActivo(Person usuarioActivo) {
        this.usuarioActivo = usuarioActivo;
    }
}