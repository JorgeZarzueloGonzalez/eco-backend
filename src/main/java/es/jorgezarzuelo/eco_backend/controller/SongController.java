package es.jorgezarzuelo.eco_backend.controller;

//Spring Web
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.jorgezarzuelo.eco_backend.dto.SongDetailDto;
import es.jorgezarzuelo.eco_backend.dto.SongListDto;
import es.jorgezarzuelo.eco_backend.service.SongService;

//Spring Core
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;

//HTTP
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

//Java IO y NIO
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/songs")
public class SongController {

    @Value("${app.library.path}")
    private String libraryPath;

    private final SongService songService;

    public SongController(SongService songService) {
        this.songService = songService;
    }

    private static final long CHUNK_SIZE = 1024 * 1024; // 1MB

    @GetMapping()
    public List<SongListDto> getAllSongs() {
        return songService.getAllSongs();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SongDetailDto> getSongById(@PathVariable Long id) {
        return ResponseEntity.ok(songService.getSongById(id));
    }

    @GetMapping("/{id}/stream")
    public ResponseEntity<ResourceRegion> streamAudio(@PathVariable Long id, @RequestHeader HttpHeaders headers)
            throws IOException {

        Path path = Paths.get(libraryPath).resolve(songService.getFilePath(id));
        UrlResource resource = new UrlResource(path.toUri());

        long contentLength = resource.contentLength();
        ResourceRegion region = getRegion(resource, headers, contentLength);

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).contentType(MediaType.parseMediaType("audio/mpeg"))
                .body(region);

    }

    @GetMapping("/{id}/cover")
    public ResponseEntity<byte[]> getCover(@PathVariable Long id) throws Exception {

        byte[] image = songService.getCover(id);

        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG) // o detectar tipo dinámicamente
                .body(image);
    }

    private ResourceRegion getRegion(Resource resource, HttpHeaders headers, long contentLength) {

        HttpRange range = headers.getRange().isEmpty() ? null : headers.getRange().get(0);

        if (range != null) {
            long start = range.getRangeStart(contentLength);
            long end = range.getRangeEnd(contentLength);
            long rangeLength = Math.min(CHUNK_SIZE, end - start + 1);
            return new ResourceRegion(resource, start, rangeLength);
        } else {
            long rangeLength = Math.min(CHUNK_SIZE, contentLength);
            return new ResourceRegion(resource, 0, rangeLength);
        }
    }

}
