package com.svalero.rutea.controller;

import com.svalero.rutea.dto.ResenaInDto;
import com.svalero.rutea.dto.ResenaOutDto;
import com.svalero.rutea.exception.*;
import com.svalero.rutea.service.ResenaService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class ResenaController {

    private static final Logger logger = LoggerFactory.getLogger(ResenaController.class);

    @Autowired
    private ResenaService resenaService;

    @GetMapping("/resenas")
    public ResponseEntity<List<ResenaOutDto>> getAll(
            @RequestParam(value = "editada", required = false) Boolean editada,
            @RequestParam(value = "likes", required = false) Integer likes,
            @RequestParam(value = "valoracion", required = false) Integer valoracion
    ) {
        logger.debug("GET /resenas - Filtros: editada={}, likes={}, valoracion={}",
                editada, likes, valoracion);
        return ResponseEntity.ok(resenaService.findAll(editada, likes, valoracion));
    }

    @GetMapping("/resenas/{id}")
    public ResponseEntity<ResenaOutDto> get(@PathVariable long id) throws ResenaNotFoundException {
        logger.debug("GET /resenas/{}", id);
        return ResponseEntity.ok(resenaService.findById(id));
    }

    @PostMapping("/resenas")
    public ResponseEntity<ResenaOutDto> add(@Valid @RequestBody ResenaInDto dto)
            throws PuntoInteresNotFoundException, UsuarioNotFoundException {
        logger.debug("POST /resenas - valoracion: {}", dto.getValoracion());
        ResenaOutDto created = resenaService.add(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/resenas/{id}")
    public ResponseEntity<ResenaOutDto> modify(@PathVariable long id, @Valid @RequestBody ResenaInDto dto)
            throws ResenaNotFoundException, PuntoInteresNotFoundException, UsuarioNotFoundException {
        logger.debug("PUT /resenas/{}", id);
        return ResponseEntity.ok(resenaService.modify(id, dto));
    }

    /**
     * PATCH /resenas/{id} - Actualización parcial de reseña
     * Permite modificar solo los campos especificados en el body
     *
     * Ejemplo de request body:
     * {
     *   "comentario": "Comentario editado",
     *   "editada": true,
     *   "valoracion": 5
     * }
     */
    @PatchMapping("/resenas/{id}")
    public ResponseEntity<ResenaOutDto> patch(
            @PathVariable long id,
            @RequestBody Map<String, Object> updates) throws ResenaNotFoundException {
        logger.debug("PATCH /resenas/{} - Campos: {}", id, updates.keySet());
        return ResponseEntity.ok(resenaService.patch(id, updates));
    }

    @DeleteMapping("/resenas/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) throws ResenaNotFoundException {
        logger.debug("DELETE /resenas/{}", id);
        resenaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(ResenaNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(ResenaNotFoundException e) {
        logger.warn("Reseña no encontrada: {}", e.getMessage());
        ErrorResponse errorResponse = ErrorResponse.notFound("La reseña no existe");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PuntoInteresNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(PuntoInteresNotFoundException e) {
        logger.warn("Punto de interés no encontrado: {}", e.getMessage());
        ErrorResponse errorResponse = ErrorResponse.notFound("El punto de interés no existe");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UsuarioNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(UsuarioNotFoundException e) {
        logger.warn("Usuario no encontrado: {}", e.getMessage());
        ErrorResponse errorResponse = ErrorResponse.notFound("El usuario no existe");
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