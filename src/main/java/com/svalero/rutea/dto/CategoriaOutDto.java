package com.svalero.rutea.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoriaOutDto {

    private long id;
    private boolean activa;
    private float costePromedio;
    private String descripcion;
    private String iconoUrl;
    private String nombre;
    private int ordenPrioridad;
}
