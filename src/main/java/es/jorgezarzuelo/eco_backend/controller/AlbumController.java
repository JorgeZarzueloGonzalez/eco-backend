package es.jorgezarzuelo.eco_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.jorgezarzuelo.eco_backend.dto.album.AlbumDetailDto;
import es.jorgezarzuelo.eco_backend.dto.album.AlbumListDto;
import es.jorgezarzuelo.eco_backend.service.AlbumService;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/api/albums")
public class AlbumController {

    private final AlbumService albumService;

    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @GetMapping
    public List<AlbumListDto> getAllAlbums() {
        return albumService.getAllAlbums();
    }

    @GetMapping("/{id}")
    public AlbumDetailDto getAlbumById(@PathVariable Long id) {
        return albumService.getAlbumById(id);
    }

    @GetMapping("/{id}/cover")
    public ResponseEntity<byte[]> getCover(@PathVariable Long id) throws Exception {

        byte[] image = albumService.getAlbumCover(id);

        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG) // o detectar tipo dinámicamente
                .body(image);
    }
    
        
}
