package com.svalero.rutea.controller;

import com.svalero.rutea.dto.RutaInDto;
import com.svalero.rutea.dto.RutaOutDto;
import com.svalero.rutea.exception.*;
import com.svalero.rutea.service.RutaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class RutaController {

    @Autowired
    private RutaService rutaService;

    @GetMapping("/rutas")
    public ResponseEntity<List<RutaOutDto>> getAll(
            @RequestParam(value = "dificultad", required = false, defaultValue = "") String dificultad,
            @RequestParam(value = "publica", required = false) Boolean publica,
            @RequestParam(value = "titulo", required = false, defaultValue = "") String titulo
    ) {
        return ResponseEntity.ok(rutaService.findAll(dificultad, publica, titulo));
    }

    @GetMapping("/rutas/{id}")
    public ResponseEntity<RutaOutDto> get(@PathVariable long id) throws RutaNotFoundException {
        return ResponseEntity.ok(rutaService.findById(id));
    }

    @PostMapping("/rutas")
    public ResponseEntity<RutaOutDto> add(@Valid @RequestBody RutaInDto dto)
            throws UsuarioNotFoundException, PuntoInteresNotFoundException {
        RutaOutDto created = rutaService.add(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/rutas/{id}")
    public ResponseEntity<RutaOutDto> modify(@PathVariable long id, @Valid @RequestBody RutaInDto dto)
            throws RutaNotFoundException, UsuarioNotFoundException, PuntoInteresNotFoundException {
        return ResponseEntity.ok(rutaService.modify(id, dto));
    }

    @DeleteMapping("/rutas/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) throws RutaNotFoundException {
        rutaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(RutaNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(RutaNotFoundException e) {
        ErrorResponse errorResponse = ErrorResponse.notFound("La ruta no existe");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UsuarioNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(UsuarioNotFoundException e) {
        ErrorResponse errorResponse = ErrorResponse.notFound("El usuario no existe");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PuntoInteresNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(PuntoInteresNotFoundException e) {
        ErrorResponse errorResponse = ErrorResponse.notFound("El punto de interés no existe");
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