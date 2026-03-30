package es.jorgezarzuelo.eco_backend.dto.album;

import java.util.List;

import es.jorgezarzuelo.eco_backend.dto.song.SongSummaryDto;

public record AlbumArtistDto(
        Long id,
        String title,
        int releaseYear,
        List<SongSummaryDto> songs) {
}
