package com.example.insurance.shared.kernel.embeddables;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class CoverageDetail {

        @Column(name = "coverage_name", nullable = false, length = 100)
        private String coverageType;

        @Column(name = "description", length = 500)
        private String description;

        @Embedded
        @AttributeOverrides({
                        @AttributeOverride(name = "amount", column = @Column(name = "limit_amount")),
                        @AttributeOverride(name = "currency", column = @Column(name = "limit_currency"))
        })
        private MonetaryAmount coverageLimit;

        @Embedded
        @AttributeOverrides({
                        @AttributeOverride(name = "amount", column = @Column(name = "deductible_amount")),
                        @AttributeOverride(name = "currency", column = @Column(name = "deductible_currency"))
        })
        private MonetaryAmount deductible;

}
