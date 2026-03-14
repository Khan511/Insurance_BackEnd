package com.example.insurance.shared.enummuration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NordicCountry {
    DENMARK("DK"),
    SWEDEN("SE"),
    NORWAY("NO");

    private final String countryCode;

}
