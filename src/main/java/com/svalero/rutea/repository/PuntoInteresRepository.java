package com.svalero.rutea.repository;

import com.svalero.rutea.domain.PuntoInteres;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PuntoInteresRepository extends JpaRepository<PuntoInteres, Long> {
}