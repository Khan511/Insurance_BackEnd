package com.example.insurance.infrastructure.web.claim;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ThirdPartyDetailsDTO {
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String name;

    @NotBlank(message = "Contact information is required")
    @Size(max = 100, message = "Contact information cannot exceed 100 characters")
    private String contactInfo;

    @NotBlank(message = "Insurance information is required")
    @Size(max = 100, message = "Insurance information cannot exceed 100 characters")
    private String insuranceInfo;
}