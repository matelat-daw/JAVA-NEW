package com.miapp; 
  
import jakarta.servlet.ServletException; 
import jakarta.servlet.annotation.WebServlet; 
import jakarta.servlet.http.HttpServlet; 
import jakarta.servlet.http.HttpServletRequest; 
import jakarta.servlet.http.HttpServletResponse; 
import java.io.IOException; 
  
@WebServlet("/tienda/producto") 
public class ProductoDetalleServlet extends HttpServlet { 
  
    @Override 
    protected void doGet(HttpServletRequest request, 
                         HttpServletResponse response) 
            throws ServletException, IOException { 
  
        String idTexto = request.getParameter("id"); 
        Producto producto = null; 
  
        if (idTexto != null) { 
            try { 
                int id = Integer.parseInt(idTexto); 
                producto = DatosTienda.buscarPorId(id); 
            } catch (NumberFormatException error) { 
                producto = null; 
            } 
        } 
  
        request.setAttribute("producto", producto); 
  
        request.getRequestDispatcher("/WEB-INF/vistas/detalle-producto.jsp") 
               .forward(request, response); 
    } 
}