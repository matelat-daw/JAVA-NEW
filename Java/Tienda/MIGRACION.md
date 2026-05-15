# 📦 Tienda Tech - Migración a Spring Boot

## 🎯 Descripción
Esta es la versión modernizada de la aplicación **Tienda Tech**, convertida de una arquitectura tradicional JSP/Servlets a **Spring Boot** con **Spring MVC**, **JPA/Hibernate** y **Thymeleaf**.

## 🚀 Cambios principales

### Tecnologías Anteriores
- ❌ Servlets (jakarta.servlet)
- ❌ JSP con JSTL
- ❌ JDBC directo para acceso a datos
- ❌ Empaquetado como WAR

### Tecnologías Actuales (Spring Boot)
- ✅ **Spring Boot 3.3.0** - Framework web moderno
- ✅ **Spring MVC** - Controladores desacoplados
- ✅ **Spring Data JPA** - ORM con Hibernate
- ✅ **Thymeleaf** - Motor de plantillas moderno
- ✅ **Java 21** - Versión LTS más reciente
- ✅ **Empaquetado JAR** - Aplicación ejecutable

## 📁 Estructura del Proyecto

```
src/
├── main/
│   ├── java/com/miapp/
│   │   ├── TiendaApplication.java          # Clase principal
│   │   ├── model/
│   │   │   ├── Producto.java               # Entidad JPA
│   │   │   └── Contacto.java               # Entidad JPA
│   │   ├── repository/
│   │   │   ├── ProductoRepository.java     # Repositorio JPA
│   │   │   └── ContactoRepository.java     # Repositorio JPA
│   │   ├── service/
│   │   │   ├── ProductoService.java        # Lógica de negocios
│   │   │   └── ContactoService.java        # Lógica de negocios
│   │   └── controller/
│   │       ├── TiendaController.java       # Controlador de inicio
│   │       ├── CatalogoController.java     # Catálogo de productos
│   │       ├── ContactoController.java     # Formulario de contacto
│   │       └── ResumenController.java      # Resumen estadístico
│   ├── resources/
│   │   ├── application.properties          # Configuración Spring Boot
│   │   ├── templates/                      # Vistas Thymeleaf
│   │   │   ├── tienda.html
│   │   │   ├── catalogo.html
│   │   │   ├── detalle-producto.html
│   │   │   ├── nuevo-producto.html
│   │   │   ├── editar-producto.html
│   │   │   ├── contacto.html
│   │   │   └── resumen.html
│   │   └── static/
│   │       ├── css/style.css
│   │       └── recursos/imgs/              # Imágenes de productos
│   └── webapp/                             # OBSOLETO (se puede eliminar)
└── pom.xml                                 # Configuración Maven
```

## 🔧 Configuración

### application.properties
La configuración de la base de datos está en `src/main/resources/application.properties`:

```properties
# Servidor
server.port=8080

# Base de datos MariaDB
spring.datasource.url=jdbc:mariadb://localhost:3306/techstore
spring.datasource.username=root
spring.datasource.password=Anubis@68
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MariaDBDialect
```

## ✅ Requisitos Previos

- **Java 21+** instalado
- **Maven 3.8+**
- **MariaDB/MySQL** ejecutándose
- **Base de datos `techstore`** existente

## 🚀 Cómo Ejecutar

### 1. Compilar el proyecto
```bash
mvn clean compile
```

### 2. Ejecutar la aplicación
```bash
mvn spring-boot:run
```

O ejecutar el JAR generado después de hacer build:
```bash
mvn clean package
java -jar target/Tienda-2.0.jar
```

### 3. Acceder a la aplicación
Abre tu navegador en: **http://localhost:8080**

## 📝 Rutas principales

| Ruta | Descripción |
|------|------------|
| `/` | Página de inicio |
| `/tienda` | Página de inicio (alias) |
| `/tienda/catalogo` | Listado de productos |
| `/tienda/detalle/{id}` | Detalle de un producto |
| `/tienda/nuevo` | Formulario para agregar producto |
| `/tienda/guardar` | POST para guardar producto |
| `/tienda/editar/{id}` | Formulario para editar |
| `/tienda/actualizar` | POST para actualizar |
| `/tienda/eliminar/{id}` | Eliminar producto |
| `/tienda/contacto` | Formulario de contacto |
| `/tienda/contacto/enviar` | POST para enviar contacto |
| `/tienda/resumen` | Estadísticas de la tienda |

## 🎓 Cambios en la Arquitectura

### Antes (JSP/Servlets)
```
ServletX → DatabaseConnection.getConnection() → JDBC → ResultSet → JSP
```

### Ahora (Spring Boot)
```
HTTP Request → Controller → Service → Repository → JPA/Hibernate → Entity → Database
                                                                               ↓
HTTP Response ← Thymeleaf Template ← Model (Map)
```

### Clases Migradas

| Antiguo | Nuevo | Tipo |
|---------|-------|------|
| `Producto.java` | `model/Producto.java` | Entity JPA |
| `ProductoDAO.java` | `repository/ProductoRepository.java` | Interface JPA |
| `DatabaseConnection.java` | ❌ Eliminada | Spring DataSource |
| `TiendaServlet` | `controller/TiendaController` | Controller |
| `CatalogoServlet` | `controller/CatalogoController` | Controller |
| `ContactoServlet` | `controller/ContactoController` | Controller |
| `ResumenServlet` | `controller/ResumenController` | Controller |
| Archivos JSP | Archivos HTML + Thymeleaf | Template Engine |

## 🛠️ Mejoras Implementadas

✨ **Inyección de Dependencias**: Uso de `@Autowired` y `@Service`
✨ **ORM**: JPA/Hibernate en lugar de JDBC directo
✨ **Vistas Modernas**: Thymeleaf en lugar de JSP
✨ **Gestión de Transacciones**: Automática con Spring
✨ **Configuración Externa**: `application.properties`
✨ **Hot Reload**: Spring Boot DevTools
✨ **Empaquetado JAR**: Más simple y portable
✨ **Separación de Responsabilidades**: Controller, Service, Repository

## 📚 Referencias

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Thymeleaf](https://www.thymeleaf.org/)
- [MariaDB JDBC Driver](https://mariadb.com/kb/en/about-mariadb-connector-j/)

## ❓ Soporte

Para más información sobre la migración o problemas técnicos, revisa la documentación oficial de Spring Boot o contacta al equipo de desarrollo.

---
**Versión**: 2.0 (Spring Boot)
**Java**: 21
**Spring Boot**: 3.3.0
**Fecha de migración**: 15 de mayo de 2026
