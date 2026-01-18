package com.example.insurance.shared.exceptions;

import java.util.Map;

public class PolicyNotFoundException extends BusinessException {
    public PolicyNotFoundException(String policyNumber) {
        super(ErrorCode.INS_001,
                Map.of("policyNumber", policyNumber),
                policyNumber);
    }
}
