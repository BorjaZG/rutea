package com.svalero.rutea.service;

import com.svalero.rutea.domain.Usuario;
import com.svalero.rutea.dto.UsuarioInDto;
import com.svalero.rutea.dto.UsuarioOutDto;
import com.svalero.rutea.exception.UsuarioNotFoundException;
import com.svalero.rutea.repository.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ModelMapper modelMapper;

    public UsuarioOutDto add(UsuarioInDto usuarioInDto) {
        logger.info("Creando nuevo usuario: {}", usuarioInDto.getUsername());
        try {
            Usuario usuario = modelMapper.map(usuarioInDto, Usuario.class);
            Usuario saved = usuarioRepository.save(usuario);
            logger.info("Usuario creado exitosamente con ID: {}", saved.getId());
            return modelMapper.map(saved, UsuarioOutDto.class);
        } catch (Exception e) {
            logger.error("Error al crear usuario: {}", e.getMessage(), e);
            throw e;
        }
    }

    public void delete(long id) throws UsuarioNotFoundException {
        logger.info("Eliminando usuario ID: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Usuario no encontrado para eliminación: ID {}", id);
                    return new UsuarioNotFoundException();
                });
        usuarioRepository.delete(usuario);
        logger.info("Usuario eliminado exitosamente: ID {}", id);
    }

    // Filtros (hasta 3): premium + nivelExperiencia + username
    public List<UsuarioOutDto> findAll(Boolean premium, Integer nivelExperiencia, String username) {
        logger.debug("Buscando usuarios con filtros: premium={}, nivelExperiencia={}, username={}",
                premium, nivelExperiencia, username);

        boolean hasPremium = (premium != null);
        boolean hasNivel = (nivelExperiencia != null);
        boolean hasUsername = (username != null && !username.isBlank());

        List<Usuario> usuarios;

        // 3 campos
        if (hasPremium && hasNivel && hasUsername) {
            usuarios = usuarioRepository.findByEsPremiumAndNivelExperienciaAndUsernameContainingIgnoreCase(
                    premium, nivelExperiencia, username);

            // 2 campos
        } else if (hasPremium && hasNivel) {
            usuarios = usuarioRepository.findByEsPremiumAndNivelExperiencia(premium, nivelExperiencia);
        } else if (hasPremium && hasUsername) {
            usuarios = usuarioRepository.findByEsPremiumAndUsernameContainingIgnoreCase(premium, username);
        } else if (hasNivel && hasUsername) {
            usuarios = usuarioRepository.findByNivelExperienciaAndUsernameContainingIgnoreCase(nivelExperiencia, username);

            // 1 campo
        } else if (hasPremium) {
            usuarios = usuarioRepository.findByEsPremium(premium);
        } else if (hasNivel) {
            usuarios = usuarioRepository.findByNivelExperiencia(nivelExperiencia);
        } else if (hasUsername) {
            usuarios = usuarioRepository.findByUsernameContainingIgnoreCase(username);

            // sin filtros
        } else {
            usuarios = usuarioRepository.findAll();
        }

        logger.info("Se encontraron {} usuarios", usuarios.size());
        return modelMapper.map(usuarios, new TypeToken<List<UsuarioOutDto>>() {}.getType());
    }

    public UsuarioOutDto findById(long id) throws UsuarioNotFoundException {
        logger.debug("Buscando usuario por ID: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Usuario no encontrado: ID {}", id);
                    return new UsuarioNotFoundException();
                });
        return modelMapper.map(usuario, UsuarioOutDto.class);
    }

    public UsuarioOutDto modify(long id, UsuarioInDto usuarioInDto) throws UsuarioNotFoundException {
        logger.info("Modificando usuario ID: {}", id);
        Usuario existing = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Usuario no encontrado para modificación: ID {}", id);
                    return new UsuarioNotFoundException();
                });

        modelMapper.map(usuarioInDto, existing);
        existing.setId(id);

        Usuario saved = usuarioRepository.save(existing);
        logger.info("Usuario modificado exitosamente: ID {}", id);
        return modelMapper.map(saved, UsuarioOutDto.class);
    }

    /**
     * Operación PATCH - Actualización parcial de usuario
     * Permite modificar cualquier atributo especificado
     */
    public UsuarioOutDto patch(long id, Map<String, Object> updates) throws UsuarioNotFoundException {
        logger.info("Aplicando PATCH a usuario ID: {} con {} campos", id, updates.size());
        logger.debug("Campos a actualizar: {}", updates.keySet());

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Usuario no encontrado para PATCH: ID {}", id);
                    return new UsuarioNotFoundException();
                });

        // Actualizar campos usando setters
        updates.forEach((campo, valor) -> {
            switch (campo) {
                case "email":
                    usuario.setEmail((String) valor);
                    logger.debug("Campo actualizado: email = {}", valor);
                    break;

                case "esPremium":
                    usuario.setEsPremium((Boolean) valor);
                    logger.debug("Campo actualizado: esPremium = {}", valor);
                    break;

                case "fechaRegistro":
                    if (valor instanceof String) {
                        usuario.setFechaRegistro(LocalDate.parse((String) valor));
                    } else if (valor instanceof LocalDate) {
                        usuario.setFechaRegistro((LocalDate) valor);
                    }
                    logger.debug("Campo actualizado: fechaRegistro = {}", valor);
                    break;

                case "nivelExperiencia":
                    usuario.setNivelExperiencia(((Number) valor).intValue());
                    logger.debug("Campo actualizado: nivelExperiencia = {}", valor);
                    break;

                case "password":
                    usuario.setPassword((String) valor);
                    logger.debug("Campo actualizado: password = ****");
                    break;

                case "username":
                    usuario.setUsername((String) valor);
                    logger.debug("Campo actualizado: username = {}", valor);
                    break;

                case "id":
                    logger.debug("Ignorando campo 'id' en PATCH");
                    break;

                default:
                    logger.warn("Campo desconocido: {}", campo);
            }
        });

        Usuario updated = usuarioRepository.save(usuario);
        logger.info("Usuario actualizado exitosamente con PATCH: ID {}", id);
        return modelMapper.map(updated, UsuarioOutDto.class);
    }
}