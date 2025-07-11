// package com.example.insurance.domain.insurancePolicy.model;

// import java.util.List;
// import jakarta.persistence.CollectionTable;
// import jakarta.persistence.Column;
// import jakarta.persistence.ElementCollection;
// import jakarta.persistence.Entity;
// import jakarta.persistence.FetchType;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
// import jakarta.persistence.JoinColumn;
// import jakarta.persistence.ManyToOne;
// import lombok.Getter;
// import lombok.Setter;
// import
// com.example.insurance.domain.insuranceCategory.model.InsuranceCategory;

// @Entity
// @Getter
// @Setter
// public class InsurancePolicy {
// @Id
// @GeneratedValue(strategy = GenerationType.IDENTITY)
// private Long id;

// // I will give this UUID when i create Insurance policy from frontend
// // @Column(name = "policy_uuid")
// // private String policyId;

// @Column(nullable = false)
// private String title;

// @Column(columnDefinition = "TEXT", nullable = false)
// private String description;

// @ElementCollection
// @CollectionTable(name = "customer_policy_target_audience", joinColumns =
// @JoinColumn(name = "policy_id"))
// @Column(name = "audience")
// private List<String> targetAudience;

// @ElementCollection
// @CollectionTable(name = "policy_region", joinColumns = @JoinColumn(name =
// "policy_id"))
// @Column(name = "region")
// private List<String> region;

// @ManyToOne(fetch = FetchType.EAGER)
// @JoinColumn(name = "category_id")
// private InsuranceCategory category;
// }
