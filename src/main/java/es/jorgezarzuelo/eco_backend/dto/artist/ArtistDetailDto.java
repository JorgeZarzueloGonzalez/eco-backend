package es.jorgezarzuelo.eco_backend.dto.artist;

import java.util.List;

public record ArtistDetailDto(
                Long id,
                String name,
                List<ArtistSongDto> songs
) {}
