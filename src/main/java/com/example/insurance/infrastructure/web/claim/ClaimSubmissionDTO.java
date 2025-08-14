package com.example.insurance.infrastructure.web.claim;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ClaimSubmissionDTO {
    @NotEmpty(message = "Policy number is required")
    private String policyNumber;

    private String storageBucket = "your-firebase-bucket-name";

    @NotEmpty(message = "Claim type is required")
    private String claimType;

    @NotNull(message = "Incident details are required")
    @Valid
    private IncidentDetailsDTO incidentDetails;

    @NotEmpty(message = "At least one document is required")
    @Valid
    private List<DocumentAttachmentDTO> documents;
}