package com.svalero.rutea.repository;

import com.svalero.rutea.domain.Ruta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RutaRepository extends JpaRepository<Ruta, Long>, JpaSpecificationExecutor<Ruta> {
}
