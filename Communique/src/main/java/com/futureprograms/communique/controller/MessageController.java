package com.futureprograms.communique.controller;

import com.futureprograms.communique.model.Message;
import com.futureprograms.communique.service.MessageService;
import com.futureprograms.communique.dto.MessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar mensajes entre usuarios
 */
@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*")
@Slf4j
public class MessageController {
    
    private final MessageService messageService;
    
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }
    
    /**
     * Envía un mensaje de un usuario a otro
     */
    @PostMapping("/send")
    public ResponseEntity<MessageDTO> sendMessage(@RequestBody MessageDTO dto) {
        log.info("Enviando mensaje de {} a {}", dto.getFromUserId(), dto.getToUserId());
        
        Message message = messageService.sendMessage(
                dto.getFromUserId(),
                dto.getToUserId(),
                dto.getText()
        );
        
        return ResponseEntity.ok(mapToDTO(message));
    }
    
    /**
     * Obtiene la conversación entre dos usuarios
     */
    @GetMapping("/conversation/{userId1}/{userId2}")
    public ResponseEntity<List<MessageDTO>> getConversation(
            @PathVariable String userId1,
            @PathVariable String userId2) {
        
        List<Message> messages = messageService.getConversation(userId1, userId2);
        // Marcar como leídos los mensajes para este usuario
        messages.stream()
                .filter(m -> m.getToUserId().equals(userId1))
                .forEach(m -> messageService.markAsRead(m.getId()));
        
        return ResponseEntity.ok(messages.stream().map(this::mapToDTO).toList());
    }
    
    /**
     * Obtiene los mensajes no leídos de un usuario
     */
    @GetMapping("/unread/{userId}")
    public ResponseEntity<List<MessageDTO>> getUnreadMessages(@PathVariable String userId) {
        List<Message> messages = messageService.getUnreadMessages(userId);
        return ResponseEntity.ok(messages.stream().map(this::mapToDTO).toList());
    }
    
    /**
     * Obtiene mensajes desde un timestamp específico
     */
    @GetMapping("/since/{userId}/{timestamp}")
    public ResponseEntity<List<MessageDTO>> getMessagesSince(
            @PathVariable String userId,
            @PathVariable long timestamp) {
        
        List<Message> messages = messageService.getMessagesSince(userId, timestamp);
        return ResponseEntity.ok(messages.stream().map(this::mapToDTO).toList());
    }
    
    /**
     * Marca todos los mensajes de un usuario como leídos
     */
    @PostMapping("/mark-read/{fromUserId}/{toUserId}")
    public ResponseEntity<Void> markAsRead(
            @PathVariable String fromUserId,
            @PathVariable String toUserId) {
        
        messageService.markAllAsRead(fromUserId, toUserId);
        return ResponseEntity.ok().build();
    }
    
    /**
     * Obtiene el conteo de mensajes no leídos
     */
    @GetMapping("/unread-count/{userId}")
    public ResponseEntity<Integer> getUnreadCount(@PathVariable String userId) {
        return ResponseEntity.ok(messageService.getUnreadCount(userId));
    }
    
    /**
     * Mapea un Message a MessageDTO
     */
    private MessageDTO mapToDTO(Message message) {
        return MessageDTO.builder()
                .id(message.getId())
                .fromUserId(message.getFromUserId())
                .toUserId(message.getToUserId())
                .text(message.getText())
                .timestamp(message.getTimestamp())
                .read(message.isRead())
                .build();
    }
}
