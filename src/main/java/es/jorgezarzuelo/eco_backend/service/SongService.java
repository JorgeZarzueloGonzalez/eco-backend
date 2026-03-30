package es.jorgezarzuelo.eco_backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import es.jorgezarzuelo.eco_backend.dto.album.AlbumSummaryDto;
import es.jorgezarzuelo.eco_backend.dto.artist.ArtistSummaryDto;
import es.jorgezarzuelo.eco_backend.dto.song.SongDetailDto;
import es.jorgezarzuelo.eco_backend.dto.song.SongListDto;
import es.jorgezarzuelo.eco_backend.model.Album;
import es.jorgezarzuelo.eco_backend.model.Artist;
import es.jorgezarzuelo.eco_backend.model.Song;
import es.jorgezarzuelo.eco_backend.repository.SongRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class SongService {

    private final SongRepository songRepository;
    private final AlbumService albumService;

    public SongService(SongRepository songRepository, AlbumService albumService) {
        this.songRepository = songRepository;
        this.albumService = albumService;
    }

    public List<SongListDto> getAllSongs() {
        return songRepository.findAll().stream()
                .map(song -> new SongListDto(song.getId(), song.getTitle(), mapToArtistsSummary(song.getArtists())))
                .toList();
    }

    public SongDetailDto getSongById(Long id) {
        Song song = songRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Song not found"));
        return new SongDetailDto(song.getId(), song.getTitle(), mapToArtistsSummary(song.getArtists()),
                mapToAlbumSummary(song.getAlbum()), song.getDuration());
    }

    public String getFilePath(Long id) {
        Song song = songRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Song not found"));
        return song.getFilePath();
    }

    public byte[] getSongCover(Long id) throws Exception {
        Song song = songRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Song not found"));
        Album album = song.getAlbum();
        if (album == null || album.getCoverFilePath() == null) {
            throw new EntityNotFoundException("Cover not found");
        }
        return albumService.getAlbumCover(album.getId());
    }

    private List<ArtistSummaryDto> mapToArtistsSummary(List<Artist> artists) {
        return artists.stream()
                .map(artist -> new ArtistSummaryDto(artist.getId(), artist.getName()))
                .toList();
    }

    private AlbumSummaryDto mapToAlbumSummary(Album album) {
        return new AlbumSummaryDto(album.getId(), album.getTitle(), album.getReleaseYear(),
                new ArtistSummaryDto(album.getArtist().getId(), album.getArtist().getName()));
    }
}
