package com.miapp.controller;

import com.miapp.model.Producto;
import com.miapp.service.ProductoService;
import com.miapp.util.ImagesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ProductApiController {

    @Autowired
    private ProductoService productoService;

    @GetMapping("/products")
    public List<Producto> list() {
        return productoService.obtenerCatalogo();
    }

    @GetMapping("/categorias")
    public List<String> categorias() {
        return productoService.obtenerCategorias();
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Producto> get(@PathVariable int id) {
        Optional<Producto> p = productoService.obtenerProductoPorId(id);
        return p.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/products")
    public ResponseEntity<Producto> create(@RequestBody Producto producto) {
        Producto saved = productoService.guardarProducto(producto);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<Producto> update(@PathVariable int id, @RequestBody Producto producto) {
        Optional<Producto> existing = productoService.obtenerProductoPorId(id);
        if (!existing.isPresent()) return ResponseEntity.notFound().build();
        Producto target = existing.get();
        target.setNombre(producto.getNombre());
        target.setPrecio(producto.getPrecio());
        target.setCategoria(producto.getCategoria());
        target.setDescripcion(producto.getDescripcion());
        if (producto.getImagen() != null && !producto.getImagen().isBlank()) {
            target.setImagen(producto.getImagen());
        }
        Producto saved = productoService.guardarProducto(target);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        Optional<Producto> existing = productoService.obtenerProductoPorId(id);
        if (!existing.isPresent()) return ResponseEntity.notFound().build();
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/products/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Producto> uploadImage(@PathVariable int id, @RequestPart("imagen") MultipartFile imagen) {
        Optional<Producto> existing = productoService.obtenerProductoPorId(id);
        if (!existing.isPresent()) return ResponseEntity.notFound().build();
        Producto producto = existing.get();
        try {
            Producto saved = productoService.guardarProductoConImagen(producto, imagen);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/imgs/{filename:.+}")
    public ResponseEntity<byte[]> getImage(@PathVariable String filename) {
        try {
            Path path = ImagesUtil.getUploadDirPath().resolve(filename);
            if (!Files.exists(path)) return ResponseEntity.notFound().build();
            String contentType = Files.probeContentType(path);
            byte[] bytes = Files.readAllBytes(path);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType != null ? contentType : "application/octet-stream"));
            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
