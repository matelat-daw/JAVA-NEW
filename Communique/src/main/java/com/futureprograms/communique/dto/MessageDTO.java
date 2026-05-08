package com.futureprograms.communique.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para transferir datos de mensajes
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("fromUserId")
    private String fromUserId;
    
    @JsonProperty("toUserId")
    private String toUserId;
    
    @JsonProperty("text")
    private String text;
    
    @JsonProperty("timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    
    @JsonProperty("read")
    private Boolean read;
}
