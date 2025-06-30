package com.example.insurance.domain.insuranceProduct.model;

import java.io.IOException;
// import java.math.BigDecimal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

// JSON Converter for complex configuration
@Converter(autoApply = true)
public class JpaJsonConverter implements AttributeConverter<PremiumCalculationConfig, String> {
    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Override
    public String convertToDatabaseColumn(PremiumCalculationConfig attribute) {
        try {
            return mapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON conversion error", e);
        }
    }

    @Override
    public PremiumCalculationConfig convertToEntityAttribute(String dbData) {
        try {
            return mapper.readValue(dbData, PremiumCalculationConfig.class);
        } catch (IOException e) {
            throw new RuntimeException("JSON conversion error", e);
        }
    }
}
