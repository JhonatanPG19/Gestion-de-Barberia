package co.edu.unicauca.notificacion_service.consumer;

import co.edu.unicauca.notificacion_service.dto.ReservaEventoDTO;
import co.edu.unicauca.notificacion_service.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventConsumer {

    private final NotificacionService notificacionService;

    @RabbitListener(queues = "${rabbitmq.queue.reserva.creada}")
    public void consumirEventoReservaCreada(ReservaEventoDTO evento) {
        try {
            log.info("Evento recibido - Reserva Creada: {}", evento);
            notificacionService.procesarEventoReservaCreada(evento);
        } catch (Exception e) {
            log.error("Error procesando evento de reserva creada: {}", e.getMessage(), e);
        }
    }

    @RabbitListener(queues = "${rabbitmq.queue.reserva.cancelada}")
    public void consumirEventoReservaCancelada(ReservaEventoDTO evento) {
        try {
            log.info("Evento recibido - Reserva Cancelada: {}", evento);
            notificacionService.procesarEventoReservaCancelada(evento);
        } catch (Exception e) {
            log.error("Error procesando evento de reserva cancelada: {}", e.getMessage(), e);
        }
    }

    @RabbitListener(queues = "${rabbitmq.queue.reserva.modificada}")
    public void consumirEventoReservaModificada(ReservaEventoDTO evento) {
        try {
            log.info("Evento recibido - Reserva Modificada: {}", evento);
            notificacionService.procesarEventoReservaModificada(evento);
        } catch (Exception e) {
            log.error("Error procesando evento de reserva modificada: {}", e.getMessage(), e);
        }
    }
}
