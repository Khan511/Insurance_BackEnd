package com.example.insurance.infrastructure.web.claim;

import org.springframework.beans.factory.annotation.Value;

import lombok.Getter;

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
}
