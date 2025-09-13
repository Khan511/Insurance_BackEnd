package com.example.insurance.infrastructure.web.claim;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;

import com.example.insurance.common.enummuration.ClaimStatus;

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
public class ClaimResponseDTO {

    private String policyNumber;

    private String claimNumber;

    @Value("${aws.bucket}")
    private String storageBucket;

    private ClaimStatus status;
    private BigDecimal amount;

    private String claimType;

    private IncidentDetailsDTO incidentDetails;

    private List<DocumentAttachmentDTO> documents;
}
