package com.example.shareme_webapp.service;

import com.example.shareme_webapp.document.FileMetadataDocument;
import com.example.shareme_webapp.document.ProfileDocument;
import com.example.shareme_webapp.document.UserCredits;
import com.example.shareme_webapp.dto.FileMetadataDto;
import com.example.shareme_webapp.repository.FileMetadataRepository;
import com.example.shareme_webapp.repository.UserCreditsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.UUID.randomUUID;

@Service
@RequiredArgsConstructor
public class FileMetadataService {

    private final ProfileService profileService;
    private final UserCreditsService userCreditsService;
    private final FileMetadataRepository fileMetadataRepository;
    private final UserCreditsRepository userCreditsRepository;
    private final CloudinaryService cloudinaryService;

    public List<FileMetadataDto> uploadFiles(
            MultipartFile[] files) throws IOException {

        ProfileDocument currentProfile =
                profileService.getCurrentProfile();

        if (!userCreditsService.hasEnoughCredits(files.length)) {
            throw new RuntimeException(
                    "Not enough credits to upload files."
            );
        }

        List<FileMetadataDocument> savedFiles =
                new ArrayList<>();

        for (MultipartFile file : files) {

            Map<String,Object> uploadResult =
                    cloudinaryService.uploadFile(
                            file,
                            currentProfile.getClerkId()
                    );

            String secureUrl =
                    (String) uploadResult.get("secure_url");

            String publicId =
                    (String) uploadResult.get("public_id");

            String resourceType =
                    (String) uploadResult.get("resource_type");

            FileMetadataDocument metadata =
                    FileMetadataDocument.builder()
                            .name(file.getOriginalFilename())
                            .size(file.getSize())
                            .type(file.getContentType())
                            .clerkId(currentProfile.getClerkId())
                            .isPublic(false)
                            .cloudinaryUrl(secureUrl)
                            .cloudinaryPublicId(publicId)
                            .resourceType(resourceType)
                            .uploadedAt(LocalDateTime.now())
                            .build();

            userCreditsService.consumeCredit();

            savedFiles.add(
                    fileMetadataRepository.save(metadata)
            );
        }

        return savedFiles.stream()
                .map(this::mapToDto)
                .toList();
    }

    private FileMetadataDto mapToDto(FileMetadataDocument fileMetadataDocument) {

        return FileMetadataDto.builder()
                .id(fileMetadataDocument.getId())
                .cloudinaryUrl(fileMetadataDocument.getCloudinaryUrl())
                .cloudinaryPublicId(fileMetadataDocument.getCloudinaryPublicId())
                .resourceType(fileMetadataDocument.getResourceType())
                .name(fileMetadataDocument.getName())
                .size(fileMetadataDocument.getSize())
                .type(fileMetadataDocument.getType())
                .clerkId(fileMetadataDocument.getClerkId())
                .isPublic(fileMetadataDocument.getIsPublic())
                .uploadedAt(fileMetadataDocument.getUploadedAt())
                .build();
    }

    public List<FileMetadataDto> getFiles() {
        ProfileDocument currentProfile = profileService.getCurrentProfile();
        UserCredits userCredits = userCreditsService.getUserCredits(currentProfile.getClerkId());

        List<FileMetadataDocument> files = fileMetadataRepository.findByClerkId(currentProfile.getClerkId());

        if(10>files.size()) //at time 10 files can store the user
        {
            userCredits.setCredits(10- files.size());
            userCreditsRepository.save(userCredits);
        }
        return files.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public FileMetadataDto getPublicFile(String id) {
        Optional<FileMetadataDocument> fileOptional = fileMetadataRepository.findById(id);
        if (fileOptional.isEmpty() || !fileOptional.get().getIsPublic()) {
            throw new RuntimeException("unable to get the file");
        }

        FileMetadataDocument document = fileOptional.get();
        return mapToDto(document);
    }

    public FileMetadataDto getDownloadableFile(String id) {
        FileMetadataDocument file = fileMetadataRepository.findById(id).orElseThrow(() -> new RuntimeException("File not found"));

        if (!Boolean.TRUE.equals(file.getIsPublic())) {
            throw new RuntimeException("This file is private");
        }
        return mapToDto(file);

    }

    public void deleteFile(String id) {

        try {

            ProfileDocument currentProfile =
                    profileService.getCurrentProfile();

            FileMetadataDocument file =
                    fileMetadataRepository.findById(id)
                            .orElseThrow(
                                    () -> new RuntimeException(
                                            "File not found"
                                    )
                            );

            if (!file.getClerkId()
                    .equals(currentProfile.getClerkId())) {

                throw new RuntimeException(
                        "You do not own this file"
                );
            }

            cloudinaryService.deleteFile(
                    file.getCloudinaryPublicId(),
                    file.getResourceType()
            );

            fileMetadataRepository.deleteById(id);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Error deleting the file",
                    e
            );
        }
    }
    public FileMetadataDto togglePublic(String id) {

        ProfileDocument currentProfile =
                profileService.getCurrentProfile();

        FileMetadataDocument file =
                fileMetadataRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException("File not found"));

        if (!file.getClerkId()
                .equals(currentProfile.getClerkId())) {

            throw new RuntimeException(
                    "You do not own this file"
            );
        }

        file.setIsPublic(!Boolean.TRUE.equals(file.getIsPublic()));

        fileMetadataRepository.save(file);

        return mapToDto(file);
    }


}
