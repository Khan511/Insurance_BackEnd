package com.example.insurance.infrastructure.web.claim;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DocumentAttachmentDTO {
    private String storageId;
    private String storagePath;
    private String sha256Checksum;
    private String downloadUrl;
    private String originalFileName;
    private String contentType;
    private String documentType;
    // @Value("${app.storage.bucket}")
    private String storageBucket;
    private String fileKey;
    private String fileUrl;
    private Long fileSize;
    private String uploadedAt;
}
