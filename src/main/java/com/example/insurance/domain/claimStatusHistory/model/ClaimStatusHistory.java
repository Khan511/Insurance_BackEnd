package com.example.insurance.domain.claimStatusHistory.model;

import java.time.Instant;
import java.util.UUID;

import com.example.insurance.common.enummuration.ClaimStatus;
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
import lombok.Getter;

@Entity
@Getter
@Table(name = "claim_status_history")
public class ClaimStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claim_id", nullable = false)
    private Claim claim;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ClaimStatus status;

    @Column(nullable = false, updatable = false)
    private Instant timestamp = Instant.now();

    @Column(name = "changed_by", nullable = false, length = 255)
    private String changedBy; // User ID or system process name

    @Column(columnDefinition = "TEXT")
    private String notes;

    // @Embedded
    // private SystemContext systemContext; // For distributed tracing

    // Required by JPA
    protected ClaimStatusHistory() {
    }

    public ClaimStatusHistory(Claim claim, ClaimStatus status, String changedBy, String notes) {
        this.claim = claim;
        this.status = status;
        this.changedBy = changedBy;
        this.notes = notes;
        // this.systemContext = SystemContext.current();
    }

}
