package com.miapp;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Servlet para guardar un nuevo producto en la BD
 */
@WebServlet("/tienda/guardar")
@MultipartConfig(maxFileSize = 5242880) // 5 MB
public class ProductoGuardarServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("=== INICIANDO PROCESAMIENTO DE NUEVO PRODUCTO ===");

        try {
            // Obtener parámetros del formulario
            String nombre = request.getParameter("nombre");
            String precioStr = request.getParameter("precio");
            String categoria = request.getParameter("categoria");
            String descripcion = request.getParameter("descripcion");
            
            System.out.println("Nombre: " + nombre);
            System.out.println("Precio: " + precioStr);
            System.out.println("Categoria: " + categoria);
            System.out.println("Descripcion: " + descripcion);

            double precio = Double.parseDouble(precioStr);

            // Procesar la imagen subida
            Part filePart = request.getPart("imagen");
            String nombreImagen = null;

            if (filePart != null && filePart.getSize() > 0) {
                // Obtener el nombre del archivo original
                String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                
                System.out.println("Archivo subido: " + fileName + " (Tamaño: " + filePart.getSize() + " bytes)");
                
                // Validar extensión
                if (!isValidImageExtension(fileName)) {
                    System.out.println("Extensión no válida: " + fileName);
                    request.setAttribute("mensaje", "Formato de imagen no permitido. Use: .webp, .jpg, .jpeg, .png");
                    request.setAttribute("tipo", "error");
                    response.sendRedirect(request.getContextPath() + "/tienda/nuevo");
                    return;
                }

                // Usar el nombre original del archivo
                nombreImagen = fileName;
                System.out.println("Usando nombre original de imagen: " + nombreImagen);

                // Obtener la ruta del directorio de imágenes (Despliegue - Donde el servidor realmente sirve las fotos)
                String rutaDespliegue = request.getServletContext().getRealPath("/recursos/imgs");
                
                // Ruta de origen (Donde el usuario quiere ver los archivos en su carpeta personal)
                String rutaOrigen = "/home/orion/Server/html/Java/JavaFundamentos/Tienda/src/main/webapp/recursos/imgs";
                
                System.out.println("Ruta de despliegue: " + rutaDespliegue);
                System.out.println("Ruta de origen: " + rutaOrigen);

                // --- PASO 1: Guardar en la ruta de despliegue (ESENCIAL para que la web funcione) ---
                if (rutaDespliegue != null) {
                    try {
                        Files.createDirectories(Paths.get(rutaDespliegue));
                        String rutaCompletaDespliegue = rutaDespliegue + "/" + nombreImagen;
                        
                        try (java.io.InputStream input = filePart.getInputStream()) {
                            Files.copy(input, Paths.get(rutaCompletaDespliegue), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                        }
                        System.out.println("✓ Imagen guardada en DESPLIEGUE: " + rutaCompletaDespliegue);
                        
                        // --- PASO 2: Intentar copiar a la ruta de origen (OPCIONAL para el usuario) ---
                        // Lo hacemos solo si el paso 1 funcionó y la ruta de origen es distinta
                        if (!rutaOrigen.equals(rutaDespliegue)) {
                            try {
                                Files.createDirectories(Paths.get(rutaOrigen));
                                String rutaCompletaOrigen = rutaOrigen + "/" + nombreImagen;
                                Files.copy(Paths.get(rutaCompletaDespliegue), Paths.get(rutaCompletaOrigen), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                                System.out.println("✓ Imagen copiada a ORIGEN: " + rutaCompletaOrigen);
                            } catch (Exception e) {
                                // Si falla el guardado en la carpeta personal, solo avisamos por consola
                                // No bloqueamos al usuario ni mostramos error en la web
                                System.err.println("Aviso: No se pudo copiar a la carpeta de origen (posible problema de permisos): " + e.getMessage());
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Error crítico al guardar en despliegue: " + e.getMessage());
                        throw new IOException("Error al guardar la imagen en el servidor: " + e.getMessage());
                    }
                } else {
                    throw new IOException("No se pudo determinar la ruta de despliegue del servidor.");
                }

            } else {
                System.out.println("ERROR: No se recibió archivo o está vacío");
                request.setAttribute("mensaje", "Error: Debe subir una imagen");
                request.setAttribute("tipo", "error");
                
                // Cargar catálogo antes de ir a la vista
                cargarDatosVista(request);
                request.getRequestDispatcher("/WEB-INF/vistas/catalogo.jsp").forward(request, response);
                return;
            }

            // Crear objeto Producto (sin ID, la BD lo asignará)
            Producto producto = new Producto(0, nombre, precio, categoria, descripcion, nombreImagen);
            System.out.println("Objeto Producto creado: " + producto);

            // Insertar en la BD
            System.out.println("Intentando insertar en BD...");
            boolean insertado = ProductoDAO.insertarProducto(producto);
            System.out.println("Resultado de inserción: " + insertado);

            if (insertado) {
                System.out.println("Producto insertado exitosamente");
                request.setAttribute("mensaje", "Producto Agregado Correctamente");
                request.setAttribute("tipo", "exito");
                request.setAttribute("producto", producto);
                request.setAttribute("nombreProducto", nombre);
                request.setAttribute("precioProducto", precio);
            } else {
                System.out.println("ERROR: No se pudo insertar el producto en la BD");
                request.setAttribute("mensaje", "Error al guardar en la base de datos");
                request.setAttribute("tipo", "error");
            }

        } catch (NumberFormatException e) {
            System.out.println("ERROR: Conversión de número - " + e.getMessage());
            request.setAttribute("mensaje", "Error: El precio debe ser un número válido");
            request.setAttribute("tipo", "error");
        } catch (Exception e) {
            System.err.println("ERROR General: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("mensaje", "Error inesperado: " + e.getMessage());
            request.setAttribute("tipo", "error");
        }

        // Cargar catálogo antes de ir a la vista
        cargarDatosVista(request);

        // Usar forward para mantener los atributos
        System.out.println("Haciendo forward a catalogo.jsp");
        request.getRequestDispatcher("/WEB-INF/vistas/catalogo.jsp")
               .forward(request, response);
    }

    /**
     * Carga los datos necesarios para la vista del catálogo
     */
    private void cargarDatosVista(HttpServletRequest request) {
        java.util.List<Producto> catalogo = ProductoDAO.obtenerCatalogo();
        java.util.List<String> categorias = ProductoDAO.obtenerCategorias();
        request.setAttribute("catalogo", catalogo);
        request.setAttribute("categorias", categorias);
        request.setAttribute("totalProductos", catalogo.size());
        request.setAttribute("titulo", "Catálogo de Productos");
    }

    /**
     * Valida que la extensión de la imagen sea permitida
     */
    private boolean isValidImageExtension(String fileName) {
        String fileName_lower = fileName.toLowerCase();
        return fileName_lower.endsWith(".webp") || 
               fileName_lower.endsWith(".jpg") || 
               fileName_lower.endsWith(".jpeg") || 
               fileName_lower.endsWith(".png");
    }
}
