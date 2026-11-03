package com.losalgarrobos.socioclub.repository;

import com.losalgarrobos.socioclub.model.Actividad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Al extender JpaRepository ya trae findAll, findById, save, deleteById y count.
// Spring Data arma la implementación solo: nosotros declaramos la interfaz.
@Repository
public interface ActividadRepository extends JpaRepository<Actividad, Long> {

    // Método derivado del nombre: SELECT * FROM actividades WHERE disciplina = ? ORDER BY id
    // Lo usa GET /api/actividades?disciplina=Fútbol
    List<Actividad> findByDisciplinaOrderByIdAsc(String disciplina);

    // SELECT * FROM actividades ORDER BY id
    List<Actividad> findAllByOrderByIdAsc();
}
