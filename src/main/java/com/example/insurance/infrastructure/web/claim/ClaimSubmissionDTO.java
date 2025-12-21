package com.example.insurance.infrastructure.web.claim;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimSubmissionDTO {
    @NotEmpty(message = "Policy number is required")
    private String policyNumber;

    @Value("${aws.bucket}")
    private String storageBucket;

    @NotEmpty(message = "Claim type is required")
    private String claimType;

    @NotNull(message = "Incident details are required")
    @Valid
    private IncidentDetailsDTO incidentDetails;

    // @NotEmpty(message = "At least one document is required")
    // @Valid
    private List<DocumentAttachmentDTO> documents;

}