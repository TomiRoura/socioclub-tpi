package com.socioclub.exception;

// Se lanza cuando se pide un socio, una actividad o una inscripción que no existe (404 en la API).
public class RecursoNoEncontradoException extends RuntimeException {

	public RecursoNoEncontradoException(String mensaje) {
		super(mensaje);
	}
}
