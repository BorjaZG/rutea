package com.svalero.rutea.service;

import com.svalero.rutea.domain.Categoria;
import com.svalero.rutea.dto.CategoriaInDto;
import com.svalero.rutea.dto.CategoriaOutDto;
import com.svalero.rutea.exception.CategoriaNotFoundException;
import com.svalero.rutea.repository.CategoriaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class CategoriaService {

    private static final Logger logger = LoggerFactory.getLogger(CategoriaService.class);

    @Autowired
    private CategoriaRepository categoriaRepository;

    public CategoriaOutDto add(CategoriaInDto dto) {
        logger.info("Creando nueva categoría: {}", dto.getNombre());
        Categoria saved = categoriaRepository.save(toEntity(dto));
        logger.info("Categoría creada exitosamente con ID: {}", saved.getId());
        return toOutDto(saved);
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

    @Transactional(readOnly = true)
    public List<CategoriaOutDto> findAll(Boolean activa, String nombre, Integer ordenPrioridad) {
        logger.debug("Buscando categorías con filtros: activa={}, nombre={}, ordenPrioridad={}",
                activa, nombre, ordenPrioridad);

        Specification<Categoria> spec = Specification.where(null);

        if (activa != null)
            spec = spec.and((root, query, cb) -> cb.equal(root.get("activa"), activa));

        if (nombre != null && !nombre.isBlank()) {
            String pattern = "%" + nombre.toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("nombre")), pattern));
        }

        if (ordenPrioridad != null)
            spec = spec.and((root, query, cb) -> cb.equal(root.get("ordenPrioridad"), ordenPrioridad));

        List<CategoriaOutDto> result = categoriaRepository.findAll(spec).stream()
                .map(this::toOutDto)
                .toList();
        logger.info("Se encontraron {} categorías", result.size());
        return result;
    }

    @Transactional(readOnly = true)
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

    public CategoriaOutDto patch(long id, Map<String, Object> updates) throws CategoriaNotFoundException {
        logger.info("Aplicando PATCH a categoría ID: {} con {} campos", id, updates.size());

        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Categoría no encontrada para PATCH: ID {}", id);
                    return new CategoriaNotFoundException();
                });

        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            String campo = entry.getKey();
            Object valor = entry.getValue();
            switch (campo) {
                case "activa":
                    categoria.setActiva((Boolean) valor);
                    logger.debug("Campo actualizado: activa = {}", valor);
                    break;
                case "costePromedio":
                    categoria.setCostePromedio(((Number) valor).floatValue());
                    logger.debug("Campo actualizado: costePromedio = {}", valor);
                    break;
                case "descripcion":
                    categoria.setDescripcion((String) valor);
                    logger.debug("Campo actualizado: descripcion");
                    break;
                case "iconoUrl":
                    categoria.setIconoUrl((String) valor);
                    logger.debug("Campo actualizado: iconoUrl");
                    break;
                case "nombre":
                    categoria.setNombre((String) valor);
                    logger.debug("Campo actualizado: nombre = {}", valor);
                    break;
                case "ordenPrioridad":
                    categoria.setOrdenPrioridad(((Number) valor).intValue());
                    logger.debug("Campo actualizado: ordenPrioridad = {}", valor);
                    break;
                case "id":
                    logger.debug("Ignorando campo 'id' en PATCH");
                    break;
                default:
                    logger.warn("Campo desconocido ignorado en PATCH: {}", campo);
            }
        }

        Categoria updated = categoriaRepository.save(categoria);
        logger.info("Categoría actualizada exitosamente con PATCH: ID {}", id);
        return toOutDto(updated);
    }

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
