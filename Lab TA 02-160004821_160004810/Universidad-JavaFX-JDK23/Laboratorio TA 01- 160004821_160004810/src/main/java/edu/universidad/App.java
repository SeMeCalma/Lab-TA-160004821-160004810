package edu.universidad;

import edu.universidad.model.*;
import edu.universidad.persistence.InscripcionDAO;
import edu.universidad.persistence.Schema;
import edu.universidad.repository.CursosInscritos;
import edu.universidad.repository.CursosProfesores;
import edu.universidad.repository.InscripcionesPersonas;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class App {
    public static void main(String[] args) {
        System.out.println("=== Universidad (consola + H2 + JDBC, sin frameworks) ===");

        Schema.crearTablas();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Schema.limpiarTablas();
                System.out.println("\n(H2) Tabla INSCRIPCION limpiada al salir.");
            } catch (Exception ex) {
                System.err.println("No se pudo limpiar H2: " + ex.getMessage());
            }
        }));

        Persona decano = new Persona(100, "Ana", "Decana", "ana.decana@uni.edu");
        Facultad ing = new Facultad(1, "Ingeniería", decano);
        Programa sis = new Programa(10, "Sistemas", 10.0, new Date(), ing);

        Estudiante e1 = new Estudiante(1, "Camilo", "Acosta", "camilo@uni.edu", 2025001, sis, true, 4.5);
        Estudiante e2 = new Estudiante(2, "Valentina", "Gómez", "valentina@uni.edu", 2025002, sis, true, 4.1);

        Profesor prof = new Profesor(3, "Carlos", "Rojas", "carlos.rojas@uni.edu", "Tiempo Completo");

        Curso TecAv = new Curso(101, "Tecnologias Avanzadas", sis, true);

        List<Estudiante> estudiantes = new ArrayList<>(); estudiantes.add(e1); estudiantes.add(e2);
        List<Curso> cursos = new ArrayList<>(); cursos.add(TecAv);

        InscripcionesPersonas repoPersonas = new InscripcionesPersonas();
        repoPersonas.inscribir(e1);
        repoPersonas.inscribir(e2);
        repoPersonas.inscribir(prof);

        CursosProfesores repoCursoProf = new CursosProfesores();
        repoCursoProf.inscribir(new CursoProfesor(prof, 2025, 7, TecAv));

        CursosInscritos repoInscritos = new CursosInscritos(new InscripcionDAO(), estudiantes, cursos);
        Inscripcion i1 = new Inscripcion(TecAv, 2025, 7, e1);
        Inscripcion i2 = new Inscripcion(TecAv, 2025, 7, e2);

        repoInscritos.inscribirCurso(i1);
        repoInscritos.inscribirCurso(i2);

        repoInscritos.guardarInformacion(i1);
        repoInscritos.guardarInformacion(i2);

        repoInscritos.getListado().clear();
        repoInscritos.cargarDatos();

        System.out.println("\n-- Personas registradas --");
        repoPersonas.imprimirListado().forEach(System.out::println);

        System.out.println("\n-- Curso/Profesor --");
        repoCursoProf.imprimirListado().forEach(System.out::println);

        System.out.println("\n-- Inscripciones (leídas de H2) --");
        repoInscritos.imprimirListado().forEach(System.out::println);

        System.out.println("\nCantidad inscripciones: " + repoInscritos.cantidadActual());
        System.out.println("Primera inscripcion: " + repoInscritos.imprimirPosicion(0));
        System.out.println("Segunda inscripcion: " + repoInscritos.imprimirPosicion(1));

        System.out.println("\nFin.");
    }
}
