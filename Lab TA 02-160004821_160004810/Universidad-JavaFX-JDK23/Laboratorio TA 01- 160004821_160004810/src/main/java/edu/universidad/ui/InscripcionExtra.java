package edu.universidad.ui;

import edu.universidad.model.Curso;
import edu.universidad.model.Estudiante;
import edu.universidad.model.Inscripcion;
import edu.universidad.persistence.InscripcionDAO;
import edu.universidad.persistence.H2DB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InscripcionExtra {

    public static List<Inscripcion> listarSinCatalogos(InscripcionDAO dao) {
        List<Inscripcion> out = new ArrayList<>();
        String sql = "SELECT CURSO_ID, ESTUDIANTE_CODIGO, ANIO, SEMESTRE FROM INSCRIPCION";
        try (Connection con = H2DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int cursoId = rs.getInt("CURSO_ID");
                    double codEst = rs.getDouble("ESTUDIANTE_CODIGO");
                    int anio = rs.getInt("ANIO");
                    int semestre = rs.getInt("SEMESTRE");
                    Curso c = new Curso(cursoId, null, null, true);
                    Estudiante e = new Estudiante(codEst, null, null, null, codEst, null, true, 0);
                    out.add(new Inscripcion(c, anio, semestre, e));
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error listando inscripciones (sin catálogos)", ex);
        }
        return out;
    }

    public static boolean eliminar(InscripcionDAO dao, Inscripcion i) {
        String sql = "DELETE FROM INSCRIPCION WHERE CURSO_ID=? AND ESTUDIANTE_CODIGO=? AND ANIO=? AND SEMESTRE=?";
        try (Connection con = H2DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, i.getCurso().getID());
            ps.setDouble(2, i.getEstudiante().getCodigo());
            ps.setInt(3, i.getAnio());
            ps.setInt(4, i.getSemestre());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new RuntimeException("Error eliminando inscripción", ex);
        }
    }

    public static int actualizar(InscripcionDAO dao, Inscripcion before, Inscripcion after) {
        String sql = "UPDATE INSCRIPCION SET CURSO_ID=?, ESTUDIANTE_CODIGO=?, ANIO=?, SEMESTRE=? " +
                "WHERE CURSO_ID=? AND ESTUDIANTE_CODIGO=? AND ANIO=? AND SEMESTRE=?";
        try (Connection con = H2DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, after.getCurso().getID());
            ps.setDouble(2, after.getEstudiante().getCodigo());
            ps.setInt(3, after.getAnio());
            ps.setInt(4, after.getSemestre());
            ps.setInt(5, before.getCurso().getID());
            ps.setDouble(6, before.getEstudiante().getCodigo());
            ps.setInt(7, before.getAnio());
            ps.setInt(8, before.getSemestre());
            return ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Error actualizando inscripción", ex);
        }
    }
}
