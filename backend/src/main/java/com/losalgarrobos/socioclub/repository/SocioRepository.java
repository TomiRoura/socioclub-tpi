package com.losalgarrobos.socioclub.repository;

import com.losalgarrobos.socioclub.model.Socio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SocioRepository extends JpaRepository<Socio, Long> {

    // SELECT * FROM socios WHERE dni = ?
    // Ingreso a la app (GET /api/socios/dni/{dni}) y control de DNI repetido en el alta y la edición.
    Optional<Socio> findByDni(String dni);

    // SELECT * FROM socios WHERE UPPER(apellido) LIKE UPPER('%texto%') LIMIT ? OFFSET ?
    // (más un SELECT COUNT(*) para saber cuántas páginas hay). Con "" trae todos.
    // La colación de la base hace que "gomez" encuentre "Gómez".
    Page<Socio> findByApellidoContainingIgnoreCase(String apellido, Pageable pageable);

    // Lista completa ordenada para el desplegable del formulario de inscripción.
    List<Socio> findAllByOrderByApellidoAscNombreAsc();
}
