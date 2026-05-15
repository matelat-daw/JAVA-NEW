package com.miapp;

import jakarta.servlet.ServletException; 
import jakarta.servlet.annotation.WebServlet; 
import jakarta.servlet.http.HttpServlet; 
import jakarta.servlet.http.HttpServletRequest; 
import jakarta.servlet.http.HttpServletResponse; 
import java.io.IOException;
  
@WebServlet("/tienda/contacto")
public class ContactoServlet extends HttpServlet {
  
    @Override
    protected void doGet(HttpServletRequest request,
                        HttpServletResponse response)
        throws ServletException, IOException {
        
        // Obtener el bean de contacto con los datos de la tienda
        ContactoBean contacto = crearContactoTienda();
        
        // Enviar el bean a la vista
        request.setAttribute("contacto", contacto);
        request.getRequestDispatcher("/WEB-INF/vistas/contacto.jsp") 
           .forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request,
        HttpServletResponse response)
        throws ServletException, IOException {
        
        // Obtener los parámetros del formulario
        String nombre = request.getParameter("nombre");
        String email = request.getParameter("email");
        String asunto = request.getParameter("asunto");
        String mensaje = request.getParameter("mensaje");
        
        // Validar que los campos no estén vacíos
        String error = "";
        if (nombre == null || nombre.trim().isEmpty()) {
            error += "El nombre es requerido. ";
        }
        if (email == null || email.trim().isEmpty()) {
            error += "El email es requerido. ";
        }
        if (asunto == null || asunto.trim().isEmpty()) {
            error += "El asunto es requerido. ";
        }
        if (mensaje == null || mensaje.trim().isEmpty()) {
            error += "El mensaje es requerido.";
        }
        
        // Si hay errores, volver al formulario
        if (!error.isEmpty()) {
            request.setAttribute("error", error);
            doGet(request, response);
            return;
        }
        
        // Obtener el bean de contacto con los datos de la tienda
        ContactoBean contacto = crearContactoTienda();
        
        // Enviar confirmación
        request.setAttribute("contacto", contacto);
        request.setAttribute("exito", "¡Mensaje enviado exitosamente! Nos pondremos en contacto pronto.");
        request.getRequestDispatcher("/WEB-INF/vistas/contacto.jsp") 
           .forward(request, response);
    }
    
    private ContactoBean crearContactoTienda() {
        return new ContactoBean(
            "TechStore",
            "Calle Principal 123, Tenerife, España",
            "+34 92 234 5678",
            "contacto@techstore.com",
            "Lunes a Viernes: 9:00 - 18:00 | Sábado: 10:00 - 14:00"
        );
    }
}