package edu.universidad.model;

public class Persona {
    private double ID;
    private String nombres;
    private String apellidos;
    private String email;

    public Persona(double ID, String nombres, String apellidos, String email) {
        this.ID = ID;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.email = email;
    }

    public double getID() { return ID; }
    public String getNombres() { return nombres; }
    public String getApellidos() { return apellidos; }
    public String getEmail() { return email; }

    @Override
    public String toString() {
        return "Persona{" +
                "ID=" + ID +
                ", nombres='" + nombres + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
