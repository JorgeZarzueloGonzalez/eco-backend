package es.jorgezarzuelo.eco_backend.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import es.jorgezarzuelo.eco_backend.dto.album.AlbumArtistDto;
import es.jorgezarzuelo.eco_backend.dto.album.AlbumSummaryDto;
import es.jorgezarzuelo.eco_backend.dto.artist.ArtistDetailDto;
import es.jorgezarzuelo.eco_backend.dto.artist.ArtistListDto;
import es.jorgezarzuelo.eco_backend.dto.artist.ArtistSummaryDto;
import es.jorgezarzuelo.eco_backend.dto.song.SongSummaryDetailDto;
import es.jorgezarzuelo.eco_backend.dto.song.SongSummaryDto;
import es.jorgezarzuelo.eco_backend.model.Album;
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
                        mapToArtistAlbums(artist.getAlbums()), mapToArtistCollaborationSongs(artist.getId(), artist.getSongs())))
                .orElseThrow(() -> new EntityNotFoundException("Artist not found"));
    }

    private List<SongSummaryDetailDto> mapToArtistCollaborationSongs(Long artistId, List<Song> songs) {
        return songs.stream()
                // Solo canciones donde participa pero el album no es suyo
                .filter(song -> song.getAlbum() != null
                        && song.getAlbum().getArtist() != null
                        && !song.getAlbum().getArtist().getId().equals(artistId))
                // Deduplicar por song.id manteniendo orden
                .collect(Collectors.toMap(
                        Song::getId,
                        song -> new SongSummaryDetailDto(
                                song.getId(),
                                song.getTitle(),
                                mapToSongAlbum(song)),
                        (first, second) -> first,
                        LinkedHashMap::new))
                .values()
                .stream()
                .toList();
    }

    private AlbumSummaryDto mapToSongAlbum(Song song) {
        return new AlbumSummaryDto(song.getAlbum().getId(), song.getAlbum().getTitle(),
                song.getAlbum().getReleaseYear(), mapToAlbumArtist(song.getAlbum()));
    }

    private ArtistSummaryDto mapToAlbumArtist(Album album) {
        return new ArtistSummaryDto(album.getArtist().getId(), album.getArtist().getName());
    }

    private List<SongSummaryDto> mapToArtistSong(List<Song> songs) {
        return songs.stream()
                .map(song -> new SongSummaryDto(song.getId(), song.getTitle()))
                .toList();
    }

    private List<AlbumArtistDto> mapToArtistAlbums(List<Album> albums) {
        return albums.stream()
                .map(album -> new AlbumArtistDto(album.getId(), album.getTitle(), album.getReleaseYear(),
                        mapToArtistSong(album.getSongs())))
                .toList();
    }
}
