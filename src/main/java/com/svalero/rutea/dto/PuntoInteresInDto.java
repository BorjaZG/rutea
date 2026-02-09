package com.svalero.rutea.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PuntoInteresInDto {

    private boolean abiertoActualmente;

    private LocalDateTime fechaCreacion;

    @DecimalMin(value = "-90.0", message = "latitud must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "latitud must be between -90 and 90")
    private double latitud;

    @DecimalMin(value = "-180.0", message = "longitud must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "longitud must be between -180 and 180")
    private double longitud;

    @NotBlank(message = "nombre is mandatory")
    @Size(max = 255, message = "nombre max length is 255")
    private String nombre;

    @DecimalMin(value = "0.0", message = "puntuacionMedia must be between 0 and 5")
    @DecimalMax(value = "5.0", message = "puntuacionMedia must be between 0 and 5")
    private float puntuacionMedia;

    @NotNull(message = "categoriaId is mandatory")
    private Long categoriaId;
}