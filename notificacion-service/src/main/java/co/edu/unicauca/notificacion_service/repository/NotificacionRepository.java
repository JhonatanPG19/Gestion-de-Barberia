package co.edu.unicauca.notificacion_service.repository;

import co.edu.unicauca.notificacion_service.model.EstadoNotificacion;
import co.edu.unicauca.notificacion_service.model.Notificacion;
import co.edu.unicauca.notificacion_service.model.TipoNotificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    
    List<Notificacion> findByEstado(EstadoNotificacion estado);
    
    List<Notificacion> findByDestinatario(String destinatario);
    
    List<Notificacion> findByReservaId(Long reservaId);
    
    List<Notificacion> findByTipo(TipoNotificacion tipo);
    
    List<Notificacion> findByFechaCreacionBetween(LocalDateTime inicio, LocalDateTime fin);
    
    Long countByEstadoAndFechaCreacionBetween(EstadoNotificacion estado, LocalDateTime inicio, LocalDateTime fin);
}
