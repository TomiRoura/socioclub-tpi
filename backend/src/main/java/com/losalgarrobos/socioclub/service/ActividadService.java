package com.losalgarrobos.socioclub.service;

import com.losalgarrobos.socioclub.model.Actividad;
import com.losalgarrobos.socioclub.model.EstadoInscripcion;
import com.losalgarrobos.socioclub.repository.ActividadRepository;
import com.losalgarrobos.socioclub.repository.InscripcionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Capa de servicio de actividades. La usan los dos tipos de controlador:
// ActividadController (Thymeleaf) y ActividadRestController (API).
@Service
public class ActividadService {

    // Mismas disciplinas que el filtro de la app React (src/utils/constantes.js): tienen que
    // coincidir letra por letra, con tildes, para que ?disciplina= encuentre resultados.
    public static final List<String> DISCIPLINAS = List.of(
            "Fútbol", "Vóley", "Básquet", "Natación", "Patín", "Ajedrez", "Fitness", "Bienestar");

    private final ActividadRepository actividadRepository;
    private final InscripcionRepository inscripcionRepository;

    // Inyección de dependencias por constructor: Spring crea los repositorios (beans)
    // y los pasa acá al crear el servicio.
    public ActividadService(ActividadRepository actividadRepository,
                            InscripcionRepository inscripcionRepository) {
        this.actividadRepository = actividadRepository;
        this.inscripcionRepository = inscripcionRepository;
    }

    // Todas las actividades, o solo las de una disciplina si viene el filtro.
    public List<Actividad> listar(String disciplina) {
        List<Actividad> actividades;
        if (disciplina == null || disciplina.isBlank()) {
            actividades = actividadRepository.findAllByOrderByIdAsc();
        } else {
            actividades = actividadRepository.findByDisciplinaOrderByIdAsc(disciplina.trim());
        }
        actividades.forEach(this::completarCupoDisponible);
        return actividades;
    }

    public Actividad buscarPorId(Long id) {
        Actividad actividad = actividadRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Actividad no encontrada"));
        completarCupoDisponible(actividad);
        return actividad;
    }

    @Transactional
    public Actividad crear(Actividad datos) {
        validar(datos);
        datos.setId(null); // siempre es un alta: el id lo genera MySQL
        Actividad guardada = actividadRepository.save(datos);
        completarCupoDisponible(guardada);
        return guardada;
    }

    @Transactional
    public Actividad actualizar(Long id, Actividad datos) {
        Actividad existente = buscarPorId(id);
        validar(datos);
        long activas = contarActivas(id);
        if (datos.getCupoMaximo() < activas) {
            throw new DatosInvalidosException(
                    "El cupo no puede ser menor que los " + activas + " inscriptos activos");
        }
        existente.setNombre(datos.getNombre().trim());
        existente.setDisciplina(datos.getDisciplina().trim());
        existente.setProfesor(datos.getProfesor().trim());
        existente.setDias(datos.getDias().trim());
        existente.setHorario(datos.getHorario().trim());
        existente.setEdadMinima(datos.getEdadMinima());
        existente.setEdadMaxima(datos.getEdadMaxima());
        existente.setCupoMaximo(datos.getCupoMaximo());
        existente.setCuotaMensual(datos.getCuotaMensual());
        Actividad guardada = actividadRepository.save(existente);
        completarCupoDisponible(guardada);
        return guardada;
    }

    // Solo se puede eliminar una actividad sin inscriptos ACTIVA (la API responde 409 si tiene).
    // Sus inscripciones dadas de BAJA se borran con ella (cascade REMOVE en Actividad).
    @Transactional
    public void eliminar(Long id) {
        Actividad existente = buscarPorId(id);
        long activas = contarActivas(id);
        if (activas > 0) {
            throw new ReglaNegocioException(null,
                    "No se puede eliminar la actividad: tiene " + activas + " inscriptos activos");
        }
        actividadRepository.delete(existente);
    }

    // cupoDisponible = cupoMaximo - inscripciones ACTIVA, sin bajar de 0.
    // Se llama antes de devolver cualquier actividad (lista, detalle y respuesta del POST).
    public void completarCupoDisponible(Actividad actividad) {
        long activas = contarActivas(actividad.getId());
        actividad.setCupoDisponible(Math.max(0, actividad.getCupoMaximo() - (int) activas));
    }

    public long contarActivas(Long actividadId) {
        return inscripcionRepository.contarPorActividadYEstado(actividadId, EstadoInscripcion.ACTIVA);
    }

    private void validar(Actividad a) {
        if (estaVacio(a.getNombre()) || estaVacio(a.getDisciplina()) || estaVacio(a.getProfesor())
                || estaVacio(a.getDias()) || estaVacio(a.getHorario())) {
            throw new DatosInvalidosException("Nombre, disciplina, profesor, días y horario son obligatorios");
        }
        if (!a.getHorario().trim().matches("\\d{1,2}:\\d{2}")) {
            throw new DatosInvalidosException("El horario debe tener el formato HH:MM, por ejemplo 18:00");
        }
        if (a.getEdadMinima() == null || a.getEdadMaxima() == null
                || a.getCupoMaximo() == null || a.getCuotaMensual() == null) {
            throw new DatosInvalidosException("Edades, cupo y cuota son obligatorios");
        }
        if (a.getEdadMinima() < 0 || a.getEdadMinima() > a.getEdadMaxima()) {
            throw new DatosInvalidosException("La edad mínima no puede ser mayor que la máxima");
        }
        if (a.getCupoMaximo() <= 0) {
            throw new DatosInvalidosException("El cupo máximo tiene que ser mayor que 0");
        }
        if (a.getCuotaMensual() < 0) {
            throw new DatosInvalidosException("La cuota mensual no puede ser negativa");
        }
    }

    private boolean estaVacio(String texto) {
        return texto == null || texto.isBlank();
    }
}
