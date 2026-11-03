package com.losalgarrobos.socioclub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Punto de entrada. @SpringBootApplication activa la configuración automática y el escaneo de
// componentes: Spring busca en este paquete y sus subpaquetes las clases marcadas con
// @Controller, @RestController, @Service, @Repository y @Configuration y crea sus beans.
@SpringBootApplication
public class SocioclubApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocioclubApplication.class, args);
    }
}
