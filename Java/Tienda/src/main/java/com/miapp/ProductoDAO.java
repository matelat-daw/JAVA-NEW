package com.miapp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) para la tabla Producto
 * Maneja todas las operaciones CRUD con la BD
 */
public class ProductoDAO {

    /**
     * Obtiene todos los productos del catálogo
     * 
     * @return Lista de productos
     */
    public static List<Producto> obtenerCatalogo() {
        List<Producto> catalogo = new ArrayList<>();
        String sql = "SELECT id, nombre, precio, categoria, descripcion, imagen FROM producto";

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Producto producto = new Producto(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getDouble("precio"),
                    rs.getString("categoria"),
                    rs.getString("descripcion"),
                    rs.getString("imagen")
                );
                catalogo.add(producto);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener catálogo: " + e.getMessage());
            e.printStackTrace();
        }

        return catalogo;
    }

    /**
     * Busca un producto por su ID
     * 
     * @param id ID del producto
     * @return Producto encontrado, null si no existe
     */
    public static Producto buscarPorId(int id) {
        String sql = "SELECT id, nombre, precio, categoria, descripcion, imagen FROM producto WHERE id = ?";
        
        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Producto(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getDouble("precio"),
                        rs.getString("categoria"),
                        rs.getString("descripcion"),
                        rs.getString("imagen")
                    );
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar producto por ID: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Obtiene todos los productos de una categoría específica
     * 
     * @param categoria Nombre de la categoría
     * @return Lista de productos de esa categoría
     */
    public static List<Producto> obtenerPorCategoria(String categoria) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT id, nombre, precio, categoria, descripcion, imagen FROM producto WHERE categoria = ?";

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, categoria);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Producto producto = new Producto(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getDouble("precio"),
                        rs.getString("categoria"),
                        rs.getString("descripcion"),
                        rs.getString("imagen")
                    );
                    productos.add(producto);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener productos por categoría: " + e.getMessage());
            e.printStackTrace();
        }

        return productos;
    }

    /**
     * Inserta un nuevo producto en la BD
     * 
     * @param producto Producto a insertar
     * @return true si se insertó correctamente
     */
    public static boolean insertarProducto(Producto producto) {
        String sql = "INSERT INTO producto (nombre, precio, categoria, descripcion, imagen) VALUES (?, ?, ?, ?, ?)";

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            System.out.println("=== INSERTANDO PRODUCTO EN BD ===");
            System.out.println("SQL: " + sql);
            System.out.println("Parámetros:");
            System.out.println("  1. Nombre: " + producto.getNombre());
            System.out.println("  2. Precio: " + producto.getPrecio());
            System.out.println("  3. Categoría: " + producto.getCategoria());
            System.out.println("  4. Descripción: " + producto.getDescripcion());
            System.out.println("  5. Imagen: " + producto.getImagen());

            ps.setString(1, producto.getNombre());
            ps.setDouble(2, producto.getPrecio());
            ps.setString(3, producto.getCategoria());
            ps.setString(4, producto.getDescripcion());
            ps.setString(5, producto.getImagen());

            int filasAfectadas = ps.executeUpdate();
            System.out.println("Filas afectadas: " + filasAfectadas);
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("=== ERROR AL INSERTAR PRODUCTO ===");
            System.err.println("SQL: " + sql);
            System.err.println("Código de error: " + e.getErrorCode());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Mensaje: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Actualiza un producto existente
     * 
     * @param producto Producto con los datos actualizados
     * @return true si se actualizó correctamente
     */
    public static boolean actualizarProducto(Producto producto) {
        String sql = "UPDATE producto SET nombre = ?, precio = ?, categoria = ?, descripcion = ?, imagen = ? WHERE id = ?";

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, producto.getNombre());
            ps.setDouble(2, producto.getPrecio());
            ps.setString(3, producto.getCategoria());
            ps.setString(4, producto.getDescripcion());
            ps.setString(5, producto.getImagen());
            ps.setInt(6, producto.getId());

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Error al actualizar producto: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Obtiene todas las categorías disponibles en la BD
     * 
     * @return Lista de nombres de categorías
     */
    public static List<String> obtenerCategorias() {
        List<String> categorias = new ArrayList<>();
        String sql = "SELECT nombre FROM categoria ORDER BY nombre";

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                categorias.add(rs.getString("nombre"));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener categorías: " + e.getMessage());
            e.printStackTrace();
        }

        return categorias;
    }

    /**
     * Elimina un producto de la BD
     * 
     * @param id ID del producto a eliminar
     * @return true si se eliminó correctamente
     */
    public static boolean eliminarProducto(int id) {
        String sql = "DELETE FROM producto WHERE id = ?";

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Error al eliminar producto: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }
}