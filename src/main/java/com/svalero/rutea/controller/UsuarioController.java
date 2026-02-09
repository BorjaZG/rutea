package com.svalero.rutea.controller;

import com.svalero.rutea.dto.UsuarioInDto;
import com.svalero.rutea.dto.UsuarioOutDto;
import com.svalero.rutea.exception.ErrorResponse;
import com.svalero.rutea.exception.UsuarioNotFoundException;
import com.svalero.rutea.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioOutDto>> getAll(
            @RequestParam(value = "premium", required = false) Boolean premium,
            @RequestParam(value = "nivelExperiencia", required = false) Integer nivelExperiencia,
            @RequestParam(value = "username", required = false, defaultValue = "") String username
    ) {
        return ResponseEntity.ok(usuarioService.findAll(premium, nivelExperiencia, username));
    }

    @GetMapping("/usuarios/{id}")
    public ResponseEntity<UsuarioOutDto> get(@PathVariable long id) throws UsuarioNotFoundException {
        return ResponseEntity.ok(usuarioService.findById(id));
    }

    @PostMapping("/usuarios")
    public ResponseEntity<UsuarioOutDto> add(@Valid @RequestBody UsuarioInDto usuarioInDto) {
        UsuarioOutDto created = usuarioService.add(usuarioInDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/usuarios/{id}")
    public ResponseEntity<UsuarioOutDto> modify(@PathVariable long id, @Valid @RequestBody UsuarioInDto usuarioInDto)
            throws UsuarioNotFoundException {
        return ResponseEntity.ok(usuarioService.modify(id, usuarioInDto));
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) throws UsuarioNotFoundException {
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(UsuarioNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(UsuarioNotFoundException e) {
        ErrorResponse errorResponse = ErrorResponse.notFound("El usuario no existe");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            String msg = error.getDefaultMessage();
            errors.put(field, msg);
        });

        ErrorResponse errorResponse = ErrorResponse.validationError(errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}