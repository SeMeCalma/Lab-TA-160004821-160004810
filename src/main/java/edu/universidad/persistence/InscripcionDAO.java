package edu.universidad.persistence;

import edu.universidad.model.Curso;
import edu.universidad.model.Estudiante;
import edu.universidad.model.Inscripcion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InscripcionDAO {

    public void insertar(Inscripcion inscripcion) {
        String sql = "INSERT INTO INSCRIPCION (CURSO_ID, ESTUDIANTE_CODIGO, ANIO, SEMESTRE) VALUES (?,?,?,?)";
        try (Connection con = H2DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, inscripcion.getCurso().getID());
            ps.setDouble(2, inscripcion.getEstudiante().getCodigo());
            ps.setInt(3, inscripcion.getAnio());
            ps.setInt(4, inscripcion.getSemestre());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error insertando inscripción", e);
        }
    }

    public List<Inscripcion> listar(List<Estudiante> estudiantes, List<Curso> cursos) {
        String sql = "SELECT CURSO_ID, ESTUDIANTE_CODIGO, ANIO, SEMESTRE FROM INSCRIPCION";
        List<Inscripcion> out = new ArrayList<>();
        try (Connection con = H2DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int cursoId = rs.getInt("CURSO_ID");
                    double codEst = rs.getDouble("ESTUDIANTE_CODIGO");
                    int anio = rs.getInt("ANIO");
                    int semestre = rs.getInt("SEMESTRE");

                    Curso curso = cursos.stream().filter(c -> c.getID() == cursoId).findFirst().orElse(null);
                    Estudiante est = estudiantes.stream().filter(e -> Double.compare(e.getCodigo(), codEst) == 0).findFirst().orElse(null);
                    if (curso != null && est != null) {
                        out.add(new Inscripcion(curso, anio, semestre, est));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error listando inscripciones", e);
        }
        return out;
    }
}
