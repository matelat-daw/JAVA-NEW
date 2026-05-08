# Prueba App - Frontend

## Estructura del Proyecto

```
Prueba/
├── index.html              # Página principal (raíz)
├── frontend/
│   ├── index.html          # Página principal del frontend
│   ├── app.js              # Lógica principal de la aplicación
│   └── styles.css          # Estilos personalizados
```

## Características

- **Login**: Iniciar sesión con username y password
- **Register**: Registrarse con username, email y password
- **Profile**: Ver información del usuario autenticado
- **Admin**: Panel de administrador (solo para usuarios con rol ADMIN)
- **Logout**: Cerrar sesión

## Uso

### Opción 1: Abrir directamente en el navegador

1. Abre el archivo `frontend/index.html` en tu navegador

### Opción 2: Servir desde el servidor Spring Boot

1. Asegúrate de que el servidor Spring Boot esté corriendo en `http://localhost:8088`
2. Abre `http://localhost:8088/index.html` en tu navegador

## Endpoints de la API

- `POST /api/auth/signin`: Iniciar sesión
- `POST /api/auth/signup`: Registrarse
- `GET /api/test/all`: Endpoint público
- `GET /api/test/user`: Endpoint para usuarios autenticados

## Notas

- El frontend usa `localStorage` para guardar el token de autenticación y la información del usuario
- El token se envía en el header `Authorization` como `Bearer <token>`
- Los roles se guardan en formato `ROLE_ADMIN`, `ROLE_USER`, etc.
