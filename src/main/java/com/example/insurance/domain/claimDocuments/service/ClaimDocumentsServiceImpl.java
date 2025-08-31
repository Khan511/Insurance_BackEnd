package com.example.insurance.domain.claimDocuments.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.insurance.common.enummuration.ClaimDocumentType;
import com.example.insurance.domain.claim.model.Claim;
import com.example.insurance.domain.claimDocuments.model.ClaimDocuments;
import com.example.insurance.domain.claimDocuments.repository.ClaimDocumentsRepository;
import com.example.insurance.infrastructure.web.claim.DocumentAttachmentDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClaimDocumentsServiceImpl implements ClaimDocumentsService {

    private final ClaimDocumentsRepository claimDocumentsRepository;

    @Override
    @Transactional
    public void saveClaimDocuments(List<DocumentAttachmentDTO> documentDTOs, Claim claim) {

        // CONvert DTOs to entities and set the claim reference
        List<ClaimDocuments> claimDocuments = documentDTOs.stream()
                .map(dto -> createClaimDocumentFromDTO(dto, claim))
                .collect(Collectors.toList());

        // Save All Documents
        claimDocumentsRepository.saveAll(claimDocuments);
    }

    private ClaimDocuments createClaimDocumentFromDTO(DocumentAttachmentDTO dto, Claim claim) {
        ClaimDocuments document = new ClaimDocuments(UUID.fromString(dto.getStorageId()), dto.getStorageBucket(),
                dto.getOriginalFileName(), dto.getContentType(), dto.getSha256Checksum(),
                ClaimDocumentType.RequiredDocument.valueOf(dto.getDocumentType()), claim);

        // Set the claim refrence
        document.setClaim(claim);

        return document;
    }

}
