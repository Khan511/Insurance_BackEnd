package com.example.insurance.common.enummuration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ClaimDocumentType {
    // Format: TYPE_SUBTYPE
    AUTOMOBILE_COLLISION(
            "AUTO",
            "Collision Damage",
            Arrays.asList(RequiredDocument.POLICE_REPORT, RequiredDocument.REPAIR_ESTIMATE)),
    HEALTH_HOSPITALIZATION(
            "HEALTH",
            "Inpatient Treatment",
            Arrays.asList(RequiredDocument.MEDICAL_BILLS, RequiredDocument.DOCTOR_REPORT)),
    CYBER_INCIDENT(
            "CYBER",
            "Data Breach",
            Arrays.asList(RequiredDocument.INCIDENT_REPORT, RequiredDocument.FORENSIC_AUDIT));

    private final String category;
    private final String displayName;
    private final List<RequiredDocument> requiredDocuments;

    // Getters

    public List<RequiredDocument> getRequiredDocuments() {
        return Collections.unmodifiableList(requiredDocuments);
    }

    public enum RequiredDocument {
        POLICE_REPORT, REPAIR_ESTIMATE, MEDICAL_BILLS,
        DOCTOR_REPORT, DEATH_CERTIFICATE, INCIDENT_REPORT,
        FORENSIC_AUDIT
    }

}

// public enum ClaimType {

// AUTOMOBILE_COLLISION,
// AUTOMOBILE_THEFT,
// AUTOMOBILE_VANDALISM,
// PROPERTY_FIRE,
// PROPERTY_FLOOD,
// PROPERTY_THEFT,
// HEALTH_HOSPITALIZATION,
// HEALTH_DENTAL,
// LIFE_DEATH,
// LIFE_DISABILITY,
// TRAVEL_CANCELLATION,
// TRAVEL_MEDICAL,
// LIABILITY_GENERAL,
// CYBER_INCIDENT
// }