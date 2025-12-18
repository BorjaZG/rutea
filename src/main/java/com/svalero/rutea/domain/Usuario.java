package com.svalero.rutea.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 4, message = "El usuario debe tener al menos 4 caracteres")
    @Column(unique = true) // No puede haber dos usuarios iguales
    private String username;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    @Min(value = 0, message = "El nivel de experiencia no puede ser negativo")
    private int nivelExperiencia; // 0: Novato, 10: Experto

    private boolean esPremium;

    @NotNull(message = "La fecha de registro es obligatoria")
    private LocalDate fechaRegistro;
}