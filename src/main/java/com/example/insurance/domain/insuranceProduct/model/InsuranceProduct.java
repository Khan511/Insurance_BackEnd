package com.example.insurance.domain.insuranceProduct.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
// import java.util.UUID;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyClass;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.OneToMany;
// import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.example.insurance.common.enummuration.ClaimDocumentType;
import com.example.insurance.common.enummuration.ProductType;

import com.example.insurance.domain.auditing.domain.AuditEntity;
import com.example.insurance.domain.customerPolicy.model.CustomerPolicy;
import com.example.insurance.domain.insuranceCategory.model.InsuranceCategory;
import com.example.insurance.embeddable.CoverageDetail;
import com.example.insurance.embeddable.ProductTranslation;
import com.example.insurance.shared.kernel.embeddables.MonetaryAmount;
import com.example.insurance.shared.kernel.embeddables.PolicyPeriod;
// import com.vladmihalcea.hibernate.type.json.JsonBinaryType;

// import org.hibernate.annotations.Type;
// import org.hibernate.annotations.TypeDef;

// @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "insurance_products")
public class InsuranceProduct extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // @GeneratedValue(strategy = GenerationType.SEQUENCE, generator =
    // "product_seq")
    // @SequenceGenerator(name = "prduct_seq", sequenceName = "product_id_seq",
    // allocationSize = 1)
    private Long id;

    // Core identification
    @Column(name = "product_code", unique = true, nullable = false, length = 20)
    private String productCode;
    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;
    // For large description
    @Lob
    @Column(name = "description", nullable = false)
    private String description;

    // Product Information
    @Enumerated(EnumType.STRING)
    @Column(name = "product_type", nullable = false, length = 30)
    private ProductType productType;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "base_premium_amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "base_premium_currency"))
    })
    private MonetaryAmount basePremium;
    @ElementCollection
    @CollectionTable(name = "product_coverage_details", joinColumns = @JoinColumn(name = "product_id"))
    private Set<CoverageDetail> coverageDetails = new HashSet<>();
    @ElementCollection
    @CollectionTable(name = "product_eligibility_rules", joinColumns = @JoinColumn(name = "product_id"))
    @MapKeyColumn(name = "rule_key")
    @Column(name = "rule_value")
    private Map<String, String> eligibilityRules = new HashMap<>();
    @Convert(converter = JpaJsonConverter.class)
    // @Type(type = "jsonb")
    @Column(name = "calculation_config", columnDefinition = "jsonb")
    private PremiumCalculationConfig calculationConfig; // JSONB storage for complex config

    // Sales/marketing info
    @ElementCollection
    @CollectionTable(name = "policy_target_audience", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "audience")
    private List<String> targetAudience = new ArrayList<>();
    @ElementCollection
    @CollectionTable(name = "poicy_region", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "region")
    private List<String> region;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private InsuranceCategory category;

    // Operational
    @Column(name = "is_archived", nullable = false)
    private boolean archived = false;
    @ElementCollection
    @CollectionTable(name = "product_translations", joinColumns = @JoinColumn(name = "product_id"))
    @MapKeyClass(Locale.class)
    @MapKeyColumn(name = "local_code", length = 10)
    private Map<Locale, ProductTranslation> translation = new HashMap<>();
    @Embedded
    private PolicyPeriod validityPeriod;
    @ElementCollection
    @CollectionTable(name = "product_claim_types", joinColumns = @JoinColumn(name = "product_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "claim_type", length = 30)
    private Set<ClaimDocumentType.RequiredDocument> allowedClaimTypes = new HashSet<>();

    // Relationships
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CustomerPolicy> activePolicies = new ArrayList<>();

    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    // Business Methods
    public void addCoverage(CoverageDetail covrage) {
        coverageDetails.add(covrage);
    }

    public void removeCoverage(CoverageDetail covrage) {
        coverageDetails.remove(covrage);
    }

    public void addEligibilityRule(String key, String value) {

        eligibilityRules.put(key, value);
    }

    public void archive() {
        this.archived = true;
    };

    public void restore() {
        this.archived = false;
    };

}
