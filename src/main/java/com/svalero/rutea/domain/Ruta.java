package com.svalero.rutea.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "rutas")
public class Ruta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    @Size(max = 255, message = "dificultad max length is 255")
    private String dificultad;

    @Column(name = "distancia_km", nullable = false)
    @PositiveOrZero(message = "distanciaKm must be >= 0")
    private float distanciaKm;

    @Column(name = "duracion_minutos", nullable = false)
    @Min(value = 0, message = "duracionMinutos must be >= 0")
    private int duracionMinutos;

    @Column(name = "fecha_realizacion", nullable = false)
    @NotNull(message = "fechaRealizacion is mandatory")
    private LocalDate fechaRealizacion;

    @Column(nullable = false)
    private boolean publica;

    @Column(nullable = false, length = 60)
    @NotBlank(message = "titulo is mandatory")
    @Size(max = 60, message = "titulo max length is 60")
    private String titulo;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    /**
     * Evita bucle infinito JSON por ManyToMany.
     * En la API se devolverán ids (DTO), no la lista completa de entidades.
     */
    @ManyToMany
    @JoinTable(
            name = "ruta_puntos",
            joinColumns = @JoinColumn(name = "ruta_id"),
            inverseJoinColumns = @JoinColumn(name = "punto_id")
    )
    @JsonIgnore
    private List<PuntoInteres> puntos;
}