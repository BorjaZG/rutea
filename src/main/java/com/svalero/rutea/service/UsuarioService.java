package com.svalero.rutea.service;

import com.svalero.rutea.domain.Usuario;
import com.svalero.rutea.dto.UsuarioInDto;
import com.svalero.rutea.dto.UsuarioOutDto;
import com.svalero.rutea.exception.UsuarioNotFoundException;
import com.svalero.rutea.repository.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ModelMapper modelMapper;

    public UsuarioOutDto add(UsuarioInDto usuarioInDto) {
        logger.info("Creando nuevo usuario: {}", usuarioInDto.getUsername());
        Usuario usuario = modelMapper.map(usuarioInDto, Usuario.class);
        Usuario saved = usuarioRepository.save(usuario);
        logger.info("Usuario creado exitosamente con ID: {}", saved.getId());
        return toOutDto(saved);
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

    @Transactional(readOnly = true)
    public List<UsuarioOutDto> findAll(Boolean premium, Integer nivelExperiencia, String username) {
        logger.debug("Buscando usuarios con filtros: premium={}, nivelExperiencia={}, username={}",
                premium, nivelExperiencia, username);

        Specification<Usuario> spec = Specification.where(null);

        if (premium != null)
            spec = spec.and((root, query, cb) -> cb.equal(root.get("esPremium"), premium));

        if (nivelExperiencia != null)
            spec = spec.and((root, query, cb) -> cb.equal(root.get("nivelExperiencia"), nivelExperiencia));

        if (username != null && !username.isBlank()) {
            String pattern = "%" + username.toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("username")), pattern));
        }

        List<UsuarioOutDto> result = usuarioRepository.findAll(spec).stream()
                .map(this::toOutDto)
                .toList();
        logger.info("Se encontraron {} usuarios", result.size());
        return result;
    }

    @Transactional(readOnly = true)
    public UsuarioOutDto findById(long id) throws UsuarioNotFoundException {
        logger.debug("Buscando usuario por ID: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Usuario no encontrado: ID {}", id);
                    return new UsuarioNotFoundException();
                });
        return toOutDto(usuario);
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
        return toOutDto(saved);
    }

    public UsuarioOutDto patch(long id, Map<String, Object> updates) throws UsuarioNotFoundException {
        logger.info("Aplicando PATCH a usuario ID: {} con {} campos", id, updates.size());

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Usuario no encontrado para PATCH: ID {}", id);
                    return new UsuarioNotFoundException();
                });

        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            String campo = entry.getKey();
            Object valor = entry.getValue();
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
                    if (valor instanceof String s)
                        usuario.setFechaRegistro(LocalDate.parse(s));
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
                    logger.warn("Campo desconocido ignorado en PATCH: {}", campo);
            }
        }

        Usuario updated = usuarioRepository.save(usuario);
        logger.info("Usuario actualizado exitosamente con PATCH: ID {}", id);
        return toOutDto(updated);
    }

    private UsuarioOutDto toOutDto(Usuario usuario) {
        return modelMapper.map(usuario, UsuarioOutDto.class);
    }
}
