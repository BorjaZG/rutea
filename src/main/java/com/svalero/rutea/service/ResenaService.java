package com.svalero.rutea.service;

import com.svalero.rutea.domain.Resena;
import com.svalero.rutea.exception.ResourceNotFoundException;
import com.svalero.rutea.repository.ResenaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ResenaService {

    @Autowired private ResenaRepository resenaRepository;

    public List<Resena> findAll() {
        return (List<Resena>) resenaRepository.findAll();
    }

    public List<Resena> filtrarResenas(int valoracion) {
        return resenaRepository.findByValoracion(valoracion);
    }

    public Resena findById(Long id) {
        return resenaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reseña no encontrada con ID: " + id));
    }

    public Resena save(Resena resena) {
        return resenaRepository.save(resena);
    }

    public Resena update(Long id, Resena nuevaResena) {
        Resena resena = findById(id);
        resena.setTitulo(nuevaResena.getTitulo());
        resena.setComentario(nuevaResena.getComentario());
        resena.setValoracion(nuevaResena.getValoracion());
        resena.setLikes(nuevaResena.getLikes());
        resena.setEditada(true); // Marcamos como editada automáticamente
        return resenaRepository.save(resena);
    }

    public void delete(Long id) {
        Resena resena = findById(id);
        resenaRepository.delete(resena);
    }
}