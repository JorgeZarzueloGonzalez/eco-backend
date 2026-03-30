package es.jorgezarzuelo.eco_backend.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;

import es.jorgezarzuelo.eco_backend.model.Artist;
import es.jorgezarzuelo.eco_backend.model.Song;
import es.jorgezarzuelo.eco_backend.repository.ArtistRepository;
import es.jorgezarzuelo.eco_backend.repository.SongRepository;

@Service
public class MusicProcessorService {

    private final SongRepository songRepository;
    private final ArtistRepository artistRepository;

    @Value("${app.raw.path}")
    private String rawPath;

    @Value("${app.library.path}")
    private String libraryPath;

    public MusicProcessorService(SongRepository songRepository, ArtistRepository artistRepository) {
        this.songRepository = songRepository;
        this.artistRepository = artistRepository;
    }

    @Transactional
    public void processFolder() throws Exception {

        File folder = new File(rawPath);
        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp3"));

        if (files == null)
            return;

        for (File file : files) {

            Mp3File mp3 = new Mp3File(file);

            if (!mp3.hasId3v2Tag())
                continue;

            ID3v2 tag = mp3.getId3v2Tag();

            String title = safe(tag.getTitle());
            String artistRaw = safe(tag.getArtist());
            String album = safe(tag.getAlbum());
            int duration = (int) mp3.getLengthInSeconds();

            // Extraer artistas (antes de "feat.", "&", ",", etc.)
            List<String> artistParts = parseArtistNames(artistRaw);

            // Comprobar si ya existen los artistas y obtener sus entidades
            List<Artist> artists = new ArrayList<>();
            for (String artist : artistParts) {
                Artist artistEntity = artistRepository.findByNameIgnoreCase(artist)
                        .orElseGet(() -> {
                            Artist newArtist = new Artist();
                            newArtist.setName(artist);
                            return artistRepository.save(newArtist);
                        });
                artists.add(artistEntity);
            }

            Artist mainArtist = artists.get(0); // Asumimos que el primer artista es el principal

            if (songRepository.existsByTitleIgnoreCaseAndMainArtist(title, mainArtist)) {
                System.out.println("Duplicado detectado: " + title + " - " + mainArtist.getName());
                file.delete();
                continue;
            }

            // 1️ Guardar en base de datos
            Song song = new Song();
            song.setTitle(title);
            song.setArtists(artists);
            song.setMainArtist(mainArtist); // Asumimos que el primer artista es el principal para búsquedas rápidas
            song.setArtistCreditRaw(artistRaw);
            song.setAlbum(album);
            song.setDuration(duration);

            // 2️ Guardar primero para obtener ID
            song = songRepository.save(song);

            // Ahora YA tenemos ID
            Long id = song.getId();

            // 3️ Crear nombre con ID
            String newFileName = String.format("%s-%s(%d).mp3", song.getMainArtist().getName(), title, id);
            newFileName = sanitizeFileName(newFileName);

            Path newPath = Paths.get(libraryPath, newFileName);

            // 4️ Mover archivo
            Files.move(file.toPath(), newPath, StandardCopyOption.REPLACE_EXISTING);

            // 5️ Actualizar filePath
            song.setFilePath(newFileName);

            System.out.println("Procesado: " + newFileName);
        }
    }

    private String safe(String value) {
        return value == null ? "Unknown" : value;
    }

    private String sanitizeFileName(String name) {
        return name.replaceAll("[\\\\/:*?\"<>|]", "_");
    }

    private List<String> parseArtistNames(String artistRaw) {
        return Arrays.stream(artistRaw
                .split("(?i),|feat\\.|ft\\.|featuring"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .toList();
    }
}