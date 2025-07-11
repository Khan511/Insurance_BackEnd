// package com.example.insurance.domain.insuranceProduct.model;

// import java.io.IOException;
// import org.postgresql.util.PGobject;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.fasterxml.jackson.databind.SerializationFeature;
// import jakarta.persistence.AttributeConverter;
// import jakarta.persistence.Converter;

// @Converter
// public class JpaJsonConverter implements
// AttributeConverter<PremiumCalculationConfig, Object> {
// private static final ObjectMapper objectMapper = new ObjectMapper();

// static {
// // Configure Jackson to serialize BigDecimals as plain strings
// objectMapper.enable(SerializationFeature.WRITE_BIGDECIMAL_AS_PLAIN);
// }

// @Override
// public Object convertToDatabaseColumn(PremiumCalculationConfig config) {
// try {
// PGobject pgObject = new PGobject();
// pgObject.setType("jsonb");
// pgObject.setValue(objectMapper.writeValueAsString(config));
// return pgObject;
// } catch (Exception e) {
// throw new RuntimeException("JSON conversion error", e);
// }
// }

// @Override
// public PremiumCalculationConfig convertToEntityAttribute(Object dbData) {
// try {
// if (dbData == null)
// return null;

// if (dbData instanceof PGobject pgObject) {
// return objectMapper.readValue(pgObject.getValue(),
// PremiumCalculationConfig.class);
// } else if (dbData instanceof String stringValue) {
// return objectMapper.readValue(stringValue, PremiumCalculationConfig.class);
// }
// throw new IllegalArgumentException("Unsupported database JSON type");
// } catch (IOException e) {
// throw new RuntimeException("JSON conversion error", e);
// }
// }
// }
// package com.example.insurance.domain.insuranceProduct.model;

// import java.io.IOException;
// import org.postgresql.util.PGobject;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import jakarta.persistence.AttributeConverter;
// import jakarta.persistence.Converter;

// @Converter(autoApply = true)
// public class JpaJsonConverter implements
// AttributeConverter<PremiumCalculationConfig, Object> {
// private static final ObjectMapper objectMapper = new ObjectMapper();

// @Override
// public Object convertToDatabaseColumn(PremiumCalculationConfig config) {
// try {
// PGobject jsonObject = new PGobject();
// jsonObject.setType("jsonb");
// jsonObject.setValue(objectMapper.writeValueAsString(config));
// return jsonObject;
// } catch (Exception e) {
// throw new IllegalArgumentException("Error converting to JSONB", e);
// }
// }

// @Override
// public PremiumCalculationConfig convertToEntityAttribute(Object dbData) {
// try {
// if (dbData instanceof PGobject) {
// return objectMapper.readValue(((PGobject) dbData).getValue(),
// PremiumCalculationConfig.class);
// } else if (dbData instanceof String) {
// return objectMapper.readValue((String) dbData,
// PremiumCalculationConfig.class);
// }
// throw new IllegalArgumentException("Unsupported database JSON type");
// } catch (IOException e) {
// throw new IllegalArgumentException("Error converting from JSONB", e);
// }
// }
// }