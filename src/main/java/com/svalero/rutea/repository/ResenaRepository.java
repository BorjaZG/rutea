package com.svalero.rutea.repository;

import com.svalero.rutea.domain.Resena;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ResenaRepository extends CrudRepository<Resena, Long> {
    List<Resena> findByValoracion(int valoracion);
}