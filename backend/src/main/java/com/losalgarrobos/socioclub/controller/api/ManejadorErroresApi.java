package com.losalgarrobos.socioclub.controller.api;

import com.losalgarrobos.socioclub.service.DatosInvalidosException;
import com.losalgarrobos.socioclub.service.RecursoNoEncontradoException;
import com.losalgarrobos.socioclub.service.ReglaNegocioException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.Map;

// Convierte las excepciones de los servicios en respuestas JSON con el código HTTP correcto.
// Sin esta clase, Spring devolvería su error por defecto ({ timestamp, status, error: "Conflict", path })
// y la app mostraría "Conflict" en lugar del motivo.
// basePackages: solo atiende a los controladores REST, no a las pantallas Thymeleaf.
@RestControllerAdvice(basePackages = "com.losalgarrobos.socioclub.controller.api")
public class ManejadorErroresApi {

    // 409 Conflict: una regla de negocio rechazó la operación. { "error": "...", "regla": "RN1" }
    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<Map<String, String>> reglaNegocio(ReglaNegocioException e) {
        return responder(HttpStatus.CONFLICT, e.getMessage(), e.getRegla());
    }

    // 404 Not Found: { "error": "Socio no encontrado" }
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, String>> noEncontrado(RecursoNoEncontradoException e) {
        return responder(HttpStatus.NOT_FOUND, e.getMessage(), null);
    }

    // 400 Bad Request: datos inválidos en un alta o modificación.
    @ExceptionHandler(DatosInvalidosException.class)
    public ResponseEntity<Map<String, String>> datosInvalidos(DatosInvalidosException e) {
        return responder(HttpStatus.BAD_REQUEST, e.getMessage(), null);
    }

    // 400: el cuerpo no es un JSON válido o tiene un tipo equivocado.
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> jsonInvalido(HttpMessageNotReadableException e) {
        return responder(HttpStatus.BAD_REQUEST, "El cuerpo de la petición no es un JSON válido", null);
    }

    // 400: un id de la ruta que no es un número (por ejemplo /api/actividades/abc).
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> parametroInvalido(MethodArgumentTypeMismatchException e) {
        return responder(HttpStatus.BAD_REQUEST, "El parámetro '" + e.getName() + "' no es válido", null);
    }

    // 409: la base rechazó el dato por una de sus restricciones (DNI repetido, check, clave foránea).
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> restriccionBase(DataIntegrityViolationException e) {
        return responder(HttpStatus.CONFLICT, "Los datos no cumplen las restricciones de la base", null);
    }

    private ResponseEntity<Map<String, String>> responder(HttpStatus status, String mensaje, String regla) {
        Map<String, String> cuerpo = new LinkedHashMap<>();
        cuerpo.put("error", mensaje);
        if (regla != null) {
            cuerpo.put("regla", regla); // el campo "regla" va solo en los 409 de RN1 a RN5
        }
        return ResponseEntity.status(status).body(cuerpo);
    }
}
