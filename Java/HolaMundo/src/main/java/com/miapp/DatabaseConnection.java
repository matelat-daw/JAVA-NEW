package com.miapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase para gestionar la conexión a la base de datos MariaDB
 */
public class DatabaseConnection {

    // Configuración de la BD
    private static final String URL = "jdbc:mariadb://localhost:3306/techstore";
    private static final String USUARIO = "root";
    private static final String CONTRASENA = "Anubis@68";
    private static final String DRIVER = "org.mariadb.jdbc.Driver";

    /**
     * Obtiene una conexión a la base de datos
     * 
     * @return Conexión a la BD
     * @throws SQLException Si hay error al conectar
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Cargar el driver JDBC de MariaDB
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("Error: Driver JDBC de MariaDB no encontrado");
            e.printStackTrace();
        }
        
        return DriverManager.getConnection(URL, USUARIO, CONTRASENA);
    }

    /**
     * Cierra una conexión a la base de datos
     * 
     * @param conexion Conexión a cerrar
     */
    public static void cerrarConexion(Connection conexion) {
        if (conexion != null) {
            try {
                conexion.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión");
                e.printStackTrace();
            }
        }
    }
}
