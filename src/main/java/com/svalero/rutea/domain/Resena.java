package com.svalero.rutea.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "resenas")
public class Resena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    @NotBlank(message = "comentario is mandatory")
    private String comentario;

    @Column(nullable = false)
    private boolean editada;

    @Column(name = "fecha_publicacion")
    private LocalDate fechaPublicacion;

    @Column(nullable = false)
    @Min(value = 0, message = "likes must be >= 0")
    private int likes;

    @Column
    private String titulo;

    @Column(nullable = false)
    @Min(value = 1, message = "valoracion must be between 1 and 5")
    @Max(value = 5, message = "valoracion must be between 1 and 5")
    private int valoracion;

    @ManyToOne
    @JoinColumn(name = "punto_id")
    @JsonIgnore
    private PuntoInteres punto;

    @JsonProperty("puntoId")
    public Long getPuntoId() {
        return punto != null ? punto.getId() : null;
    }

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @JsonIgnore
    private Usuario usuario;

    @JsonProperty("usuarioId")
    public Long getUsuarioId() {
        return usuario != null ? usuario.getId() : null;
    }
}