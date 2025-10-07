package com.example.insurance.domain.claimDocuments.model;

import java.time.Instant;
import java.util.UUID;
import com.example.insurance.common.enummuration.ClaimDocumentType;
import com.example.insurance.common.enummuration.DocumentStatus;
import com.example.insurance.domain.claim.model.Claim;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "claim_documents")
@AllArgsConstructor
@RequiredArgsConstructor
// @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClaimDocuments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private UUID storageId;

    // private String storagePath;

    private String fileKey;

    @Column(nullable = false)
    private String storageBucket;

    private String fileUrl;

    private Long fileSize;

    @Column(nullable = false)
    private String originalFilename;

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false)
    private String sha256Checksum;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentStatus status = DocumentStatus.PENDING_VERIFICATION;

    @Column(nullable = false)
    private Instant uploadedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClaimDocumentType.RequiredDocument documentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claim_id", nullable = false)
    private Claim claim;

    // Immutable construction
    public ClaimDocuments(UUID storageId,
            // String storagePath,
            String storageBucket,
            String originalFilename,
            String contentType,
            String sha256Checksum,

            ClaimDocumentType.RequiredDocument documentType,
            Claim claim,
            String fileUrl,
            Long fileSize,
            String fileKey) {
        this.storageId = storageId;
        this.storageBucket = storageBucket;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.sha256Checksum = sha256Checksum;
        this.documentType = documentType;
        this.uploadedAt = Instant.now();
        this.claim = claim;
        this.fileUrl = fileUrl;
        this.fileSize = fileSize;
        this.fileKey = fileKey;
    }

    // Domain logic --------------------------------------------------
    public boolean isVerified() {
        return status == DocumentStatus.VERIFIED;
    }

    public void verify() {
        this.status = DocumentStatus.VERIFIED;
    }

    public void reject(String reason) {
        this.status = DocumentStatus.REJECTED;
    }
}
