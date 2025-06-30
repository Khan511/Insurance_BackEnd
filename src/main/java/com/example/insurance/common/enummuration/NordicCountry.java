package com.example.insurance.common.enummuration;

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
