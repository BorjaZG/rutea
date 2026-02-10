package com.svalero.rutea.service;

import com.svalero.rutea.domain.PuntoInteres;
import com.svalero.rutea.domain.Ruta;
import com.svalero.rutea.domain.Usuario;
import com.svalero.rutea.dto.RutaInDto;
import com.svalero.rutea.dto.RutaOutDto;
import com.svalero.rutea.exception.PuntoInteresNotFoundException;
import com.svalero.rutea.exception.RutaNotFoundException;
import com.svalero.rutea.exception.UsuarioNotFoundException;
import com.svalero.rutea.repository.PuntoInteresRepository;
import com.svalero.rutea.repository.RutaRepository;
import com.svalero.rutea.repository.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
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
        try {
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
        } catch (UsuarioNotFoundException | PuntoInteresNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error al crear ruta: {}", e.getMessage(), e);
            throw e;
        }
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

    // NUEVO: Filtros (hasta 3): dificultad + publica + titulo contiene
    public List<RutaOutDto> findAll(String dificultad, Boolean publica, String titulo) {
        logger.debug("Buscando rutas con filtros: dificultad={}, publica={}, titulo={}",
                dificultad, publica, titulo);

        List<Ruta> rutas = rutaRepository.findAll();

        if (dificultad != null && !dificultad.isBlank()) {
            String safe = dificultad.toLowerCase();
            rutas = rutas.stream()
                    .filter(r -> r.getDificultad() != null && r.getDificultad().toLowerCase().contains(safe))
                    .toList();
        }

        if (publica != null) {
            rutas = rutas.stream()
                    .filter(r -> r.isPublica() == publica)
                    .toList();
        }

        if (titulo != null && !titulo.isBlank()) {
            String safe = titulo.toLowerCase();
            rutas = rutas.stream()
                    .filter(r -> r.getTitulo() != null && r.getTitulo().toLowerCase().contains(safe))
                    .toList();
        }

        logger.info("Se encontraron {} rutas", rutas.size());

        List<RutaOutDto> out = modelMapper.map(rutas, new TypeToken<List<RutaOutDto>>() {}.getType());

        // completar ids (usuarioId y puntosIds)
        for (int i = 0; i < rutas.size(); i++) {
            out.get(i).setUsuarioId(rutas.get(i).getUsuario() != null ? rutas.get(i).getUsuario().getId() : null);
            out.get(i).setPuntosIds(
                    rutas.get(i).getPuntos() == null ? List.of() :
                            rutas.get(i).getPuntos().stream().map(PuntoInteres::getId).toList()
            );
        }

        return out;
    }

    public RutaOutDto findById(long id) throws RutaNotFoundException {
        logger.debug("Buscando ruta por ID: {}", id);
        Ruta ruta = rutaRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Ruta no encontrada: ID {}", id);
                    return new RutaNotFoundException();
                });
        return toOutDto(ruta);
    }

    public RutaOutDto modify(long id, RutaInDto dto)
            throws RutaNotFoundException, UsuarioNotFoundException, PuntoInteresNotFoundException {
        logger.info("Modificando ruta ID: {}", id);

        Ruta existing = rutaRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Ruta no encontrada para modificación: ID {}", id);
                    return new RutaNotFoundException();
                });

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

    /**
     * Operación PATCH - Actualización parcial de ruta
     */
    public RutaOutDto patch(long id, Map<String, Object> updates) throws RutaNotFoundException {
        logger.info("Aplicando PATCH a ruta ID: {} con {} campos", id, updates.size());
        logger.debug("Campos a actualizar: {}", updates.keySet());

        Ruta ruta = rutaRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Ruta no encontrada para PATCH: ID {}", id);
                    return new RutaNotFoundException();
                });

        // Actualizar campos usando setters
        updates.forEach((campo, valor) -> {
            switch (campo) {
                case "usuarioId":
                    if (valor != null && valor instanceof Number) {
                        long userId = ((Number) valor).longValue();
                        Usuario usuario = usuarioRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + userId));
                        ruta.setUsuario(usuario);
                        logger.debug("Relación usuario actualizada: ID {}", userId);
                    }
                    break;

                case "puntosIds":
                    if (valor instanceof List) {
                        @SuppressWarnings("unchecked")
                        List<Number> puntosIds = (List<Number>) valor;
                        try {
                            List<Long> ids = puntosIds.stream()
                                    .map(Number::longValue)
                                    .collect(Collectors.toList());
                            List<PuntoInteres> puntos = fetchPuntos(ids);
                            ruta.setPuntos(puntos);
                            logger.debug("Relación puntos actualizada: {} puntos", puntos.size());
                        } catch (PuntoInteresNotFoundException e) {
                            throw new RuntimeException("Error actualizando puntos", e);
                        }
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
                    if (valor instanceof String) {
                        ruta.setFechaRealizacion(LocalDate.parse((String) valor));
                    } else if (valor instanceof LocalDate) {
                        ruta.setFechaRealizacion((LocalDate) valor);
                    }
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
                    logger.warn("Campo desconocido: {}", campo);
            }
        });

        Ruta updated = rutaRepository.save(ruta);
        logger.info("Ruta actualizada exitosamente con PATCH: ID {}", id);
        return toOutDto(updated);
    }

    private List<PuntoInteres> fetchPuntos(List<Long> puntosIds) throws PuntoInteresNotFoundException {
        if (puntosIds == null || puntosIds.isEmpty()) return new ArrayList<>();

        List<PuntoInteres> puntos = new ArrayList<>();
        for (Long pid : puntosIds) {
            PuntoInteres p = puntoInteresRepository.findById(pid)
                    .orElseThrow(PuntoInteresNotFoundException::new);
            puntos.add(p);
        }
        return puntos;
    }

    private RutaOutDto toOutDto(Ruta ruta) {
        RutaOutDto out = modelMapper.map(ruta, RutaOutDto.class);
        out.setUsuarioId(ruta.getUsuario() != null ? ruta.getUsuario().getId() : null);
        out.setPuntosIds(
                ruta.getPuntos() == null ? List.of() :
                        ruta.getPuntos().stream().map(PuntoInteres::getId).collect(Collectors.toList())
        );
        return out;
    }
}