# Configuración de aplicación para diferentes ambientes

## Desarrollo (application.properties - ACTUAL)
spring.datasource.url=jdbc:mariadb://localhost:3306/techstore
spring.datasource.username=root
spring.datasource.password=Anubis@68
spring.jpa.hibernate.ddl-auto=validate
logging.level.root=INFO
logging.level.com.miapp=DEBUG

## Producción (application-prod.properties)
# spring.datasource.url=jdbc:mariadb://prod-db-server:3306/techstore
# spring.datasource.username=${DB_USERNAME}
# spring.datasource.password=${DB_PASSWORD}
# spring.jpa.hibernate.ddl-auto=validate
# logging.level.root=WARN
# spring.thymeleaf.cache=true

## Testing (application-test.properties)
# spring.datasource.url=jdbc:h2:mem:testdb
# spring.datasource.driver-class-name=org.h2.Driver
# spring.jpa.hibernate.ddl-auto=create-drop
# logging.level.root=INFO

# Para usar un perfil específico al ejecutar:
# mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"
# O en la aplicación:
# java -jar target/Tienda-2.0.jar --spring.profiles.active=prod
