package com.example.insurance.infrastructure.web.custommerPolicy;

import java.time.LocalDate;

import lombok.Data;

@Data
public class CoveragePeriodDto {
    private LocalDate effectiveDate;
    private LocalDate expirationDate;
}
