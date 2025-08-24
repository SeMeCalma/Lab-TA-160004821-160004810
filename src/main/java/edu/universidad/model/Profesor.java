package edu.universidad.model;

public class Profesor extends Persona {
    private String tipoContrato;

    public Profesor(double id, String nombres, String apellidos, String email, String tipoContrato) {
        super(id, nombres, apellidos, email);
        this.tipoContrato = tipoContrato;
    }

    public String getTipoContrato() { return tipoContrato; }

    @Override
    public String toString() {
        return "Profesor{" +
                "tipoContrato='" + tipoContrato + '\'' +
                ", datos=" + super.toString() +
                '}';
    }
}
