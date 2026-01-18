package com.example.insurance.shared.exceptions;

import java.util.Map;

public class ClaimProcessinExcetion {
    public class ClaimProcessingException extends BusinessException {
        public ClaimProcessingException(String claimId, String reason) {
            super(ErrorCode.INS_002,
                    Map.of("claimId", claimId, "reason", reason),
                    claimId, reason);
        }
    }
}