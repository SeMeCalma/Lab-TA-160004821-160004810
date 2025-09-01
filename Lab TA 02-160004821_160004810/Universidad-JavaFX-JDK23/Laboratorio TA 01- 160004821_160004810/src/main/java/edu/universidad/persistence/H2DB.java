package edu.universidad.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class H2DB {
    private static final String URL = "jdbc:h2:./data/universidad;AUTO_SERVER=TRUE";
    private static final String USER = "sa";
    private static final String PASS = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
