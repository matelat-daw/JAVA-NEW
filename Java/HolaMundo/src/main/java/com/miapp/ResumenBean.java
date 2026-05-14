package com.miapp;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumenBean implements Serializable {
    private int totalProductos;
    private int totalCategorias;
    private String productoMasCaro;
}