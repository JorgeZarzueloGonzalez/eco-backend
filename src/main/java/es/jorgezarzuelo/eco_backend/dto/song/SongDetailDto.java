package es.jorgezarzuelo.eco_backend.dto.song;

import java.util.List;

import es.jorgezarzuelo.eco_backend.dto.artist.ArtistSummaryDto;

public record SongDetailDto(
                Long id,
                String title,
                List<ArtistSummaryDto> artists,
                String album,
                Integer duration) {
}
