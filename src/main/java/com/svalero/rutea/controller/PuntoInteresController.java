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

import java.util.*;

@RestController
public class PuntoInteresController {

    @Autowired
    private PuntoInteresService puntoInteresService;

    @GetMapping("/puntos")
    public ResponseEntity<List<PuntoInteresOutDto>> getAll(
            // opcional: sigues pudiendo filtrar por categoria si quieres
            @RequestParam(value = "categoriaId", required = false) Long categoriaId,

            // NUEVO nombre “abiertoActualmente”
            @RequestParam(value = "abiertoActualmente", required = false) Boolean abiertoActualmente,

            // compatibilidad con tu antiguo "abierto"
            @RequestParam(value = "abierto", required = false) Boolean abierto,

            @RequestParam(value = "nombre", required = false, defaultValue = "") String nombre,

            // NUEVO: puntuacionMedia
            @RequestParam(value = "puntuacionMedia", required = false) Float puntuacionMedia
    ) {
        Boolean abiertoFinal = (abiertoActualmente != null) ? abiertoActualmente : abierto;
        return ResponseEntity.ok(puntoInteresService.findAll(categoriaId, abiertoFinal, nombre, puntuacionMedia));
    }

    @GetMapping("/puntos/{id}")
    public ResponseEntity<PuntoInteresOutDto> get(@PathVariable long id) throws PuntoInteresNotFoundException {
        return ResponseEntity.ok(puntoInteresService.findById(id));
    }

    @PostMapping("/puntos")
    public ResponseEntity<PuntoInteresOutDto> add(@Valid @RequestBody PuntoInteresInDto dto)
            throws CategoriaNotFoundException {
        PuntoInteresOutDto created = puntoInteresService.add(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/puntos/{id}")
    public ResponseEntity<PuntoInteresOutDto> modify(@PathVariable long id, @Valid @RequestBody PuntoInteresInDto dto)
            throws PuntoInteresNotFoundException, CategoriaNotFoundException {
        return ResponseEntity.ok(puntoInteresService.modify(id, dto));
    }

    @DeleteMapping("/puntos/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) throws PuntoInteresNotFoundException {
        puntoInteresService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(PuntoInteresNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(PuntoInteresNotFoundException e) {
        ErrorResponse errorResponse = ErrorResponse.notFound("El punto de interés no existe");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CategoriaNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(CategoriaNotFoundException e) {
        ErrorResponse errorResponse = ErrorResponse.notFound("La categoría no existe");
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