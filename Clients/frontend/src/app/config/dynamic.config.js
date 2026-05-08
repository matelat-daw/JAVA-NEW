/**
 * dynamic.config.js - Configuración dinámica para portabilidad total
 * Esta configuración se ajusta automáticamente según el entorno de ejecución
 */

(function() {
    'use strict';
    
    // Configuración por defecto (puede ser sobrescrita)
    const defaultConfig = {
        // API Configuration - Detectar automáticamente o usar localhost
        API_BASE_URL: (typeof process !== 'undefined' && process.env && process.env.API_BASE_URL) ||
                      window.API_BASE_URL ||
                      'http://localhost:8080/api',
        
        // App Configuration - Detectar automáticamente
        APP_BASE_PATH: detectAppBasePath(),
        
        // Debug mode
        DEBUG: false
    };
    
    /**
     * Detecta automáticamente el basePath basándose en la estructura de archivos
     * Funciona tanto desde servidor local como desde archivo local (file://)
     */
    function detectAppBasePath() {
        // Si ya está configurado, usarlo
        if (window.APP_BASE_PATH) {
            return window.APP_BASE_PATH;
        }
        
        // Si estamos usando un sistema de módulos o bundler
        if (typeof module !== 'undefined' && module.exports) {
            return '/';
        }
        
        // Detectar desde la URL actual
        const currentPath = window.location.pathname;
        const pathParts = currentPath.split('/').filter(Boolean);
        
        // Si estamos en el root (ej: /index.html), el basePath es /
        if (pathParts.length === 0 || pathParts[pathParts.length - 1] === 'index.html') {
            return '/';
        }
        
        // Si estamos en una subcarpeta, detectar la estructura
        // Por ejemplo: /Clients/frontend/src/app/
        // El basePath debería ser /Clients/
        const lastPart = pathParts[pathParts.length - 1];
        
        // Si el último segmento es un archivo HTML o carpeta de scripts, ir hacia atrás
        if (lastPart === 'app' || lastPart.endsWith('.html') || lastPart === 'src' || lastPart === 'frontend') {
            // Buscar la carpeta Clients o el punto donde empieza la aplicación
            for (let i = pathParts.length - 1; i >= 0; i--) {
                if (pathParts[i].toLowerCase() === 'clients') {
                    return '/' + pathParts.slice(0, i + 1).join('/') + '/';
                }
            }
        }
        
        // Fallback: usar el directorio actual
        const lastSlash = currentPath.lastIndexOf('/');
        return lastSlash >= 0 ? currentPath.substring(0, lastSlash + 1) : '/';
    }
    
    /**
     * Crea una configuración portátil
     */
    function createPortableConfig() {
        // Combinar configuración por defecto con configuración del usuario
        const config = {
            ...defaultConfig,
            API_BASE_URL: window.ENV_CONFIG?.API_BASE_URL || defaultConfig.API_BASE_URL,
            APP_BASE_PATH: window.ENV_CONFIG?.APP_BASE_PATH || detectAppBasePath(),
            DEBUG: window.ENV_CONFIG?.DEBUG || defaultConfig.DEBUG
        };
        
        return config;
    }
    
    // Crear configuración final
    const finalConfig = createPortableConfig();
    
    // Exponer globalmente
    window.PORTABLE_CONFIG = finalConfig;
    
    // Log de depuración
    if (finalConfig.DEBUG) {
        console.log('📊 Configuración portátil:', finalConfig);
    }
    
})();
