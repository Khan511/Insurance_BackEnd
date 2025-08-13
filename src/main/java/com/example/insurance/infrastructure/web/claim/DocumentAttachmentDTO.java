package com.example.insurance.infrastructure.web.claim;

import lombok.Getter;

@Getter
public class DocumentAttachmentDTO {
       private String storageId;
    private String storagePath;
    private String downloadUrl;
    private String originalFileName;
    private String contentType;
    private String documentType;
}
