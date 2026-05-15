# 🏪 Tienda Tech - Spring Boot Edition

Aplicación de tienda online moderna basada en **Spring Boot**, **Spring MVC**, **JPA/Hibernate** y **Thymeleaf**.

## 🚀 Quick Start

### Requisitos
- Java 21+
- Maven 3.8+
- MariaDB/MySQL

### Ejecutar
```bash
# Compilar
mvn clean compile

# Ejecutar
mvn spring-boot:run

# Acceder a http://localhost:8080
```

## 📋 Funcionalidades

✅ **Catálogo de Productos**: Ver, crear, editar y eliminar productos
✅ **Gestión de Categorías**: Filtrar por categoría
✅ **Formulario de Contacto**: Recibir mensajes de clientes
✅ **Estadísticas**: Resumen de precios y productos
✅ **Interfaz Responsiva**: Bootstrap 5.3.8
✅ **Base de Datos**: MariaDB con JPA/Hibernate

## 🏗️ Arquitectura

**Patrón MVC con Spring Boot**

```
        HTTP Request
             ↓
       [Controller]
             ↓
       [Service Layer]
             ↓
      [Repository/JPA]
             ↓
      [Database]
```

## 📁 Estructura

```
src/main/java/com/miapp/
├── TiendaApplication.java
├── controller/
├── service/
├── repository/
└── model/
```

## 🗄️ Configuración BD

Edita `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mariadb://localhost:3306/techstore
spring.datasource.username=root
spring.datasource.password=Anubis@68
```

## 🎯 Rutas Principales

- `GET /` - Inicio
- `GET /tienda/catalogo` - Catálogo
- `GET /tienda/nuevo` - Agregar producto
- `POST /tienda/guardar` - Guardar producto
- `GET /tienda/contacto` - Contacto
- `GET /tienda/resumen` - Estadísticas

## 📝 Versión

**2.0** - Spring Boot Edition (15 de mayo de 2026)
