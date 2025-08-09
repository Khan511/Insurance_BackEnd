package com.example.insurance.domain.claim.model;

import java.time.LocalDateTime;
import com.example.insurance.common.enummuration.IncidentType;
import com.example.insurance.embeddable.GeoCooordinates;
import com.example.insurance.embeddable.ThirdPartyDetails;
import com.example.insurance.embeddable.WeatherConditions;
import com.example.insurance.shared.kernel.embeddables.Address;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class IncidentDetails {

    @Column(name = "incident_datetime", nullable = false)
    private LocalDateTime incidentDateTime;

    @Column(name = "report_datetime", nullable = false)
    private LocalDateTime reportDateTime = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "incident_type", length = 50)
    private IncidentType type;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "street", column = @Column(name = "incident_street")),
            @AttributeOverride(name = "city", column = @Column(name = "incident_city")),
            // @AttributeOverride(name = "state", column = @Column(name = "incident_state")),
            @AttributeOverride(name = "postalCode", column = @Column(name = "incident_postal_code")),
            @AttributeOverride(name = "country", column = @Column(name = "incident_country"))
    })
    private Address location;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "police_report_number", length = 50)
    private String policeReportNumber;

    @Column(name = "is_third_party_involved")
    private boolean thirdPartyInvolved;

    @Embedded
    private ThirdPartyDetails thirdPartyDetails;

    // @Embedded
    // private GeoCooordinates coordinates;

    @Embedded
    private WeatherConditions weatherCondition;

    // Business login
    public boolean isRepostedWithin24Hourse() {
        return reportDateTime.isBefore(incidentDateTime.plusHours(24));
    }

    public boolean requiresPoliceRepost() {
        return type.isRequiresPoliceReport() && policeReportNumber == null;
    }

}
