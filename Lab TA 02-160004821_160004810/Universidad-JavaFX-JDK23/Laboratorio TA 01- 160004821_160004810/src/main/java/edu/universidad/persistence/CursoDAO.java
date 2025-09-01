
package edu.universidad.persistence;

import edu.universidad.model.Curso;
import java.sql.*;
import java.util.*;

public class CursoDAO {

    public void upsert(Curso c, int semestre, Double profesorId) {
        String sql = "MERGE INTO CURSO (ID,NOMBRE,ACTIVO,SEMESTRE,PROFESOR_ID) KEY(ID) VALUES (?,?,?,?,?)";
        try (Connection con = H2DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, c.getID());
            ps.setString(2, c.getNombre());
            ps.setBoolean(3, c.isActivo());
            ps.setInt(4, semestre);
            if (profesorId == null) ps.setNull(5, Types.DOUBLE); else ps.setDouble(5, profesorId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Error upsert curso", ex);
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM CURSO WHERE ID=?";
        try (Connection con = H2DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new RuntimeException("Error eliminando curso", ex);
        }
    }

    public List<Map<String,Object>> listarConExtras() {
        String sql = "SELECT c.ID,c.NOMBRE,c.ACTIVO,c.SEMESTRE,c.PROFESOR_ID," +
                     " (SELECT COUNT(*) FROM INSCRIPCION i WHERE i.CURSO_ID=c.ID) AS INSCRITOS, " +
                     " p.NOMBRES, p.APELLIDOS, p.EMAIL " +
                     " FROM CURSO c LEFT JOIN PROFESOR p ON p.ID=c.PROFESOR_ID";
        List<Map<String,Object>> out = new ArrayList<>();
        try (Connection con = H2DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String,Object> m = new HashMap<>();
                m.put("ID", rs.getInt("ID"));
                m.put("NOMBRE", rs.getString("NOMBRE"));
                m.put("ACTIVO", rs.getBoolean("ACTIVO"));
                m.put("SEMESTRE", rs.getInt("SEMESTRE"));
                m.put("PROFESOR_ID", rs.getObject("PROFESOR_ID")==null?null:rs.getDouble("PROFESOR_ID"));
                m.put("INSCRITOS", rs.getInt("INSCRITOS"));
                m.put("PROF_NOMBRES", rs.getString("NOMBRES"));
                m.put("PROF_APELLIDOS", rs.getString("APELLIDOS"));
                m.put("PROF_EMAIL", rs.getString("EMAIL"));
                out.add(m);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error listando cursos", ex);
        }
        return out;
    }

    public Integer getSemestre(int id) {
        String sql = "SELECT SEMESTRE FROM CURSO WHERE ID=?";
        try (Connection con = H2DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
                return null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error leyendo semestre de curso", ex);
        }
    }

    public void asignarProfesor(int cursoId, Double profesorId) {
        String sql = "UPDATE CURSO SET PROFESOR_ID=? WHERE ID=?";
        try (Connection con = H2DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (profesorId==null) ps.setNull(1, Types.DOUBLE); else ps.setDouble(1, profesorId);
            ps.setInt(2, cursoId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Error asignando profesor", ex);
        }
    }
}
