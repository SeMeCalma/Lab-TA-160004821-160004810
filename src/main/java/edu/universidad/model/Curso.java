package edu.universidad.model;

public class Curso {
    private Integer ID;
    private String nombre;
    private Programa programa;
    private boolean activo;

    public Curso(Integer ID, String nombre, Programa programa, boolean activo) {
        this.ID = ID;
        this.nombre = nombre;
        this.programa = programa;
        this.activo = activo;
    }

    public int getID() { return ID; }
    public String getNombre() { return nombre; }
    public Programa getPrograma() { return programa; }
    public boolean isActivo() { return activo; }

    @Override
    public String toString() {
        return "Curso{" +
                "ID=" + ID +
                ", nombre='" + nombre + '\'' +
                ", programa=" + (programa != null ? programa.getNombre() : "N/A") +
                ", activo=" + activo +
                '}';
    }
}
