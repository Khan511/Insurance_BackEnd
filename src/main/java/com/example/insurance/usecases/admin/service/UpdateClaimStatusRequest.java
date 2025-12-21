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
    private String status; // ClaimStatus enum name
    private String reason; // Optional for rejections
    private BigDecimal amount; // Optional for approvals
}
