package com.miapp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto {

    private int id;
    private String nombre;
    private double precio;
    private String categoria;
    private String descripcion;
    private String imagen;
}