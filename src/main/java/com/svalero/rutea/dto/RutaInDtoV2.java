package com.svalero.rutea.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RutaInDtoV2 {

    @Size(max = 255, message = "dificultad max length is 255")
    private String dificultad;

    @PositiveOrZero(message = "distanciaKm must be >= 0")
    private float distanciaKm;

    @Min(value = 0, message = "duracionMinutos must be >= 0")
    private int duracionMinutos;

    @NotNull(message = "fechaRealizacion is mandatory")
    private LocalDate fechaRealizacion;

    private boolean publica;

    @NotBlank(message = "titulo is mandatory")
    @Size(max = 60, message = "titulo max length is 60")
    private String titulo;

    @NotNull(message = "usuarioId is mandatory")
    private Long usuarioId;

    @Size(max = 500, message = "puntosIds max size is 500")
    private List<Long> puntosIds;

    @Size(max = 255, message = "etiquetas max length is 255")
    private String etiquetas;
}
