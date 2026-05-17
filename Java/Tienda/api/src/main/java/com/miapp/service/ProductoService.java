package com.miapp.service;

import com.miapp.model.Producto;
import com.miapp.repository.ProductoRepository;
import com.miapp.util.ImagesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> obtenerCatalogo() {
        return productoRepository.findAll();
    }

    public Optional<Producto> obtenerProductoPorId(Integer id) {
        return productoRepository.findById(id);
    }

    public Producto guardarProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    public void eliminarProducto(Integer id) {
        Optional<Producto> producto = productoRepository.findById(id);
        if (producto.isPresent() && producto.get().getImagen() != null) {
            ImagesUtil.eliminarImagen(producto.get().getImagen());
        }
        productoRepository.deleteById(id);
    }

    public List<Producto> buscarPorCategoria(String categoria) {
        return productoRepository.findByCategoria(categoria);
    }

    public List<Producto> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public List<String> obtenerCategorias() {
        return productoRepository.findAllCategoriaNombres();
    }

        public Producto guardarProductoConImagen(Producto producto, MultipartFile imagenFile) throws Exception {
        String imagenAnterior = null;
        
        // CORRECCIÓN: Validar que el objeto ID exista y sea mayor que cero
        if ((Integer)producto.getId() != null && producto.getId() > 0) {
            Optional<Producto> productoExistente = productoRepository.findById(producto.getId());
            if (productoExistente.isPresent()) {
                imagenAnterior = productoExistente.get().getImagen();
            }
        }

        if (imagenFile != null && !imagenFile.isEmpty()) {
            if (imagenAnterior != null) {
                ImagesUtil.eliminarImagen(imagenAnterior);
            }
            String nombreImagen = ImagesUtil.guardarImagen(imagenFile);
            producto.setImagen(nombreImagen);
        } else if ((Integer)producto.getId() != null && producto.getId() > 0 && imagenAnterior != null) {
            producto.setImagen(imagenAnterior);
        }

        return productoRepository.save(producto);
    }
}
