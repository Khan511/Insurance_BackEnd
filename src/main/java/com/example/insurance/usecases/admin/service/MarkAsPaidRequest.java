package com.example.insurance.usecases.admin.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MarkAsPaidRequest {
    private String paymentReference;
    private String notes;

}
