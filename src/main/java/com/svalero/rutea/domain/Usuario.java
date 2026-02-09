package com.svalero.rutea.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    @Email(message = "email must be valid")
    @NotBlank(message = "email is mandatory")
    private String email;

    @Column(name = "es_premium", nullable = false)
    private boolean esPremium;

    @Column(name = "fecha_registro", nullable = false)
    @NotNull(message = "fechaRegistro is mandatory")
    private LocalDate fechaRegistro;

    @Column(name = "nivel_experiencia", nullable = false)
    @Min(value = 0, message = "nivelExperiencia must be >= 0")
    private int nivelExperiencia;

    @Column(nullable = false)
    @NotBlank(message = "password is mandatory")
    @Size(min = 6, message = "password must have at least 6 characters")
    private String password;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "username is mandatory")
    private String username;

    @OneToMany(mappedBy = "usuario")
    @JsonManagedReference("usuario-resenas")
    private List<Resena> resenas;
}