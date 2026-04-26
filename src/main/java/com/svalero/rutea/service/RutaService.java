package com.svalero.rutea.service;

import com.svalero.rutea.domain.PuntoInteres;
import com.svalero.rutea.domain.Ruta;
import com.svalero.rutea.domain.Usuario;
import com.svalero.rutea.dto.RutaInDto;
import com.svalero.rutea.dto.RutaInDtoV2;
import com.svalero.rutea.dto.RutaOutDto;
import com.svalero.rutea.dto.RutaOutDtoV2;
import com.svalero.rutea.exception.PuntoInteresNotFoundException;
import com.svalero.rutea.exception.RutaNotFoundException;
import com.svalero.rutea.exception.UsuarioNotFoundException;
import com.svalero.rutea.repository.PuntoInteresRepository;
import com.svalero.rutea.repository.RutaRepository;
import com.svalero.rutea.repository.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class RutaService {

    private static final Logger logger = LoggerFactory.getLogger(RutaService.class);

    @Autowired
    private RutaRepository rutaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PuntoInteresRepository puntoInteresRepository;
    @Autowired
    private ModelMapper modelMapper;

    public RutaOutDto add(RutaInDto dto) throws UsuarioNotFoundException, PuntoInteresNotFoundException {
        logger.info("Creando nueva ruta: {}", dto.getTitulo());

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> {
                    logger.error("Usuario no encontrado: ID {}", dto.getUsuarioId());
                    return new UsuarioNotFoundException();
                });

        Ruta ruta = modelMapper.map(dto, Ruta.class);
        ruta.setUsuario(usuario);
        ruta.setPuntos(fetchPuntos(dto.getPuntosIds()));

        Ruta saved = rutaRepository.save(ruta);
        logger.info("Ruta creada exitosamente con ID: {}", saved.getId());
        return toOutDto(saved);
    }

    public void delete(long id) throws RutaNotFoundException {
        logger.info("Eliminando ruta ID: {}", id);
        Ruta ruta = rutaRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Ruta no encontrada para eliminación: ID {}", id);
                    return new RutaNotFoundException();
                });
        rutaRepository.delete(ruta);
        logger.info("Ruta eliminada exitosamente: ID {}", id);
    }

    @Transactional(readOnly = true)
    public List<RutaOutDto> findAll(String dificultad, Boolean publica, String titulo) {
        logger.debug("Buscando rutas con filtros: dificultad={}, publica={}, titulo={}",
                dificultad, publica, titulo);

        Specification<Ruta> spec = Specification.where(
                (root, query, cb) -> cb.isFalse(root.get("eliminada")));

        if (dificultad != null && !dificultad.isBlank()) {
            String pattern = "%" + dificultad.toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("dificultad")), pattern));
        }

        if (publica != null)
            spec = spec.and((root, query, cb) -> cb.equal(root.get("publica"), publica));

        if (titulo != null && !titulo.isBlank()) {
            String pattern = "%" + titulo.toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("titulo")), pattern));
        }

        List<RutaOutDto> result = rutaRepository.findAll(spec).stream()
                .map(this::toOutDto)
                .toList();
        logger.info("Se encontraron {} rutas", result.size());
        return result;
    }

    @Transactional(readOnly = true)
    public RutaOutDto findById(long id) throws RutaNotFoundException {
        logger.debug("Buscando ruta por ID: {}", id);
        Ruta ruta = rutaRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Ruta no encontrada: ID {}", id);
                    return new RutaNotFoundException();
                });
        if (ruta.isEliminada()) {
            logger.error("Ruta no encontrada (eliminada): ID {}", id);
            throw new RutaNotFoundException();
        }
        return toOutDto(ruta);
    }

    @Transactional(readOnly = true)
    public RutaOutDtoV2 findByIdV2(long id) throws RutaNotFoundException {
        logger.debug("Buscando ruta v2 por ID: {}", id);
        Ruta ruta = rutaRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Ruta no encontrada v2: ID {}", id);
                    return new RutaNotFoundException();
                });
        if (ruta.isEliminada()) {
            logger.error("Ruta no encontrada v2 (eliminada): ID {}", id);
            throw new RutaNotFoundException();
        }
        return toOutDtoV2(ruta);
    }

    public RutaOutDto modify(long id, RutaInDto dto)
            throws RutaNotFoundException, UsuarioNotFoundException, PuntoInteresNotFoundException {
        logger.info("Modificando ruta ID: {}", id);

        Ruta existing = rutaRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Ruta no encontrada para modificación: ID {}", id);
                    return new RutaNotFoundException();
                });
        if (existing.isEliminada()) {
            logger.error("Ruta no encontrada para modificación (eliminada): ID {}", id);
            throw new RutaNotFoundException();
        }

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> {
                    logger.error("Usuario no encontrado: ID {}", dto.getUsuarioId());
                    return new UsuarioNotFoundException();
                });

        modelMapper.map(dto, existing);
        existing.setId(id);
        existing.setUsuario(usuario);
        existing.setPuntos(fetchPuntos(dto.getPuntosIds()));

        Ruta saved = rutaRepository.save(existing);
        logger.info("Ruta modificada exitosamente: ID {}", id);
        return toOutDto(saved);
    }

    public RutaOutDto patch(long id, Map<String, Object> updates)
            throws RutaNotFoundException, UsuarioNotFoundException, PuntoInteresNotFoundException {
        logger.info("Aplicando PATCH a ruta ID: {} con {} campos", id, updates.size());

        Ruta ruta = rutaRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Ruta no encontrada para PATCH: ID {}", id);
                    return new RutaNotFoundException();
                });
        if (ruta.isEliminada()) {
            logger.error("Ruta no encontrada para PATCH (eliminada): ID {}", id);
            throw new RutaNotFoundException();
        }

        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            String campo = entry.getKey();
            Object valor = entry.getValue();
            switch (campo) {
                case "usuarioId":
                    if (valor instanceof Number n) {
                        long userId = n.longValue();
                        Usuario usuario = usuarioRepository.findById(userId)
                                .orElseThrow(() -> {
                                    logger.error("Usuario no encontrado: ID {}", userId);
                                    return new UsuarioNotFoundException();
                                });
                        ruta.setUsuario(usuario);
                        logger.debug("Relación usuario actualizada: ID {}", userId);
                    }
                    break;
                case "puntosIds":
                    if (valor instanceof List<?> lista) {
                        List<Long> ids = lista.stream()
                                .filter(o -> o instanceof Number)
                                .map(o -> ((Number) o).longValue())
                                .collect(Collectors.toList());
                        ruta.setPuntos(fetchPuntos(ids));
                        logger.debug("Relación puntos actualizada: {} puntos", ids.size());
                    }
                    break;
                case "dificultad":
                    ruta.setDificultad((String) valor);
                    logger.debug("Campo actualizado: dificultad = {}", valor);
                    break;
                case "distanciaKm":
                    ruta.setDistanciaKm(((Number) valor).floatValue());
                    logger.debug("Campo actualizado: distanciaKm = {}", valor);
                    break;
                case "duracionMinutos":
                    ruta.setDuracionMinutos(((Number) valor).intValue());
                    logger.debug("Campo actualizado: duracionMinutos = {}", valor);
                    break;
                case "fechaRealizacion":
                    if (valor instanceof String s)
                        ruta.setFechaRealizacion(LocalDate.parse(s));
                    logger.debug("Campo actualizado: fechaRealizacion = {}", valor);
                    break;
                case "publica":
                    ruta.setPublica((Boolean) valor);
                    logger.debug("Campo actualizado: publica = {}", valor);
                    break;
                case "titulo":
                    ruta.setTitulo((String) valor);
                    logger.debug("Campo actualizado: titulo = {}", valor);
                    break;
                case "id":
                    logger.debug("Ignorando campo 'id' en PATCH");
                    break;
                default:
                    logger.warn("Campo desconocido ignorado en PATCH: {}", campo);
            }
        }

        Ruta updated = rutaRepository.save(ruta);
        logger.info("Ruta actualizada exitosamente con PATCH: ID {}", id);
        return toOutDto(updated);
    }

    private List<PuntoInteres> fetchPuntos(List<Long> puntosIds) throws PuntoInteresNotFoundException {
        if (puntosIds == null || puntosIds.isEmpty()) return new ArrayList<>();
        List<PuntoInteres> puntos = new ArrayList<>();
        for (Long pid : puntosIds) {
            PuntoInteres p = puntoInteresRepository.findById(pid)
                    .orElseThrow(() -> {
                        logger.error("Punto de interés no encontrado: ID {}", pid);
                        return new PuntoInteresNotFoundException();
                    });
            puntos.add(p);
        }
        return puntos;
    }

    // -------------------- V2 --------------------

    @Transactional(readOnly = true)
    public List<RutaOutDtoV2> findAllV2(String dificultad, Boolean publica, String titulo) {
        logger.debug("Buscando rutas v2 con filtros: dificultad={}, publica={}, titulo={}",
                dificultad, publica, titulo);

        Specification<Ruta> spec = Specification.where(
                (root, query, cb) -> cb.isFalse(root.get("eliminada")));

        if (dificultad != null && !dificultad.isBlank()) {
            String pattern = "%" + dificultad.toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("dificultad")), pattern));
        }
        if (publica != null)
            spec = spec.and((root, query, cb) -> cb.equal(root.get("publica"), publica));
        if (titulo != null && !titulo.isBlank()) {
            String pattern = "%" + titulo.toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("titulo")), pattern));
        }

        List<RutaOutDtoV2> result = rutaRepository.findAll(spec).stream()
                .map(this::toOutDtoV2)
                .toList();
        logger.info("Se encontraron {} rutas (v2)", result.size());
        return result;
    }

    public RutaOutDtoV2 addV2(RutaInDtoV2 dto) throws UsuarioNotFoundException, PuntoInteresNotFoundException {
        logger.info("Creando nueva ruta v2: {}", dto.getTitulo());

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> {
                    logger.error("Usuario no encontrado: ID {}", dto.getUsuarioId());
                    return new UsuarioNotFoundException();
                });

        Ruta ruta = modelMapper.map(dto, Ruta.class);
        ruta.setUsuario(usuario);
        ruta.setPuntos(fetchPuntos(dto.getPuntosIds()));

        Ruta saved = rutaRepository.save(ruta);
        logger.info("Ruta v2 creada exitosamente con ID: {}", saved.getId());
        return toOutDtoV2(saved);
    }

    public RutaOutDtoV2 modifyV2(long id, RutaInDtoV2 dto)
            throws RutaNotFoundException, UsuarioNotFoundException, PuntoInteresNotFoundException {
        logger.info("Modificando ruta v2 ID: {}", id);

        Ruta existing = rutaRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Ruta no encontrada para modificación v2: ID {}", id);
                    return new RutaNotFoundException();
                });
        if (existing.isEliminada()) {
            logger.error("Ruta no encontrada para modificación v2 (eliminada): ID {}", id);
            throw new RutaNotFoundException();
        }

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> {
                    logger.error("Usuario no encontrado: ID {}", dto.getUsuarioId());
                    return new UsuarioNotFoundException();
                });

        modelMapper.map(dto, existing);
        existing.setId(id);
        existing.setUsuario(usuario);
        existing.setPuntos(fetchPuntos(dto.getPuntosIds()));

        Ruta saved = rutaRepository.save(existing);
        logger.info("Ruta v2 modificada exitosamente: ID {}", id);
        return toOutDtoV2(saved);
    }

    public void softDelete(long id) throws RutaNotFoundException {
        logger.info("Soft-delete ruta ID: {}", id);
        Ruta ruta = rutaRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Ruta no encontrada para soft-delete: ID {}", id);
                    return new RutaNotFoundException();
                });
        if (ruta.isEliminada()) {
            logger.error("Ruta ya eliminada: ID {}", id);
            throw new RutaNotFoundException();
        }
        ruta.setEliminada(true);
        rutaRepository.save(ruta);
        logger.info("Ruta marcada como eliminada: ID {}", id);
    }

    // -------------------- Helpers --------------------

    private RutaOutDto toOutDto(Ruta ruta) {
        RutaOutDto out = modelMapper.map(ruta, RutaOutDto.class);
        out.setUsuarioId(ruta.getUsuario() != null ? ruta.getUsuario().getId() : null);
        out.setPuntosIds(ruta.getPuntos() == null ? List.of() :
                ruta.getPuntos().stream().map(PuntoInteres::getId).collect(Collectors.toList()));
        return out;
    }

    private RutaOutDtoV2 toOutDtoV2(Ruta ruta) {
        RutaOutDtoV2 out = modelMapper.map(ruta, RutaOutDtoV2.class);
        out.setUsuarioId(ruta.getUsuario() != null ? ruta.getUsuario().getId() : null);
        List<Long> puntosIds = ruta.getPuntos() == null ? List.of() :
                ruta.getPuntos().stream().map(PuntoInteres::getId).collect(Collectors.toList());
        out.setPuntosIds(puntosIds);
        out.setTotalPuntos(puntosIds.size());
        return out;
    }
}
