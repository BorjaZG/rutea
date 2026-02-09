package com.svalero.rutea.service;

import com.svalero.rutea.domain.Categoria;
import com.svalero.rutea.dto.CategoriaInDto;
import com.svalero.rutea.dto.CategoriaOutDto;
import com.svalero.rutea.exception.CategoriaNotFoundException;
import com.svalero.rutea.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    public CategoriaOutDto add(CategoriaInDto dto) {
        Categoria categoria = toEntity(dto);
        Categoria saved = categoriaRepository.save(categoria);
        return toOutDto(saved);
    }

    public void delete(long id) throws CategoriaNotFoundException {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(CategoriaNotFoundException::new);
        categoriaRepository.delete(categoria);
    }

    // Filtros (combinables): activa + nombre + ordenPrioridad
    public List<CategoriaOutDto> findAll(Boolean activa, String nombre, Integer ordenPrioridad) {

        boolean hasActiva = (activa != null);
        boolean hasNombre = (nombre != null && !nombre.isBlank());
        boolean hasOrden = (ordenPrioridad != null);

        List<Categoria> categorias;

        // 3 campos
        if (hasActiva && hasNombre && hasOrden) {
            categorias = categoriaRepository
                    .findByActivaAndNombreContainingIgnoreCaseAndOrdenPrioridad(activa, nombre, ordenPrioridad);

            // 2 campos
        } else if (hasActiva && hasNombre) {
            categorias = categoriaRepository
                    .findByActivaAndNombreContainingIgnoreCase(activa, nombre);
        } else if (hasActiva && hasOrden) {
            categorias = categoriaRepository
                    .findByActivaAndOrdenPrioridad(activa, ordenPrioridad);
        } else if (hasNombre && hasOrden) {
            categorias = categoriaRepository
                    .findByNombreContainingIgnoreCaseAndOrdenPrioridad(nombre, ordenPrioridad);

            // 1 campo
        } else if (hasActiva) {
            categorias = categoriaRepository.findByActiva(activa);
        } else if (hasNombre) {
            categorias = categoriaRepository.findByNombreContainingIgnoreCase(nombre);
        } else if (hasOrden) {
            categorias = categoriaRepository.findByOrdenPrioridad(ordenPrioridad);

            // sin filtros
        } else {
            categorias = categoriaRepository.findAll();
        }

        return categorias.stream().map(this::toOutDto).toList();
    }

    public CategoriaOutDto findById(long id) throws CategoriaNotFoundException {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(CategoriaNotFoundException::new);
        return toOutDto(categoria);
    }

    public CategoriaOutDto modify(long id, CategoriaInDto dto) throws CategoriaNotFoundException {
        Categoria existing = categoriaRepository.findById(id)
                .orElseThrow(CategoriaNotFoundException::new);

        // Actualización controlada (sin tocar relaciones)
        existing.setActiva(dto.isActiva());
        existing.setCostePromedio(dto.getCostePromedio());
        existing.setDescripcion(dto.getDescripcion());
        existing.setIconoUrl(dto.getIconoUrl());
        existing.setNombre(dto.getNombre());
        existing.setOrdenPrioridad(dto.getOrdenPrioridad());

        Categoria saved = categoriaRepository.save(existing);
        return toOutDto(saved);
    }

    // ----------------- Mappers manuales -----------------

    private Categoria toEntity(CategoriaInDto dto) {
        return Categoria.builder()
                .activa(dto.isActiva())
                .costePromedio(dto.getCostePromedio())
                .descripcion(dto.getDescripcion())
                .iconoUrl(dto.getIconoUrl())
                .nombre(dto.getNombre())
                .ordenPrioridad(dto.getOrdenPrioridad())
                .build();
    }

    private CategoriaOutDto toOutDto(Categoria categoria) {
        return CategoriaOutDto.builder()
                .id(categoria.getId())
                .activa(categoria.isActiva())
                .costePromedio(categoria.getCostePromedio())
                .descripcion(categoria.getDescripcion())
                .iconoUrl(categoria.getIconoUrl())
                .nombre(categoria.getNombre())
                .ordenPrioridad(categoria.getOrdenPrioridad())
                .build();
    }
}