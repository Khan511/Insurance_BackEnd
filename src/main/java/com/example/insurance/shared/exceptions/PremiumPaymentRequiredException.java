package com.example.insurance.shared.exceptions;

import java.util.Map;

public class PremiumPaymentRequiredException extends BusinessException {
    public PremiumPaymentRequiredException(String policyNumber, Double amountDue) {
        super(ErrorCode.INS_003,
                Map.of("policyNumber", policyNumber, "amountDue", amountDue),
                policyNumber, amountDue);
    }
}