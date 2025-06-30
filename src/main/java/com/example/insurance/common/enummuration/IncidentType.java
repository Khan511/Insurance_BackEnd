package com.example.insurance.common.enummuration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum IncidentType {
    AUTO_COLLISION(true),
    THEFT(true),
    NATURAL_DISASTER(false),
    MEDICAL_EMERGENCY(false),
    FIRE(true),
    VANDALISM(true),
    LIABILITY_CLAIM(false);

    private final boolean requiresPoliceReport;

}
