package edu.universidad.model;

public class Facultad {
    private double ID;
    private String nombre;
    private Persona decano;

    public Facultad(double ID, String nombre, Persona decano) {
        this.ID = ID;
        this.nombre = nombre;
        this.decano = decano;
    }

    public double getID() { return ID; }
    public String getNombre() { return nombre; }
    public Persona getDecano() { return decano; }

    @Override
    public String toString() {
        return "Facultad{" +
                "ID=" + ID +
                ", nombre='" + nombre + '\'' +
                ", decano=" + (decano != null ? decano.getNombres()+" "+decano.getApellidos() : "N/A") +
                '}';
    }
}
