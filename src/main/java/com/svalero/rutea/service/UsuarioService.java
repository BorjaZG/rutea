package com.svalero.rutea.service;

import com.svalero.rutea.domain.Usuario;
import com.svalero.rutea.dto.UsuarioInDto;
import com.svalero.rutea.dto.UsuarioOutDto;
import com.svalero.rutea.exception.UsuarioNotFoundException;
import com.svalero.rutea.repository.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ModelMapper modelMapper;

    public UsuarioOutDto add(UsuarioInDto usuarioInDto) {
        Usuario usuario = modelMapper.map(usuarioInDto, Usuario.class);
        Usuario saved = usuarioRepository.save(usuario);
        return modelMapper.map(saved, UsuarioOutDto.class);
    }

    public void delete(long id) throws UsuarioNotFoundException {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(UsuarioNotFoundException::new);
        usuarioRepository.delete(usuario);
    }

    // Filtros (hasta 3): premium + nivelExperiencia + username
    public List<UsuarioOutDto> findAll(Boolean premium, Integer nivelExperiencia, String username) {

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

        return modelMapper.map(usuarios, new TypeToken<List<UsuarioOutDto>>() {}.getType());
    }

    public UsuarioOutDto findById(long id) throws UsuarioNotFoundException {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(UsuarioNotFoundException::new);
        return modelMapper.map(usuario, UsuarioOutDto.class);
    }

    public UsuarioOutDto modify(long id, UsuarioInDto usuarioInDto) throws UsuarioNotFoundException {
        Usuario existing = usuarioRepository.findById(id)
                .orElseThrow(UsuarioNotFoundException::new);

        modelMapper.map(usuarioInDto, existing);
        existing.setId(id);

        Usuario saved = usuarioRepository.save(existing);
        return modelMapper.map(saved, UsuarioOutDto.class);
    }
}