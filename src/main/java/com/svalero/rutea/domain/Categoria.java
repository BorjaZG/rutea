package com.svalero.rutea.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "categorias")
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Size(max = 500, message = "La descripción es demasiado larga")
    private String descripcion;
    private String iconoUrl;
    private int ordenPrioridad;
    private boolean activa;

    @Min(value = 0, message = "El coste no puede ser negativo")
    private float costePromedio;
}