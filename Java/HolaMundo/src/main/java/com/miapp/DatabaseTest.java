package com.miapp;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Clase de utilidad para verificar la conexión a la BD
 * Útil para testing y debugging
 */
public class DatabaseTest {

    public static void main(String[] args) {
        System.out.println("=== Prueba de Conexión a MariaDB ===\n");

        try {
            // Intentar conexión
            Connection conexion = DatabaseConnection.getConnection();
            System.out.println("✓ Conexión establecida correctamente");
            System.out.println("  URL: jdbc:mariadb://localhost:3306/techstore");
            System.out.println("  Usuario: root\n");

            // Probar consulta de productos
            System.out.println("=== Productos en la BD ===");
            String sql = "SELECT COUNT(*) as total FROM producto";
            Statement stmt = conexion.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                int total = rs.getInt("total");
                System.out.println("Total de productos: " + total);
            }

            // Listar algunos productos
            sql = "SELECT id, nombre, precio, categoria FROM producto LIMIT 5";
            rs = stmt.executeQuery(sql);

            System.out.println("\nPrimeros 5 productos:");
            System.out.println("ID | Nombre | Precio | Categoría");
            System.out.println("---+--------+--------+----------");

            while (rs.next()) {
                System.out.printf("%d | %s | $%.2f | %s%n",
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getDouble("precio"),
                    rs.getString("categoria")
                );
            }

            DatabaseConnection.cerrarConexion(conexion);
            System.out.println("\n✓ Prueba completada exitosamente");

        } catch (SQLException e) {
            System.out.println("✗ Error de conexión: " + e.getMessage());
            System.out.println("\nVerifica que:");
            System.out.println("1. MariaDB esté ejecutándose en localhost:3306");
            System.out.println("2. La base de datos 'techstore' existe");
            System.out.println("3. El usuario 'root' y contraseña 'Anubis@68' sean correctos");
            System.out.println("4. La tabla 'producto' exista");
            e.printStackTrace();
        }
    }
}
