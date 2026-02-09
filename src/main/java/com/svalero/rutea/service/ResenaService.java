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
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResenaService {

    @Autowired
    private ResenaRepository resenaRepository;
    @Autowired
    private PuntoInteresRepository puntoInteresRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ModelMapper modelMapper;

    public ResenaOutDto add(ResenaInDto dto)
            throws PuntoInteresNotFoundException, UsuarioNotFoundException {

        PuntoInteres punto = puntoInteresRepository.findById(dto.getPuntoId())
                .orElseThrow(PuntoInteresNotFoundException::new);

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(UsuarioNotFoundException::new);

        Resena resena = modelMapper.map(dto, Resena.class);
        resena.setPunto(punto);
        resena.setUsuario(usuario);

        Resena saved = resenaRepository.save(resena);
        return toOutDto(saved);
    }

    public void delete(long id) throws ResenaNotFoundException {
        Resena resena = resenaRepository.findById(id)
                .orElseThrow(ResenaNotFoundException::new);
        resenaRepository.delete(resena);
    }

    // NUEVO: Filtros (hasta 3): editada + likes + valoracion
    public List<ResenaOutDto> findAll(Boolean editada, Integer likes, Integer valoracion) {
        List<Resena> resenas = resenaRepository.findAll();

        if (editada != null) {
            resenas = resenas.stream()
                    .filter(r -> r.isEditada() == editada)
                    .toList();
        }

        if (likes != null) {
            resenas = resenas.stream()
                    .filter(r -> r.getLikes() == likes)
                    .toList();
        }

        if (valoracion != null) {
            resenas = resenas.stream()
                    .filter(r -> r.getValoracion() == valoracion)
                    .toList();
        }

        return modelMapper.map(resenas, new TypeToken<List<ResenaOutDto>>() {}.getType());
    }

    public ResenaOutDto findById(long id) throws ResenaNotFoundException {
        Resena resena = resenaRepository.findById(id)
                .orElseThrow(ResenaNotFoundException::new);
        return toOutDto(resena);
    }

    public ResenaOutDto modify(long id, ResenaInDto dto)
            throws ResenaNotFoundException, PuntoInteresNotFoundException, UsuarioNotFoundException {

        Resena existing = resenaRepository.findById(id)
                .orElseThrow(ResenaNotFoundException::new);

        PuntoInteres punto = puntoInteresRepository.findById(dto.getPuntoId())
                .orElseThrow(PuntoInteresNotFoundException::new);

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(UsuarioNotFoundException::new);

        modelMapper.map(dto, existing);
        existing.setId(id);
        existing.setPunto(punto);
        existing.setUsuario(usuario);

        Resena saved = resenaRepository.save(existing);
        return toOutDto(saved);
    }

    private ResenaOutDto toOutDto(Resena resena) {
        ResenaOutDto out = modelMapper.map(resena, ResenaOutDto.class);
        out.setPuntoId(resena.getPunto() != null ? resena.getPunto().getId() : null);
        out.setUsuarioId(resena.getUsuario() != null ? resena.getUsuario().getId() : null);
        return out;
    }
}