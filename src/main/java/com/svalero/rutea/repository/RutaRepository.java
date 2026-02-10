package com.svalero.rutea.repository;

import com.svalero.rutea.domain.Ruta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RutaRepository extends JpaRepository<Ruta, Long> {

    // Hasta 3 filtros: dificultad + publica + titulo contiene
    List<Ruta> findByDificultadContainingIgnoreCaseAndPublicaAndTituloContainingIgnoreCase(
            String dificultad, boolean publica, String titulo
    );
}