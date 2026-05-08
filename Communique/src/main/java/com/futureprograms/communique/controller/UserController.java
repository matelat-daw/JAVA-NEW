package com.futureprograms.communique.controller;

import com.futureprograms.communique.dto.RegisterUserDTO;
import com.futureprograms.communique.dto.UserResponseDTO;
import com.futureprograms.communique.model.User;
import com.futureprograms.communique.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar usuarios
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
@Slf4j
public class UserController {
    
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    /**
     * Registra un nuevo usuario en el servidor
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@RequestBody RegisterUserDTO dto, HttpServletRequest request) {
        log.info("Registrando usuario: {}", dto.getUsername());
        
        // Obtener la IP real del cliente
        String remoteAddr = request.getHeader("X-Forwarded-For");
        if (remoteAddr == null || remoteAddr.isEmpty()) {
            remoteAddr = request.getRemoteAddr();
        }
        
        User user = userService.registerUser(
                dto.getUsername(),
                dto.getHostname(),
                remoteAddr
        );
        return ResponseEntity.ok(mapToDTO(user));
    }
    
    /**
     * Obtiene todos los usuarios en línea
     */
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllOnlineUsers() {
        List<User> users = userService.getAllOnlineUsers();
        return ResponseEntity.ok(users.stream().map(this::mapToDTO).toList());
    }
    
    /**
     * Obtiene un usuario específico por ID
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable String userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mapToDTO(user));
    }
    
    /**
     * Obtiene el conteo de usuarios en línea
     */
    @GetMapping("/count/online")
    public ResponseEntity<Integer> getOnlineUserCount() {
        return ResponseEntity.ok(userService.getOnlineUserCount());
    }
    
    /**
     * Desconecta un usuario
     */
    @PostMapping("/{userId}/disconnect")
    public ResponseEntity<Void> disconnectUser(@PathVariable String userId) {
        log.info("Desconectando usuario: {}", userId);
        userService.disconnectUser(userId);
        return ResponseEntity.ok().build();
    }
    
    /**
     * Actualiza el heartbeat de un usuario
     */
    @PostMapping("/{userId}/heartbeat")
    public ResponseEntity<Void> updateHeartbeat(@PathVariable String userId) {
        userService.updateHeartbeat(userId);
        return ResponseEntity.ok().build();
    }
    
    /**
     * Mapea un User a UserResponseDTO
     */
    private UserResponseDTO mapToDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .hostname(user.getHostname())
                .ipAddress(user.getIpAddress())
                .online(user.isOnline())
                .socketId(user.getSocketId())
                .build();
    }
}
