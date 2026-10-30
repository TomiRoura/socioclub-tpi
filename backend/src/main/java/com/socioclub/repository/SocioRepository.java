package com.socioclub.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.socioclub.model.Socio;

@Repository
public interface SocioRepository extends JpaRepository<Socio, Long> {

	// Ingreso a la app por DNI y control de DNI duplicado
	Optional<Socio> findByDni(String dni);

	// Listado paginado con búsqueda por apellido (con "" trae todos)
	Page<Socio> findByApellidoContainingIgnoreCase(String apellido, Pageable pageable);
}
