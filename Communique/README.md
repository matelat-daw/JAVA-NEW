# Communique API

API de comunicación en tiempo real con audio para redes locales.

## Características

✅ **API REST** para gestión de usuarios  
✅ **WebSocket** para señalización en tiempo real  
✅ **WebRTC** para comunicación P2P de audio  
✅ **Soporte multi-cliente** - múltiples usuarios simultáneamente  
✅ **Arquitectura centralizada** - servidor gestiona todas las conexiones  

## Requisitos

- Java 17 o superior
- Maven 3.8.1 o superior
- Puerto 8080 disponible

## Instalación y Ejecución

```bash
# Clonar o navegar al proyecto
cd Communique

# Compilar el proyecto
mvn clean install

# Ejecutar la aplicación
mvn spring-boot:run
```

La API estará disponible en: `http://localhost:8080`

## Estructura del Proyecto

```
src/
├── main/
│   ├── java/com/futureprograms/communique/
│   │   ├── CommuniqueApplication.java       # Clase principal
│   │   ├── controller/
│   │   │   └── UserController.java          # Endpoints REST
│   │   ├── service/
│   │   │   └── UserService.java             # Lógica de negocio
│   │   ├── model/
│   │   │   └── User.java                    # Modelo de usuario
│   │   ├── dto/
│   │   │   ├── RegisterUserDTO.java         # DTO registro
│   │   │   └── UserResponseDTO.java         # DTO respuesta
│   │   ├── config/
│   │   │   ├── CorsConfig.java              # Config CORS
│   │   │   └── WebSocketConfig.java         # Config WebSocket
│   │   └── websocket/
│   │       └── SignalingWebSocketHandler.java # Manejador de señalización
│   └── resources/
│       └── application.properties            # Configuración
├── test/                                     # Tests unitarios
├── pom.xml                                  # Dependencias Maven
└── API_DOCUMENTATION.md                     # Documentación de endpoints
```

## Flujo de Uso

1. Cliente hace **POST /api/users/register** para registrarse
2. Cliente conecta a **ws://localhost:8080/ws/signal**
3. Cliente envía mensaje de registro con su userId
4. Servidor responde con lista de usuarios en línea
5. Clientes intercambian **ofertas y respuestas WebRTC**
6. Se establece **conexión P2P** directa para audio

## Dependencias Principales

- **Spring Boot 3.2.0** - Framework web
- **Spring WebSocket** - Soporte WebSocket
- **Socket.IO 4.1.4** - Comunicación en tiempo real
- **Lombok** - Reducción de código boilerplate
- **Jackson** - Procesamiento de JSON

## API REST

- `POST /api/users/register` - Registrar nuevo usuario
- `GET /api/users` - Obtener todos los usuarios en línea
- `GET /api/users/{userId}` - Obtener usuario específico
- `GET /api/users/count/online` - Contar usuarios en línea
- `POST /api/users/{userId}/disconnect` - Desconectar usuario
- `POST /api/users/{userId}/heartbeat` - Actualizar heartbeat

Para más detalles, ver [API_DOCUMENTATION.md](API_DOCUMENTATION.md)

## WebSocket Events

- `register` - Registrar en WebSocket
- `offer` - Enviar oferta WebRTC
- `answer` - Enviar respuesta WebRTC
- `ice-candidate` - Enviar candidato ICE
- `ping/pong` - Keep-alive

## Próximos Pasos

1. ✅ **API Backend** (completado)
2. ⏳ **Frontend Web** - Cliente JavaScript para conectarse y manejar WebRTC
3. ⏳ **Cliente Escritorio** - Aplicación de escritorio con captura de audio

## Autor

Future Programs

## Licencia

MIT
