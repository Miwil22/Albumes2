package org.example.users.repositories;

import org.example.users.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    // Buscar por username (para el login)
    Optional<User> findByUsername(String username);

    // Buscar por email
    Optional<User> findByEmail(String email);

    // Comprobar existencia (útil para validaciones al registrarse)
    Optional<User> findByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(String username, String email);
}