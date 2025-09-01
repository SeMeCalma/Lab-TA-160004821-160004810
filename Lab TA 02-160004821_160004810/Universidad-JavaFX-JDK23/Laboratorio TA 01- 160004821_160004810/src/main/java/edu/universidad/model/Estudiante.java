package edu.universidad.model;

public class Estudiante extends Persona {
    private double codigo;
    private Programa programa;
    private boolean activo;
    private double promedio;

    public Estudiante(double id, String nombres, String apellidos, String email,
                      double codigo, Programa programa, boolean activo, double promedio) {
        super(id, nombres, apellidos, email);
        this.codigo = codigo;
        this.programa = programa;
        this.activo = activo;
        this.promedio = promedio;
    }

    public double getCodigo() { return codigo; }
    public Programa getPrograma() { return programa; }
    public boolean isActivo() { return activo; }
    public double getPromedio() { return promedio; }

    @Override
    public String toString() {
        return "Estudiante{" +
                "codigo=" + codigo +
                ", programa=" + (programa != null ? programa.getNombre() : "N/A") +
                ", activo=" + activo +
                ", promedio=" + promedio +
                ", datos=" + super.toString() +
                '}';
    }
}
