package com.example.insurance.domain.claim.model;

import java.util.ArrayList;
import java.util.List;
// import java.util.UUID;

import com.example.insurance.common.enummuration.ClaimDocumentType;
// import com.example.insurance.common.enummuration.ClaimStatus;
// import com.example.insurance.domain.auditing.domain.AuditEntity;
// import com.example.insurance.domain.claimStatusHistory.model.ClaimStatusHistory;
// import com.example.insurance.domain.customerPolicy.model.CustomerPolicy;
// import com.example.insurance.embeddable.DigitalEvidence;
import com.example.insurance.embeddable.DocumentAttachment;

// import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
// import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
// import jakarta.persistence.ManyToOne;
// import jakarta.persistence.OneToMany;
// import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "claims")
// public class Claim extends AuditEntity {
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @ManyToOne(fetch = FetchType.LAZY)
    // private CustomerPolicy policy;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private ClaimDocumentType claimType;

    @ElementCollection
    @CollectionTable(name = "claim_documents", joinColumns = @JoinColumn(name = "claim_id"))
    private List<DocumentAttachment> attachedDocuments = new ArrayList<>();

    @Embedded
    private IncidentDetails IncidentDetails;

    public boolean isValidIncidentType() {
        return claimType.getSupportedIncidentTypes().contains(IncidentDetails.getType());
    }

    // @ElementCollection
    // @CollectionTable(name = "claim_digital_evidence", joinColumns =
    // @JoinColumn(name = "claim_id"))
    // private List<DigitalEvidence> digitalEvidence;

    // @Embedded
    // private ClaimAssessment assement;

    // @Enumerated(EnumType.STRING)
    // private ClaimStatus status;

    // @OneToMany(mappedBy = "claim", cascade = CascadeType.ALL, orphanRemoval =
    // true)
    // // @JoinColumn(name = "claim_id", nullable = false)
    // @OrderBy("timestamp DESC")
    // private List<ClaimStatusHistory> statusHistory = new ArrayList<>();

    // Business method to change status
    // public void transitionStatus(ClaimStatus newStatus, String changedBy, String
    // notes) {
    // if (this.status == newStatus)
    // return;

    // statusHistory.add(new ClaimStatusHistory(
    // this,
    // newStatus,
    // changedBy,
    // "Transition: " + this.status + " → " + newStatus + ". " + notes));

    // this.status = newStatus;
    // }

    // Get latest status without loading full history
    // public ClaimStatusHistory getLatestStatusChange() {
    // return statusHistory.isEmpty() ? null : statusHistory.get(0);
    // }

}
