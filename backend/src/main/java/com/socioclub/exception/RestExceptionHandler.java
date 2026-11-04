package com.socioclub.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Solo atiende a los controladores REST: las pantallas de Thymeleaf manejan sus errores en cada controlador.
// Todas las respuestas de error tienen la misma forma: { "error": "...", "regla": "RN1" }.
@RestControllerAdvice(basePackages = "com.socioclub.controller.api")
public class RestExceptionHandler {

	// 409: una regla de negocio rechazó la operación
	@ExceptionHandler(ReglaNegocioException.class)
	public ResponseEntity<Map<String, String>> manejarReglaNegocio(ReglaNegocioException ex) {
		return armarRespuesta(HttpStatus.CONFLICT, ex.getMessage(), ex.getRegla());
	}

	// 404: el socio, la actividad o la inscripción no existe
	@ExceptionHandler(RecursoNoEncontradoException.class)
	public ResponseEntity<Map<String, String>> manejarNoEncontrado(RecursoNoEncontradoException ex) {
		return armarRespuesta(HttpStatus.NOT_FOUND, ex.getMessage(), null);
	}

	// 400: faltan datos o están mal
	@ExceptionHandler(DatosInvalidosException.class)
	public ResponseEntity<Map<String, String>> manejarDatosInvalidos(DatosInvalidosException ex) {
		return armarRespuesta(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
	}

	private ResponseEntity<Map<String, String>> armarRespuesta(HttpStatus estado, String mensaje, String regla) {
		Map<String, String> cuerpo = new LinkedHashMap<>();
		cuerpo.put("error", mensaje);
		if (regla != null) {
			cuerpo.put("regla", regla);
		}
		return ResponseEntity.status(estado).body(cuerpo);
	}
}
