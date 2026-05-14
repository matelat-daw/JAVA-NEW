package com.miapp;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
  
@WebServlet("/tienda")
public class TiendaServlet extends HttpServlet {
  
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {
  
        request.setAttribute("nombreTienda", "Tienda Tech");
        request.setAttribute("mensaje", "Aplicación JSP conectada con Servlets");
  
        request.getRequestDispatcher("/WEB-INF/vistas/tienda.jsp")
               .forward(request, response);
    }
}