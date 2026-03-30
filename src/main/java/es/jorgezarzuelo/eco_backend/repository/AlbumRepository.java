package es.jorgezarzuelo.eco_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import es.jorgezarzuelo.eco_backend.model.Album;
import es.jorgezarzuelo.eco_backend.model.Artist;

public interface AlbumRepository extends JpaRepository<Album, Long> {

    boolean existsByTitleIgnoreCaseAndArtist(String title, Artist artist);

    Optional<Album> findByTitleIgnoreCaseAndArtist(String title, Artist artist);

}
