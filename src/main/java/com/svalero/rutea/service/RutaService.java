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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RutaService {

    @Autowired
    private RutaRepository rutaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PuntoInteresRepository puntoInteresRepository;
    @Autowired
    private ModelMapper modelMapper;

    public RutaOutDto add(RutaInDto dto) throws UsuarioNotFoundException, PuntoInteresNotFoundException {
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(UsuarioNotFoundException::new);

        Ruta ruta = modelMapper.map(dto, Ruta.class);
        ruta.setUsuario(usuario);
        ruta.setPuntos(fetchPuntos(dto.getPuntosIds()));

        Ruta saved = rutaRepository.save(ruta);
        return toOutDto(saved);
    }

    public void delete(long id) throws RutaNotFoundException {
        Ruta ruta = rutaRepository.findById(id)
                .orElseThrow(RutaNotFoundException::new);
        rutaRepository.delete(ruta);
    }

    // NUEVO: Filtros (hasta 3): dificultad + publica + titulo contiene
    public List<RutaOutDto> findAll(String dificultad, Boolean publica, String titulo) {
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
        Ruta ruta = rutaRepository.findById(id)
                .orElseThrow(RutaNotFoundException::new);
        return toOutDto(ruta);
    }

    public RutaOutDto modify(long id, RutaInDto dto)
            throws RutaNotFoundException, UsuarioNotFoundException, PuntoInteresNotFoundException {

        Ruta existing = rutaRepository.findById(id)
                .orElseThrow(RutaNotFoundException::new);

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(UsuarioNotFoundException::new);

        modelMapper.map(dto, existing);
        existing.setId(id);
        existing.setUsuario(usuario);
        existing.setPuntos(fetchPuntos(dto.getPuntosIds()));

        Ruta saved = rutaRepository.save(existing);
        return toOutDto(saved);
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