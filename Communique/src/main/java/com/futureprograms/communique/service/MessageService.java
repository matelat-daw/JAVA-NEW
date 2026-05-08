package com.futureprograms.communique.service;

import com.futureprograms.communique.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Servicio para gestionar mensajes entre usuarios
 */
@Service
@Slf4j
public class MessageService {
    
    // Todos los mensajes indexados por ID
    private final Map<String, Message> allMessages = new ConcurrentHashMap<>();
    
    // Mensajes agrupados por conversación (clave: par de IDs ordenado)
    private final Map<String, List<Message>> conversations = new ConcurrentHashMap<>();
    
    /**
     * Envía un mensaje de un usuario a otro
     */
    public Message sendMessage(String fromUserId, String toUserId, String text) {
        String messageId = UUID.randomUUID().toString();
        Message message = Message.builder()
                .id(messageId)
                .fromUserId(fromUserId)
                .toUserId(toUserId)
                .text(text)
                .timestamp(LocalDateTime.now())
                .read(false)
                .build();
        
        allMessages.put(messageId, message);
        
        // Agregar a la conversación
        String convKey = getConversationKey(fromUserId, toUserId);
        conversations.computeIfAbsent(convKey, k -> new CopyOnWriteArrayList<>()).add(message);
        
        log.info("Mensaje enviado de {} a {}: {}", fromUserId, toUserId, text);
        return message;
    }
    
    /**
     * Obtiene los mensajes entre dos usuarios
     */
    public List<Message> getConversation(String userId1, String userId2) {
        String convKey = getConversationKey(userId1, userId2);
        return conversations.getOrDefault(convKey, Collections.emptyList());
    }
    
    /**
     * Obtiene los nuevos mensajes para un usuario (no leídos)
     */
    public List<Message> getUnreadMessages(String userId) {
        return allMessages.values().stream()
                .filter(m -> m.getToUserId().equals(userId) && !m.isRead())
                .sorted(Comparator.comparing(Message::getTimestamp))
                .toList();
    }
    
    /**
     * Obtiene todos los mensajes desde un tiempo específico
     */
    public List<Message> getMessagesSince(String userId, long timestamp) {
        LocalDateTime cutoff = LocalDateTime.ofInstant(
                java.time.Instant.ofEpochMilli(timestamp), 
                java.time.ZoneId.systemDefault()
        );
        
        return allMessages.values().stream()
                .filter(m -> (m.getFromUserId().equals(userId) || m.getToUserId().equals(userId)) &&
                            m.getTimestamp().isAfter(cutoff))
                .sorted(Comparator.comparing(Message::getTimestamp))
                .toList();
    }
    
    /**
     * Marca un mensaje como leído
     */
    public void markAsRead(String messageId) {
        Message message = allMessages.get(messageId);
        if (message != null) {
            message.setRead(true);
        }
    }
    
    /**
     * Marca todos los mensajes de un usuario como leídos
     */
    public void markAllAsRead(String fromUserId, String toUserId) {
        String convKey = getConversationKey(fromUserId, toUserId);
        List<Message> conversation = conversations.get(convKey);
        if (conversation != null) {
            conversation.stream()
                    .filter(m -> m.getFromUserId().equals(fromUserId) && m.getToUserId().equals(toUserId))
                    .forEach(m -> m.setRead(true));
        }
    }
    
    /**
     * Obtiene el conteo de mensajes no leídos
     */
    public int getUnreadCount(String userId) {
        return (int) allMessages.values().stream()
                .filter(m -> m.getToUserId().equals(userId) && !m.isRead())
                .count();
    }
    
    /**
     * Limpia mensajes antiguos (mayores a X días)
     */
    public void cleanupOldMessages(int daysOld) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(daysOld);
        allMessages.values().removeIf(m -> {
            boolean shouldRemove = m.getTimestamp().isBefore(cutoff);
            if (shouldRemove) {
                // También remover de las conversaciones
                String convKey = getConversationKey(m.getFromUserId(), m.getToUserId());
                List<Message> conversation = conversations.get(convKey);
                if (conversation != null) {
                    conversation.remove(m);
                }
            }
            return shouldRemove;
        });
    }
    
    /**
     * Genera una clave única determinista para un par de usuarios
     */
    private String getConversationKey(String u1, String u2) {
        return u1.compareTo(u2) < 0 ? u1 + "_" + u2 : u2 + "_" + u1;
    }
}
