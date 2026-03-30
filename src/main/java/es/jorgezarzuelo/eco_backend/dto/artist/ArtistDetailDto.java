package es.jorgezarzuelo.eco_backend.dto.artist;

import java.util.List;

import es.jorgezarzuelo.eco_backend.dto.album.AlbumArtistDto;
import es.jorgezarzuelo.eco_backend.dto.song.SongSummaryDetailDto;

public record ArtistDetailDto(
        Long id,
        String name,
        List<AlbumArtistDto> ownAlbums,
        List<SongSummaryDetailDto> collaborationSongs) {
}
