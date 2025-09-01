package edu.universidad.model;

public class CursoProfesor {
    private Profesor profesor;
    private Integer anio;
    private Integer semestre;
    private Curso curso;

    public CursoProfesor(Profesor profesor, Integer anio, Integer semestre, Curso curso) {
        this.profesor = profesor;
        this.anio = anio;
        this.semestre = semestre;
        this.curso = curso;
    }

    public Profesor getProfesor() { return profesor; }
    public Integer getAnio() { return anio; }
    public Integer getSemestre() { return semestre; }
    public Curso getCurso() { return curso; }

    @Override
    public String toString() {
        return "CursoProfesor{" +
                "profesor=" + (profesor != null ? profesor.getNombres()+" "+profesor.getApellidos() : "N/A") +
                ", anio=" + anio +
                ", semestre=" + semestre +
                ", curso=" + (curso != null ? curso.getNombre() : "N/A") +
                '}';
    }
}
