package com.example.insurance.usecases.admin.service;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateClaimStatusRequest {
    private String status;
    private String reason;
    private BigDecimal amount;
}
