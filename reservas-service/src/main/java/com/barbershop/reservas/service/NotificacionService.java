package com.barbershop.reservas.service;

import com.barbershop.reservas.entities.ReservaEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificacionService {

    private final RabbitTemplate rabbitTemplate;
    
    private static final String EXCHANGE = "barbershop.notifications";
    private static final String ROUTING_KEY_RESERVA_CREADA = "reserva.creada";
    private static final String ROUTING_KEY_RESERVA_CONFIRMADA = "reserva.confirmada";
    private static final String ROUTING_KEY_RESERVA_CANCELADA = "reserva.cancelada";
    private static final String ROUTING_KEY_RESERVA_REPROGRAMADA = "reserva.reprogramada";
    private static final String ROUTING_KEY_SERVICIO_INICIADO = "servicio.iniciado";
    private static final String ROUTING_KEY_SERVICIO_COMPLETADO = "servicio.completado";

    public void enviarNotificacionReservaCreada(ReservaEntity reserva) {
        log.info("Enviando notificación de reserva creada: {}", reserva.getId());
        Map<String, Object> mensaje = construirMensaje(reserva, "RESERVA_CREADA");
        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY_RESERVA_CREADA, mensaje);
    }

    public void enviarNotificacionReservaConfirmada(ReservaEntity reserva) {
        log.info("Enviando notificación de reserva confirmada: {}", reserva.getId());
        Map<String, Object> mensaje = construirMensaje(reserva, "RESERVA_CONFIRMADA");
        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY_RESERVA_CONFIRMADA, mensaje);
    }

    public void enviarNotificacionReservaCancelada(ReservaEntity reserva, String motivo) {
        log.info("Enviando notificación de reserva cancelada: {}", reserva.getId());
        Map<String, Object> mensaje = construirMensaje(reserva, "RESERVA_CANCELADA");
        mensaje.put("motivo", motivo);
        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY_RESERVA_CANCELADA, mensaje);
    }

    public void enviarNotificacionReservaReprogramada(ReservaEntity reserva) {
        log.info("Enviando notificación de reserva reprogramada: {}", reserva.getId());
        Map<String, Object> mensaje = construirMensaje(reserva, "RESERVA_REPROGRAMADA");
        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY_RESERVA_REPROGRAMADA, mensaje);
    }

    public void enviarNotificacionServicioIniciado(ReservaEntity reserva) {
        log.info("Enviando notificación de servicio iniciado: {}", reserva.getId());
        Map<String, Object> mensaje = construirMensaje(reserva, "SERVICIO_INICIADO");
        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY_SERVICIO_INICIADO, mensaje);
    }

    public void enviarNotificacionServicioCompletado(ReservaEntity reserva) {
        log.info("Enviando notificación de servicio completado: {}", reserva.getId());
        Map<String, Object> mensaje = construirMensaje(reserva, "SERVICIO_COMPLETADO");
        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY_SERVICIO_COMPLETADO, mensaje);
    }

    private Map<String, Object> construirMensaje(ReservaEntity reserva, String tipo) {
        Map<String, Object> mensaje = new HashMap<>();
        mensaje.put("tipo", tipo);
        mensaje.put("reservaId", reserva.getId());
        mensaje.put("clienteId", reserva.getClienteId());
        mensaje.put("barberoId", reserva.getBarberoId());
        mensaje.put("servicioId", reserva.getServicioId());
        mensaje.put("fechaHora", reserva.getFechaHora().toString());
        mensaje.put("estado", reserva.getEstado().name());
        return mensaje;
    }
}
