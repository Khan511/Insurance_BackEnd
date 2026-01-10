package com.example.insurance.common.enummuration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum IncidentType {
    AUTO_COLLISION("Auto Collision", true),
    THEFT("Theft", true),
    NATURAL_DISASTER("Natural Disaster", false),
    MEDICAL_EMERGENCY("Medical Emergency", false),
    FIRE("Fire", true),
    VANDALISM("Vandalism", true),
    LIABILITY_CLAIM("Liability Claim", false),
    CYBER_ATTACK("Cyber Attack", false), // Added missing type
    WATER_DAMAGE("Water Damage", false); // Added missing type

    private final String displayName;
    private final boolean requiresPoliceReport;

    // Add this method for better display handling
    public String getDisplayName() {
        return displayName;
    }
}
