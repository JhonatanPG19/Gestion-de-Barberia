package com.barbershop.reservas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Aplicación principal del microservicio de Reservas
 * Arquitectura en capas:
 * - controller: Controladores REST
 * - service: Lógica de negocio y servicios de fachada
 * - repository: Acceso a datos con Spring Data JPA
 * - entities: Entidades JPA
 * - dto: Objetos de transferencia de datos
 * - config: Configuraciones de Spring
 */
@SpringBootApplication
@EnableFeignClients(basePackages = "com.barbershop.reservas.service.client")
@EnableJpaAuditing
public class ReservasServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReservasServiceApplication.class, args);
    }
}
