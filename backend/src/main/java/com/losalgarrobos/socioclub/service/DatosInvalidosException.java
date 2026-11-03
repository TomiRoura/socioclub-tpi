package com.losalgarrobos.socioclub.service;

// La lanza un servicio cuando los datos de un alta o modificación no son válidos
// (DNI mal formado, edades al revés, campos vacíos...). La API la convierte en 400 Bad Request.
public class DatosInvalidosException extends RuntimeException {

    public DatosInvalidosException(String mensaje) {
        super(mensaje);
    }
}
