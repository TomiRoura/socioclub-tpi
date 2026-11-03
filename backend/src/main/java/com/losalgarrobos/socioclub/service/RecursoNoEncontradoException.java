package com.losalgarrobos.socioclub.service;

// La lanza un servicio cuando no existe el socio, la actividad o la inscripción pedida.
// La API la convierte en 404 Not Found.
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
