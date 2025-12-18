package com.svalero.rutea.controller;

import com.svalero.rutea.domain.Usuario;
import com.svalero.rutea.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin
public class UsuarioController {

    @Autowired private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<Usuario>> getAll(
            @RequestParam(required = false) Integer experiencia,
            @RequestParam(required = false) Boolean premium) {
        if (experiencia != null && premium != null) {
            return new ResponseEntity<>(usuarioService.filtrarUsuarios(experiencia, premium), HttpStatus.OK);
        }
        return new ResponseEntity<>(usuarioService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getById(@PathVariable long id) {
        return new ResponseEntity<>(usuarioService.findById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Usuario> add(@Valid @RequestBody Usuario usuario) {
        return new ResponseEntity<>(usuarioService.save(usuario), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> update(@PathVariable long id, @Valid @RequestBody Usuario usuario) {
        return new ResponseEntity<>(usuarioService.update(id, usuario), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        usuarioService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}