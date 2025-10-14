package com.example.insurance.infrastructure.web.custommerPolicy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PaymentScheduleDto {
    private Long id;
    private BigDecimal dueAmount;
    private String currency;
    private LocalDate dueDate;
    private LocalDateTime paidDate;
    private String status; // You can add this if you have payment status
    private String transactionId;
}