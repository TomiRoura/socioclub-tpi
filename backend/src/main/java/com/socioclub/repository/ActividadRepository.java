package com.socioclub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.socioclub.model.Actividad;

@Repository
public interface ActividadRepository extends JpaRepository<Actividad, Long> {

	// Filtro del catálogo por disciplina
	List<Actividad> findByDisciplina(String disciplina);
}
