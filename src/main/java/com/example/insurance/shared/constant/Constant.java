package com.example.insurance.shared.constant;

import java.util.Arrays;
import java.util.List;

public class Constant {
    public static final String LOGIN_PATH = "/login";
    public static final String EMAIL = "email";
    public static final String ROLES = "roles";
    public static final String ROLE_PREFIX = "ROLE_";
    public static final String LOGIN_EMAIL = "loginEmail";
    public static final String TOKEN_INVALIDATED_MSG = "Token invalidated";
    public static final List<String> ALLOWED_ORIGIONS = List.of("http://securedoc.com", "http://localhost:4200",
            "http://localhost:5173", "http://localhost:5173/documents",
            "http://localhost:3000");

    public static final List<String> ALLOWED_METHODS = List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");
    public static final List<String> EXPOSED_HEADERS = List.of(
            "Authorization",
            "Content-Disposition",
            "X-Request-ID",
            "X-RateLimit-Limit",
            "X-RateLimit-Remaining");

    public static final Long MAX_AGE = 3600L;
}
