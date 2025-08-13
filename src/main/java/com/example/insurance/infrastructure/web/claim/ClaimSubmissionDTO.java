package com.example.insurance.infrastructure.web.claim;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClaimSubmissionDTO {
    @NotEmpty(message = "Policy number is required")
    private String policyNumber;
    
    @NotEmpty(message = "Claim type is required")
    private String claimType;
    
    @NotNull(message = "Incident details are required")
    @Valid
    private IncidentDetailsDTO incidentDetails;
    
    @NotEmpty(message = "At least one document is required")
    @Valid
    private List<DocumentAttachmentDTO> documents;
}