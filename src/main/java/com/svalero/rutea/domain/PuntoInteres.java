package com.svalero.rutea.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "puntos_interes")
public class PuntoInteres {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del lugar es obligatorio")
    private String nombre;

    // Coordenadas para Google Maps (Android)
    @NotNull(message = "La latitud es obligatoria")
    private double latitud;

    @NotNull(message = "La longitud es obligatoria")
    private double longitud;

    @Min(0) @Max(5)
    private float puntuacionMedia;

    private boolean abiertoActualmente;
    private LocalDateTime fechaCreacion;

    @ManyToOne
    @JoinColumn(name = "categoria_id") // Clave foránea
    private Categoria categoria;
}