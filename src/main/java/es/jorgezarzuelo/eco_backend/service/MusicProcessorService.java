package es.jorgezarzuelo.eco_backend.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;

import es.jorgezarzuelo.eco_backend.model.Song;
import es.jorgezarzuelo.eco_backend.repository.SongRepository;

@Service
public class MusicProcessorService {

    private final SongRepository songRepository;

    @Value("${app.raw.path}")
    private String rawPath;

    @Value("${app.library.path}")
    private String libraryPath;

    public MusicProcessorService(SongRepository songRepository) {
        this.songRepository = songRepository;
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
            String artist = safe(tag.getArtist());
            String album = safe(tag.getAlbum());

            if (songRepository.existsByTitleIgnoreCaseAndArtistIgnoreCase(title, artist)) {
                System.out.println("Duplicado detectado: " + title + " - " + artist);
                file.delete();
                continue;
            }

            int duration = (int) mp3.getLengthInSeconds();

            // 1️ Guardar en base de datos
            Song song = new Song();
            song.setTitle(title);
            song.setArtist(artist);
            song.setAlbum(album);
            song.setDuration(duration);

            // 2️ Guardar primero para obtener ID
            song = songRepository.save(song);

            // Ahora YA tenemos ID
            Long id = song.getId();

            // 3️ Crear nombre con ID
            String newFileName = String.format("%s-%s(%d).mp3", artist, title, id);
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
}