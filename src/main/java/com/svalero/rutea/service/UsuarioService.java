package com.svalero.rutea.service;

import com.svalero.rutea.domain.Usuario;
import com.svalero.rutea.exception.ResourceNotFoundException;
import com.svalero.rutea.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UsuarioService {

    @Autowired private UsuarioRepository usuarioRepository;

    // 1. Listar todos
    public List<Usuario> findAll() {
        return (List<Usuario>) usuarioRepository.findAll();
    }

    public List<Usuario> filtrarUsuarios(int experiencia, boolean premium) {
        return usuarioRepository.findByNivelExperienciaAndEsPremium(experiencia, premium);
    }

    // 2. Buscar por ID (con control de error 404)
    public Usuario findById(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
    }

    // 3. Guardar nuevo
    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    // 4. Modificar existente
    public Usuario update(Long id, Usuario nuevoUsuario) {
        Usuario usuario = findById(id); // Busca o falla
        // Actualizamos los datos
        usuario.setUsername(nuevoUsuario.getUsername());
        usuario.setEmail(nuevoUsuario.getEmail());
        usuario.setPassword(nuevoUsuario.getPassword());
        usuario.setNivelExperiencia(nuevoUsuario.getNivelExperiencia());
        usuario.setEsPremium(nuevoUsuario.isEsPremium());
        // No cambiamos la fecha de registro original
        return usuarioRepository.save(usuario);
    }

    // 5. Eliminar
    public void delete(Long id) {
        Usuario usuario = findById(id); // Verificamos que existe antes de borrar
        usuarioRepository.delete(usuario);
    }
}