package com.miapp;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@WebServlet("/tienda/resumen")
public class ResumenServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Obtener el catálogo de productos
        List<Producto> catalogo = DatosTienda.obtenerCatalogo();
        
        // Calcular total de categorías únicas
        Set<String> categorias = new HashSet<>();
        String productoMasCaro = "";
        double precioMaximo = 0;
        
        for (Producto producto : catalogo) {
            categorias.add(producto.getCategoria());
            
            // Encontrar el producto más caro
            if (producto.getPrecio() > precioMaximo) {
                precioMaximo = producto.getPrecio();
                productoMasCaro = producto.getNombre();
            }
        }
        
        // Crear el bean de resumen con el constructor
        ResumenBean resumen = new ResumenBean(
            catalogo.size(),
            categorias.size(),
            productoMasCaro
        );
        
        // Enviar el bean a la vista
        request.setAttribute("resumen", resumen);
        request.getRequestDispatcher("/WEB-INF/vistas/resumen.jsp")
                .forward(request, response);
    }
}