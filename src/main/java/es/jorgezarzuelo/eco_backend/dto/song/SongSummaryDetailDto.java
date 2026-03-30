package es.jorgezarzuelo.eco_backend.dto.song;

import es.jorgezarzuelo.eco_backend.dto.album.AlbumSummaryDto;

public record SongSummaryDetailDto(
        Long id,
        String title,
        AlbumSummaryDto album) {
}
