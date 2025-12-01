package com.barbershop.reservas.service.client;

import com.barbershop.reservas.dto.external.ClienteDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "usuario-service", url = "${services.usuario.url}")
public interface ClienteFeignClient {
    
    @GetMapping("/api/v1/usuarios/{id}")
    ClienteDto obtenerCliente(@PathVariable("id") Long id);
    
    @GetMapping("/api/v1/usuarios/{id}/existe")
    Boolean existeCliente(@PathVariable("id") Long id);
}
