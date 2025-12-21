package com.example.insurance.infrastructure.web.claim;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IncidentDetailsDTO {

    @NotNull(message = "Incident date/time is required")
    @PastOrPresent(message = "Incident date/time must be in the past or present")
    private LocalDateTime incidentDateTime;

    @NotBlank(message = "Incident type is required")
    @Size(max = 50, message = "Incident type cannot exceed 50 characters")
    private String type; // Enum name string

    private BigDecimal claimAmount;

    @NotNull(message = "Location details are required")
    @Valid
    private AddressDTO location;

    @NotBlank(message = "Description is required")
    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    @Size(max = 50, message = "Police report number cannot exceed 50 characters")
    private String policeReportNumber;

    @NotNull(message = "Third party involvement must be specified")
    private Boolean thirdPartyInvolved;

    @Valid
    private ThirdPartyDetailsDTO thirdPartyDetails;

}