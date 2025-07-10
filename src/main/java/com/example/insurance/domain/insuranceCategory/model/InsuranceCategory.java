package com.example.insurance.domain.insuranceCategory.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InsuranceCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // "Private", "Commercial"

    public InsuranceCategory(String name) {
        this.name = name;
    }

    @Column(columnDefinition = "TEXT")
    private String description;

    public InsuranceCategory(String name, String description) {
        this.name = name;
        this.description = description;

    }
}
