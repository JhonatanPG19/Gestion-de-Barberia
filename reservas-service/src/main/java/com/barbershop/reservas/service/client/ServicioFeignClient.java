package com.barbershop.reservas.service.client;

import com.barbershop.reservas.dto.external.ServicioDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "servicio-service", url = "${services.servicio.url}")
public interface ServicioFeignClient {
    
    @GetMapping("/api/v1/servicios/{id}")
    ServicioDto obtenerServicio(@PathVariable("id") Long id);
    
    @GetMapping("/api/v1/servicios/{id}/existe")
    Boolean existeServicio(@PathVariable("id") Long id);
    
    @GetMapping("/api/v1/servicios/barbero-realiza")
    Boolean barberoRealizaServicio(
            @RequestParam("barberoId") Long barberoId,
            @RequestParam("servicioId") Long servicioId
    );
}
