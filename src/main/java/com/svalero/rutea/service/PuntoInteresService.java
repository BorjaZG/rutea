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
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PuntoInteresService {

    @Autowired
    private PuntoInteresRepository puntoInteresRepository;
    @Autowired
    private CategoriaRepository categoriaRepository;
    @Autowired
    private ModelMapper modelMapper;

    public PuntoInteresOutDto add(PuntoInteresInDto dto) throws CategoriaNotFoundException {
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(CategoriaNotFoundException::new);

        PuntoInteres punto = modelMapper.map(dto, PuntoInteres.class);
        punto.setCategoria(categoria);

        PuntoInteres saved = puntoInteresRepository.save(punto);
        return toOutDto(saved);
    }

    public void delete(long id) throws PuntoInteresNotFoundException {
        PuntoInteres punto = puntoInteresRepository.findById(id)
                .orElseThrow(PuntoInteresNotFoundException::new);
        puntoInteresRepository.delete(punto);
    }

    // NUEVO: filtros (hasta 3): abiertoActualmente + nombre + puntuacionMedia
    // (mantengo categoriaId opcional)
    public List<PuntoInteresOutDto> findAll(Long categoriaId, Boolean abiertoActualmente, String nombre, Float puntuacionMedia) {
        List<PuntoInteres> puntos = puntoInteresRepository.findAll();

        if (categoriaId != null) {
            puntos = puntos.stream()
                    .filter(p -> p.getCategoria() != null && p.getCategoria().getId() == categoriaId)
                    .toList();
        }

        if (abiertoActualmente != null) {
            puntos = puntos.stream()
                    .filter(p -> p.isAbiertoActualmente() == abiertoActualmente)
                    .toList();
        }

        if (nombre != null && !nombre.isBlank()) {
            String safe = nombre.toLowerCase();
            puntos = puntos.stream()
                    .filter(p -> p.getNombre() != null && p.getNombre().toLowerCase().contains(safe))
                    .toList();
        }

        if (puntuacionMedia != null) {
            final double eps = 0.0001; // tolerancia decimales
            double target = puntuacionMedia.doubleValue();
            puntos = puntos.stream()
                    .filter(p -> Math.abs(p.getPuntuacionMedia() - target) < eps)
                    .toList();
        }

        return modelMapper.map(puntos, new TypeToken<List<PuntoInteresOutDto>>() {}.getType());
    }

    public PuntoInteresOutDto findById(long id) throws PuntoInteresNotFoundException {
        PuntoInteres punto = puntoInteresRepository.findById(id)
                .orElseThrow(PuntoInteresNotFoundException::new);
        return toOutDto(punto);
    }

    public PuntoInteresOutDto modify(long id, PuntoInteresInDto dto)
            throws PuntoInteresNotFoundException, CategoriaNotFoundException {

        PuntoInteres existing = puntoInteresRepository.findById(id)
                .orElseThrow(PuntoInteresNotFoundException::new);

        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(CategoriaNotFoundException::new);

        modelMapper.map(dto, existing);
        existing.setId(id);
        existing.setCategoria(categoria);

        PuntoInteres saved = puntoInteresRepository.save(existing);
        return toOutDto(saved);
    }

    private PuntoInteresOutDto toOutDto(PuntoInteres punto) {
        PuntoInteresOutDto out = modelMapper.map(punto, PuntoInteresOutDto.class);
        out.setCategoriaId(punto.getCategoria() != null ? punto.getCategoria().getId() : null);
        return out;
    }
}