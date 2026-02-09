package com.svalero.rutea.controller;

import com.svalero.rutea.dto.CategoriaInDto;
import com.svalero.rutea.dto.CategoriaOutDto;
import com.svalero.rutea.exception.CategoriaNotFoundException;
import com.svalero.rutea.exception.ErrorResponse;
import com.svalero.rutea.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping("/categorias")
    public ResponseEntity<List<CategoriaOutDto>> getAll(
            @RequestParam(value = "activa", required = false) Boolean activa,
            @RequestParam(value = "nombre", required = false) String nombre,
            @RequestParam(value = "ordenPrioridad", required = false) Integer ordenPrioridad
    ) {
        List<CategoriaOutDto> categorias = categoriaService.findAll(activa, nombre, ordenPrioridad);
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/categorias/{id}")
    public ResponseEntity<CategoriaOutDto> get(@PathVariable long id) throws CategoriaNotFoundException {
        return ResponseEntity.ok(categoriaService.findById(id));
    }

    @PostMapping("/categorias")
    public ResponseEntity<CategoriaOutDto> add(@Valid @RequestBody CategoriaInDto dto) {
        CategoriaOutDto created = categoriaService.add(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/categorias/{id}")
    public ResponseEntity<CategoriaOutDto> modify(@PathVariable long id, @Valid @RequestBody CategoriaInDto dto)
            throws CategoriaNotFoundException {
        return ResponseEntity.ok(categoriaService.modify(id, dto));
    }

    @DeleteMapping("/categorias/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) throws CategoriaNotFoundException {
        categoriaService.delete(id);
        return ResponseEntity.noContent().build();
    }


    @ExceptionHandler(CategoriaNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(CategoriaNotFoundException e) {
        ErrorResponse errorResponse = ErrorResponse.notFound("La categoria no existe");
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