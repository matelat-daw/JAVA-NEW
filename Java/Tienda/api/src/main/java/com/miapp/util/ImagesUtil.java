package com.miapp.util;

import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class ImagesUtil {
    private static final String IMAGES_PATH = "/imgs/";
    private static final String[] ALLOWED_EXTENSIONS = {"jpg", "jpeg", "png", "gif", "webp"};
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    public static Path getUploadDirPath() {
        String configured = Optional.ofNullable(System.getProperty("tienda.images.dir"))
                .filter(s -> !s.isBlank())
                .orElseGet(() -> Optional.ofNullable(System.getenv("TIENDA_IMAGES_DIR"))
                        .filter(s -> !s.isBlank())
                        .orElseGet(() -> {
                            String catalinaBase = System.getProperty("catalina.base");
                            if (catalinaBase != null && !catalinaBase.isBlank()) {
                                return Paths.get(catalinaBase, "tienda-api", "imgs").toString();
                            }
                            return Paths.get(System.getProperty("user.home"), "tienda-api", "imgs").toString();
                        }));

        return Paths.get(configured);
    }

    public static String getImagenURL(String nombreImagen) {
        if (nombreImagen == null || nombreImagen.isEmpty()) {
            return getImagenURL("placeholder.webp");
        }
        return IMAGES_PATH + nombreImagen;
    }

    public static boolean esImagenValida(String nombreImagen) {
        if (nombreImagen == null || nombreImagen.isEmpty()) {
            return false;
        }
        String extension = getExtension(nombreImagen).toLowerCase();
        for (String ext : ALLOWED_EXTENSIONS) {
            if (ext.equals(extension)) {
                return true;
            }
        }
        return false;
    }

    public static String guardarImagen(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("El archivo de imagen no puede estar vacío");
        if (file.getSize() > MAX_FILE_SIZE) throw new IllegalArgumentException("El archivo excede el tamaño máximo permitido (5MB)");
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) throw new IllegalArgumentException("El nombre del archivo es inválido");

        String extension = getExtension(originalFilename).toLowerCase();
        if (!esImagenValida(originalFilename)) throw new IllegalArgumentException("Extensión de archivo no permitida.");

        Path uploadDirPath = getUploadDirPath();
        Files.createDirectories(uploadDirPath);

        String nombreSanitizado = originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_");
        if (nombreSanitizado.contains(".")) {
            nombreSanitizado = nombreSanitizado.substring(0, nombreSanitizado.lastIndexOf("."));
        }

        String nombreArchivo = nombreSanitizado + "." + extension;
        Path filepath = uploadDirPath.resolve(nombreArchivo);
        int contador = 1;
        while (Files.exists(filepath)) {
            nombreArchivo = nombreSanitizado + "_" + contador + "." + extension;
            filepath = uploadDirPath.resolve(nombreArchivo);
            contador++;
        }

        Files.write(filepath, file.getBytes());
        return nombreArchivo;
    }

    private static String getExtension(String nombreArchivo) {
        int lastDot = nombreArchivo.lastIndexOf('.');
        if (lastDot > 0) return nombreArchivo.substring(lastDot + 1);
        return "";
    }

    public static boolean eliminarImagen(String nombreImagen) {
        if (nombreImagen == null || nombreImagen.isEmpty() || nombreImagen.equals("placeholder.webp")) return false;
        try {
            Path filepath = getUploadDirPath().resolve(nombreImagen);
            Files.deleteIfExists(filepath);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
