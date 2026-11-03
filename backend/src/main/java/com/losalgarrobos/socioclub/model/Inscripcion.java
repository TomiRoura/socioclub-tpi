package com.losalgarrobos.socioclub.model;

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

import java.time.LocalDate;

// Entidad JPA de la tabla "inscripciones": une a un socio con una actividad.
// Es el lado dueño de las dos relaciones: acá están las claves foráneas socio_id y actividad_id.
@Entity
@Table(name = "inscripciones")
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Muchas inscripciones pertenecen a un socio. @JsonIgnore: el JSON de una inscripción NO trae
    // el socio (si lo trajera, el socio traería sus inscripciones y así hasta el infinito).
    @ManyToOne
    @JoinColumn(name = "socio_id", nullable = false)
    @JsonIgnore
    private Socio socio;

    // Muchas inscripciones pertenecen a una actividad. Viaja completa en el JSON.
    @ManyToOne
    @JoinColumn(name = "actividad_id", nullable = false)
    private Actividad actividad;

    @Column(name = "fecha_inscripcion", nullable = false)
    private LocalDate fechaInscripcion; // sale en el JSON como "AAAA-MM-DD"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private EstadoInscripcion estado = EstadoInscripcion.ACTIVA;

    public Inscripcion() {
    }

    public Inscripcion(Socio socio, Actividad actividad, LocalDate fechaInscripcion) {
        this.socio = socio;
        this.actividad = actividad;
        this.fechaInscripcion = fechaInscripcion;
        this.estado = EstadoInscripcion.ACTIVA;
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
}
