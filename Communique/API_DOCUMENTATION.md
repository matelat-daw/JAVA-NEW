# Communique API - API de Comunicación en Tiempo Real

## Descripción
API REST + WebSocket para crear una red de comunicación centralizada donde todos los equipos pueden conectarse y comunicarse con audio en tiempo real.

## Endpoints REST

### 1. Registrar Usuario
```
POST /api/users/register
Content-Type: application/json

{
  "username": "nombre_usuario",
  "hostname": "PC-nombre",
  "ipAddress": "192.168.1.100"
}

Respuesta:
{
  "id": "uuid-usuario",
  "username": "nombre_usuario",
  "hostname": "PC-nombre",
  "ipAddress": "192.168.1.100",
  "online": true,
  "socketId": "socket-id"
}
```

### 2. Obtener Todos los Usuarios En Línea
```
GET /api/users

Respuesta:
[
  {
    "id": "uuid-usuario",
    "username": "usuario1",
    "hostname": "PC-1",
    "ipAddress": "192.168.1.100",
    "online": true,
    "socketId": "socket-id"
  },
  ...
]
```

### 3. Obtener Usuario Específico
```
GET /api/users/{userId}

Respuesta:
{
  "id": "uuid-usuario",
  "username": "usuario1",
  "hostname": "PC-1",
  "ipAddress": "192.168.1.100",
  "online": true,
  "socketId": "socket-id"
}
```

### 4. Obtener Conteo de Usuarios En Línea
```
GET /api/users/count/online

Respuesta:
5
```

### 5. Desconectar Usuario
```
POST /api/users/{userId}/disconnect

Respuesta: 200 OK
```

### 6. Actualizar Heartbeat (Mantener Usuario Vivo)
```
POST /api/users/{userId}/heartbeat

Respuesta: 200 OK
```

## WebSocket - Señalización

Conéctate a: `ws://localhost:8080/ws/signal`

### Mensajes

#### 1. Registro en WebSocket
```json
{
  "type": "register",
  "userId": "uuid-usuario"
}
```

Respuesta:
```json
{
  "type": "users-online",
  "users": [...]
}
```

#### 2. Enviar Oferta WebRTC (Offer)
```json
{
  "type": "offer",
  "targetUserId": "uuid-usuario-destino",
  "offer": {
    "type": "offer",
    "sdp": "..."
  }
}
```

#### 3. Enviar Respuesta WebRTC (Answer)
```json
{
  "type": "answer",
  "targetUserId": "uuid-usuario-destino",
  "answer": {
    "type": "answer",
    "sdp": "..."
  }
}
```

#### 4. Enviar Candidato ICE
```json
{
  "type": "ice-candidate",
  "targetUserId": "uuid-usuario-destino",
  "candidate": {
    "candidate": "...",
    "sdpMLineIndex": 0,
    "sdpMid": "..."
  }
}
```

#### 5. Keep-Alive (Ping)
```json
{
  "type": "ping"
}
```

Respuesta:
```json
{
  "type": "pong"
}
```

## Flujo de Conexión

1. **Registro HTTP**: El cliente hace POST a `/api/users/register` con su información
2. **Conexión WebSocket**: El cliente conecta a `ws://localhost:8080/ws/signal`
3. **Registro WebSocket**: Envía mensaje de registro con su userId
4. **Obtener Usuarios**: Recibe lista de usuarios en línea
5. **Señalización WebRTC**: Intercambia ofertas, respuestas y candidatos ICE
6. **Conexión P2P**: Una vez completada la señalización, se establece conexión P2P directa para audio
7. **Comunicación**: El audio fluye directamente entre clientes (P2P)

## Compilar y Ejecutar

```bash
# Compilar
mvn clean install

# Ejecutar
mvn spring-boot:run

# El servidor estará disponible en: http://localhost:8080
```

## Tecnologías Utilizadas

- **Spring Boot 3.2.0** - Framework web
- **WebSocket** - Comunicación en tiempo real
- **WebRTC** - Comunicación P2P de audio
- **Socket.IO** - Soporte para fallback y características avanzadas
- **Lombok** - Reducción de código boilerplate
- **Maven** - Gestor de dependencias

## Próximo Paso: Frontend

El frontend se conectará a esta API y manejará:
- Captura de audio del micrófono
- Establecimiento de conexiones WebRTC
- Interfaz para ver usuarios en línea y iniciar llamadas
