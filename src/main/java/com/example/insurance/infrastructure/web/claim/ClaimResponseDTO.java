
package com.example.insurance.infrastructure.web.claim;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    private Long id;
    private String policyNumber;
    private String claimNumber;

    @Value("${aws.bucket}")
    private String storageBucket;

    private ClaimStatus status;
    // private BigDecimal amount;
    private BigDecimal approvedAmount;
    private String claimType;

    // Date fields
    private LocalDateTime submissionDate;
    private LocalDateTime approvedDate;
    private LocalDateTime rejectedDate;
    private LocalDateTime paidDate;
    private LocalDateTime closedDate;

    // Admin fields
    private String processedBy;
    private String rejectionReason;

    // Calculated/utility fields
    private Long processingDays;
    private Boolean isOpen;
    private Boolean isProcessed;
    private Boolean canBeApproved;
    private Boolean canBeRejected;

    // Payment information
    private String paymentStatus;
    private String approvalNotes;
    private String paidBy;
    private String paymentReference;
    private String paymentNotes;

    // UI flags
    private Boolean canBePaid;
    private Boolean isFullyPaid;

    // Incident details
    private IncidentDetailsDTO incidentDetails;

    // Documents
    private List<DocumentAttachmentDTO> documents;

    // Product info (optional but useful)
    private String productName;
    private String productCode;

    // User info (optional)
    private String customerName;
    private String customerEmail;

    // Helper methods for frontend
    public boolean getIsTerminal() {
        return status != null && status.isTerminal();
    }

    public boolean getIsActive() {
        return status != null && status.isActive();
    }
}