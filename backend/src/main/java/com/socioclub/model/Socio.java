package com.socioclub.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "socios", uniqueConstraints = @UniqueConstraint(name = "uk_socios_dni", columnNames = "dni"))
public class Socio {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "dni", nullable = false, length = 8)
	private String dni;

	@Column(name = "nombre", nullable = false, length = 50)
	private String nombre;

	@Column(name = "apellido", nullable = false, length = 50)
	private String apellido;

	// Sin esto el <input type="date"> del formulario de edición aparece vacío
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	@Column(name = "fecha_nacimiento", nullable = false)
	private LocalDate fechaNacimiento;

	@Column(name = "email", length = 100)
	private String email;

	@Column(name = "telefono", length = 20)
	private String telefono;

	@Enumerated(EnumType.STRING)
	@Column(name = "estado_cuota", nullable = false, length = 10)
	private EstadoCuota estadoCuota = EstadoCuota.AL_DIA;

	// Lado inverso, no agrega columnas. El @JsonIgnore evita el ciclo infinito
	// Socio -> Inscripcion -> Socio al pasar a JSON
	@OneToMany(mappedBy = "socio")
	@JsonIgnore
	private List<Inscripcion> inscripciones = new ArrayList<>();

	public Socio() {
	}

	public Socio(String dni, String nombre, String apellido, LocalDate fechaNacimiento, String email,
			String telefono, EstadoCuota estadoCuota) {
		this.dni = dni;
		this.nombre = nombre;
		this.apellido = apellido;
		this.fechaNacimiento = fechaNacimiento;
		this.email = email;
		this.telefono = telefono;
		this.estadoCuota = estadoCuota;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDni() {
		return dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public LocalDate getFechaNacimiento() {
		return fechaNacimiento;
	}

	public void setFechaNacimiento(LocalDate fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public EstadoCuota getEstadoCuota() {
		return estadoCuota;
	}

	public void setEstadoCuota(EstadoCuota estadoCuota) {
		this.estadoCuota = estadoCuota;
	}

	public List<Inscripcion> getInscripciones() {
		return inscripciones;
	}

	public void setInscripciones(List<Inscripcion> inscripciones) {
		this.inscripciones = inscripciones;
	}

	@Override
	public String toString() {
		return "Socio{id=" + id + ", dni='" + dni + "', nombre='" + nombre + "', apellido='" + apellido
				+ "', fechaNacimiento=" + fechaNacimiento + ", estadoCuota=" + estadoCuota + "}";
	}
}
