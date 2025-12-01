package co.edu.unicauca.notificacion_service.controller;

import co.edu.unicauca.notificacion_service.model.EstadoNotificacion;
import co.edu.unicauca.notificacion_service.repository.NotificacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final NotificacionRepository notificacionRepository;

    @GetMapping("/metricas")
    public ResponseEntity<Map<String, Object>> obtenerMetricas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {

        // Si no se especifican fechas, usar el mes actual
        if (inicio == null) {
            inicio = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        }
        if (fin == null) {
            fin = LocalDateTime.now();
        }

        Map<String, Object> metricas = new HashMap<>();
        
        metricas.put("periodo", Map.of(
                "inicio", inicio,
                "fin", fin
        ));

        metricas.put("enviadas", notificacionRepository.countByEstadoAndFechaCreacionBetween(
                EstadoNotificacion.ENVIADA, inicio, fin));
        
        metricas.put("fallidas", notificacionRepository.countByEstadoAndFechaCreacionBetween(
                EstadoNotificacion.FALLIDA, inicio, fin));
        
        metricas.put("pendientes", notificacionRepository.countByEstadoAndFechaCreacionBetween(
                EstadoNotificacion.PENDIENTE, inicio, fin));

        Long total = (Long) metricas.get("enviadas") + 
                     (Long) metricas.get("fallidas") + 
                     (Long) metricas.get("pendientes");
        
        metricas.put("total", total);

        // Calcular tasa de éxito
        if (total > 0) {
            double tasaExito = ((Long) metricas.get("enviadas") * 100.0) / total;
            metricas.put("tasaExito", String.format("%.2f%%", tasaExito));
        } else {
            metricas.put("tasaExito", "0.00%");
        }

        return ResponseEntity.ok(metricas);
    }

    @GetMapping("/resumen-mensual")
    public ResponseEntity<Map<String, Object>> resumenMensual() {
        LocalDateTime inicioMes = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime finMes = LocalDateTime.now();

        return obtenerMetricas(inicioMes, finMes);
    }

    @GetMapping("/resumen-diario")
    public ResponseEntity<Map<String, Object>> resumenDiario() {
        LocalDateTime inicioDia = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime finDia = LocalDateTime.now();

        return obtenerMetricas(inicioDia, finDia);
    }
}
