package es.jorgezarzuelo.eco_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import es.jorgezarzuelo.eco_backend.model.Artist;
import es.jorgezarzuelo.eco_backend.model.Song;

public interface SongRepository extends JpaRepository<Song, Long> {

    Optional<Song> findByTitleIgnoreCaseAndMainArtist(String title, Artist mainArtist);

    boolean existsByTitleIgnoreCaseAndMainArtist(String title, Artist mainArtist);

}
