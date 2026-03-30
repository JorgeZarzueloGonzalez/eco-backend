package es.jorgezarzuelo.eco_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.jorgezarzuelo.eco_backend.dto.artist.ArtistDetailDto;
import es.jorgezarzuelo.eco_backend.dto.artist.ArtistListDto;
import es.jorgezarzuelo.eco_backend.service.ArtistService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/artists")
public class ArtistController {

    private final ArtistService artistService;

    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @GetMapping
    public List<ArtistListDto> getAllArtists() {
        return artistService.getAllArtists();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ArtistDetailDto> getArtistById(@PathVariable Long id) {
        return ResponseEntity.ok(artistService.getArtistById(id));
    }
    

}
