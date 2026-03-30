package es.jorgezarzuelo.eco_backend.dto.album;

import java.util.List;

import es.jorgezarzuelo.eco_backend.dto.artist.ArtistSummaryDto;
import es.jorgezarzuelo.eco_backend.dto.song.SongSummaryDto;

public record AlbumDetailDto(
        Long id,
        String title,
        int releaseYear,
        ArtistSummaryDto artist,
        List<SongSummaryDto> songs) {
}
