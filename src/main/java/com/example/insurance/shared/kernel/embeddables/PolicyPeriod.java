package com.example.insurance.shared.kernel.embeddables;

import java.time.LocalDate;
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
public class PolicyPeriod {

    private LocalDate effectiveDate;
    private LocalDate expirationDate;

    // Business Logic
    public boolean isActive() {
        LocalDate today = LocalDate.now();
        return !today.isBefore(effectiveDate) && !today.isAfter(expirationDate);
    }

    public boolean isExpired() {
        return LocalDate.now().isAfter(expirationDate);
    }

}
