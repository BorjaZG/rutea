package com.svalero.rutea.repository;

import com.svalero.rutea.domain.Resena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Long> {

    // NUEVO (por si algún día quieres usarlo directo desde JPA)
    List<Resena> findByEditadaAndLikesAndValoracion(boolean editada, int likes, int valoracion);
}