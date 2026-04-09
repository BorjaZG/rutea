package com.svalero.rutea.repository;

import com.svalero.rutea.domain.Resena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Long>, JpaSpecificationExecutor<Resena> {
}
