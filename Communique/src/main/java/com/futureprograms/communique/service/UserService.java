package com.futureprograms.communique.service;

import com.futureprograms.communique.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servicio para gestionar usuarios conectados en la red
 */
@Service
@Slf4j
public class UserService {
    
    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final Map<String, String> socketIdToUserId = new ConcurrentHashMap<>();
    
    /**
     * Busca un usuario por nombre
     */
    public User getUserByUsername(String username) {
        return users.values().stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Registra un nuevo usuario o reutiliza uno existente
     * Si el usuario ya existe, solo lo reactiva y actualiza sus datos
     */
    public User registerUser(String username, String hostname, String ipAddress) {
        // Buscar si el usuario ya existe
        User existingUser = getUserByUsername(username);
        
        if (existingUser != null) {
            // El usuario ya existe, solo reactívalo
            existingUser.setOnline(true);
            existingUser.setHostname(hostname);
            existingUser.setIpAddress(ipAddress);
            existingUser.setLastHeartbeat(System.currentTimeMillis());
            
            // Limpiar socket anterior si existe
            if (existingUser.getSocketId() != null) {
                socketIdToUserId.remove(existingUser.getSocketId());
                existingUser.setSocketId(null);
            }
            
            log.info("Usuario reactivado: {} ({})", username, existingUser.getId());
            return existingUser;
        }
        
        // Usuario no existe, crear uno nuevo
        String userId = UUID.randomUUID().toString();
        User newUser = User.builder()
                .id(userId)
                .username(username)
                .hostname(hostname)
                .ipAddress(ipAddress)
                .online(true)
                .lastHeartbeat(System.currentTimeMillis())
                .build();
        
        users.put(userId, newUser);
        log.info("Usuario nuevo registrado: {} ({})", username, userId);
        return newUser;
    }
    
    /**
     * Actualiza el socket ID de un usuario
     */
    public void updateUserSocket(String userId, String socketId) {
        User user = users.get(userId);
        if (user != null) {
            String oldSocketId = user.getSocketId();
            if (oldSocketId != null) {
                socketIdToUserId.remove(oldSocketId);
            }
            user.setSocketId(socketId);
            socketIdToUserId.put(socketId, userId);
            user.setLastHeartbeat(System.currentTimeMillis());
            log.debug("Socket actualizado para usuario {}: {}", userId, socketId);
        }
    }
    
    /**
     * Obtiene un usuario por ID
     */
    public User getUserById(String userId) {
        return users.get(userId);
    }
    
    /**
     * Obtiene un usuario por Socket ID
     */
    public User getUserBySocketId(String socketId) {
        String userId = socketIdToUserId.get(socketId);
        return userId != null ? users.get(userId) : null;
    }
    
    /**
     * Obtiene todos los usuarios en línea
     */
    public List<User> getAllOnlineUsers() {
        return users.values().stream()
                .filter(User::isOnline)
                .toList();
    }
    
    /**
     * Obtiene todos los usuarios excepto uno
     */
    public List<User> getOtherOnlineUsers(String excludeUserId) {
        return users.values().stream()
                .filter(User::isOnline)
                .filter(u -> !u.getId().equals(excludeUserId))
                .toList();
    }
    
    /**
     * Desconecta un usuario
     */
    public void disconnectUser(String userId) {
        User user = users.get(userId);
        if (user != null) {
            user.setOnline(false);
            if (user.getSocketId() != null) {
                socketIdToUserId.remove(user.getSocketId());
            }
            log.info("Usuario desconectado: {} ({})", user.getUsername(), userId);
        }
    }
    
    /**
     * Desconecta un usuario por Socket ID
     */
    public void disconnectUserBySocket(String socketId) {
        String userId = socketIdToUserId.get(socketId);
        if (userId != null) {
            disconnectUser(userId);
        }
    }
    
    /**
     * Actualiza el heartbeat del usuario (mantener vivo)
     */
    public void updateHeartbeat(String userId) {
        User user = users.get(userId);
        if (user != null) {
            user.setLastHeartbeat(System.currentTimeMillis());
        }
    }
    
    /**
     * Obtiene el número de usuarios conectados
     */
    public int getOnlineUserCount() {
        return (int) users.values().stream()
                .filter(User::isOnline)
                .count();
    }
    
    /**
     * Limpia usuarios que no han enviado heartbeat en mucho tiempo
     */
    public void cleanupInactiveUsers(long timeoutMs) {
        long now = System.currentTimeMillis();
        users.values().removeIf(user -> {
            if (user.isOnline() && (now - user.getLastHeartbeat()) > timeoutMs) {
                if (user.getSocketId() != null) {
                    socketIdToUserId.remove(user.getSocketId());
                }
                log.info("Usuario inactivo removido: {} ({})", user.getUsername(), user.getId());
                return true;
            }
            return false;
        });
    }
}
