package com.example.insurance.embeddable;

import java.time.Instant;
// import java.time.LocalDateTime;
import java.util.UUID;
import com.example.insurance.common.enummuration.ClaimDocumentType;
import com.example.insurance.common.enummuration.DocumentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA requirement
public class DocumentAttachment {

    @Column(nullable = false, updatable = false)
    private UUID storageId; // Obfuscated reference

    @Column(nullable = false, updatable = false)
    private String storageBucket;

    @Column(nullable = false, updatable = false)
    private String originalFilename;

    @Column(nullable = false, updatable = false)
    private String contentType;

    @Column(nullable = false, updatable = false)
    private String sha256Checksum;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentStatus status = DocumentStatus.PENDING_VERIFICATION;

    @Column(nullable = false, updatable = false)
    private Instant uploadedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private ClaimDocumentType documentType;

    // Immutable construction
    public DocumentAttachment(UUID storageId,
            String storageBucket,
            String originalFilename,
            String contentType,
            String sha256Checksum,
            ClaimDocumentType documentType) {
        this.storageId = storageId;
        this.storageBucket = storageBucket;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.sha256Checksum = sha256Checksum;
        this.documentType = documentType;
        this.uploadedAt = Instant.now();
    }

    // Domain logic --------------------------------------------------
    public boolean isVerified() {
        return status == DocumentStatus.VERIFIED;
    }

    // CREATE PRE-SIGNED URLS
    // public String generateTemporaryUrl(Duration expiryDuration) {
    // return CloudStorage.generateSignedUrl(
    // storageBucket,
    // storageId.toString(),
    // expiryDuration);
    // }

    public void verify() {
        this.status = DocumentStatus.VERIFIED;
    }

    public void reject(String reason) {
        this.status = DocumentStatus.REJECTED;
        // Additional rejection logic
    }
}
// public class DocumentAttachment {
// @Enumerated(EnumType.STRING)
// private ClaimDocumentType.RequiredDocument documentType;

// private String storageKey; // e.g., "s3://bucket/path/to/file.pdf"
// private String originalFilename;
// private LocalDateTime uploadedAt = LocalDateTime.now();

// public DocumentAttachment(ClaimDocumentType.RequiredDocument documentType,
// String storageKey,
// String originalFilename) {
// this.documentType = documentType;
// this.storageKey = storageKey;
// this.originalFilename = originalFilename;
// }

// }
