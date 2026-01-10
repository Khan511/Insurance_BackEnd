package com.example.insurance.infrastructure.web.claim;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DocumentAttachmentDTO {
    private String storageId;
    private String sha256Checksum;
    private String originalFileName;
    private String contentType;
    private String documentType;
    private String storageBucket;
    private String fileKey;
    private String fileUrl;
    private Long fileSize;
    private Instant uploadedAt;
}
