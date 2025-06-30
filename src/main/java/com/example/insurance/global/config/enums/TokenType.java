
package com.example.insurance.global.config.enums;

import lombok.Getter;

@Getter
public enum TokenType {
    ACCESS("access-token"),
    REFRESH("refresh-token");

    private final String value;

    private TokenType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
