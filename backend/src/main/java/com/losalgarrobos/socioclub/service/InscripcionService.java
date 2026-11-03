package com.losalgarrobos.socioclub.service;

import com.losalgarrobos.socioclub.model.Actividad;
import com.losalgarrobos.socioclub.model.EstadoCuota;
import com.losalgarrobos.socioclub.model.EstadoInscripcion;
import com.losalgarrobos.socioclub.model.Inscripcion;
import com.losalgarrobos.socioclub.model.Socio;
import com.losalgarrobos.socioclub.repository.InscripcionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

// Corazón del sistema: acá viven las reglas de negocio RN1 a RN5.
// Se escriben UNA sola vez. La secretaría (InscripcionController, Thymeleaf) y la app de socios
// (InscripcionRestController, API) llaman al mismo método inscribir(), así que una inscripción
// se valida exactamente igual entre por donde entre.
@Service
public class InscripcionService {

    // RN5: máximo de inscripciones ACTIVA por socio. Si piden pasarlo a 4, se cambia solo acá
    // (el mensaje usa la constante) y en src/utils/constantes.js de la app.
    public static final int MAX_ACTIVIDADES = 3;

    // RN6: mismos descuentos que la app (10 % con 2 actividades, 15 % con 3 o más).
    public static final int DESCUENTO_DOS_ACTIVIDADES = 10;
    public static final int DESCUENTO_TRES_ACTIVIDADES = 15;

    private static final ZoneId ZONA_CLUB = ZoneId.of("America/Argentina/Cordoba");

    private final InscripcionRepository inscripcionRepository;
    private final SocioService socioService;
    private final ActividadService actividadService;

    public InscripcionService(InscripcionRepository inscripcionRepository,
                              SocioService socioService,
                              ActividadService actividadService) {
        this.inscripcionRepository = inscripcionRepository;
        this.socioService = socioService;
        this.actividadService = actividadService;
    }

    // RF6: inscribe a un socio en una actividad aplicando RN1 a RN5.
    // @Transactional: las validaciones y el INSERT van en una misma transacción.
    @Transactional
    public Inscripcion inscribir(Long socioId, Long actividadId) {
        if (socioId == null || actividadId == null) {
            throw new DatosInvalidosException("Hay que indicar el socio y la actividad");
        }
        Socio socio = socioService.buscarPorId(socioId);                // 404 si no existe
        Actividad actividad = actividadService.buscarPorId(actividadId); // 404 si no existe

        validarReglas(socio, actividad);

        Inscripcion nueva = inscripcionRepository.save(new Inscripcion(socio, actividad, hoy()));
        // Se recalcula después de guardar para que la respuesta ya traiga el cupo descontado.
        actividadService.completarCupoDisponible(actividad);
        return nueva;
    }

    // Orden de evaluación acordado con la app (src/utils/reglas.js): RN4, RN2, RN5, RN3, RN1.
    // Si fallan dos reglas a la vez, el socio ve el mensaje de la primera.
    private void validarReglas(Socio socio, Actividad actividad) {
        // RN4: cuota al día. Un MOROSO no puede inscribirse (sí consultar y darse de baja).
        if (socio.getEstadoCuota() == EstadoCuota.MOROSO) {
            throw new ReglaNegocioException("RN4", "Debe regularizar su cuota en secretaría");
        }

        // RN2: sin duplicados entre las inscripciones ACTIVA.
        boolean yaInscripto = inscripcionRepository.existsBySocioIdAndActividadIdAndEstado(
                socio.getId(), actividad.getId(), EstadoInscripcion.ACTIVA);
        if (yaInscripto) {
            throw new ReglaNegocioException("RN2", "El socio ya está inscripto en esta actividad");
        }

        // RN5: máximo de actividades ACTIVA.
        long activasDelSocio = inscripcionRepository.countBySocioIdAndEstado(
                socio.getId(), EstadoInscripcion.ACTIVA);
        if (activasDelSocio >= MAX_ACTIVIDADES) {
            throw new ReglaNegocioException("RN5", "Alcanzó el máximo de " + MAX_ACTIVIDADES + " actividades");
        }

        // RN3: edad a la fecha de hoy dentro del rango de la actividad (extremos incluidos).
        int edad = socio.calcularEdad(hoy());
        if (!actividad.esAptaParaEdad(edad)) {
            throw new ReglaNegocioException("RN3", "La edad permitida para esta actividad es de "
                    + actividad.getEdadMinima() + " a " + actividad.getEdadMaxima() + " años");
        }

        // RN1: cupo. Las ACTIVA no pueden igualar o superar el cupo máximo.
        long activasDeLaActividad = actividadService.contarActivas(actividad.getId());
        if (activasDeLaActividad >= actividad.getCupoMaximo()) {
            throw new ReglaNegocioException("RN1", "La actividad no tiene cupo disponible");
        }
    }

    // RF9: baja lógica. No se borra la fila: el estado pasa a BAJA y el cupo se libera solo.
    // Un MOROSO también puede darse de baja.
    @Transactional
    public Inscripcion darDeBaja(Long inscripcionId) {
        Inscripcion inscripcion = inscripcionRepository.findById(inscripcionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Inscripción no encontrada"));
        if (inscripcion.getEstado() == EstadoInscripcion.ACTIVA) {
            inscripcion.setEstado(EstadoInscripcion.BAJA);
            inscripcion = inscripcionRepository.save(inscripcion);
        }
        actividadService.completarCupoDisponible(inscripcion.getActividad());
        return inscripcion;
    }

    // RF8: inscripciones ACTIVA de un socio, cada una con su actividad adentro.
    public List<Inscripcion> listarActivasDeSocio(Long socioId) {
        socioService.buscarPorId(socioId); // 404 si el socio no existe
        List<Inscripcion> activas = inscripcionRepository
                .findBySocioIdAndEstadoOrderByFechaInscripcionAsc(socioId, EstadoInscripcion.ACTIVA);
        activas.forEach(i -> actividadService.completarCupoDisponible(i.getActividad()));
        return activas;
    }

    // RF7: inscriptos activos de una actividad (pantalla de la secretaría).
    public List<Inscripcion> listarInscriptosDeActividad(Long actividadId) {
        actividadService.buscarPorId(actividadId); // 404 si la actividad no existe
        return inscripcionRepository
                .findByActividadIdAndEstadoOrderByFechaInscripcionAsc(actividadId, EstadoInscripcion.ACTIVA);
    }

    // RN6 (la misma cuenta que src/utils/cuota.js de la app): suma de las cuotas de las
    // actividades activas, con 10 % de descuento si son 2 y 15 % si son 3 o más,
    // redondeada al peso entero.
    public int calcularCuotaMensual(List<Inscripcion> activas) {
        int subtotal = activas.stream().mapToInt(i -> i.getActividad().getCuotaMensual()).sum();
        int porcentaje = porcentajeDescuento(activas.size());
        return (int) Math.round(subtotal * (100 - porcentaje) / 100.0);
    }

    public int porcentajeDescuento(int cantidadActividades) {
        if (cantidadActividades >= 3) {
            return DESCUENTO_TRES_ACTIVIDADES;
        }
        if (cantidadActividades == 2) {
            return DESCUENTO_DOS_ACTIVIDADES;
        }
        return 0;
    }

    private LocalDate hoy() {
        return LocalDate.now(ZONA_CLUB);
    }
}
