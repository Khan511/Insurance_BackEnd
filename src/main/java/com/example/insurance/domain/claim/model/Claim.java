
package com.example.insurance.domain.claim.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import com.example.insurance.common.enummuration.ClaimDocumentType;
import com.example.insurance.common.enummuration.ClaimStatus;
import com.example.insurance.domain.auditing.domain.AuditEntity;
import com.example.insurance.domain.claimDocuments.model.ClaimDocuments;
import com.example.insurance.domain.insuranceProduct.model.InsuranceProduct;
import com.example.insurance.domain.paymentSchedule.model.PaymentStatus;
import com.example.insurance.domain.user.model.User;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "claims")
public class Claim extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "policy_number", nullable = false)
    private String policyNumber;

    @Column(name = "claim_number", unique = true, nullable = false)
    private String claimNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ClaimStatus status = ClaimStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus = PaymentStatus.NOT_PAID;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "approved_amount")
    private BigDecimal approvedAmount;

    /**
     * Notes from admin when approving
     */
    @Column(name = "approval_notes", length = 2000)
    private String approvalNotes;

    /**
     * Date when the claim was approved
     */
    @Column(name = "approved_date")
    private LocalDateTime approvedDate;

    /**
     * Date when the claim was rejected
     */
    @Column(name = "rejected_date")
    private LocalDateTime rejectedDate;

    /**
     * Reason for rejection (if applicable)
     */
    @Column(name = "rejection_reason", length = 1000)
    private String rejectionReason;

    /**
     * Date when the claim amount was paid to the customer
     */
    @Column(name = "paid_date")
    private LocalDateTime paidDate;

    /**
     * Admin who marked the claim as paid
     */
    @Column(name = "paid_by")
    private String paidBy;

    /**
     * Notes from admin when marking as paid
     */
    @Column(name = "payment_notes", length = 2000)
    private String paymentNotes;

    /**
     * Date when the claim was closed (regardless of outcome)
     */
    @Column(name = "closed_date")
    private LocalDateTime closedDate;

    /**
     * Admin who approved/rejected the claim
     */
    @Column(name = "processed_by")
    private String processedBy;

    /**
     * Payment reference/transaction ID
     */
    @Column(name = "payment_reference", length = 100)
    private String paymentReference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private ClaimDocumentType claimType;

    @OneToMany(mappedBy = "claim", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ClaimDocuments> attachedDocuments = new ArrayList<>();

    @Embedded
    private IncidentDetails incidentDetails;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private InsuranceProduct insuranceProduct;

    // ========== HELPER METHODS FOR DATE CONVERSION ==========

    /**
     * Get submission date as LocalDateTime (converts from createdAt Instant)
     * This is the date when the customer submitted the claim
     */
    public LocalDateTime getSubmissionDate() {
        Instant createdAt = getCreatedAt();
        if (createdAt == null) {
            return null;
        }
        return LocalDateTime.ofInstant(createdAt, ZoneId.systemDefault());
    }

    /**
     * Get last modified date as LocalDateTime (converts from updatedAt Instant)
     */
    public LocalDateTime getLastModifiedDate() {
        Instant updatedAt = getUpdatedAt();
        if (updatedAt == null) {
            return null;
        }
        return LocalDateTime.ofInstant(updatedAt, ZoneId.systemDefault());
    }

    @Override
    public String toString() {
        return "Claim{" +
                "id=" + id +
                ", claimNumber='" + claimNumber + '\'' +
                ", policyNumber='" + policyNumber + '\'' +
                ", status=" + status +
                ", amount=" + amount +
                ", submissionDate=" + getSubmissionDate() +
                ", processedBy='" + processedBy + '\'' +
                '}';
    }

    // ========== BUSINESS METHODS ==========

    /**
     * Mark claim as approved
     */
    public void approve(String adminUsername, BigDecimal approvedAmount) {
        this.status = ClaimStatus.APPROVED;
        this.approvedDate = LocalDateTime.now();
        this.processedBy = adminUsername;
        this.approvedAmount = approvedAmount;
        this.paymentStatus = PaymentStatus.PENDING;
    }

    /**
     * Mark claim as rejected
     */
    public void reject(String adminUsername, String reason) {
        this.status = ClaimStatus.REJECTED;
        this.rejectedDate = LocalDateTime.now();
        this.processedBy = adminUsername;
        this.rejectionReason = reason;
        this.closedDate = LocalDateTime.now();
        this.approvedAmount = null;
        this.approvedDate = null;
        this.approvalNotes = null;
        this.paymentStatus = PaymentStatus.NOT_PAID;

    }

    /**
     * Mark claim as paid
     */
    public void markAsPaid(String adminUsername) {
        this.status = ClaimStatus.PAID;
        this.paidDate = LocalDateTime.now();
        this.paidBy = adminUsername;
        this.closedDate = LocalDateTime.now();
        this.paymentStatus = PaymentStatus.PAID;

    }

    /**
     * Calculate processing time in days
     * Uses createdAt (submission date) to closed date or current time
     */
    public long getProcessingDays() {
        LocalDateTime submissionDate = getSubmissionDate();
        if (submissionDate == null) {
            return 0;
        }

        LocalDateTime endDate = closedDate != null ? closedDate : LocalDateTime.now();
        return java.time.Duration.between(submissionDate, endDate).toDays();
    }

    /**
     * Check if claim is still open (not closed)
     */
    public boolean isOpen() {
        return closedDate == null &&
                (status == ClaimStatus.PENDING ||
                        status == ClaimStatus.UNDER_REVIEW ||
                        status == ClaimStatus.APPROVED);
    }

    /**
     * Check if claim has been processed (approved or rejected)
     */
    public boolean isProcessed() {
        return approvedDate != null || rejectedDate != null;
    }

    /**
     * Check if claim can be paid
     */
    public boolean canBePaid() {
        return status == ClaimStatus.APPROVED &&
                paymentStatus == PaymentStatus.PENDING &&
                approvedAmount != null &&
                approvedAmount.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Check if claim is fully paid
     */
    public boolean isFullyPaid() {
        return paymentStatus == PaymentStatus.PAID;
    }

    /**
     * Check if transition to new status is valid
     */
    public boolean canTransitionTo(ClaimStatus newStatus) {
        if (this.status == newStatus)
            return true;

        switch (this.status) {
            case PENDING:
                return newStatus == ClaimStatus.UNDER_REVIEW ||
                        newStatus == ClaimStatus.APPROVED ||
                        newStatus == ClaimStatus.REJECTED;
            case UNDER_REVIEW:
                return newStatus == ClaimStatus.APPROVED ||
                        newStatus == ClaimStatus.REJECTED;
            case APPROVED:
                return newStatus == ClaimStatus.PAID ||
                        newStatus == ClaimStatus.REJECTED;
            case PAID:
            case REJECTED:
                return false;
            default:
                return false;
        }
    }

    public boolean isValidIncidentType() {
        return claimType.getSupportedIncidentTypes().contains(incidentDetails.getType());
    }

    public void addDocument(ClaimDocuments document) {
        attachedDocuments.add(document);
        document.setClaim(this);
    }

    public void removeDocument(ClaimDocuments document) {
        attachedDocuments.remove(document);
        document.setClaim(null);
    }
}