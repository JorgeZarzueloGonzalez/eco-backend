package es.jorgezarzuelo.eco_backend.dto;

import java.util.List;

public record SongDetailDto(
                Long id,
                String title,
                List<ArtistSummaryDto> artists,
                String album,
                Integer duration) {
}
