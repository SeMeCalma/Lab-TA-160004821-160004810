
package edu.universidad.persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Schema {
    public static void crearTablas() {
        String tEst = "CREATE TABLE IF NOT EXISTS ESTUDIANTE (" +
                "CODIGO DOUBLE PRIMARY KEY, " +
                "NOMBRES VARCHAR(120), " +
                "APELLIDOS VARCHAR(120), " +
                "EMAIL VARCHAR(180), " +
                "PROMEDIO DOUBLE, " +
                "ACTIVO BOOLEAN, " +
                "SEMESTRE INT NOT NULL" +
                ")";

        String tProf = "CREATE TABLE IF NOT EXISTS PROFESOR (" +
                "ID DOUBLE PRIMARY KEY, " +
                "NOMBRES VARCHAR(120), " +
                "APELLIDOS VARCHAR(120), " +
                "EMAIL VARCHAR(180), " +
                "TIPOCONTRATO VARCHAR(80)" +
                ")";

        String tCurso = "CREATE TABLE IF NOT EXISTS CURSO (" +
                "ID INT PRIMARY KEY, " +
                "NOMBRE VARCHAR(160), " +
                "ACTIVO BOOLEAN, " +
                "SEMESTRE INT NOT NULL, " +
                "PROFESOR_ID DOUBLE" +
                ")";

        String tIns = "CREATE TABLE IF NOT EXISTS INSCRIPCION (" +
                "ID IDENTITY PRIMARY KEY, " +
                "CURSO_ID INT NOT NULL, " +
                "ESTUDIANTE_CODIGO DOUBLE NOT NULL, " +
                "ANIO INT NOT NULL, " +
                "SEMESTRE INT NOT NULL" +
                ")";

        try (Connection con = H2DB.getConnection();
             Statement st = con.createStatement()) {
            st.execute(tEst);
            st.execute(tProf);
            st.execute(tCurso);
            st.execute(tIns);
        } catch (SQLException e) {
            throw new RuntimeException("Error creando esquema H2", e);
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
