package com.untec.utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Conexion {
    // 1. Instancia estática única (Patrón Singleton)
    private static Conexion instance;
    private Connection connection;

    // 2. Constructor privado
    private Conexion() throws SQLException {
        Properties props = new Properties();

        try (InputStream input = getClass().getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                throw new SQLException("Error: No se encontró el archivo database.properties");
            }

            props.load(input);

            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String pass = props.getProperty("db.password");
            String driver = "com.mysql.cj.jdbc.Driver";

            // Cargar Driver y establecer conexión JDBC
            Class.forName(driver);
            this.connection = DriverManager.getConnection(url, user, pass);

        } catch (IOException | ClassNotFoundException e) {
            throw new SQLException("Error al configurar la conexión: " + e.getMessage());
        }
    }

    // 3. Metodo estático pra obtener la instancia
    public static Conexion getInstance() throws SQLException {
        if (instance == null || instance.getConnection().isClosed()) {
            instance = new Conexion();
        }
        return instance;
    }

    // 4. Getter para la conexión
    public Connection getConnection() {
        return connection;
    }
}