package com.svalero.rutea.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResenaInDto {

    @NotBlank(message = "comentario is mandatory")
    @Size(max = 255, message = "comentario max length is 255")
    private String comentario;

    private boolean editada;

    private LocalDate fechaPublicacion;

    @PositiveOrZero(message = "likes must be >= 0")
    private int likes;

    @Size(max = 255, message = "titulo max length is 255")
    private String titulo;

    @Min(value = 1, message = "valoracion must be between 1 and 5")
    @Max(value = 5, message = "valoracion must be between 1 and 5")
    private int valoracion;

    @NotNull(message = "puntoId is mandatory")
    private Long puntoId;

    @NotNull(message = "usuarioId is mandatory")
    private Long usuarioId;
}