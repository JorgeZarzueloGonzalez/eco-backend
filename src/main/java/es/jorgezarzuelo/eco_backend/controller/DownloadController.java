package es.jorgezarzuelo.eco_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.jorgezarzuelo.eco_backend.service.YoutubeDownloadService;

@RestController
@RequestMapping("/api/download")
public class DownloadController {

    private final YoutubeDownloadService youtubeDownloadService;

    public DownloadController(YoutubeDownloadService youtubeDownloadService) {
        this.youtubeDownloadService = youtubeDownloadService;
    }

    @PostMapping
    public ResponseEntity<String> download(@RequestParam String url) throws Exception {

        return ResponseEntity.ok(youtubeDownloadService.downloadMp3(url));
    }
}
