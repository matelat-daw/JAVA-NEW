package com.miapp;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet para eliminar un producto de la BD
 */
@WebServlet("/tienda/eliminar")
public class ProductoEliminarServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int id = Integer.parseInt(request.getParameter("id"));

            // Eliminar de la BD
            boolean eliminado = ProductoDAO.eliminarProducto(id);

            if (eliminado) {
                request.setAttribute("mensaje", "Producto eliminado correctamente");
                request.setAttribute("tipo", "exito");
            } else {
                request.setAttribute("mensaje", "Error al eliminar el producto");
                request.setAttribute("tipo", "error");
            }

        } catch (NumberFormatException e) {
            request.setAttribute("mensaje", "Error: ID inválido");
            request.setAttribute("tipo", "error");
        }

        // Redirigir a la página de catálogo
        response.sendRedirect(request.getContextPath() + "/tienda/catalogo");
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int id = Integer.parseInt(request.getParameter("id"));

            // Eliminar de la BD
            boolean eliminado = ProductoDAO.eliminarProducto(id);

            if (eliminado) {
                request.setAttribute("mensaje", "Producto eliminado correctamente");
                request.setAttribute("tipo", "exito");
            } else {
                request.setAttribute("mensaje", "Error al eliminar el producto");
                request.setAttribute("tipo", "error");
            }

        } catch (NumberFormatException e) {
            request.setAttribute("mensaje", "Error: ID inválido");
            request.setAttribute("tipo", "error");
        }

        // Redirigir a la página de catálogo
        response.sendRedirect(request.getContextPath() + "/tienda/catalogo");
    }
}
