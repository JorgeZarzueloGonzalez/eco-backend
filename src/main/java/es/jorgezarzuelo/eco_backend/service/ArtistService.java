package es.jorgezarzuelo.eco_backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import es.jorgezarzuelo.eco_backend.dto.artist.ArtistDetailDto;
import es.jorgezarzuelo.eco_backend.dto.artist.ArtistListDto;
import es.jorgezarzuelo.eco_backend.dto.artist.ArtistSongDto;
import es.jorgezarzuelo.eco_backend.model.Song;
import es.jorgezarzuelo.eco_backend.repository.ArtistRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class ArtistService {

    private final ArtistRepository artistRepository;

    public ArtistService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    public List<ArtistListDto> getAllArtists() {
        return artistRepository.findAll().stream()
                .map(artist -> new ArtistListDto(artist.getId(), artist.getName())).toList();
    }

    public ArtistDetailDto getArtistById(Long id) {
        return artistRepository.findById(id)
                .map(artist -> new ArtistDetailDto(artist.getId(), artist.getName(),
                        mapToArtistSongs(artist.getSongs())))
                .orElseThrow(() -> new EntityNotFoundException("Artist not found"));
    }

    private List<ArtistSongDto> mapToArtistSongs(List<Song> songs) {
        return songs.stream()
                .map(song -> new ArtistSongDto(song.getId(), song.getTitle(), song.getAlbum()))
                .toList();
    }
}
