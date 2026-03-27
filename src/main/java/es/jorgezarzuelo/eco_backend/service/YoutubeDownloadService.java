package es.jorgezarzuelo.eco_backend.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class YoutubeDownloadService {

    private final MusicProcessorService proccesor;

    public YoutubeDownloadService(MusicProcessorService proccesor) {
        this.proccesor = proccesor;
    }

    @Value("${app.raw.path}")
    private String rawFolder;

    public String downloadMp3(String url) throws Exception {

        // -x --audio-format mp3 --embed-metadata --embed-thumbnail
        ProcessBuilder builder = new ProcessBuilder(
                "yt-dlp",
                "-f", "bestaudio",
                "-x",
                "--audio-format", "mp3",
                "--audio-quality", "0",
                "--embed-metadata",
                "--embed-thumbnail",
                "--download-archive", rawFolder + "/downloaded.txt",
                "--yes-playlist",
                "--convert-thumbnails", "jpg",
                "-o", rawFolder + "/%(title)s.%(ext)s",
                url);

        builder.redirectErrorStream(true);

        Process process = builder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line); // Log opcional
        }

        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("Error downloading from YouTube");
        }

        proccesor.processFolder();

        return "Download completed";
    }
}
