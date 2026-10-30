package com.socioclub.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "actividades")
public class Actividad {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "nombre", nullable = false, length = 60)
	private String nombre;

	// Con tildes, tiene que coincidir con las disciplinas que filtra la app
	@Column(name = "disciplina", nullable = false, length = 30)
	private String disciplina;

	@Column(name = "profesor", nullable = false, length = 60)
	private String profesor;

	@Column(name = "dias", nullable = false, length = 40)
	private String dias;

	// Se guarda como texto, igual que en el enunciado ("18:00", "9:00")
	@Column(name = "horario", nullable = false, length = 5)
	private String horario;

	@Column(name = "edad_minima", nullable = false)
	private Integer edadMinima;

	@Column(name = "edad_maxima", nullable = false)
	private Integer edadMaxima;

	@Column(name = "cupo_maximo", nullable = false)
	private Integer cupoMaximo;

	@Column(name = "cuota_mensual", nullable = false)
	private Integer cuotaMensual;

	// No se guarda en la tabla, lo calcula el servicio con cupoMaximo menos las inscripciones activas
	@Transient
	private Integer cupoDisponible;

	// Al borrar la actividad se borran también sus inscripciones
	@OneToMany(mappedBy = "actividad", cascade = CascadeType.REMOVE)
	@JsonIgnore
	private List<Inscripcion> inscripciones = new ArrayList<>();

	public Actividad() {
	}

	public Actividad(String nombre, String disciplina, String profesor, String dias, String horario,
			Integer edadMinima, Integer edadMaxima, Integer cupoMaximo, Integer cuotaMensual) {
		this.nombre = nombre;
		this.disciplina = disciplina;
		this.profesor = profesor;
		this.dias = dias;
		this.horario = horario;
		this.edadMinima = edadMinima;
		this.edadMaxima = edadMaxima;
		this.cupoMaximo = cupoMaximo;
		this.cuotaMensual = cuotaMensual;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDisciplina() {
		return disciplina;
	}

	public void setDisciplina(String disciplina) {
		this.disciplina = disciplina;
	}

	public String getProfesor() {
		return profesor;
	}

	public void setProfesor(String profesor) {
		this.profesor = profesor;
	}

	public String getDias() {
		return dias;
	}

	public void setDias(String dias) {
		this.dias = dias;
	}

	public String getHorario() {
		return horario;
	}

	public void setHorario(String horario) {
		this.horario = horario;
	}

	public Integer getEdadMinima() {
		return edadMinima;
	}

	public void setEdadMinima(Integer edadMinima) {
		this.edadMinima = edadMinima;
	}

	public Integer getEdadMaxima() {
		return edadMaxima;
	}

	public void setEdadMaxima(Integer edadMaxima) {
		this.edadMaxima = edadMaxima;
	}

	public Integer getCupoMaximo() {
		return cupoMaximo;
	}

	public void setCupoMaximo(Integer cupoMaximo) {
		this.cupoMaximo = cupoMaximo;
	}

	public Integer getCuotaMensual() {
		return cuotaMensual;
	}

	public void setCuotaMensual(Integer cuotaMensual) {
		this.cuotaMensual = cuotaMensual;
	}

	public Integer getCupoDisponible() {
		return cupoDisponible;
	}

	public void setCupoDisponible(Integer cupoDisponible) {
		this.cupoDisponible = cupoDisponible;
	}

	public List<Inscripcion> getInscripciones() {
		return inscripciones;
	}

	public void setInscripciones(List<Inscripcion> inscripciones) {
		this.inscripciones = inscripciones;
	}

	@Override
	public String toString() {
		return "Actividad{id=" + id + ", nombre='" + nombre + "', disciplina='" + disciplina + "', cupoMaximo="
				+ cupoMaximo + ", cupoDisponible=" + cupoDisponible + "}";
	}
}
