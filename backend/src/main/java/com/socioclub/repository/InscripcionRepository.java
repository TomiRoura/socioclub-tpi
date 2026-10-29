package com.socioclub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.socioclub.model.EstadoInscripcion;
import com.socioclub.model.Inscripcion;

@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {

	// Actividades de un socio (la app pide solo las ACTIVA)
	List<Inscripcion> findBySocioIdAndEstado(Long socioId, EstadoInscripcion estado);

	// Inscriptos de una actividad
	List<Inscripcion> findByActividadIdAndEstado(Long actividadId, EstadoInscripcion estado);

	// RN2: el socio ya está inscripto en la actividad
	boolean existsBySocioIdAndActividadIdAndEstado(Long socioId, Long actividadId, EstadoInscripcion estado);

	// RN5: cantidad de actividades del socio
	long countBySocioIdAndEstado(Long socioId, EstadoInscripcion estado);

	// RN1 y cupo disponible: cuántas inscripciones tiene la actividad en ese estado
	@Query("SELECT COUNT(i) FROM Inscripcion i WHERE i.actividad.id = :actividadId AND i.estado = :estado")
	long contarPorActividadYEstado(@Param("actividadId") Long actividadId, @Param("estado") EstadoInscripcion estado);
}
