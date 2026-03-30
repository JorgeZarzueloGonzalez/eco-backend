package es.jorgezarzuelo.eco_backend.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import es.jorgezarzuelo.eco_backend.dto.album.AlbumDetailDto;
import es.jorgezarzuelo.eco_backend.dto.album.AlbumListDto;
import es.jorgezarzuelo.eco_backend.dto.artist.ArtistSummaryDto;
import es.jorgezarzuelo.eco_backend.dto.song.SongSummaryDto;
import es.jorgezarzuelo.eco_backend.model.Album;
import es.jorgezarzuelo.eco_backend.model.Song;
import es.jorgezarzuelo.eco_backend.repository.AlbumRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class AlbumService {

    @Value("${app.cover.path}")
    private String coverPath;

    private final AlbumRepository albumRepository;

    public AlbumService(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
    }

    public List<AlbumListDto> getAllAlbums() {
        return albumRepository.findAll().stream()
                .map(album -> new AlbumListDto(album.getId(), album.getTitle(), album.getReleaseYear()))
                .toList();
    }

    public AlbumDetailDto getAlbumById(Long id) {
        return albumRepository.findById(id)
                .map(album -> new AlbumDetailDto(album.getId(), album.getTitle(), album.getReleaseYear(),
                        mapToAlbumArtist(album), mapToAlbumSongs(album.getSongs())))
                .orElseThrow(() -> new EntityNotFoundException("Album not found"));
    }

    public byte[] getAlbumCover(Long id) throws Exception {
        Album album = albumRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Album not found"));

        Path coverFilePath = Path.of(coverPath, album.getCoverFilePath());
        return Files.readAllBytes(coverFilePath);
    }

    private ArtistSummaryDto mapToAlbumArtist(Album album) {
        return new ArtistSummaryDto(album.getArtist().getId(), album.getArtist().getName());
    }

    private List<SongSummaryDto> mapToAlbumSongs(List<Song> songs) {
        return songs.stream()
                .map(song -> new SongSummaryDto(song.getId(), song.getTitle()))
                .toList();
    }

}
