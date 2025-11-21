package co.edu.uniquindio.hospital;

public enum Especialidad {
    CARDIOLOGIA("Cardiología"),
    PEDIATRIA("Pediatría"),
    DERMATOLOGIA("Dermatología"),
    NEUROLOGIA("Neurología"),
    TRAUMATOLOGIA("Traumatología"),
    OFTALMOLOGIA("Oftalmología"),
    GINECOLOGIA("Ginecología"),
    MEDICINA_GENERAL("Medicina General");

    private final String displayName;

    Especialidad(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
