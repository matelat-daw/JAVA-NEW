package com.miapp;

/**
 * Clase utilitaria para construir URLs de imágenes de productos
 */
public class ImagesUtil {

    // Ruta base de las imágenes en el servidor web
    private static final String IMAGES_PATH = "/recursos/imgs/";

    /**
     * Obtiene la URL completa de una imagen a partir de su nombre
     *
     * @param nombreImagen Nombre del archivo de imagen (ej: "combo.webp")
     * @return URL completa de la imagen
     */
    public static String getImagenURL(String nombreImagen) {
        if (nombreImagen == null || nombreImagen.isEmpty()) {
            return getImagenURL("placeholder.webp");
        }
        return IMAGES_PATH + nombreImagen;
    }

    /**
     * Obtiene la ruta relativa para usar en JSP con pageContext.request.contextPath
     *
     * @param contextPath El contextPath de la aplicación
     * @param nombreImagen Nombre del archivo de imagen
     * @return Ruta completa con contextPath
     */
    public static String getImagenURLConContextPath(String contextPath, String nombreImagen) {
        if (nombreImagen == null || nombreImagen.isEmpty()) {
            nombreImagen = "placeholder.webp";
        }
        return contextPath + "/recursos/imgs/" + nombreImagen;
    }

    /**
     * Valida que el nombre de la imagen tenga una extensión válida
     *
     * @param nombreImagen Nombre del archivo a validar
     * @return true si la extensión es válida
     */
    public static boolean esImagenValida(String nombreImagen) {
        if (nombreImagen == null || nombreImagen.isEmpty()) {
            return false;
        }
        return nombreImagen.endsWith(".webp");
    }
}