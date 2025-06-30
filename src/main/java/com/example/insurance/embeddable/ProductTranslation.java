package com.example.insurance.embeddable;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
public class ProductTranslation {

    private String displayName;
    private String description;
    private String termsAndConditions;

    public ProductTranslation(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

}
