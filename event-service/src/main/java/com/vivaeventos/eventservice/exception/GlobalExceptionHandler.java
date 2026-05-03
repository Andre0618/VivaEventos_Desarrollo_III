package com.vivaeventos.eventservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @RestControllerAdvice → Intercepta excepciones de TODOS los controllers
 *                         y devuelve respuestas JSON en lugar de páginas de error feas.
 *
 * Sin este archivo, si la validación falla se devuelve un JSON enorme y confuso
 * de Spring. Se expresa con más claridad:
 *
 * {
 *   "timestamp": "2025-04-25T10:30:00",
 *   "status": 400,
 *   "error": "Validation failed",
 *   "fields": {
 *     "name": "El nombre del evento es obligatorio",
 *     "capacity": "La capacidad debe ser al menos 1"
 *   }
 * }
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Se dispara cuando @Valid encuentra errores en el request body.
     * Devuelve 400 Bad Request con los campos que fallaron y por qué.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        // Recopila todos los errores de campo en un mapa campo → mensaje
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Validation failed");
        body.put("fields", fieldErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Captura cualquier excepción no manejada.
     * Devuelve 500 Internal Server Error con un mensaje genérico
     * (nunca exponemos el stack trace al cliente).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericError(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal server error");
        body.put("message", "¡Ocurrió un error inesperado!"); //Modificado por error genérico para evitar exposición interna

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}