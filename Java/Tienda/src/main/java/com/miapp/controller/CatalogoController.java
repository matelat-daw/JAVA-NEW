package com.miapp.controller;

import com.miapp.model.Producto;
import com.miapp.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
import java.util.Optional;

@Controller
@RequestMapping("/tienda")
public class CatalogoController {
    
    @Autowired
    private ProductoService productoService;
    
    @GetMapping("/catalogo")
    public String catalogo(Model model) {
        model.addAttribute("productos", productoService.obtenerCatalogo());
        model.addAttribute("categorias", productoService.obtenerCategorias());
        return "catalogo";
    }
    
    @GetMapping("/catalogo/categoria/{categoria}")
    public String catalogoPorCategoria(@PathVariable String categoria, Model model) {
        model.addAttribute("productos", productoService.buscarPorCategoria(categoria));
        model.addAttribute("categoriaSeleccionada", categoria);
        model.addAttribute("categorias", productoService.obtenerCategorias());
        return "catalogo";
    }
    
    @GetMapping("/detalle/{id}")
    public String detalleProducto(@PathVariable int id, Model model) {
        Optional<Producto> producto = productoService.obtenerProductoPorId(id);
        if (producto.isPresent()) {
            model.addAttribute("producto", producto.get());
            return "detalle-producto";
        }
        return "redirect:/tienda/catalogo";
    }
    
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("categorias", productoService.obtenerCategorias());
        return "nuevo-producto";
    }
    
    @PostMapping("/guardar")
    public String guardarProducto(
            @RequestParam("nombre") String nombre,
            @RequestParam("precio") BigDecimal precio,
            @RequestParam("categoria") String categoria,
            @RequestParam("descripcion") String descripcion,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen,
            RedirectAttributes redirectAttributes,
            Model model) {
        try {
            // Validar que se proporcionó una imagen
            if (imagen == null || imagen.isEmpty()) {
                model.addAttribute("error", "Debes seleccionar una imagen para el producto");
                model.addAttribute("nombre", nombre);
                model.addAttribute("precio", precio);
                model.addAttribute("categoria", categoria);
                model.addAttribute("descripcion", descripcion);
                model.addAttribute("categorias", productoService.obtenerCategorias());
                return "nuevo-producto";
            }
            
            // Construir objeto Producto manualmente
            Producto producto = new Producto();
            producto.setNombre(nombre);
            producto.setPrecio(precio);
            producto.setCategoria(categoria);
            producto.setDescripcion(descripcion);
            
            // Guardar con imagen
            Producto productoGuardado = productoService.guardarProductoConImagen(producto, imagen);
            
            // Usar flash attributes para pasar el mensaje de éxito y datos del producto para el modal
            redirectAttributes.addFlashAttribute("tipo", "exito");
            redirectAttributes.addFlashAttribute("mensaje", "¡Producto creado exitosamente!");
            redirectAttributes.addFlashAttribute("nombreProducto", productoGuardado.getNombre());
            redirectAttributes.addFlashAttribute("precioProducto", productoGuardado.getPrecio());
            
            return "redirect:/tienda/catalogo";
        } catch (IllegalArgumentException e) {
            // Error de validación (extensión, tamaño, etc)
            model.addAttribute("error", "Error: " + e.getMessage());
            model.addAttribute("nombre", nombre);
            model.addAttribute("precio", precio);
            model.addAttribute("categoria", categoria);
            model.addAttribute("descripcion", descripcion);
            model.addAttribute("categorias", productoService.obtenerCategorias());
            return "nuevo-producto";
        } catch (Exception e) {
            // Otros errores
            model.addAttribute("error", "Error inesperado al guardar: " + e.getMessage());
            model.addAttribute("nombre", nombre);
            model.addAttribute("precio", precio);
            model.addAttribute("categoria", categoria);
            model.addAttribute("descripcion", descripcion);
            model.addAttribute("categorias", productoService.obtenerCategorias());
            return "nuevo-producto";
        }
    }
    
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable int id, Model model) {
        Optional<Producto> producto = productoService.obtenerProductoPorId(id);
        if (producto.isPresent()) {
            model.addAttribute("producto", producto.get());
            model.addAttribute("categorias", productoService.obtenerCategorias());
            return "editar-producto";
        }
        return "redirect:/tienda/catalogo";
    }
    
    @PostMapping("/actualizar")
    public String actualizarProducto(
            @RequestParam("id") int id,
            @RequestParam("nombre") String nombre,
            @RequestParam("precio") BigDecimal precio,
            @RequestParam("categoria") String categoria,
            @RequestParam("descripcion") String descripcion,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen,
            Model model) {
        try {
            // Construir objeto Producto manualmente
            Producto producto = new Producto();
            producto.setId(id);
            producto.setNombre(nombre);
            producto.setPrecio(precio);
            producto.setCategoria(categoria);
            producto.setDescripcion(descripcion);
            
            // Guardar con imagen (imagen puede ser null/vacío)
            productoService.guardarProductoConImagen(producto, imagen);
            
            return "redirect:/tienda/detalle/" + id;
        } catch (IllegalArgumentException e) {
            // Error de validación (extensión, tamaño, etc)
            model.addAttribute("error", "Error: " + e.getMessage());
            Producto productoTemp = new Producto();
            productoTemp.setId(id);
            productoTemp.setNombre(nombre);
            productoTemp.setPrecio(precio);
            productoTemp.setCategoria(categoria);
            productoTemp.setDescripcion(descripcion);
            model.addAttribute("producto", productoTemp);
            model.addAttribute("categorias", productoService.obtenerCategorias());
            return "editar-producto";
        } catch (Exception e) {
            // Otros errores
            return "redirect:/tienda/editar/" + id;
        }
    }
    
    @GetMapping("/eliminar/{id}")
    public String eliminarProducto(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            Optional<Producto> producto = productoService.obtenerProductoPorId(id);
            if (producto.isPresent()) {
                String nombreProducto = producto.get().getNombre();
                productoService.eliminarProducto(id);
                
                // Usar sistema de modales para la eliminación
                redirectAttributes.addFlashAttribute("tipo", "eliminado");
                redirectAttributes.addFlashAttribute("mensaje", "El producto ha sido eliminado permanentemente.");
                redirectAttributes.addFlashAttribute("nombreProducto", nombreProducto);
                
                return "redirect:/tienda/catalogo";
            } else {
                redirectAttributes.addFlashAttribute("error", "Producto no encontrado");
                return "redirect:/tienda/catalogo";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar producto: " + e.getMessage());
            return "redirect:/tienda/catalogo";
        }
    }
}
