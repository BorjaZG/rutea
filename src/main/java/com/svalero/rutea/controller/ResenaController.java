package com.svalero.rutea.controller;

import com.svalero.rutea.dto.ResenaInDto;
import com.svalero.rutea.dto.ResenaOutDto;
import com.svalero.rutea.exception.*;
import com.svalero.rutea.service.ResenaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class ResenaController {

    @Autowired
    private ResenaService resenaService;

    @GetMapping("/resenas")
    public ResponseEntity<List<ResenaOutDto>> getAll(
            @RequestParam(value = "editada", required = false) Boolean editada,
            @RequestParam(value = "likes", required = false) Integer likes,
            @RequestParam(value = "valoracion", required = false) Integer valoracion
    ) {
        return ResponseEntity.ok(resenaService.findAll(editada, likes, valoracion));
    }

    @GetMapping("/resenas/{id}")
    public ResponseEntity<ResenaOutDto> get(@PathVariable long id) throws ResenaNotFoundException {
        return ResponseEntity.ok(resenaService.findById(id));
    }

    @PostMapping("/resenas")
    public ResponseEntity<ResenaOutDto> add(@Valid @RequestBody ResenaInDto dto)
            throws PuntoInteresNotFoundException, UsuarioNotFoundException {
        ResenaOutDto created = resenaService.add(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/resenas/{id}")
    public ResponseEntity<ResenaOutDto> modify(@PathVariable long id, @Valid @RequestBody ResenaInDto dto)
            throws ResenaNotFoundException, PuntoInteresNotFoundException, UsuarioNotFoundException {
        return ResponseEntity.ok(resenaService.modify(id, dto));
    }

    @DeleteMapping("/resenas/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) throws ResenaNotFoundException {
        resenaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(ResenaNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(ResenaNotFoundException e) {
        ErrorResponse errorResponse = ErrorResponse.notFound("La reseña no existe");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PuntoInteresNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(PuntoInteresNotFoundException e) {
        ErrorResponse errorResponse = ErrorResponse.notFound("El punto de interés no existe");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
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