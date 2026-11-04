package com.socioclub.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// La pantalla principal de secretaría es el listado de actividades
@Controller
public class InicioController {

	@GetMapping("/")
	public String inicio() {
		return "redirect:/actividades";
	}
}
