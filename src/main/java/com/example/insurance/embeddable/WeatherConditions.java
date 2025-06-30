package com.example.insurance.embeddable;

import java.math.BigDecimal;

import jakarta.persistence.Embeddable;

@Embeddable
public class WeatherConditions {
    private String conditions; // rain, snow, fog
    private BigDecimal temperature;
    private BigDecimal visibility;
    private String source; // NOAA, WeatherAPI

}
