package com.svalero.rutea.controller;

import com.svalero.rutea.dto.UsuarioInDto;
import com.svalero.rutea.dto.UsuarioOutDto;
import com.svalero.rutea.exception.UsuarioNotFoundException;
import com.svalero.rutea.service.UsuarioService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class UsuarioController {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioOutDto>> getAll(
            @RequestParam(required = false) Boolean premium,
            @RequestParam(required = false) Integer nivelExperiencia,
            @RequestParam(required = false, defaultValue = "") String username
    ) {
        logger.debug("GET /usuarios - Filtros: premium={}, nivelExperiencia={}, username={}",
                premium, nivelExperiencia, username);
        return ResponseEntity.ok(usuarioService.findAll(premium, nivelExperiencia, username));
    }

    @GetMapping("/usuarios/{id}")
    public ResponseEntity<UsuarioOutDto> get(@PathVariable long id) throws UsuarioNotFoundException {
        logger.debug("GET /usuarios/{}", id);
        return ResponseEntity.ok(usuarioService.findById(id));
    }

    @PostMapping("/usuarios")
    public ResponseEntity<UsuarioOutDto> add(@Valid @RequestBody UsuarioInDto usuarioInDto) {
        logger.debug("POST /usuarios - {}", usuarioInDto.getUsername());
        return new ResponseEntity<>(usuarioService.add(usuarioInDto), HttpStatus.CREATED);
    }

    @PutMapping("/usuarios/{id}")
    public ResponseEntity<UsuarioOutDto> modify(@PathVariable long id, @Valid @RequestBody UsuarioInDto usuarioInDto)
            throws UsuarioNotFoundException {
        logger.debug("PUT /usuarios/{}", id);
        return ResponseEntity.ok(usuarioService.modify(id, usuarioInDto));
    }

    @PatchMapping("/usuarios/{id}")
    public ResponseEntity<UsuarioOutDto> patch(
            @PathVariable long id,
            @RequestBody Map<String, Object> updates) throws UsuarioNotFoundException {
        logger.debug("PATCH /usuarios/{} - Campos: {}", id, updates.keySet());
        return ResponseEntity.ok(usuarioService.patch(id, updates));
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) throws UsuarioNotFoundException {
        logger.debug("DELETE /usuarios/{}", id);
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
