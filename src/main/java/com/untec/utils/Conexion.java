package com.untec.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Conexion {
    private static final Logger LOGGER = Logger.getLogger(Conexion.class.getName());
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String EXTERNAL_CONFIG_PROPERTY = "app.db.config";
    private static final String EXTERNAL_CONFIG_ENV = "APP_DB_CONFIG";
    private static volatile Conexion instance;

    private final Properties props;

    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError("No se pudo cargar el driver JDBC de MySQL.");
        }
    }

    private Conexion() throws SQLException {
        this.props = cargarConfiguracion();
    }

    public static Conexion getInstance() throws SQLException {
        if (instance == null) {
            synchronized (Conexion.class) {
                if (instance == null) {
                    instance = new Conexion();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        Connection connection = crearConexion();
        LOGGER.fine("Conexion JDBC creada correctamente.");
        return connection;
    }

    private Connection crearConexion() throws SQLException {
        String url = resolverValor(props, "db.url", "DB_URL");
        String user = resolverValor(props, "db.user", "DB_USER");
        String pass = resolverValor(props, "db.password", "DB_PASSWORD");

        if (estaVacio(url) || estaVacio(user) || pass == null) {
            throw new SQLException("Configuración de base de datos incompleta. Define DB_URL, DB_USER y DB_PASSWORD, o provee un archivo database.properties externo/local.");
        }

        return DriverManager.getConnection(url, user, pass);
    }

    private static Properties cargarConfiguracion() throws SQLException {
        Properties props = new Properties();
        cargarDesdeArchivoExterno(props);

        if (props.isEmpty()) {
            cargarDesdeClasspath(props);
        }

        return props;
    }

    private static void cargarDesdeArchivoExterno(Properties props) throws SQLException {
        String externalPath = primerNoVacio(
                System.getProperty(EXTERNAL_CONFIG_PROPERTY),
                System.getenv(EXTERNAL_CONFIG_ENV)
        );

        if (estaVacio(externalPath)) {
            return;
        }

        Path path = Paths.get(externalPath);
        if (!Files.exists(path)) {
            throw new SQLException("No se encontró el archivo de configuración externo: " + path.toAbsolutePath());
        }

        try (InputStream input = Files.newInputStream(path)) {
            props.load(input);
            LOGGER.log(Level.INFO, "Configuracion de BD cargada desde archivo externo: {0}", path.toAbsolutePath());
        } catch (IOException e) {
            throw new SQLException("Error al leer el archivo de configuración externo: " + path.toAbsolutePath(), e);
        }
    }

    private static void cargarDesdeClasspath(Properties props) throws SQLException {
        try (InputStream input = Conexion.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (input != null) {
                props.load(input);
                LOGGER.info("Configuracion de BD cargada desde classpath (database.properties).");
            }
        } catch (IOException e) {
            throw new SQLException("Error al leer database.properties desde el classpath.", e);
        }
    }

    private static String resolverValor(Properties props, String propertyName, String envName) {
        String value = primerNoVacio(
                System.getProperty(propertyName),
                System.getenv(envName),
                props.getProperty(propertyName)
        );
        return value == null ? null : value.trim();
    }

    private static String primerNoVacio(String... valores) {
        for (String valor : valores) {
            if (!estaVacio(valor)) {
                return valor;
            }
        }
        return null;
    }

    private static boolean estaVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}