package es.jorgezarzuelo.eco_backend.dto.album;

import es.jorgezarzuelo.eco_backend.dto.artist.ArtistSummaryDto;

public record AlbumSummaryDto(
                Long id,
                String title,
                int releaseYear,
                ArtistSummaryDto artist) {
}
