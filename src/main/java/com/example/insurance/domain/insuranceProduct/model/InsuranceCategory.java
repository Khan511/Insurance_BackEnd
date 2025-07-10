// package com.example.insurance.domain.insuranceProduct.model;

// import jakarta.persistence.*;
// import lombok.AllArgsConstructor;
// import lombok.Getter;
// import lombok.NoArgsConstructor;
// import lombok.Setter;
// import java.util.List;
// import com.fasterxml.jackson.annotation.JsonIgnore;

// @Entity
// @Getter
// @Setter
// @AllArgsConstructor
// @NoArgsConstructor
// public class InsuranceCategory {

// @Id
// @GeneratedValue(strategy = GenerationType.IDENTITY)
// private Long id;

// @Column(nullable = false, unique = true)
// private String name; // "Private", "Commercial"

// public InsuranceCategory(String name) {
// this.name = name;
// }

// @Column(columnDefinition = "TEXT")
// private String description;

// public InsuranceCategory(String name, String description) {
// this.name = name;
// this.description = description;

// }

// // @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval
// =
// // true)
// // @JsonIgnore
// // private List<CreateInsurancePolicy> policies;

// }
