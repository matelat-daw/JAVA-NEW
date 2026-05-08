package com.futureprograms.communique.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Representa un mensaje entre dos usuarios
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String id;
    private String fromUserId;
    private String toUserId;
    private String text;
    private LocalDateTime timestamp;
    private boolean read;
}
