package com.svalero.rutea.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Error 404 (Objeto no encontrado)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleNotFound(ResourceNotFoundException ex) {
        return new ResponseEntity<>(Map.of("code", "404", "message", ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    // Error 400 (Validación fallida)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error ->
                errors.put(((FieldError) error).getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // Error 404 Técnico (Favicon, Swagger URL mal puesta) - Evita el 500
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Object> handleResourceNotFound(NoResourceFoundException ex) {
        return new ResponseEntity<>("Recurso no encontrado", HttpStatus.NOT_FOUND);
    }

    // Error 500 (Todo lo demás)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleInternalError(Exception ex) {
        logger.error("Error 500: ", ex);
        return new ResponseEntity<>(Map.of("code", "500", "message", "Error interno del servidor"), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}