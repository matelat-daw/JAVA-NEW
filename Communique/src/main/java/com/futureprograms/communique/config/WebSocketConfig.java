package com.futureprograms.communique.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import com.futureprograms.communique.websocket.SignalingWebSocketHandler;

/**
 * Configuración de WebSocket para la comunicación en tiempo real
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    
    private final SignalingWebSocketHandler signalingHandler;
    
    public WebSocketConfig(SignalingWebSocketHandler signalingHandler) {
        this.signalingHandler = signalingHandler;
    }
    
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(signalingHandler, "/ws")
                .setAllowedOrigins("*");
        
        System.out.println("WebSocket handlers registrados en /ws");
    }
}
