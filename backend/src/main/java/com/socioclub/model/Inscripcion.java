package com.socioclub.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "inscripciones")
public class Inscripcion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// Lado dueño de la relación: socio_id es una columna de esta tabla.
	// No va en el JSON porque la app ya sabe de qué socio se trata
	@ManyToOne
	@JoinColumn(name = "socio_id", nullable = false)
	@JsonIgnore
	private Socio socio;

	// Esta sí va en el JSON porque la app muestra los datos de la actividad
	@ManyToOne
	@JoinColumn(name = "actividad_id", nullable = false)
	private Actividad actividad;

	@Column(name = "fecha_inscripcion", nullable = false)
	private LocalDate fechaInscripcion;

	@Enumerated(EnumType.STRING)
	@Column(name = "estado", nullable = false, length = 10)
	private EstadoInscripcion estado = EstadoInscripcion.ACTIVA;

	public Inscripcion() {
	}

	public Inscripcion(Socio socio, Actividad actividad, LocalDate fechaInscripcion, EstadoInscripcion estado) {
		this.socio = socio;
		this.actividad = actividad;
		this.fechaInscripcion = fechaInscripcion;
		this.estado = estado;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Socio getSocio() {
		return socio;
	}

	public void setSocio(Socio socio) {
		this.socio = socio;
	}

	public Actividad getActividad() {
		return actividad;
	}

	public void setActividad(Actividad actividad) {
		this.actividad = actividad;
	}

	public LocalDate getFechaInscripcion() {
		return fechaInscripcion;
	}

	public void setFechaInscripcion(LocalDate fechaInscripcion) {
		this.fechaInscripcion = fechaInscripcion;
	}

	public EstadoInscripcion getEstado() {
		return estado;
	}

	public void setEstado(EstadoInscripcion estado) {
		this.estado = estado;
	}

	@Override
	public String toString() {
		return "Inscripcion{id=" + id + ", socioId=" + (socio != null ? socio.getId() : null) + ", actividadId="
				+ (actividad != null ? actividad.getId() : null) + ", fechaInscripcion=" + fechaInscripcion
				+ ", estado=" + estado + "}";
	}
}
