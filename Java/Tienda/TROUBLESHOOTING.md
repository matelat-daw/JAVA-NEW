# 🐛 Guía de Resolución de Problemas

## Problemas Comunes

### 1. **Error de Conexión a Base de Datos**
```
java.sql.SQLException: Unable to connect to datasource
```

**Solución:**
- Verifica que MariaDB está ejecutándose: `mysql -u root -p`
- Confirma credenciales en `application.properties`
- Asegúrate que la base de datos `techstore` existe
- Ejecuta el script SQL: `sql/migration.sql`

### 2. **Error: Table 'techstore.producto' doesn't exist**
```
Caused by: com.mysql.cj.jdbc.exceptions.MySQLSyntaxErrorException: Table 'techstore.producto' doesn't exist
```

**Solución:**
```bash
# Conéctate a MariaDB
mysql -u root -p techstore < sql/migration.sql

# O ejecuta manualmente en el cliente MySQL
mysql> USE techstore;
mysql> SOURCE sql/migration.sql;
```

### 3. **Error: ClassNotFoundException for org.mariadb.jdbc.Driver**
```
ClassNotFoundException: org.mariadb.jdbc.Driver
```

**Solución:**
```bash
# Descarga las dependencias
mvn clean install

# Si persiste, verifica pom.xml tenga:
# <dependency>
#     <groupId>org.mariadb.jdbc</groupId>
#     <artifactId>mariadb-java-client</artifactId>
# </dependency>
```

### 4. **Puerto 8080 ya está en uso**
```
Caused by: java.net.BindException: Address already in use
```

**Solución:**
- Cambia el puerto en `application.properties`:
  ```properties
  server.port=8081
  ```
- O mata el proceso anterior:
  ```bash
  # En Linux/Mac
  lsof -ti:8080 | xargs kill -9
  
  # En Windows
  netstat -ano | findstr :8080
  taskkill /PID <PID> /F
  ```

### 5. **Error: Unsupported class version (Java versión incorrecta)**
```
java.lang.UnsupportedClassVersionError: com/miapp/TiendaApplication has unsupported version 65.0
```

**Solución:**
```bash
# Verifica tu versión de Java
java -version

# Debe ser Java 21 o superior
# Actualiza o instala Java 21 LTS
```

### 6. **Las vistas (HTML) no se cargan (error 404)**
```
Whitelabel Error Page
This application has no explicit mapping for /error
```

**Solución:**
- Verifica que los archivos HTML están en `src/main/resources/templates/`
- Los nombres deben coincidir con los nombres en los controladores
- Reinicia la aplicación después de agregar nuevas vistas
- Asegúrate de que Spring Boot DevTools está habilitado

### 7. **Error en Thymeleaf: "Cannot find template"**
```
org.thymeleaf.exceptions.TemplateInputException: An error happened during template parsing
```

**Solución:**
- Verifica el nombre exacto del archivo en `application.properties`:
  ```properties
  spring.thymeleaf.prefix=classpath:/templates/
  ```
- Reinicia la aplicación
- Limpia el caché: `mvn clean`

### 8. **Imágenes no se cargan (404 en recursos)**
```
GET /recursos/imgs/producto.jpg HTTP/1.1" 404
```

**Solución:**
- Coloca las imágenes en `src/main/resources/static/recursos/imgs/`
- Usa la ruta correcta en Thymeleaf:
  ```html
  <img th:src="@{/recursos/imgs/producto.jpg}">
  ```
- Reinicia la aplicación

### 9. **Error de validación JPA/Hibernate**
```
Caused by: org.hibernate.HibernateException: Missing column in table
```

**Solución:**
- Cambia el DDL auto mode si necesitas actualizar la BD:
  ```properties
  spring.jpa.hibernate.ddl-auto=update
  ```
- O crea manualmente las columnas faltantes
- Después, vuelve a cambiar a:
  ```properties
  spring.jpa.hibernate.ddl-auto=validate
  ```

### 10. **La aplicación inicia pero no responde**
```
Tomcat started on port 8080
```

**Solución:**
- Espera a que Spring Boot complete la inicialización
- Busca en los logs: `Started TiendaApplication`
- Verifica que no hay excepciones (ERROR, WARN)
- Intenta acceder a `http://localhost:8080/`

## 🔍 Debugging

### Habilitar logs detallados
En `application.properties`:
```properties
logging.level.com.miapp=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

### Ver SQL ejecutado
```properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

### Verificar propiedades
```bash
# Muestra todas las propiedades configuradas
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Ddebug"
```

## 📞 Soporte

Si el problema persiste:
1. Revisa los logs completos en la consola
2. Ejecuta `mvn clean install`
3. Asegúrate que sigues los pasos de `README.md`
4. Verifica que todos los requisitos están instalados

---
**Última actualización**: 15 de mayo de 2026
