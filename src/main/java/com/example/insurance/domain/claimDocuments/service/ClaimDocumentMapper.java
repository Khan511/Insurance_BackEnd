package com.example.insurance.domain.claimDocuments.service;

import com.example.insurance.domain.claimDocuments.model.ClaimDocuments;
import com.example.insurance.infrastructure.web.claim.DocumentAttachmentDTO;

public class ClaimDocumentMapper {

    public static DocumentAttachmentDTO toDto(ClaimDocuments documents) {

        DocumentAttachmentDTO doc = new DocumentAttachmentDTO();

        // doc.setStorageId(documents.getStorageId()).toString();
        // doc.setStoragePath(documents.getStoragePath());
        // doc.setSha256Checksum(documents.getSha256Checksum());

        return null;
    }
}
