package com.losalgarrobos.socioclub.model;

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
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

// Entidad JPA de la tabla "socios".
@Entity
@Table(name = "socios")
public class Socio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Único y de 7 u 8 dígitos (RN7). La base también lo controla con uk_socios_dni y ck_socios_dni.
    @Column(nullable = false, unique = true, length = 8)
    private String dni;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(nullable = false, length = 50)
    private String apellido;

    // ISO (AAAA-MM-DD): es el formato que manda el input type="date" del formulario.
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Column(length = 100)
    private String email;

    @Column(length = 20)
    private String telefono;

    // EnumType.STRING guarda el nombre ("AL_DIA") y no la posición (0), que es frágil.
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_cuota", nullable = false, length = 10)
    private EstadoCuota estadoCuota = EstadoCuota.AL_DIA;

    // Un socio tiene muchas inscripciones. @JsonIgnore evita el ciclo infinito en el JSON.
    @OneToMany(mappedBy = "socio")
    @JsonIgnore
    private List<Inscripcion> inscripciones = new ArrayList<>();

    public Socio() {
    }

    // Años cumplidos a la fecha indicada (RN3). Tiene parámetro, así Jackson no lo manda en el JSON.
    public int calcularEdad(LocalDate hoy) {
        return Period.between(fechaNacimiento, hoy).getYears();
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
}
