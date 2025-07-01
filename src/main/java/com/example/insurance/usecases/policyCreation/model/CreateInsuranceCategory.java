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
// public class CreateInsuranceCategory {

// @Id
// @GeneratedValue(strategy = GenerationType.IDENTITY)
// private Long id;

// @Column(nullable = false, unique = true)
// private String name; // "Private", "Commercial"

// @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval =
// true)
// private List<CreateInsurancePolicy> policies;

// // Getters and Setters
// }
