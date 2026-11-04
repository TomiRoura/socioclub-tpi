package com.socioclub.dto;

// Objeto del formulario de inscripción de secretaría (th:object). Solo lleva los dos ids elegidos.
public class InscripcionForm {

	private Long socioId;
	private Long actividadId;

	public Long getSocioId() {
		return socioId;
	}

	public void setSocioId(Long socioId) {
		this.socioId = socioId;
	}

	public Long getActividadId() {
		return actividadId;
	}

	public void setActividadId(Long actividadId) {
		this.actividadId = actividadId;
	}
}
