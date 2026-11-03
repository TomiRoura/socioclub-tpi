package com.losalgarrobos.socioclub.service;

import com.losalgarrobos.socioclub.model.EstadoCuota;
import com.losalgarrobos.socioclub.model.Socio;
import com.losalgarrobos.socioclub.repository.SocioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class SocioService {

    public static final int TAMANIO_PAGINA = 10;

    private final SocioRepository socioRepository;

    public SocioService(SocioRepository socioRepository) {
        this.socioRepository = socioRepository;
    }

    // Listado paginado con búsqueda por apellido (RF5). "pagina" empieza en 0.
    public Page<Socio> listarPaginado(String apellido, int pagina) {
        String texto = (apellido == null) ? "" : apellido.trim();
        PageRequest pedido = PageRequest.of(Math.max(pagina, 0), TAMANIO_PAGINA,
                Sort.by("apellido").ascending().and(Sort.by("nombre").ascending()));
        return socioRepository.findByApellidoContainingIgnoreCase(texto, pedido);
    }

    public List<Socio> listarTodos() {
        return socioRepository.findAllByOrderByApellidoAscNombreAsc();
    }

    public Socio buscarPorId(Long id) {
        return socioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Socio no encontrado"));
    }

    // Ingreso a la app de socios con el DNI (RF8).
    public Socio buscarPorDni(String dni) {
        return socioRepository.findByDni(dni == null ? "" : dni.trim())
                .orElseThrow(() -> new RecursoNoEncontradoException("Socio no encontrado"));
    }

    @Transactional
    public Socio crear(Socio datos) {
        normalizar(datos);
        validar(datos);
        if (socioRepository.findByDni(datos.getDni()).isPresent()) {
            throw new DatosInvalidosException("Ya existe un socio con el DNI " + datos.getDni());
        }
        datos.setId(null);
        if (datos.getEstadoCuota() == null) {
            datos.setEstadoCuota(EstadoCuota.AL_DIA); // un socio nuevo arranca al día
        }
        return socioRepository.save(datos);
    }

    @Transactional
    public Socio actualizar(Long id, Socio datos) {
        Socio existente = buscarPorId(id);
        normalizar(datos);
        validar(datos);
        // El DNI puede ser el mismo del socio, pero no el de otro.
        Optional<Socio> conEseDni = socioRepository.findByDni(datos.getDni());
        if (conEseDni.isPresent() && !conEseDni.get().getId().equals(id)) {
            throw new DatosInvalidosException("Ya existe otro socio con el DNI " + datos.getDni());
        }
        existente.setDni(datos.getDni());
        existente.setNombre(datos.getNombre());
        existente.setApellido(datos.getApellido());
        existente.setFechaNacimiento(datos.getFechaNacimiento());
        existente.setEmail(datos.getEmail());
        existente.setTelefono(datos.getTelefono());
        // Si el formulario no manda el estado, se conserva (así no se pierde un MOROSO sin avisar).
        if (datos.getEstadoCuota() != null) {
            existente.setEstadoCuota(datos.getEstadoCuota());
        }
        return socioRepository.save(existente);
    }

    private void normalizar(Socio s) {
        s.setDni(s.getDni() == null ? null : s.getDni().trim());
        s.setNombre(s.getNombre() == null ? null : s.getNombre().trim());
        s.setApellido(s.getApellido() == null ? null : s.getApellido().trim());
        s.setEmail(s.getEmail() == null || s.getEmail().isBlank() ? null : s.getEmail().trim());
        s.setTelefono(s.getTelefono() == null || s.getTelefono().isBlank() ? null : s.getTelefono().trim());
    }

    private void validar(Socio s) {
        // RN7 también del lado del servidor: 7 u 8 dígitos numéricos, sin puntos.
        if (s.getDni() == null || !s.getDni().matches("\\d{7,8}")) {
            throw new DatosInvalidosException("El DNI debe tener 7 u 8 dígitos numéricos, sin puntos");
        }
        if (s.getNombre() == null || s.getNombre().isEmpty()
                || s.getApellido() == null || s.getApellido().isEmpty()) {
            throw new DatosInvalidosException("Nombre y apellido son obligatorios");
        }
        if (s.getFechaNacimiento() == null || s.getFechaNacimiento().isAfter(LocalDate.now())) {
            throw new DatosInvalidosException("La fecha de nacimiento es obligatoria y no puede ser futura");
        }
        if (s.getEmail() != null && !s.getEmail().matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) {
            throw new DatosInvalidosException("El email no tiene un formato válido");
        }
    }
}
