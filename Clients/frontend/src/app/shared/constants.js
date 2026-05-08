/**
 * constants.js - Constantes de la aplicación
 * Configuración portátil usando variables de entorno
 */

// Cargar configuración dinámica primero
if (typeof require === 'function') {
    // Si usamos require (Node.js o bundler)
    // require('./dynamic.config');
} else {
    // Configuración dinámica ya se carga por separado en index.html
}

// Configuración final (se sobrescribe con valores de window.PORTABLE_CONFIG si existe)
const API_CONFIG = {
    BASE_URL: (window.PORTABLE_CONFIG && window.PORTABLE_CONFIG.API_BASE_URL) ||
              (window.ENV_CONFIG && window.ENV_CONFIG.API_BASE_URL) ||
              (window.API_BASE_URL) ||
              'http://localhost:8080/api',
    TIMEOUT: 5000,
    ENDPOINTS: {
        REGISTER: '/user/register',
        LOGIN: '/auth/login',
        PROFILE: '/profile',
        USERS: '/user',
        USER_BY_ID: '/user/:id',
        UPDATE_USER: '/user/:id',
        DELETE_USER: '/user/:id',
        // Endpoints de Customers (MyIkea)
        CUSTOMERS: '/myikea/customer',
        CUSTOMER_BY_ID: '/myikea/customer/:id',
        CUSTOMER_SEARCH_FIRSTNAME: '/myikea/customer/search/firstName/:firstName',
        CUSTOMER_SEARCH_LASTNAME: '/myikea/customer/search/lastName/:lastName',
        DELETE_CUSTOMER: '/myikea/customer/:id',
        UPDATE_CUSTOMER: '/myikea/customer/:id'
    }
};

// App Base Path - Usar configuración dinámica
const APP_BASE_PATH = (window.PORTABLE_CONFIG && window.PORTABLE_CONFIG.APP_BASE_PATH) ||
                      (window.ENV_CONFIG && window.ENV_CONFIG.APP_BASE_PATH) ||
                      window.APP_BASE_PATH ||
                      '/';

// Rutas de la aplicación
const ROUTES = {
    HOME: APP_BASE_PATH,
    REGISTER: `${APP_BASE_PATH}register`,
    LOGIN: `${APP_BASE_PATH}login`,
    USERS: `${APP_BASE_PATH}users`,
    DASHBOARD: `${APP_BASE_PATH}dashboard`,
    CUSTOMERS: `${APP_BASE_PATH}customers`
};

// Mensajes de la aplicación
const MESSAGES = {
    SUCCESS: {
        REGISTER: '¡User registration successful! Your account has been created.',
        LOGIN: '¡Welcome! You have logged in successfully.',
        UPDATE: 'The user has been updated correctly.',
        DELETE: 'The user has been deleted correctly.'
    },
    ERROR: {
        REGISTER_FAILED: 'Error registering the account. Please try again.',
        LOGIN_FAILED: 'Invalid credentials. Please verify your username and password.',
        SERVER_ERROR: 'Server error. Please try later.',
        VALIDATION_ERROR: 'Please complete all fields correctly.',
        EMAIL_EXISTS: 'The email address is already registered.',
        NICK_EXISTS: 'The username (nick) is already taken.',
        CONNECTION_ERROR: 'Connection error. Please verify your internet connection.'
    }
};

// Patrones de validación
const VALIDATION_PATTERNS = {
    EMAIL: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
    PHONE: /^[+]?[0-9]{7,15}$/,
    PASSWORD: /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!_.\-*])/, // Debe tener mayúscula, minúscula, número y carácter especial
    NAME: /^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]{2,255}$/,
    NICK: /^[a-zA-Z0-9_]{3,255}$/ // Alfanumérico y guión bajo, mínimo 3
};

// Exponer globalmente para la verificación de carga
window.API_CONFIG = API_CONFIG;
window.APP_BASE_PATH = APP_BASE_PATH;
window.ROUTES = ROUTES;
window.MESSAGES = MESSAGES;
window.VALIDATION_PATTERNS = VALIDATION_PATTERNS;
// Registrar que este script se ha cargado
if (typeof AppScripts !== 'undefined') AppScripts.register('constants');

