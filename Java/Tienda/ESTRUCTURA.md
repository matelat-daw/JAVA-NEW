# 📦 Tienda Tech - Estructura del Proyecto Reorganizado

## 🏗️ Arquitectura Limpia

```
src/main/java/com/miapp/
│
├── 📄 TiendaApplication.java           ⭐ Punto de entrada (Spring Boot)
├── 📄 PackageInfo.java                 📚 Documentación de estructura
│
├── 📁 config/                          ⚙️ Configuración
│   └── 📄 DatabaseConfig.java          Base de datos
│
├── 📁 controller/                      🎮 API / Controladores Web
│   ├── 📄 TiendaController.java        Página principal
│   ├── 📄 CatalogoController.java      Listado de productos
│   ├── 📄 ContactoController.java      Formulario de contacto
│   └── 📄 ResumenController.java       Resumen de datos
│
├── 📁 model/                           📊 Entidades (JPA/Hibernate)
│   ├── 📄 Producto.java                Productos
│   └── 📄 Contacto.java                Contactos
│
├── 📁 repository/                      🔌 Acceso a Datos (Spring Data JPA)
│   ├── 📄 ProductoRepository.java      Operaciones CRUD Productos
│   └── 📄 ContactoRepository.java      Operaciones CRUD Contactos
│
├── 📁 service/                         ⚡ Lógica de Negocio
│   ├── 📄 ProductoService.java         Servicios de Productos
│   └── 📄 ContactoService.java         Servicios de Contactos
│
├── 📁 dto/                             📨 Data Transfer Objects
│   ├── 📄 ResumenDTO.java              DTO para resumen
│   └── 📄 TiendaInfoDTO.java           DTO para info de tienda
│
└── 📁 util/                            🛠️ Utilidades
    └── 📄 ImagesUtil.java              Manejo de imágenes
```

## 🔄 Flujo de Datos (Patrón MVC + Service)

```
Request HTTP
    ↓
┌───────────────────────────┐
│   Controller              │  ← Recibe solicitud HTTP
│  (e.g., Producto...)     │
└───────────┬───────────────┘
            ↓
┌───────────────────────────┐
│   Service Layer           │  ← Lógica de negocio
│  (e.g., ProductoService) │
└───────────┬───────────────┘
            ↓
┌───────────────────────────┐
│   Repository              │  ← Acceso a datos
│  (Spring Data JPA)        │
└───────────┬───────────────┘
            ↓
┌───────────────────────────┐
│   Database                │  ← MariaDB
│  (techstore)              │
└───────────────────────────┘
            ↓
         Response HTTP
```

## 📋 Mapeo de Clases Antiguas → Nuevas

| Antiguo | Nuevo | Ubicación | Tipo |
|---------|-------|-----------|------|
| `DatabaseConnection` | `DatabaseConfig` | `config/` | Config |
| `ImagesUtil` | `ImagesUtil` | `util/` | Util (movida) |
| `ContactoBean` | `TiendaInfoDTO` | `dto/` | DTO |
| `ResumenBean` | `ResumenDTO` | `dto/` | DTO |
| `ProductoDAO` | `ProductoRepository` | `repository/` | Repository |
| `DatosTienda` | `ProductoService` | `service/` | Service |
| `*Servlet` | `*Controller` | `controller/` | Controller |
| `Producto` (raíz) | `Producto` | `model/` | Model |

## 🎯 Principios de Organización

### ✅ Separación de Responsabilidades
- **Controller**: Maneja HTTP requests/responses
- **Service**: Contiene lógica de negocio
- **Repository**: Acceso a datos (CRUD)
- **Model**: Entidades JPA
- **DTO**: Objetos para transferencia de datos
- **Config**: Configuración de la app
- **Util**: Funciones de utilidad

### ✅ Spring Boot Best Practices
- Uso de anotaciones (`@Service`, `@Repository`, `@Controller`)
- Inyección de dependencias automática
- Configuración centralizada en `application.properties`
- Spring Data JPA en lugar de JDBC manual

### ✅ Escalabilidad
- Fácil agregar nuevos módulos
- Testing simplificado con MockMvc
- Documentación clara del código

## 📖 Cómo Usar

### Importar Clases Correctamente

```java
// ✅ Nuevo (Correcto)
import com.miapp.controller.ProductoController;
import com.miapp.service.ProductoService;
import com.miapp.repository.ProductoRepository;
import com.miapp.model.Producto;
import com.miapp.dto.ResumenDTO;
import com.miapp.util.ImagesUtil;
import com.miapp.config.DatabaseConfig;

// ❌ Antiguo (NO USAR)
import com.miapp.ProductoDAO;
import com.miapp.DatosTienda;
import com.miapp.DatabaseConnection;
// ... etc
```

### Ejemplo: Crear nuevo Servicio

```java
@Service
public class MiServicio {
    
    @Autowired
    private ProductoRepository productoRepository;
    
    public List<Producto> obtenerProductos() {
        return productoRepository.findAll();
    }
}
```

### Ejemplo: Usar en Controlador

```java
@Controller
@RequestMapping("/api/productos")
public class MiController {
    
    @Autowired
    private MiServicio miServicio;
    
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("productos", miServicio.obtenerProductos());
        return "vista";
    }
}
```

## 🧪 Compilación y Testing

```bash
# Compilar
mvn clean compile

# Tests
mvn clean test

# Build JAR
mvn clean package

# Run aplicación
java -jar target/Tienda-2.0.jar
```

## 📚 Archivos de Documentación

- **REFACTORING.md** - Detalles técnicos de cambios
- **README.md** - Este archivo
- **PackageInfo.java** - Documentación en código

---

**Última actualización**: 15 de Mayo de 2026
**Versión**: 2.0 (Spring Boot 4.0.6, Java 25)
