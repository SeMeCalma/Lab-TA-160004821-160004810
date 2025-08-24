package edu.universidad.model;

import java.util.Date;

public class Programa {
    private double ID;
    private String nombre;
    private double duracion;
    private Date registro;
    private Facultad facultad;

    public Programa(double ID, String nombre, double duracion, Date registro, Facultad facultad) {
        this.ID = ID;
        this.nombre = nombre;
        this.duracion = duracion;
        this.registro = registro;
        this.facultad = facultad;
    }

    public double getID() { return ID; }
    public String getNombre() { return nombre; }
    public double getDuracion() { return duracion; }
    public Date getRegistro() { return registro; }
    public Facultad getFacultad() { return facultad; }

    @Override
    public String toString() {
        return "Programa{" +
                "ID=" + ID +
                ", nombre='" + nombre + '\'' +
                ", duracion=" + duracion +
                ", registro=" + registro +
                ", facultad=" + (facultad != null ? facultad.getNombre() : "N/A") +
                '}';
    }
}
