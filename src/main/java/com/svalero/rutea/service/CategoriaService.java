package com.svalero.rutea.service;

import com.svalero.rutea.domain.Categoria;
import com.svalero.rutea.dto.CategoriaInDto;
import com.svalero.rutea.dto.CategoriaOutDto;
import com.svalero.rutea.exception.CategoriaNotFoundException;
import com.svalero.rutea.repository.CategoriaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@Service
public class CategoriaService {

    private static final Logger logger = LoggerFactory.getLogger(CategoriaService.class);

    @Autowired
    private CategoriaRepository categoriaRepository;

    public CategoriaOutDto add(CategoriaInDto dto) {
        logger.info("Creando nueva categoría: {}", dto.getNombre());
        try {
            Categoria categoria = toEntity(dto);
            Categoria saved = categoriaRepository.save(categoria);
            logger.info("Categoría creada exitosamente con ID: {}", saved.getId());
            return toOutDto(saved);
        } catch (Exception e) {
            logger.error("Error al crear categoría: {}", e.getMessage(), e);
            throw e;
        }
    }

    public void delete(long id) throws CategoriaNotFoundException {
        logger.info("Eliminando categoría ID: {}", id);
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Categoría no encontrada para eliminación: ID {}", id);
                    return new CategoriaNotFoundException();
                });
        categoriaRepository.delete(categoria);
        logger.info("Categoría eliminada exitosamente: ID {}", id);
    }

    // Filtros (combinables): activa + nombre + ordenPrioridad
    public List<CategoriaOutDto> findAll(Boolean activa, String nombre, Integer ordenPrioridad) {
        logger.debug("Buscando categorías con filtros: activa={}, nombre={}, ordenPrioridad={}",
                activa, nombre, ordenPrioridad);

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

        logger.info("Se encontraron {} categorías", categorias.size());
        return categorias.stream().map(this::toOutDto).toList();
    }

    public CategoriaOutDto findById(long id) throws CategoriaNotFoundException {
        logger.debug("Buscando categoría por ID: {}", id);
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Categoría no encontrada: ID {}", id);
                    return new CategoriaNotFoundException();
                });
        return toOutDto(categoria);
    }

    public CategoriaOutDto modify(long id, CategoriaInDto dto) throws CategoriaNotFoundException {
        logger.info("Modificando categoría ID: {}", id);
        Categoria existing = categoriaRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Categoría no encontrada para modificación: ID {}", id);
                    return new CategoriaNotFoundException();
                });

        // Actualización controlada (sin tocar relaciones)
        existing.setActiva(dto.isActiva());
        existing.setCostePromedio(dto.getCostePromedio());
        existing.setDescripcion(dto.getDescripcion());
        existing.setIconoUrl(dto.getIconoUrl());
        existing.setNombre(dto.getNombre());
        existing.setOrdenPrioridad(dto.getOrdenPrioridad());

        Categoria saved = categoriaRepository.save(existing);
        logger.info("Categoría modificada exitosamente: ID {}", id);
        return toOutDto(saved);
    }

    /**
     * Operación PATCH - Actualización parcial de categoría
     * Permite modificar solo los campos especificados
     *
     * @param id ID de la categoría
     * @param updates Map con los campos a actualizar
     * @return CategoriaOutDto actualizado
     * @throws CategoriaNotFoundException si no existe la categoría
     */
    public CategoriaOutDto patch(long id, Map<String, Object> updates) throws CategoriaNotFoundException {
        logger.info("Aplicando PATCH a categoría ID: {} con {} campos", id, updates.size());
        logger.debug("Campos a actualizar: {}", updates.keySet());

        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Categoría no encontrada para PATCH: ID {}", id);
                    return new CategoriaNotFoundException();
                });

        updates.forEach((campo, valor) -> {
            try {
                Field field = Categoria.class.getDeclaredField(campo);
                field.setAccessible(true);

                // Convertir tipos según necesidad
                if (field.getType() == boolean.class && valor instanceof Boolean) {
                    field.setBoolean(categoria, (Boolean) valor);
                } else if (field.getType() == float.class && valor instanceof Number) {
                    field.setFloat(categoria, ((Number) valor).floatValue());
                } else if (field.getType() == int.class && valor instanceof Number) {
                    field.setInt(categoria, ((Number) valor).intValue());
                } else {
                    field.set(categoria, valor);
                }

                logger.debug("Campo actualizado: {} = {}", campo, valor);
            } catch (NoSuchFieldException e) {
                logger.warn("Campo no existe en Categoria: {}", campo);
                throw new RuntimeException("Campo no válido: " + campo);
            } catch (IllegalAccessException e) {
                logger.error("Error de acceso al campo {}: {}", campo, e.getMessage());
                throw new RuntimeException("Error actualizando campo: " + campo, e);
            }
        });

        Categoria updated = categoriaRepository.save(categoria);
        logger.info("Categoría actualizada exitosamente con PATCH: ID {}", id);
        return toOutDto(updated);
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