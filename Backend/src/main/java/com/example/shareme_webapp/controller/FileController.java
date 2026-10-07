package com.example.shareme_webapp.controller;

import com.example.shareme_webapp.document.UserCredits;
import com.example.shareme_webapp.dto.FileMetadataDto;
import com.example.shareme_webapp.service.FileMetadataService;
import com.example.shareme_webapp.service.UserCreditsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class FileController {

    private final FileMetadataService fileMetadataService;
    private final UserCreditsService userCreditsService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFiles(@RequestPart("files")MultipartFile files[]) throws IOException
    {
        Map<String, Object> response = new HashMap<>();
        List<FileMetadataDto> list = fileMetadataService.uploadFiles(files);

        UserCredits finalCredits = userCreditsService.getUserCredits();

        response.put("files",list);
        response.put("remainingCredits", finalCredits.getCredits());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    public ResponseEntity<?> getFilesForCurrentUser() {
        List<FileMetadataDto> files = fileMetadataService.getFiles();

        return ResponseEntity.ok(files);
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<?> getPublicFile(@PathVariable String id) {
        FileMetadataDto file = fileMetadataService.getPublicFile(id);
        return ResponseEntity.ok(file);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Void> downloadFile(
            @PathVariable String id
    ) {

        FileMetadataDto file =
                fileMetadataService.getDownloadableFile(id);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(file.getCloudinaryUrl()))
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFile(@PathVariable String id)
    {
        fileMetadataService.deleteFile(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle-public")
    public ResponseEntity<?> togglePublic(@PathVariable String id)
    {
        FileMetadataDto file = fileMetadataService.togglePublic(id);
        return ResponseEntity.ok(file);
    }
}
