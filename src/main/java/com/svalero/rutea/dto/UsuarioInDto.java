package com.svalero.rutea.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioInDto {

    @NotBlank(message = "email is mandatory")
    @Email(message = "email must be valid")
    @Size(max = 255, message = "email max length is 255")
    private String email;

    private boolean esPremium;

    @NotNull(message = "fechaRegistro is mandatory")
    private LocalDate fechaRegistro;

    @Min(value = 0, message = "nivelExperiencia must be >= 0")
    private int nivelExperiencia;

    @NotBlank(message = "password is mandatory")
    @Size(min = 6, max = 255, message = "password length must be between 6 and 255")
    private String password;

    @NotBlank(message = "username is mandatory")
    @Size(max = 255, message = "username max length is 255")
    private String username;
}