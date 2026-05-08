package com.futureprograms.communique.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa un cliente/usuario conectado en la red
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String id;
    private String username;
    private String ipAddress;
    private String hostname;
    private boolean online;
    private long lastHeartbeat;
    private String socketId;
}
