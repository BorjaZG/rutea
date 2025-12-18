package com.svalero.rutea.service;

import com.svalero.rutea.domain.PuntoInteres;
import com.svalero.rutea.exception.ResourceNotFoundException;
import com.svalero.rutea.repository.PuntoInteresRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PuntoInteresService {

    @Autowired private PuntoInteresRepository puntoRepository;

    public List<PuntoInteres> findAll() {
        return (List<PuntoInteres>) puntoRepository.findAll();
    }

    public List<PuntoInteres> filtrarPuntos(boolean abierto, float puntuacionMin) {
        return puntoRepository.findByAbiertoActualmenteAndPuntuacionMediaGreaterThanEqual(abierto, puntuacionMin);
    }

    public PuntoInteres findById(Long id) {
        return puntoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Punto de interés no encontrado con ID: " + id));
    }

    public PuntoInteres save(PuntoInteres punto) {
        return puntoRepository.save(punto);
    }

    public PuntoInteres update(Long id, PuntoInteres nuevoPunto) {
        PuntoInteres punto = findById(id);
        punto.setNombre(nuevoPunto.getNombre());
        punto.setLatitud(nuevoPunto.getLatitud());
        punto.setLongitud(nuevoPunto.getLongitud());
        punto.setPuntuacionMedia(nuevoPunto.getPuntuacionMedia());
        punto.setAbiertoActualmente(nuevoPunto.isAbiertoActualmente());
        // No actualizamos fechaCreacion usualmente
        return puntoRepository.save(punto);
    }

    public void delete(Long id) {
        PuntoInteres punto = findById(id);
        puntoRepository.delete(punto);
    }
}