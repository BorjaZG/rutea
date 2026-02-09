package com.svalero.rutea.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioOutDto {
    private long id;
    private String email;
    private boolean esPremium;
    private LocalDate fechaRegistro;
    private int nivelExperiencia;
    private String username;
}
