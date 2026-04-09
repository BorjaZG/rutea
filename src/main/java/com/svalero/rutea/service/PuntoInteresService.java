package com.svalero.rutea.service;

import com.svalero.rutea.domain.Categoria;
import com.svalero.rutea.domain.PuntoInteres;
import com.svalero.rutea.dto.PuntoInteresInDto;
import com.svalero.rutea.dto.PuntoInteresOutDto;
import com.svalero.rutea.exception.CategoriaNotFoundException;
import com.svalero.rutea.exception.PuntoInteresNotFoundException;
import com.svalero.rutea.repository.CategoriaRepository;
import com.svalero.rutea.repository.PuntoInteresRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class PuntoInteresService {

    private static final Logger logger = LoggerFactory.getLogger(PuntoInteresService.class);

    @Autowired
    private PuntoInteresRepository puntoInteresRepository;
    @Autowired
    private CategoriaRepository categoriaRepository;
    @Autowired
    private ModelMapper modelMapper;

    public PuntoInteresOutDto add(PuntoInteresInDto dto) throws CategoriaNotFoundException {
        logger.info("Creando nuevo punto de interés: {}", dto.getNombre());

        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> {
                    logger.error("Categoría no encontrada: ID {}", dto.getCategoriaId());
                    return new CategoriaNotFoundException();
                });

        PuntoInteres punto = modelMapper.map(dto, PuntoInteres.class);
        punto.setCategoria(categoria);

        PuntoInteres saved = puntoInteresRepository.save(punto);
        logger.info("Punto de interés creado exitosamente con ID: {}", saved.getId());
        return toOutDto(saved);
    }

    public void delete(long id) throws PuntoInteresNotFoundException {
        logger.info("Eliminando punto de interés ID: {}", id);
        PuntoInteres punto = puntoInteresRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Punto de interés no encontrado para eliminación: ID {}", id);
                    return new PuntoInteresNotFoundException();
                });
        puntoInteresRepository.delete(punto);
        logger.info("Punto de interés eliminado exitosamente: ID {}", id);
    }

    @Transactional(readOnly = true)
    public List<PuntoInteresOutDto> findAll(Long categoriaId, Boolean abiertoActualmente, String nombre, Float puntuacionMedia) {
        logger.debug("Buscando puntos de interés con filtros: categoriaId={}, abiertoActualmente={}, nombre={}, puntuacionMedia={}",
                categoriaId, abiertoActualmente, nombre, puntuacionMedia);

        Specification<PuntoInteres> spec = Specification.where(null);

        if (categoriaId != null)
            spec = spec.and((root, query, cb) -> cb.equal(root.get("categoria").get("id"), categoriaId));

        if (abiertoActualmente != null)
            spec = spec.and((root, query, cb) -> cb.equal(root.get("abiertoActualmente"), abiertoActualmente));

        if (nombre != null && !nombre.isBlank()) {
            String pattern = "%" + nombre.toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("nombre")), pattern));
        }

        if (puntuacionMedia != null)
            spec = spec.and((root, query, cb) -> cb.equal(root.get("puntuacionMedia"), puntuacionMedia));

        List<PuntoInteresOutDto> result = puntoInteresRepository.findAll(spec).stream()
                .map(this::toOutDto)
                .toList();
        logger.info("Se encontraron {} puntos de interés", result.size());
        return result;
    }

    @Transactional(readOnly = true)
    public PuntoInteresOutDto findById(long id) throws PuntoInteresNotFoundException {
        logger.debug("Buscando punto de interés por ID: {}", id);
        PuntoInteres punto = puntoInteresRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Punto de interés no encontrado: ID {}", id);
                    return new PuntoInteresNotFoundException();
                });
        return toOutDto(punto);
    }

    public PuntoInteresOutDto modify(long id, PuntoInteresInDto dto)
            throws PuntoInteresNotFoundException, CategoriaNotFoundException {
        logger.info("Modificando punto de interés ID: {}", id);

        PuntoInteres existing = puntoInteresRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Punto de interés no encontrado para modificación: ID {}", id);
                    return new PuntoInteresNotFoundException();
                });

        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> {
                    logger.error("Categoría no encontrada: ID {}", dto.getCategoriaId());
                    return new CategoriaNotFoundException();
                });

        modelMapper.map(dto, existing);
        existing.setId(id);
        existing.setCategoria(categoria);

        PuntoInteres saved = puntoInteresRepository.save(existing);
        logger.info("Punto de interés modificado exitosamente: ID {}", id);
        return toOutDto(saved);
    }

    public PuntoInteresOutDto patch(long id, Map<String, Object> updates)
            throws PuntoInteresNotFoundException, CategoriaNotFoundException {
        logger.info("Aplicando PATCH a punto de interés ID: {} con {} campos", id, updates.size());

        PuntoInteres punto = puntoInteresRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Punto de interés no encontrado para PATCH: ID {}", id);
                    return new PuntoInteresNotFoundException();
                });

        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            String campo = entry.getKey();
            Object valor = entry.getValue();
            switch (campo) {
                case "categoriaId":
                    if (valor instanceof Number n) {
                        long catId = n.longValue();
                        Categoria categoria = categoriaRepository.findById(catId)
                                .orElseThrow(() -> {
                                    logger.error("Categoría no encontrada: ID {}", catId);
                                    return new CategoriaNotFoundException();
                                });
                        punto.setCategoria(categoria);
                        logger.debug("Relación categoría actualizada: ID {}", catId);
                    }
                    break;
                case "nombre":
                    punto.setNombre((String) valor);
                    logger.debug("Campo actualizado: nombre = {}", valor);
                    break;
                case "latitud":
                    punto.setLatitud(((Number) valor).doubleValue());
                    logger.debug("Campo actualizado: latitud = {}", valor);
                    break;
                case "longitud":
                    punto.setLongitud(((Number) valor).doubleValue());
                    logger.debug("Campo actualizado: longitud = {}", valor);
                    break;
                case "abiertoActualmente":
                    punto.setAbiertoActualmente((Boolean) valor);
                    logger.debug("Campo actualizado: abiertoActualmente = {}", valor);
                    break;
                case "puntuacionMedia":
                    punto.setPuntuacionMedia(((Number) valor).floatValue());
                    logger.debug("Campo actualizado: puntuacionMedia = {}", valor);
                    break;
                case "fechaCreacion":
                    if (valor instanceof String s)
                        punto.setFechaCreacion(LocalDateTime.parse(s));
                    logger.debug("Campo actualizado: fechaCreacion = {}", valor);
                    break;
                case "id":
                    logger.debug("Ignorando campo 'id' en PATCH");
                    break;
                default:
                    logger.warn("Campo desconocido ignorado en PATCH: {}", campo);
            }
        }

        PuntoInteres updated = puntoInteresRepository.save(punto);
        logger.info("Punto de interés actualizado exitosamente con PATCH: ID {}", id);
        return toOutDto(updated);
    }

    private PuntoInteresOutDto toOutDto(PuntoInteres punto) {
        PuntoInteresOutDto out = modelMapper.map(punto, PuntoInteresOutDto.class);
        if (punto.getCategoria() != null) {
            out.setCategoriaId(punto.getCategoria().getId());
            out.setCategoriaNombre(punto.getCategoria().getNombre());
        }
        return out;
    }
}
