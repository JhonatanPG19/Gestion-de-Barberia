package co.edu.unicauca.barbero_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.unicauca.barbero_service.model.Barbero;

@Repository
public interface BarberoRepository extends JpaRepository<Barbero, Integer> {
    List<Barbero> findByEstado(String estado);
    boolean existsByEmail(String email);
    void deleteById(Integer id);
    Optional<Barbero> findByUserId(Integer userId);
}
