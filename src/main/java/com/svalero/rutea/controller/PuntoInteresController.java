package com.svalero.rutea.controller;

import com.svalero.rutea.dto.PuntoInteresInDto;
import com.svalero.rutea.dto.PuntoInteresOutDto;
import com.svalero.rutea.exception.CategoriaNotFoundException;
import com.svalero.rutea.exception.ErrorResponse;
import com.svalero.rutea.exception.PuntoInteresNotFoundException;
import com.svalero.rutea.service.PuntoInteresService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

import java.util.*;

@RestController
public class PuntoInteresController {

    private static final Logger logger = LoggerFactory.getLogger(PuntoInteresController.class);

    @Autowired
    private PuntoInteresService puntoInteresService;

    @GetMapping("/puntos")
    public ResponseEntity<List<PuntoInteresOutDto>> getAll(
            @RequestParam(value = "categoriaId", required = false) Long categoriaId,
            @RequestParam(value = "abiertoActualmente", required = false) Boolean abiertoActualmente,
            @RequestParam(value = "abierto", required = false) Boolean abierto,
            @RequestParam(value = "nombre", required = false, defaultValue = "") String nombre,
            @RequestParam(value = "puntuacionMedia", required = false) Float puntuacionMedia
    ) {
        logger.debug("GET: /puntos - Filtros aplicados");
        Boolean abiertoFinal = (abiertoActualmente != null) ? abiertoActualmente : abierto;
        return ResponseEntity.ok(puntoInteresService.findAll(categoriaId, abiertoFinal, nombre, puntuacionMedia));
    }

    @GetMapping("/puntos/{id}")
    public ResponseEntity<PuntoInteresOutDto> get(@PathVariable long id) throws PuntoInteresNotFoundException {
        logger.debug("GET: /puntos/{}", id);
        return ResponseEntity.ok(puntoInteresService.findById(id));
    }

    @PostMapping("/puntos")
    public ResponseEntity<PuntoInteresOutDto> add(@Valid @RequestBody PuntoInteresInDto dto)
            throws CategoriaNotFoundException {
        logger.debug("POST: /puntos/ - {}", dto.getNombre());
        PuntoInteresOutDto created = puntoInteresService.add(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/puntos/{id}")
    public ResponseEntity<PuntoInteresOutDto> modify(@PathVariable long id, @Valid @RequestBody PuntoInteresInDto dto)
            throws PuntoInteresNotFoundException, CategoriaNotFoundException {
        logger.debug("PUT: /puntos/{}", id);
        return ResponseEntity.ok(puntoInteresService.modify(id, dto));
    }

    @PatchMapping("/puntos/{id}")
    public ResponseEntity<PuntoInteresOutDto> patch(
            @PathVariable long id,
            @RequestBody Map<String, Object> updates) throws PuntoInteresNotFoundException {
        logger.debug("PATCH /puntos/{} - Campos: {}", id, updates.keySet());
        return ResponseEntity.ok(puntoInteresService.patch(id, updates));
    }

    @DeleteMapping("/puntos/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) throws PuntoInteresNotFoundException {
        logger.debug("DELETE: /puntos/{}", id);
        puntoInteresService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(PuntoInteresNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(PuntoInteresNotFoundException e) {
        logger.warn("Punto de interés no encontrado: {}", e.getMessage());
        ErrorResponse errorResponse = ErrorResponse.notFound("El punto de interés no existe");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CategoriaNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(CategoriaNotFoundException e) {
        logger.warn("Categoría no encontrada: {}", e.getMessage());
        ErrorResponse errorResponse = ErrorResponse.notFound("La categoría no existe");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleException(MethodArgumentNotValidException e) {
        logger.warn("Error de validación en request");
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