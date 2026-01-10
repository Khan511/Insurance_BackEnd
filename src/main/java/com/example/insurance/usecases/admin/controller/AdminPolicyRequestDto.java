package com.example.insurance.usecases.admin.controller;

import java.math.BigDecimal;
import java.util.List;
import com.example.insurance.infrastructure.web.custommerPolicy.BeneficiaryDto;
import com.example.insurance.infrastructure.web.custommerPolicy.CoveragePeriodDto;
import lombok.Data;

@Data
public class AdminPolicyRequestDto {
    private Long id;
    private String status;

    private CoveragePeriodDto validityPeriod;
    private BigDecimal premium;

    private String cancellationReason;
    private String cancelledBy;
    private String statusChangeNotes;
    private String updatedBy;

    private Boolean isCancelling;

    private String paymentFrequency;

    private List<BeneficiaryDto> beneficiaries;
};
