package com.socioclub.exception;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity handleReglaNegocio(ReglaNegocioException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        response.put("regla", ex.getRegla());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
}