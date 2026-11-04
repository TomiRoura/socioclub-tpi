package com.socioclub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Sin esto el navegador bloquea los pedidos de la app React (localhost:3000) al backend (localhost:8080),
// porque son orígenes distintos. Se acepta cualquier puerto de localhost por si el 3000 está ocupado
// y React arranca en otro (3001, 3002...). Solo se abre /api/**, las pantallas de secretaría no lo necesitan.
@Configuration
public class CorsConfig {

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/api/**")
						.allowedOriginPatterns("http://localhost:[*]")
						.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
						.allowedHeaders("*");
			}
		};
	}
}
