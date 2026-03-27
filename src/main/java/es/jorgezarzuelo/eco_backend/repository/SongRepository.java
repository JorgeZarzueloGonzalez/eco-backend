package es.jorgezarzuelo.eco_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import es.jorgezarzuelo.eco_backend.model.Song;

public interface SongRepository extends JpaRepository<Song, Long> {

    Optional<Song> findByTitleIgnoreCaseAndArtistIgnoreCase(String title, String artist);

    boolean existsByTitleIgnoreCaseAndArtistIgnoreCase(String title, String artist);

}
