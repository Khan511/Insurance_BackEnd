package com.example.insurance.domain.claim.model;

import java.util.ArrayList;
import java.util.List;

import com.example.insurance.common.enummuration.ClaimDocumentType;
import com.example.insurance.domain.claimDocuments.model.ClaimDocuments;
import com.example.insurance.domain.insuranceProduct.model.InsuranceProduct;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "claims")
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "policy_number", nullable = false)
    private String policyNumber;

    // @ManyToOne(fetch = FetchType.LAZY)
    // private CustomerPolicy policy;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private ClaimDocumentType claimType;

    @OneToMany(mappedBy = "claim", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ClaimDocuments> attachedDocuments = new ArrayList<>();

    @Embedded
    private IncidentDetails incidentDetails;

    // Add relationship to InsuranceProduct
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private InsuranceProduct insuranceProduct;

    public boolean isValidIncidentType() {
        return claimType.getSupportedIncidentTypes().contains(incidentDetails.getType());
    }

    // Helper method to maintain bidirectional relationship
    public void addDocument(ClaimDocuments document) {
        attachedDocuments.add(document);
        document.setClaim(this);
    }

    public void removeDocument(ClaimDocuments document) {
        attachedDocuments.remove(document);
        document.setClaim(null);
    }

}
