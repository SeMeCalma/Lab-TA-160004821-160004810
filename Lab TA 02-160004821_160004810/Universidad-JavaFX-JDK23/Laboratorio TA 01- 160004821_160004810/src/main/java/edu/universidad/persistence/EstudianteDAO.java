
package edu.universidad.persistence;

import edu.universidad.model.Estudiante;
import java.sql.*;
import java.util.*;

public class EstudianteDAO {

    public void upsert(Estudiante e, int semestre) {
        String sql = "MERGE INTO ESTUDIANTE (CODIGO,NOMBRES,APELLIDOS,EMAIL,PROMEDIO,ACTIVO,SEMESTRE) KEY(CODIGO) VALUES (?,?,?,?,?,?,?)";
        try (Connection con = H2DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, e.getCodigo());
            ps.setString(2, e.getNombres());
            ps.setString(3, e.getApellidos());
            ps.setString(4, e.getEmail());
            ps.setDouble(5, e.getPromedio());
            ps.setBoolean(6, e.isActivo());
            ps.setInt(7, semestre);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Error upsert estudiante", ex);
        }
    }

    public boolean eliminar(double codigo) {
        String sql = "DELETE FROM ESTUDIANTE WHERE CODIGO=?";
        try (Connection con = H2DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, codigo);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new RuntimeException("Error eliminando estudiante", ex);
        }
    }

    public List<Estudiante> listar() {
        String sql = "SELECT CODIGO,NOMBRES,APELLIDOS,EMAIL,PROMEDIO,ACTIVO,SEMESTRE FROM ESTUDIANTE";
        List<Estudiante> out = new ArrayList<>();
        try (Connection con = H2DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                double codigo = rs.getDouble(1);
                String nombres = rs.getString(2);
                String apellidos = rs.getString(3);
                String email = rs.getString(4);
                double promedio = rs.getDouble(5);
                boolean activo = rs.getBoolean(6);
                int semestre = rs.getInt(7);
                out.add(new Estudiante(codigo, nombres, apellidos, email, codigo, null, activo, promedio));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error listando estudiantes", ex);
        }
        return out;
    }

    public Integer getSemestre(double codigo) {
        String sql = "SELECT SEMESTRE FROM ESTUDIANTE WHERE CODIGO=?";
        try (Connection con = H2DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
                return null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error leyendo semestre de estudiante", ex);
        }
    }
}
