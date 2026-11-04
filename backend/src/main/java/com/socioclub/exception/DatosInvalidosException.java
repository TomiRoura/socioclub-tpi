package com.socioclub.exception;

// Datos mal cargados en un formulario o en el cuerpo de un pedido (DNI con letras, edades al revés...).
// En la API es un 400 Bad Request; en las vistas se muestra el mensaje arriba del formulario.
public class DatosInvalidosException extends RuntimeException {

	public DatosInvalidosException(String mensaje) {
		super(mensaje);
	}
}
