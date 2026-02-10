package com.svalero.rutea.repository;

import com.svalero.rutea.domain.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    // ----------------- Filtros -----------------
    // 1 campo
    List<Categoria> findByActiva(boolean activa);
    List<Categoria> findByNombreContainingIgnoreCase(String nombre);
    List<Categoria> findByOrdenPrioridad(int ordenPrioridad);

    // 2 campos
    List<Categoria> findByActivaAndNombreContainingIgnoreCase(boolean activa, String nombre);
    List<Categoria> findByActivaAndOrdenPrioridad(boolean activa, int ordenPrioridad);
    List<Categoria> findByNombreContainingIgnoreCaseAndOrdenPrioridad(String nombre, int ordenPrioridad);

    // 3 campos
    List<Categoria> findByActivaAndNombreContainingIgnoreCaseAndOrdenPrioridad(
            boolean activa, String nombre, int ordenPrioridad);
}