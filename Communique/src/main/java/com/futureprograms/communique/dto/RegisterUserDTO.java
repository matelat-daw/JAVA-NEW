package com.futureprograms.communique.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para registrar un usuario en el servidor
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserDTO {
    @JsonProperty("username")
    private String username;
    
    @JsonProperty("hostname")
    private String hostname;
    
    @JsonProperty("ipAddress")
    private String ipAddress;
}
