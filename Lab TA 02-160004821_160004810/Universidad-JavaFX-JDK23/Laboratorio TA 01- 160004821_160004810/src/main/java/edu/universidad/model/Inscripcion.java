package edu.universidad.model;

import java.util.Objects;

public class Inscripcion {
    private Curso curso;
    private Integer anio;
    private Integer semestre;
    private Estudiante estudiante;

    public Inscripcion(Curso curso, Integer anio, Integer semestre, Estudiante estudiante) {
        this.curso = curso;
        this.anio = anio;
        this.semestre = semestre;
        this.estudiante = estudiante;
    }

    public Curso getCurso() { return curso; }
    public int getAnio() { return anio; }
    public int getSemestre() { return semestre; }
    public Estudiante getEstudiante() { return estudiante; }

    @Override
    public String toString() {
        return "Inscripcion{" +
                "curso=" + (curso != null ? curso.getNombre() : "N/A") +
                ", anio=" + anio +
                ", semestre=" + semestre +
                ", estudiante=" + (estudiante != null ? estudiante.getNombres()+" "+estudiante.getApellidos() : "N/A") +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Inscripcion)) return false;
        Inscripcion that = (Inscripcion) o;
        return anio == that.anio &&
                semestre == that.semestre &&
                Objects.equals(curso != null ? curso.getID() : null, that.curso != null ? that.curso.getID() : null) &&
                Objects.equals(estudiante != null ? estudiante.getCodigo() : null, that.estudiante != null ? that.estudiante.getCodigo() : null);
    }

    @Override
    public int hashCode() {
        return Objects.hash(curso != null ? curso.getID() : null, anio, semestre, estudiante != null ? estudiante.getCodigo() : null);
    }
}
