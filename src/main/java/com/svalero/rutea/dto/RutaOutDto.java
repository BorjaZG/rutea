package com.svalero.rutea.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RutaOutDto {
    private long id;
    private String dificultad;
    private float distanciaKm;
    private int duracionMinutos;
    private LocalDate fechaRealizacion;
    private boolean publica;
    private String titulo;
    private Long usuarioId;
    private List<Long> puntosIds;
}
