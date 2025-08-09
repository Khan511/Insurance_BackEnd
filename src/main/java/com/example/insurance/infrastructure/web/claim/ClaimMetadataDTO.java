package com.example.insurance.infrastructure.web.claim;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClaimMetadataDTO {
    private String type;
    private String displayName;
}
