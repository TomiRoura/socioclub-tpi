package com.socioclub.exception;

public class ReglaNegocioException extends RuntimeException {
    private String regla;

    public ReglaNegocioException(String mensaje, String regla) {
        super(mensaje);
        this.regla = regla;
    }

    public String getRegla() {
        return regla;
    }
}