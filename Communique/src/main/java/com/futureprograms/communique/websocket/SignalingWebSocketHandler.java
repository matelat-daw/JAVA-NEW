package com.futureprograms.communique.websocket;

import com.futureprograms.communique.service.UserService;
import com.futureprograms.communique.model.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manejador de WebSocket para la señalización de WebRTC
 * Facilita el intercambio de ofertas (offers) y respuestas (answers) entre clientes
 */
@Component
@Slf4j
public class SignalingWebSocketHandler extends TextWebSocketHandler {
    
    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    
    public SignalingWebSocketHandler(UserService userService) {
        this.userService = userService;
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("WebSocket conectado: {}", session.getId());
        sessions.put(session.getId(), session);
    }
    
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            JsonNode json = objectMapper.readTree(message.getPayload());
            String type = json.get("type").asText();
            
            switch (type) {
                case "register" -> handleRegister(session, json);
                case "offer" -> handleOffer(session, json);
                case "answer" -> handleAnswer(session, json);
                case "ice-candidate" -> handleIceCandidate(session, json);
                case "chat-message" -> handleChatMessage(session, json);
                case "hangup" -> handleHangup(session, json);
                case "ping" -> handlePing(session);
                default -> log.warn("Tipo de mensaje desconocido: {}", type);
            }
        } catch (Exception e) {
            log.error("Error procesando mensaje WebSocket: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Maneja el registro de un usuario a través de WebSocket
     */
    private void handleRegister(WebSocketSession session, JsonNode json) {
        String userId = json.get("userId").asText();
        userService.updateUserSocket(userId, session.getId());
        
        log.info("Usuario registrado en WebSocket: {} ({})", userId, session.getId());
        
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("type", "users-online");
            response.put("users", userService.getOtherOnlineUsers(userId));
            
            String responseStr = objectMapper.writeValueAsString(response);
            session.sendMessage(new TextMessage(responseStr));
        } catch (IOException e) {
            log.error("Error enviando lista de usuarios: {}", e.getMessage());
        }
    }
    
    /**
     * Maneja una oferta (offer) de conexión WebRTC
     */
    private void handleOffer(WebSocketSession session, JsonNode json) throws IOException {
        String targetUserId = json.get("targetUserId").asText();
        String sourceUserId = getUserIdFromSession(session.getId());
        
        forwardMessage(targetUserId, sourceUserId, "offer", json.get("offer"));
    }
    
    /**
     * Maneja una respuesta (answer) a una oferta de WebRTC
     */
    private void handleAnswer(WebSocketSession session, JsonNode json) throws IOException {
        String targetUserId = json.get("targetUserId").asText();
        String sourceUserId = getUserIdFromSession(session.getId());
        
        forwardMessage(targetUserId, sourceUserId, "answer", json.get("answer"));
    }
    
    /**
     * Maneja candidatos ICE para la conexión P2P
     */
    private void handleIceCandidate(WebSocketSession session, JsonNode json) throws IOException {
        String targetUserId = json.get("targetUserId").asText();
        String sourceUserId = getUserIdFromSession(session.getId());
        
        forwardMessage(targetUserId, sourceUserId, "ice-candidate", json.get("candidate"));
    }

    /**
     * Maneja un mensaje de chat a través de WebSocket para entrega inmediata
     */
    private void handleChatMessage(WebSocketSession session, JsonNode json) throws IOException {
        String targetUserId = json.get("targetUserId").asText();
        String sourceUserId = getUserIdFromSession(session.getId());
        
        forwardMessage(targetUserId, sourceUserId, "chat-message", json.get("message"));
    }

    /**
     * Maneja el fin de una llamada (hangup)
     */
    private void handleHangup(WebSocketSession session, JsonNode json) throws IOException {
        String targetUserId = json.get("targetUserId").asText();
        String sourceUserId = getUserIdFromSession(session.getId());
        
        forwardMessage(targetUserId, sourceUserId, "hangup", null);
    }

    /**
     * Reenvía un mensaje a un usuario específico
     */
    private void forwardMessage(String targetUserId, String sourceUserId, String type, JsonNode data) throws IOException {
        User targetUser = userService.getUserById(targetUserId);
        if (targetUser != null && targetUser.getSocketId() != null) {
            WebSocketSession targetSession = sessions.get(targetUser.getSocketId());
            
            if (targetSession != null && targetSession.isOpen()) {
                Map<String, Object> message = new HashMap<>();
                message.put("type", type);
                message.put("sourceUserId", sourceUserId);
                if (data != null) {
                    message.put(type.equals("ice-candidate") ? "candidate" : type, data);
                }
                
                targetSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
                log.debug("Mensaje {} reenviado de {} a {}", type, sourceUserId, targetUserId);
            }
        }
    }
    
    /**
     * Maneja ping para mantener viva la conexión
     */
    private void handlePing(WebSocketSession session) throws IOException {
        Map<String, Object> pong = new HashMap<>();
        pong.put("type", "pong");
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(pong)));
    }
    
    /**
     * Obtiene el userId asociado a una sesión
     */
    private String getUserIdFromSession(String sessionId) {
        User user = userService.getUserBySocketId(sessionId);
        return user != null ? user.getId() : null;
    }
    
    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) {
        log.info("WebSocket desconectado: {} (Estado: {})", session.getId(), status);
        userService.disconnectUserBySocket(session.getId());
        sessions.remove(session.getId());
    }
}
