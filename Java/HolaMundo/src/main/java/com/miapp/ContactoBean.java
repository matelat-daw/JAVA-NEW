package com.miapp;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactoBean implements Serializable {
    private String nombreTienda;
    private String direccion;
    private String telefono;
    private String email;
    private String horario;
}