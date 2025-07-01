// package com.example.insurance.usecases.policyCreation.model;

// import jakarta.persistence.*;
// import lombok.AllArgsConstructor;
// import lombok.Getter;
// import lombok.NoArgsConstructor;
// import lombok.Setter;

// import java.util.List;

// @Entity
// @Getter
// @Setter
// @AllArgsConstructor
// @NoArgsConstructor
// public class CreateInsurancePolicy {

// @Id
// @GeneratedValue(strategy = GenerationType.IDENTITY)
// private Long id;

// @Column(nullable = false)
// private String title;

// @Column(columnDefinition = "TEXT", nullable = false)
// private String description;

// @ElementCollection
// @CollectionTable(name = "policy_target_audience", joinColumns =
// @JoinColumn(name = "policy_id"))
// @Column(name = "audience")
// private List<String> targetAudience;

// @ElementCollection
// @CollectionTable(name = "policy_region", joinColumns = @JoinColumn(name =
// "policy_id"))
// @Column(name = "region")
// private List<String> region;

// @ManyToOne(fetch = FetchType.LAZY)
// @JoinColumn(name = "category_id")
// private CreateInsuranceCategory category;

// // ✅ Custom constructor for seeding
// public CreateInsurancePolicy(
// String title,
// String description,
// List<String> targetAudience,
// List<String> region,
// CreateInsuranceCategory category) {
// this.title = title;
// this.description = description;
// this.targetAudience = targetAudience;
// this.region = region;
// this.category = category;
// }
// }

// // @Entity
// // @Getter
// // @Setter
// // @AllArgsConstructor
// // @NoArgsConstructor
// // public class CreateInsurancePolicy {

// // @Id
// // @GeneratedValue(strategy = GenerationType.IDENTITY)
// // private Long id;

// // @Column(nullable = false)
// // private String title;

// // @Column(columnDefinition = "TEXT", nullable = false)
// // private String description;

// // @ElementCollection
// // @CollectionTable(name = "policy_target_audience", joinColumns =
// // @JoinColumn(name = "policy_id"))
// // @Column(name = "audience")
// // private List<String> targetAudience;

// // @ElementCollection
// // @CollectionTable(name = "policy_region", joinColumns = @JoinColumn(name =
// // "policy_id"))
// // @Column(name = "region")
// // private List<String> region;

// // @ManyToOne(fetch = FetchType.LAZY)
// // @JoinColumn(name = "category_id")
// // private CreateInsuranceCategory category;

// // // Getters and Setters
// // }
