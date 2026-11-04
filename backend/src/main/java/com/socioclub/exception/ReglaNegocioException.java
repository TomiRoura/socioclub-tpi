package com.socioclub.exception;

// Se lanza desde la capa de servicio cuando una regla de negocio (RN1 a RN5) rechaza la operación.
// La API la transforma en un 409 Conflict y las vistas Thymeleaf muestran el mensaje.
public class ReglaNegocioException extends RuntimeException {

	private final String regla;

	public ReglaNegocioException(String mensaje, String regla) {
		super(mensaje);
		this.regla = regla;
	}

	public String getRegla() {
		return regla;
	}
}
