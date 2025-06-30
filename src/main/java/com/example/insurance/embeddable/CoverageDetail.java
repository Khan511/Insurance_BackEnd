package com.example.insurance.embeddable;

import java.math.BigDecimal;

import com.example.insurance.shared.kernel.embeddables.MonetaryAmount;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class CoverageDetail {

    @Column(name = "coverage_name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "limit_amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "limit_currency"))
    })
    private MonetaryAmount coverageLimit;

    @Column(name = "deductible")
    private BigDecimal deductible;

}
