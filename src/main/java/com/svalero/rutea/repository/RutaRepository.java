package com.svalero.rutea.repository;

import com.svalero.rutea.domain.Ruta;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RutaRepository extends CrudRepository<Ruta, Long> {

    List<Ruta> findAll();

    // Hasta 3 filtros: dificultad + publica + titulo contiene
    List<Ruta> findByDificultadContainingIgnoreCaseAndPublicaAndTituloContainingIgnoreCase(
            String dificultad, boolean publica, String titulo
    );
}