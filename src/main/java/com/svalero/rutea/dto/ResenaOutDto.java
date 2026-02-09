package com.svalero.rutea.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResenaOutDto {
    private long id;
    private String comentario;
    private boolean editada;
    private LocalDate fechaPublicacion;
    private int likes;
    private String titulo;
    private int valoracion;
    private Long puntoId;
    private Long usuarioId;
}