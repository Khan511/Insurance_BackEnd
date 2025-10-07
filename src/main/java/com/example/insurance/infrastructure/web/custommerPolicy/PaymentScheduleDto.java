package com.example.insurance.infrastructure.web.custommerPolicy;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class PaymentScheduleDto {
    private Long id;
    private BigDecimal dueAmount;
    private String currency;
    private LocalDate dueDate;
    private LocalDate paidDate;
    private String status; // You can add this if you have payment status
}