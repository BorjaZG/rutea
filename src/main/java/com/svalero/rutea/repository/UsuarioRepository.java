package com.svalero.rutea.repository;
import com.svalero.rutea.domain.Usuario;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuario, Long> {
    // Filtro personalizado: Experiencia y Premium
    List<Usuario> findByNivelExperienciaAndEsPremium(int nivelExperiencia, boolean esPremium);
}