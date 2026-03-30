package es.jorgezarzuelo.eco_backend.service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;

import es.jorgezarzuelo.eco_backend.dto.artist.ArtistSummaryDto;
import es.jorgezarzuelo.eco_backend.dto.song.SongDetailDto;
import es.jorgezarzuelo.eco_backend.dto.song.SongListDto;
import es.jorgezarzuelo.eco_backend.model.Artist;
import es.jorgezarzuelo.eco_backend.model.Song;
import es.jorgezarzuelo.eco_backend.repository.SongRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class SongService {

    @Value("${app.library.path}")
    private String libraryPath;

    private final SongRepository songRepository;

    public SongService(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    public List<SongListDto> getAllSongs() {
        return songRepository.findAll().stream()
                .map(song -> new SongListDto(song.getId(), song.getTitle(), mapToArtistsSummary(song.getArtists())))
                .toList();
    }

    public SongDetailDto getSongById(Long id) {
        Song song = songRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Song not found"));
        return new SongDetailDto(song.getId(), song.getTitle(), mapToArtistsSummary(song.getArtists()), song.getAlbum(),
                song.getDuration());
    }

    public String getFilePath(Long id) {
        Song song = songRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Song not found"));
        return song.getFilePath();
    }

    public byte[] getCover(Long id) throws Exception {
        Song song = songRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Song not found"));

        Path path = Paths.get(libraryPath).resolve(song.getFilePath());

        Mp3File mp3 = new Mp3File(path);

        if (!mp3.hasId3v2Tag()) {
            throw new RuntimeException("No ID3v2 tag found");
        }

        ID3v2 tag = mp3.getId3v2Tag();
        byte[] image = tag.getAlbumImage();
        if (image == null) {
            throw new RuntimeException("No album image found");
        }
        return image;

    }

    private List<ArtistSummaryDto> mapToArtistsSummary(List<Artist> artists) {
        return artists.stream()
                .map(artist -> new ArtistSummaryDto(artist.getId(), artist.getName()))
                .toList();
    }

}
