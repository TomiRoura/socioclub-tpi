package com.losalgarrobos.socioclub.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// CORS: la app React corre en http://localhost:3000 y el backend en :8080. Son orígenes distintos,
// así que el navegador bloquea las respuestas salvo que el backend diga que ese origen está permitido
// (cabecera Access-Control-Allow-Origin). Antes de un POST con JSON, el navegador manda una petición
// OPTIONS ("preflight") para preguntar si puede: por eso se habilita OPTIONS y el header Content-Type.
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}
