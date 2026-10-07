package com.example.shareme_webapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileMetadataDto {

    private String id;
    private String name;
    private String type;
    private Long size;
    private String clerkId;
    private Boolean isPublic;
    private String cloudinaryUrl;
    private String cloudinaryPublicId;
    private String resourceType;
    private LocalDateTime uploadedAt;
}
