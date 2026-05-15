package com.miapp; 
  
import java.util.List; 
  
/**
 * Clase que actúa como interfaz para obtener datos de la tienda
 * Ahora obtiene los datos de la base de datos MariaDB en lugar de datos ficticios
 */
public class DatosTienda { 
  
    /**
     * Obtiene el catálogo completo de productos desde la BD
     * 
     * @return Lista de productos
     */
    public static List<Producto> obtenerCatalogo() { 
        return ProductoDAO.obtenerCatalogo(); 
    } 
  
    /**
     * Busca un producto por su ID en la BD
     * 
     * @param id ID del producto
     * @return Producto encontrado, null si no existe
     */
    public static Producto buscarPorId(int id) { 
        return ProductoDAO.buscarPorId(id); 
    } 
} 