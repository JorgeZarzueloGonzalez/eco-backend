package es.jorgezarzuelo.eco_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import es.jorgezarzuelo.eco_backend.model.Artist;

public interface ArtistRepository extends JpaRepository<Artist, Long> {

    boolean existsByNameIgnoreCase(String name);

    Optional<Artist> findByNameIgnoreCase(String name);
    
} 
