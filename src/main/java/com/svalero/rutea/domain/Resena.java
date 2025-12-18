package com.svalero.rutea.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "resenas")
public class Resena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    @NotBlank(message = "El comentario no puede estar vacío")
    @Size(min = 10, message = "El comentario debe tener al menos 10 caracteres")
    private String comentario;

    @Min(1) @Max(5)
    private int valoracion;

    private int likes;
    private boolean editada;
    private LocalDate fechaPublicacion;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "punto_id")
    private PuntoInteres puntoInteres;
}