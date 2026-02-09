package com.svalero.rutea.repository;

import com.svalero.rutea.domain.Usuario;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuario, Long> {

    List<Usuario> findAll();

    // 1 campo
    List<Usuario> findByEsPremium(boolean esPremium);
    List<Usuario> findByNivelExperiencia(int nivelExperiencia);
    List<Usuario> findByUsernameContainingIgnoreCase(String username);

    // 2 campos
    List<Usuario> findByEsPremiumAndNivelExperiencia(boolean esPremium, int nivelExperiencia);
    List<Usuario> findByEsPremiumAndUsernameContainingIgnoreCase(boolean esPremium, String username);
    List<Usuario> findByNivelExperienciaAndUsernameContainingIgnoreCase(int nivelExperiencia, String username);

    // 3 campos (premium + nivelExperiencia + username)
    List<Usuario> findByEsPremiumAndNivelExperienciaAndUsernameContainingIgnoreCase(
            boolean esPremium, int nivelExperiencia, String username);
}