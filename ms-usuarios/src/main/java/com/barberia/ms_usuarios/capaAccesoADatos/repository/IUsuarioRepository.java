package com.barberia.ms_usuarios.capaAccesoADatos.repository;

import com.barberia.ms_usuarios.dominio.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUsuarioRepository extends JpaRepository<Usuario, Integer> {
    // Spring Data JPA implementa esto automáticamente en tiempo de ejecución
    boolean existsByUsername(String username);
    boolean existsByCorreo(String correo);
}