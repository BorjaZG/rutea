package com.svalero.rutea.service;

import com.svalero.rutea.domain.PuntoInteres;
import com.svalero.rutea.domain.Resena;
import com.svalero.rutea.domain.Usuario;
import com.svalero.rutea.dto.ResenaInDto;
import com.svalero.rutea.dto.ResenaOutDto;
import com.svalero.rutea.exception.PuntoInteresNotFoundException;
import com.svalero.rutea.exception.ResenaNotFoundException;
import com.svalero.rutea.exception.UsuarioNotFoundException;
import com.svalero.rutea.repository.PuntoInteresRepository;
import com.svalero.rutea.repository.ResenaRepository;
import com.svalero.rutea.repository.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ResenaService {

    private static final Logger logger = LoggerFactory.getLogger(ResenaService.class);

    @Autowired
    private ResenaRepository resenaRepository;
    @Autowired
    private PuntoInteresRepository puntoInteresRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ModelMapper modelMapper;

    public ResenaOutDto add(ResenaInDto dto) throws PuntoInteresNotFoundException, UsuarioNotFoundException {
        logger.info("Creando nueva reseña para punto ID: {}", dto.getPuntoId());

        PuntoInteres punto = puntoInteresRepository.findById(dto.getPuntoId())
                .orElseThrow(() -> {
                    logger.error("Punto de interés no encontrado: ID {}", dto.getPuntoId());
                    return new PuntoInteresNotFoundException();
                });

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> {
                    logger.error("Usuario no encontrado: ID {}", dto.getUsuarioId());
                    return new UsuarioNotFoundException();
                });

        Resena resena = modelMapper.map(dto, Resena.class);
        resena.setPunto(punto);
        resena.setUsuario(usuario);

        Resena saved = resenaRepository.save(resena);
        logger.info("Reseña creada exitosamente con ID: {}", saved.getId());
        return toOutDto(saved);
    }

    public void delete(long id) throws ResenaNotFoundException {
        logger.info("Eliminando reseña ID: {}", id);
        Resena resena = resenaRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Reseña no encontrada para eliminación: ID {}", id);
                    return new ResenaNotFoundException();
                });
        resenaRepository.delete(resena);
        logger.info("Reseña eliminada exitosamente: ID {}", id);
    }

    @Transactional(readOnly = true)
    public List<ResenaOutDto> findAll(Boolean editada, Integer likes, Integer valoracion) {
        logger.debug("Buscando reseñas con filtros: editada={}, likes={}, valoracion={}",
                editada, likes, valoracion);

        Specification<Resena> spec = Specification.where(null);

        if (editada != null)
            spec = spec.and((root, query, cb) -> cb.equal(root.get("editada"), editada));

        if (likes != null)
            spec = spec.and((root, query, cb) -> cb.equal(root.get("likes"), likes));

        if (valoracion != null)
            spec = spec.and((root, query, cb) -> cb.equal(root.get("valoracion"), valoracion));

        List<ResenaOutDto> result = resenaRepository.findAll(spec).stream()
                .map(this::toOutDto)
                .toList();
        logger.info("Se encontraron {} reseñas", result.size());
        return result;
    }

    @Transactional(readOnly = true)
    public ResenaOutDto findById(long id) throws ResenaNotFoundException {
        logger.debug("Buscando reseña por ID: {}", id);
        Resena resena = resenaRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Reseña no encontrada: ID {}", id);
                    return new ResenaNotFoundException();
                });
        return toOutDto(resena);
    }

    public ResenaOutDto modify(long id, ResenaInDto dto)
            throws ResenaNotFoundException, PuntoInteresNotFoundException, UsuarioNotFoundException {
        logger.info("Modificando reseña ID: {}", id);

        Resena existing = resenaRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Reseña no encontrada para modificación: ID {}", id);
                    return new ResenaNotFoundException();
                });

        PuntoInteres punto = puntoInteresRepository.findById(dto.getPuntoId())
                .orElseThrow(() -> {
                    logger.error("Punto de interés no encontrado: ID {}", dto.getPuntoId());
                    return new PuntoInteresNotFoundException();
                });

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> {
                    logger.error("Usuario no encontrado: ID {}", dto.getUsuarioId());
                    return new UsuarioNotFoundException();
                });

        modelMapper.map(dto, existing);
        existing.setId(id);
        existing.setPunto(punto);
        existing.setUsuario(usuario);

        Resena saved = resenaRepository.save(existing);
        logger.info("Reseña modificada exitosamente: ID {}", id);
        return toOutDto(saved);
    }

    public ResenaOutDto patch(long id, Map<String, Object> updates)
            throws ResenaNotFoundException, PuntoInteresNotFoundException, UsuarioNotFoundException {
        logger.info("Aplicando PATCH a reseña ID: {} con {} campos", id, updates.size());

        Resena resena = resenaRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Reseña no encontrada para PATCH: ID {}", id);
                    return new ResenaNotFoundException();
                });

        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            String campo = entry.getKey();
            Object valor = entry.getValue();
            switch (campo) {
                case "puntoId":
                    if (valor instanceof Number n) {
                        long puntoId = n.longValue();
                        PuntoInteres punto = puntoInteresRepository.findById(puntoId)
                                .orElseThrow(() -> {
                                    logger.error("Punto de interés no encontrado: ID {}", puntoId);
                                    return new PuntoInteresNotFoundException();
                                });
                        resena.setPunto(punto);
                        logger.debug("Relación punto actualizada: ID {}", puntoId);
                    }
                    break;
                case "usuarioId":
                    if (valor instanceof Number n) {
                        long userId = n.longValue();
                        Usuario usuario = usuarioRepository.findById(userId)
                                .orElseThrow(() -> {
                                    logger.error("Usuario no encontrado: ID {}", userId);
                                    return new UsuarioNotFoundException();
                                });
                        resena.setUsuario(usuario);
                        logger.debug("Relación usuario actualizada: ID {}", userId);
                    }
                    break;
                case "comentario":
                    resena.setComentario((String) valor);
                    logger.debug("Campo actualizado: comentario");
                    break;
                case "editada":
                    resena.setEditada((Boolean) valor);
                    logger.debug("Campo actualizado: editada = {}", valor);
                    break;
                case "fechaPublicacion":
                    if (valor instanceof String s)
                        resena.setFechaPublicacion(LocalDate.parse(s));
                    logger.debug("Campo actualizado: fechaPublicacion = {}", valor);
                    break;
                case "likes":
                    resena.setLikes(((Number) valor).intValue());
                    logger.debug("Campo actualizado: likes = {}", valor);
                    break;
                case "titulo":
                    resena.setTitulo((String) valor);
                    logger.debug("Campo actualizado: titulo");
                    break;
                case "valoracion":
                    resena.setValoracion(((Number) valor).intValue());
                    logger.debug("Campo actualizado: valoracion = {}", valor);
                    break;
                case "id":
                    logger.debug("Ignorando campo 'id' en PATCH");
                    break;
                default:
                    logger.warn("Campo desconocido ignorado en PATCH: {}", campo);
            }
        }

        Resena updated = resenaRepository.save(resena);
        logger.info("Reseña actualizada exitosamente con PATCH: ID {}", id);
        return toOutDto(updated);
    }

    private ResenaOutDto toOutDto(Resena resena) {
        ResenaOutDto out = modelMapper.map(resena, ResenaOutDto.class);
        out.setPuntoId(resena.getPunto() != null ? resena.getPunto().getId() : null);
        out.setUsuarioId(resena.getUsuario() != null ? resena.getUsuario().getId() : null);
        return out;
    }
}
