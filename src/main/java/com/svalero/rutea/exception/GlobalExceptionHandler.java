package com.svalero.rutea.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ----------- 404 (tus custom) -----------
    @ExceptionHandler({
            CategoriaNotFoundException.class,
            PuntoInteresNotFoundException.class,
            ResenaNotFoundException.class,
            RutaNotFoundException.class,
            UsuarioNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFound(Exception ex) {
        ErrorResponse errorResponse = ErrorResponse.notFound(ex.getMessage() != null ? ex.getMessage() : "Resource not found");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // ----------- 400 (Validación body) -----------
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });
        ErrorResponse errorResponse = ErrorResponse.validationError(errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // ----------- 400 (PathVariable / RequestParam mal tipado) -----------
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Map<String, String> errors = new HashMap<>();
        String param = ex.getName();
        String value = ex.getValue() != null ? ex.getValue().toString() : "null";
        errors.put(param, "Invalid value: " + value);

        ErrorResponse errorResponse = ErrorResponse.validationError(errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // ----------- 400 (JSON mal formado / tipos incorrectos en body) -----------
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException ex) {
        ErrorResponse errorResponse = ErrorResponse.generalError(400, "bad-request", "Malformed JSON or invalid body");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // ----------- 400 (Content-Type incorrecto) -----------
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMediaType(HttpMediaTypeNotSupportedException ex) {
        ErrorResponse errorResponse = ErrorResponse.generalError(400, "bad-request", "Unsupported Content-Type");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // ----------- 500 (fallback) -----------
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        ErrorResponse errorResponse = ErrorResponse.generalError(500, "internal-server-error", "Unexpected error");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}