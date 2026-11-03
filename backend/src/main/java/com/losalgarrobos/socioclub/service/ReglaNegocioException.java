package com.losalgarrobos.socioclub.service;

// La lanza un servicio cuando una regla de negocio rechaza la operación.
// La secretaría muestra el mensaje en la vista y la API responde 409 Conflict con { error, regla }.
// "regla" es el código (RN1 a RN5) o null cuando no es una de las reglas numeradas
// (por ejemplo, borrar una actividad con inscriptos activos).
public class ReglaNegocioException extends RuntimeException {

    private final String regla;

    public ReglaNegocioException(String regla, String mensaje) {
        super(mensaje);
        this.regla = regla;
    }

    public String getRegla() {
        return regla;
    }
}
