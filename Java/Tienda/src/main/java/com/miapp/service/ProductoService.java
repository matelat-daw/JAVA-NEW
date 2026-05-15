package com.miapp.service;

import com.miapp.model.Producto;
import com.miapp.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {
    
    @Autowired
    private ProductoRepository productoRepository;
    
    public List<Producto> obtenerCatalogo() {
        return productoRepository.findAll();
    }
    
    public Optional<Producto> obtenerProductoPorId(int id) {
        return productoRepository.findById(id);
    }
    
    public Producto guardarProducto(Producto producto) {
        return productoRepository.save(producto);
    }
    
    public void eliminarProducto(int id) {
        productoRepository.deleteById(id);
    }
    
    public List<Producto> buscarPorCategoria(String categoria) {
        return productoRepository.findByCategoria(categoria);
    }
    
    public List<Producto> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }
    
    public List<String> obtenerCategorias() {
        return productoRepository.findAllCategorias();
    }
}
