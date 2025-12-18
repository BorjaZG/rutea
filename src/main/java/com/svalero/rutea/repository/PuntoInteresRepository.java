package com.svalero.rutea.repository;

import com.svalero.rutea.domain.PuntoInteres;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PuntoInteresRepository extends CrudRepository<PuntoInteres, Long> {
    List<PuntoInteres> findByAbiertoActualmenteAndPuntuacionMediaGreaterThanEqual(boolean abierto, float puntuacionMinima);
}