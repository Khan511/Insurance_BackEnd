package com.example.insurance.shared.kernel.embeddables;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class ProductTranslation {

    private String displayName;
    private String description;
    // private String termsAndConditions;
}
