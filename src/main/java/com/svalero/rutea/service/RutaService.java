package com.svalero.rutea.service;

import com.svalero.rutea.domain.Ruta;
import com.svalero.rutea.exception.ResourceNotFoundException;
import com.svalero.rutea.repository.RutaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RutaService {

    @Autowired private RutaRepository rutaRepository;

    public List<Ruta> findAll() {
        return (List<Ruta>) rutaRepository.findAll();
    }

    public List<Ruta> filtrarRutas(String dificultad) {
        return rutaRepository.findByDificultad(dificultad);
    }

    public Ruta findById(Long id) {
        return rutaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ruta no encontrada con ID: " + id));
    }

    public Ruta save(Ruta ruta) {
        return rutaRepository.save(ruta);
    }

    public Ruta update(Long id, Ruta nuevaRuta) {
        Ruta ruta = findById(id);
        ruta.setTitulo(nuevaRuta.getTitulo());
        ruta.setDificultad(nuevaRuta.getDificultad());
        ruta.setDistanciaKm(nuevaRuta.getDistanciaKm());
        ruta.setDuracionMinutos(nuevaRuta.getDuracionMinutos());
        ruta.setPublica(nuevaRuta.isPublica());
        ruta.setFechaRealizacion(nuevaRuta.getFechaRealizacion());
        return rutaRepository.save(ruta);
    }

    public void delete(Long id) {
        Ruta ruta = findById(id);
        rutaRepository.delete(ruta);
    }
}