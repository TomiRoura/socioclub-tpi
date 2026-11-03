package com.losalgarrobos.socioclub.repository;

import com.losalgarrobos.socioclub.model.EstadoInscripcion;
import com.losalgarrobos.socioclub.model.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

// IMPORTANTE: siempre pasar EstadoInscripcion.ACTIVA en estos métodos. Si se cuentan las BAJA,
// el cupo, RN2 y RN5 dan mal (por ejemplo, Ajedrez aparecería lleno al arrancar).
@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {

    // GET /api/socios/{id}/inscripciones y pantalla del socio en la secretaría.
    // "socio.id" se escribe SocioId: Spring recorre la relación y arma el JOIN.
    List<Inscripcion> findBySocioIdAndEstadoOrderByFechaInscripcionAsc(Long socioId, EstadoInscripcion estado);

    // Listado de inscriptos de una actividad (secretaría).
    List<Inscripcion> findByActividadIdAndEstadoOrderByFechaInscripcionAsc(Long actividadId, EstadoInscripcion estado);

    // RN2: ¿el socio ya tiene una inscripción ACTIVA en esta actividad?
    boolean existsBySocioIdAndActividadIdAndEstado(Long socioId, Long actividadId, EstadoInscripcion estado);

    // RN5: cuántas inscripciones ACTIVA tiene el socio.
    long countBySocioIdAndEstado(Long socioId, EstadoInscripcion estado);

    // Consulta escrita a mano con @Query (JPQL: usa entidades y atributos, no tablas y columnas).
    // RN1, cupo disponible y el 409 al borrar una actividad con inscriptos activos.
    @Query("SELECT COUNT(i) FROM Inscripcion i WHERE i.actividad.id = :actividadId AND i.estado = :estado")
    long contarPorActividadYEstado(@Param("actividadId") Long actividadId,
                                   @Param("estado") EstadoInscripcion estado);
}
