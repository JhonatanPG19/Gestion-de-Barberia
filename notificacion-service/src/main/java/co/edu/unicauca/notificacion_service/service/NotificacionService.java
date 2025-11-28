package co.edu.unicauca.notificacion_service.service;

import co.edu.unicauca.notificacion_service.dto.ReservaEventoDTO;
import co.edu.unicauca.notificacion_service.model.Notificacion;
import co.edu.unicauca.notificacion_service.model.TipoNotificacion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificacionService {

    private final EmailService emailService;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public void procesarEventoReservaCreada(ReservaEventoDTO evento) {
        log.info("Procesando evento de reserva creada: {}", evento.getReservaId());

        String mensaje = construirMensajeReservaCreada(evento);
        
        Notificacion notificacion = Notificacion.builder()
                .destinatario(evento.getClienteEmail())
                .asunto("Confirmación de Reserva - Barbería")
                .mensaje(mensaje)
                .tipo(TipoNotificacion.RESERVA_CREADA)
                .reservaId(evento.getReservaId())
                .build();

        emailService.enviarEmail(notificacion);
    }

    public void procesarEventoReservaCancelada(ReservaEventoDTO evento) {
        log.info("Procesando evento de reserva cancelada: {}", evento.getReservaId());

        String mensaje = construirMensajeReservaCancelada(evento);
        
        Notificacion notificacion = Notificacion.builder()
                .destinatario(evento.getClienteEmail())
                .asunto("Cancelación de Reserva - Barbería")
                .mensaje(mensaje)
                .tipo(TipoNotificacion.RESERVA_CANCELADA)
                .reservaId(evento.getReservaId())
                .build();

        emailService.enviarEmail(notificacion);
    }

    public void procesarEventoReservaModificada(ReservaEventoDTO evento) {
        log.info("Procesando evento de reserva modificada: {}", evento.getReservaId());

        String mensaje = construirMensajeReservaModificada(evento);
        
        Notificacion notificacion = Notificacion.builder()
                .destinatario(evento.getClienteEmail())
                .asunto("Modificación de Reserva - Barbería")
                .mensaje(mensaje)
                .tipo(TipoNotificacion.RESERVA_MODIFICADA)
                .reservaId(evento.getReservaId())
                .build();

        emailService.enviarEmail(notificacion);
    }

    private String construirMensajeReservaCreada(ReservaEventoDTO evento) {
        return String.format("""
                Hola %s,
                
                Tu reserva ha sido confirmada exitosamente.
                
                Detalles de tu reserva:
                - Fecha y Hora: %s
                - Barbero: %s
                - Servicio: %s
                
                Te esperamos en nuestra barbería.
                
                Si necesitas cancelar o modificar tu reserva, por favor contáctanos con anticipación.
                
                Saludos,
                Equipo de Barbería
                """,
                evento.getClienteNombre(),
                evento.getFechaHora().format(formatter),
                evento.getBarberoNombre(),
                evento.getServicio()
        );
    }

    private String construirMensajeReservaCancelada(ReservaEventoDTO evento) {
        String motivoTexto = evento.getMotivoCancelacion() != null 
                ? "\nMotivo: " + evento.getMotivoCancelacion() 
                : "";

        return String.format("""
                Hola %s,
                
                Tu reserva ha sido cancelada.
                
                Detalles de la reserva cancelada:
                - Fecha y Hora: %s
                - Barbero: %s
                - Servicio: %s%s
                
                Esperamos poder atenderte en otra ocasión.
                Para agendar una nueva reserva, visita nuestro sistema de reservas.
                
                Saludos,
                Equipo de Barbería
                """,
                evento.getClienteNombre(),
                evento.getFechaHora().format(formatter),
                evento.getBarberoNombre(),
                evento.getServicio(),
                motivoTexto
        );
    }

    private String construirMensajeReservaModificada(ReservaEventoDTO evento) {
        return String.format("""
                Hola %s,
                
                Tu reserva ha sido modificada.
                
                Nuevos detalles de tu reserva:
                - Fecha y Hora: %s
                - Barbero: %s
                - Servicio: %s
                
                Por favor confirma que estos cambios son correctos.
                
                Saludos,
                Equipo de Barbería
                """,
                evento.getClienteNombre(),
                evento.getFechaHora().format(formatter),
                evento.getBarberoNombre(),
                evento.getServicio()
        );
    }
}
