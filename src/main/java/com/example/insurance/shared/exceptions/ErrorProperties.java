package com.example.insurance.shared.exceptions;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.error")
public class ErrorProperties {
    private String documentationBaseUrl = "https://docs.insurance.com/api/errors";
}