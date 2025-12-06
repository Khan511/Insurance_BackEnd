package com.example.insurance.usecases.admin.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.insurance.domain.paymentSchedule.model.PaymentStatus;
import com.example.insurance.shared.kernel.embeddables.MonetaryAmount;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public class AdminAllPaymentsDto {

    private Long id;

    private String customer;

    private String policyNumber;

    private MonetaryAmount dueAmount;

    private String currency;

    private LocalDate dueDate;

    private LocalDateTime paidDate;

    private PaymentStatus status;

}
