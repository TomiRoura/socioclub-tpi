package com.losalgarrobos.socioclub.model;

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

import java.util.ArrayList;
import java.util.List;

// Entidad JPA: cada objeto Actividad es una fila de la tabla "actividades".
// Los nombres de los atributos son los mismos que lee la app React, así el JSON llega sin cambios.
@Entity
@Table(name = "actividades")
public class Actividad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // el id lo genera MySQL (AUTO_INCREMENT)
    private Long id;

    @Column(nullable = false, length = 60)
    private String nombre;

    @Column(nullable = false, length = 30)
    private String disciplina;

    @Column(nullable = false, length = 60)
    private String profesor;

    @Column(nullable = false, length = 40)
    private String dias;

    @Column(nullable = false, length = 5)
    private String horario;

    @Column(name = "edad_minima", nullable = false)
    private Integer edadMinima;

    @Column(name = "edad_maxima", nullable = false)
    private Integer edadMaxima;

    @Column(name = "cupo_maximo", nullable = false)
    private Integer cupoMaximo;

    @Column(name = "cuota_mensual", nullable = false)
    private Integer cuotaMensual;

    // No es una columna: lo calcula el servicio (cupoMaximo menos las inscripciones ACTIVA)
    // antes de devolver la actividad a una vista o a la API.
    @Transient
    private Integer cupoDisponible;

    // Lado inverso de la relación: la clave foránea está en Inscripcion.actividad (mappedBy).
    // @JsonIgnore corta el ciclo Actividad -> Inscripcion -> Actividad al armar el JSON.
    // cascade REMOVE: al borrar una actividad (solo se permite sin inscriptos activos) se borran
    // también sus inscripciones dadas de BAJA.
    @OneToMany(mappedBy = "actividad", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<Inscripcion> inscripciones = new ArrayList<>();

    public Actividad() {
    }

    // Edad incluida en el rango de la actividad (los dos extremos cuentan). Se usa en RN3.
    public boolean esAptaParaEdad(int edad) {
        return edad >= edadMinima && edad <= edadMaxima;
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
}
