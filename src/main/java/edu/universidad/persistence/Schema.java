package edu.universidad.persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Schema {
    public static void crearTablas() {
        String sql = "CREATE TABLE IF NOT EXISTS INSCRIPCION (" +
                "ID IDENTITY PRIMARY KEY," +
                "CURSO_ID INT NOT NULL," +
                "ESTUDIANTE_CODIGO DOUBLE NOT NULL," +
                "ANIO INT NOT NULL," +
                "SEMESTRE INT NOT NULL" +
                ")";
        try (Connection con = H2DB.getConnection();
             Statement st = con.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Error creando tablas H2", e);
        }
    }

    public static void limpiarTablas() {
        String sql = "DELETE FROM INSCRIPCION";
        try (Connection con = H2DB.getConnection();
             Statement st = con.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Error limpiando tablas H2", e);
        }
    }

}
