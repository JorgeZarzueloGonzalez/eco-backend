package es.jorgezarzuelo.eco_backend.dto;

public record SongDetailDto(
        Long id,
        String title,
        String artist,
        String album,
        Integer duration) {
}
