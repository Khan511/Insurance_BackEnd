package com.example.insurance.usecases.admin.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RejectClaimRequest {
    private String rejectionReason;
    private String notes;
}