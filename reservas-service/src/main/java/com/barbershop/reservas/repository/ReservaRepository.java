package com.barbershop.reservas.repository;

import com.barbershop.reservas.entities.ReservaEntity;
import com.barbershop.reservas.entities.enums.EstadoReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<ReservaEntity, Long> {
    
    List<ReservaEntity> findByClienteId(Long clienteId);
    
    @Query("SELECT r FROM ReservaEntity r WHERE r.barberoId = :barberoId " +
           "AND DATE(r.fechaHora) = :fecha ORDER BY r.fechaHora")
    List<ReservaEntity> findByBarberoIdAndFecha(@Param("barberoId") Long barberoId, 
                                                  @Param("fecha") LocalDate fecha);
    
    @Query("SELECT r FROM ReservaEntity r WHERE DATE(r.fechaHora) = :fecha ORDER BY r.fechaHora")
    List<ReservaEntity> findByFecha(@Param("fecha") LocalDate fecha);
    
    List<ReservaEntity> findByEstado(EstadoReserva estado);
    
    @Query("SELECT r FROM ReservaEntity r WHERE r.barberoId = :barberoId " +
           "AND r.fechaHora < :fin AND r.fechaHoraFin > :inicio " +
           "AND r.estado IN ('CONFIRMADA', 'PENDIENTE', 'EN_PROCESO')")
    List<ReservaEntity> findByBarberoIdAndFechaHoraOverlap(
            @Param("barberoId") Long barberoId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);
    
    @Query("SELECT COUNT(r) FROM ReservaEntity r WHERE r.barberoId = :barberoId " +
           "AND r.estado = :estado AND DATE(r.fechaHora) = :fecha")
    Integer countByBarberoIdAndEstadoAndFecha(
            @Param("barberoId") Long barberoId,
            @Param("estado") EstadoReserva estado,
            @Param("fecha") LocalDate fecha);
    
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM ReservaEntity r " +
           "WHERE r.barberoId = :barberoId AND r.fechaHora < :fin AND r.fechaHoraFin > :inicio " +
           "AND r.estado IN ('CONFIRMADA', 'PENDIENTE', 'EN_PROCESO')")
    boolean existsByBarberoIdAndFechaHoraOverlap(
            @Param("barberoId") Long barberoId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);
}
