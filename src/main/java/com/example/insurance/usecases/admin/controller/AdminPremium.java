package com.example.insurance.usecases.admin.controller;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class AdminPremium {

    private BigDecimal amount;
    private String currency;

}
