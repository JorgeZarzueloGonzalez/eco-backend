package es.jorgezarzuelo.eco_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import es.jorgezarzuelo.eco_backend.enums.AuthProvider;
import es.jorgezarzuelo.eco_backend.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsernameIgnoreCase(String username);

    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByEmailIgnoreCase(String email);

    Optional<User> findByAuthProviderAndProviderId(AuthProvider authProvider, String providerId);
}
