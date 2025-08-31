package com.example.insurance.domain.claimDocuments.service;

import java.util.List;
import com.example.insurance.domain.claim.model.Claim;
import com.example.insurance.domain.claimDocuments.model.ClaimDocuments;
import com.example.insurance.infrastructure.web.claim.DocumentAttachmentDTO;

public interface ClaimDocumentsService {
    void saveClaimDocuments(List<DocumentAttachmentDTO> documentDTOs, Claim claim);

}
