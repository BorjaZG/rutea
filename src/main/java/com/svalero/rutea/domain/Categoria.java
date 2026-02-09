package com.svalero.rutea.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "categorias")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private boolean activa;

    @Column(name = "coste_promedio", nullable = false)
    @PositiveOrZero(message = "costePromedio must be >= 0")
    private float costePromedio;

    @Column(length = 500)
    @Size(max = 500, message = "descripcion max length is 500")
    private String descripcion;

    @Column(name = "icono_url")
    @Size(max = 255, message = "iconoUrl max length is 255")
    private String iconoUrl;

    @Column(nullable = false)
    @NotBlank(message = "nombre is mandatory")
    @Size(max = 255, message = "nombre max length is 255")
    private String nombre;

    @Column(name = "orden_prioridad", nullable = false)
    @Min(value = 0, message = "ordenPrioridad must be >= 0")
    private int ordenPrioridad;

    @OneToMany(mappedBy = "categoria")
    @JsonManagedReference("categoria-puntos")
    private List<PuntoInteres> puntosInteres;
}