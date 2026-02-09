package com.svalero.rutea.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PuntoInteresOutDto {
    private long id;
    private boolean abiertoActualmente;
    private LocalDateTime fechaCreacion;
    private double latitud;
    private double longitud;
    private String nombre;
    private float puntuacionMedia;
    private Long categoriaId;
    private String categoriaNombre;
}