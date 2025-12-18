package com.svalero.rutea.repository;

import com.svalero.rutea.domain.Categoria;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CategoriaRepository extends CrudRepository<Categoria, Long> {
    List<Categoria> findByActivaAndCostePromedioLessThan(boolean activa, float costeMaximo);
}