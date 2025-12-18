package com.svalero.rutea.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "rutas")
public class Ruta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String titulo;

    private String dificultad; // BAJA, MEDIA, ALTA
    private float distanciaKm;
    private int duracionMinutos;
    private boolean publica;

    @NotNull
    private LocalDate fechaRealizacion;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario creador;

    @ManyToMany
    @JoinTable(name = "ruta_puntos",
            joinColumns = @JoinColumn(name = "ruta_id"),
            inverseJoinColumns = @JoinColumn(name = "punto_id"))
    private List<PuntoInteres> puntosInteres;
}