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
            "AUTO", "Collision Damage",
            Arrays.asList(RequiredDocument.POLICE_REPORT, RequiredDocument.REPAIR_ESTIMATE),
            Arrays.asList(IncidentType.AUTO_COLLISION, IncidentType.THEFT)// Added supported incidents
    ),
    HEALTH_HOSPITALIZATION(
            "HEALTH", "Inpatient Treatment",
            Arrays.asList(RequiredDocument.MEDICAL_BILLS, RequiredDocument.DOCTOR_REPORT),
            Arrays.asList(IncidentType.MEDICAL_EMERGENCY)),
    CYBER_INCIDENT(
            "CYBER", "Data Breach",
            Arrays.asList(RequiredDocument.INCIDENT_REPORT, RequiredDocument.FORENSIC_AUDIT),
            Arrays.asList(IncidentType.CYBER_ATTACK)),
    HOME_DAMAGE(
            "HOME", "Property Damage",
            Arrays.asList(RequiredDocument.PROPERTY_DAMAGE_REPORT, RequiredDocument.ESTIMATE,
                    RequiredDocument.INVENTORY_LIST),
            Arrays.asList(IncidentType.NATURAL_DISASTER, IncidentType.FIRE, IncidentType.WATER_DAMAGE));

    private final String category;
    private final String displayName;
    private final List<RequiredDocument> requiredDocuments;
    private final List<IncidentType> supportedIncidentTypes;

    public List<IncidentType> getSupportedIncidentTypes() {
        return Collections.unmodifiableList(supportedIncidentTypes);
    }

    public List<RequiredDocument> getRequiredDocuments() {
        return Collections.unmodifiableList(requiredDocuments);
    }

    @Getter
    public enum RequiredDocument {
        POLICE_REPORT("Police Report"),
        REPAIR_ESTIMATE("Repair Estimate"),
        MEDICAL_BILLS("Medical Bills"),
        DOCTOR_REPORT("Doctor Report"),
        DEATH_CERTIFICATE("Death Certificate"),
        INCIDENT_REPORT("Incident Report"),
        FORENSIC_AUDIT("Forensic Audit"),
        PROPERTY_DAMAGE_REPORT("Property Damage Report"),
        ESTIMATE("Estimate"),
        INVENTORY_LIST("Inventory List"),
        BENEFICIARY_DOCS("Beneficiary Documents");

        private final String displayName;

        RequiredDocument(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // public enum RequiredDocument {
    // POLICE_REPORT, REPAIR_ESTIMATE, MEDICAL_BILLS,
    // DOCTOR_REPORT, DEATH_CERTIFICATE, INCIDENT_REPORT,
    // FORENSIC_AUDIT, PROPERTY_DAMAGE_REPORT,
    // ESTIMATE, INVENTORY_LIST, BENEFICIARY_DOCS
    // }

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