package com.miapp;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet para actualizar un producto en la BD
 */
@WebServlet("/tienda/actualizar")
public class ProductoActualizarServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Obtener parámetros del formulario
            int id = Integer.parseInt(request.getParameter("id"));
            String nombre = request.getParameter("nombre");
            double precio = Double.parseDouble(request.getParameter("precio"));
            String categoria = request.getParameter("categoria");
            String descripcion = request.getParameter("descripcion");
            String imagen = request.getParameter("imagen");

            // Crear objeto Producto con los datos
            Producto producto = new Producto(id, nombre, precio, categoria, descripcion, imagen);

            // Actualizar en la BD
            boolean actualizado = ProductoDAO.actualizarProducto(producto);

            if (actualizado) {
                request.setAttribute("mensaje", "Producto actualizado correctamente");
                request.setAttribute("tipo", "exito");
            } else {
                request.setAttribute("mensaje", "Error al actualizar el producto");
                request.setAttribute("tipo", "error");
            }

        } catch (NumberFormatException e) {
            request.setAttribute("mensaje", "Error: Datos inválidos");
            request.setAttribute("tipo", "error");
        }

        // Redirigir a la página de catálogo
        response.sendRedirect(request.getContextPath() + "/tienda/catalogo");
    }
}
