package com.miapp;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet para mostrar el formulario de edición de un producto
 */
@WebServlet("/tienda/editar")
public class ProductoEditarServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        String idTexto = request.getParameter("id");
        Producto producto = null;

        if (idTexto != null) {
            try {
                int id = Integer.parseInt(idTexto);
                producto = ProductoDAO.buscarPorId(id);
            } catch (NumberFormatException e) {
                producto = null;
            }
        }

        request.setAttribute("producto", producto);
        request.setAttribute("titulo", "Editar Producto");

        request.getRequestDispatcher("/WEB-INF/vistas/editar-producto.jsp")
               .forward(request, response);
    }
}
