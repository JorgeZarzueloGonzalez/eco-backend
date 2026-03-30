package es.jorgezarzuelo.eco_backend.dto;

import java.util.List;

public record SongListDto(
                Long id,
                String title,
                List<ArtistSummaryDto> artists) {
}
