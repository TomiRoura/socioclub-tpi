package com.losalgarrobos.socioclub.controller;

import com.losalgarrobos.socioclub.service.RecursoNoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

// Errores de las pantallas de la secretaría (no de la API, que tiene su propio manejador).
// Si alguien entra a /socios/999 o /actividades/999/editar, muestra la vista "error" con un 404.
@ControllerAdvice(assignableTypes = {
        InicioController.class, ActividadController.class, SocioController.class, InscripcionController.class})
public class ManejadorErroresVistas {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String noEncontrado(RecursoNoEncontradoException e, Model model) {
        model.addAttribute("status", 404);
        model.addAttribute("mensaje", e.getMessage());
        return "error";
    }
}
