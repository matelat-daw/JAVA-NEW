package com.miapp.controller;

import com.miapp.model.Producto;
import com.miapp.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    public String guardarProducto(@ModelAttribute Producto producto) {
        productoService.guardarProducto(producto);
        return "redirect:/tienda/catalogo";
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
    public String actualizarProducto(@ModelAttribute Producto producto) {
        productoService.guardarProducto(producto);
        return "redirect:/tienda/detalle/" + producto.getId();
    }
    
    @GetMapping("/eliminar/{id}")
    public String eliminarProducto(@PathVariable int id) {
        productoService.eliminarProducto(id);
        return "redirect:/tienda/catalogo";
    }
}
