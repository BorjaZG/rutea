package com.svalero.rutea.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "puntos_interes")
public class PuntoInteres {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "abierto_actualmente", nullable = false)
    private boolean abiertoActualmente;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    @DecimalMin(value = "-90.0", message = "latitud must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "latitud must be between -90 and 90")
    private double latitud;

    @Column(nullable = false)
    @DecimalMin(value = "-180.0", message = "longitud must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "longitud must be between -180 and 180")
    private double longitud;

    @Column(nullable = false)
    @NotBlank(message = "nombre is mandatory")
    @Size(max = 255, message = "nombre max length is 255")
    private String nombre;

    @Column(name = "puntuacion_media", nullable = false)
    @DecimalMin(value = "0.0", message = "puntuacionMedia must be between 0 and 5")
    @DecimalMax(value = "5.0", message = "puntuacionMedia must be between 0 and 5")
    private float puntuacionMedia;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    @JsonIgnore
    private Categoria categoria;

    @JsonProperty("categoriaId")
    public Long getCategoriaId() {
        return categoria != null ? categoria.getId() : null;
    }

    @OneToMany(mappedBy = "punto")
    @JsonManagedReference("punto-resenas")
    private List<Resena> resenas;

    @ManyToMany(mappedBy = "puntos")
    @JsonIgnore
    private List<Ruta> rutas;
}