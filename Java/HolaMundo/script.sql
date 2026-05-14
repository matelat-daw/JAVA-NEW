-- Script SQL para la tienda TechStore
-- Base de datos para gestionar productos

CREATE DATABASE IF NOT EXISTS techstore;
USE techstore;

-- Crear la tabla de categorías
CREATE TABLE IF NOT EXISTS categorias (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(255)
);

-- Crear la tabla de productos
CREATE TABLE IF NOT EXISTS productos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(150) NOT NULL,
    precio DECIMAL(10, 2) NOT NULL,
    categoria VARCHAR(100) NOT NULL,
    descripcion VARCHAR(500),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (categoria) REFERENCES categorias(nombre)
);

-- Insertar categorías
INSERT INTO categorias (nombre, descripcion) VALUES
('Periféricos', 'Accesorios y dispositivos periféricos para computadora'),
('Pantallas', 'Monitores y pantallas para computadora'),
('Audio', 'Dispositivos de audio y sonido'),
('Cables y Adaptadores', 'Cables, adaptadores y conectores diversos');

-- Insertar productos
INSERT INTO productos (id, nombre, precio, categoria, descripcion) VALUES
(1, 'Teclado mecánico RGB', 89.99, 'Periféricos', 'Teclado cómodo para escribir y jugar.'),
(2, 'Monitor 27 pulgadas 144Hz', 349.00, 'Pantallas', 'Monitor grande y fluido para trabajar y jugar.'),
(3, 'Ratón gaming 12000 DPI', 45.50, 'Periféricos', 'Ratón preciso con varios botones configurables.'),
(4, 'Auriculares con micrófono', 69.99, 'Audio', 'Auriculares adecuados para videollamadas y juegos.');

-- Consultas útiles

-- Ver todos los productos
SELECT * FROM productos;

-- Ver productos por categoría
SELECT * FROM productos WHERE categoria = 'Periféricos';

-- Ver el producto más caro
SELECT * FROM productos ORDER BY precio DESC LIMIT 1;

-- Contar total de productos
SELECT COUNT(*) as total_productos FROM productos;

-- Contar categorías disponibles
SELECT COUNT(DISTINCT categoria) as total_categorias FROM productos;

-- Ver estadísticas por categoría
SELECT categoria, COUNT(*) as cantidad, AVG(precio) as precio_promedio, MAX(precio) as precio_maximo
FROM productos
GROUP BY categoria
ORDER BY cantidad DESC;

-- Ver precio total de inventario (si tuvieras stock)
SELECT SUM(precio) as valor_total_inventario FROM productos;

-- Crear tabla de contactos (opcional)
CREATE TABLE IF NOT EXISTS contactos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    asunto VARCHAR(200) NOT NULL,
    mensaje TEXT NOT NULL,
    fecha_envio TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Ver todos los contactos recibidos
SELECT * FROM contactos ORDER BY fecha_envio DESC;
