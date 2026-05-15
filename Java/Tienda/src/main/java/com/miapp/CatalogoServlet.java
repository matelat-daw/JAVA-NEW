package com.miapp; 
  
import jakarta.servlet.ServletException; 
import jakarta.servlet.annotation.WebServlet; 
import jakarta.servlet.http.HttpServlet; 
import jakarta.servlet.http.HttpServletRequest; 
import jakarta.servlet.http.HttpServletResponse; 
import java.io.IOException; 
import java.util.List; 
  
@WebServlet("/tienda/catalogo") 
public class CatalogoServlet extends HttpServlet { 
  
    @Override 
    protected void doGet(HttpServletRequest request, 
                         HttpServletResponse response) 
            throws ServletException, IOException {
  
        List<Producto> catalogo = DatosTienda.obtenerCatalogo(); 
  
        request.setAttribute("catalogo", catalogo); 
        request.setAttribute("totalProductos", catalogo.size()); 
        request.setAttribute("titulo", "Catálogo de productos"); 
  
        request.getRequestDispatcher("/WEB-INF/vistas/catalogo.jsp") 
               .forward(request, response); 
    } 
} 