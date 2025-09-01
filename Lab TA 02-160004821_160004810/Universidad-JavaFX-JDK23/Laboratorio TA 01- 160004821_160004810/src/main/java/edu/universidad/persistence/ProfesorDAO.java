
package edu.universidad.persistence;

import edu.universidad.model.Profesor;
import java.sql.*;
import java.util.*;

public class ProfesorDAO {

    public void upsert(Profesor p) {
        String sql = "MERGE INTO PROFESOR (ID,NOMBRES,APELLIDOS,EMAIL,TIPOCONTRATO) KEY(ID) VALUES (?,?,?,?,?)";
        try (Connection con = H2DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, p.getID());
            ps.setString(2, p.getNombres());
            ps.setString(3, p.getApellidos());
            ps.setString(4, p.getEmail());
            ps.setString(5, p.getTipoContrato());
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Error upsert profesor", ex);
        }
    }

    public boolean eliminar(double id) {
        String sql = "DELETE FROM PROFESOR WHERE ID=?";
        try (Connection con = H2DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new RuntimeException("Error eliminando profesor", ex);
        }
    }

    public List<Profesor> listar() {
        String sql = "SELECT ID,NOMBRES,APELLIDOS,EMAIL,TIPOCONTRATO FROM PROFESOR";
        List<Profesor> out = new ArrayList<>();
        try (Connection con = H2DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                double id = rs.getDouble(1);
                String nom = rs.getString(2);
                String ape = rs.getString(3);
                String email = rs.getString(4);
                String tipo = rs.getString(5);
                out.add(new Profesor(id, nom, ape, email, tipo));
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error listando profesores", ex);
        }
        return out;
    }
}
