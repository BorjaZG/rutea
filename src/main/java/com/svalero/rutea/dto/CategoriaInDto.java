package com.svalero.rutea.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoriaInDto {

    private boolean activa;

    @PositiveOrZero(message = "costePromedio must be >= 0")
    private float costePromedio;

    @Size(max = 500, message = "descripcion max length is 500")
    private String descripcion;

    @Size(max = 255, message = "iconoUrl max length is 255")
    private String iconoUrl;

    @NotBlank(message = "nombre is mandatory")
    @Size(max = 255, message = "nombre max length is 255")
    private String nombre;

    @Min(value = 0, message = "ordenPrioridad must be >= 0")
    private int ordenPrioridad;
}
