package com.example.insurance.shared.constant;

import java.util.List;

public class Constant {
        public static final String LOGIN_PATH = "/api/user/login";
        public static final String GET_ALL_POLICIES = "/api/product/all-products";
        public static final String GET_POLICY_DETAILS = "/api/product/product-details";
        public static final String CREATE_USER = "/api/user/create-user";
        public static final String EMAIL = "email";
        public static final String ROLES = "roles";
        public static final String ROLE_PREFIX = "ROLE_";
        public static final String LOGIN_EMAIL = "loginEmail";
        public static final String TOKEN_INVALIDATED_MSG = "Token invalidated";
        public static final String VERIFY_EMAIL_PATH = "/api/v1/auth/verify-email";
        public static final String RESEND_VERIFICATION_PATH = "/api/v1/auth/resend-verification";
        public static final String API_CHATBOT = "/api/chatbot/**";
        public static final String FORGOT_PASSWORD = "/api/user/forgot-password";
        public static final String VALIDATE_REQUEST_TOKEN = "/api/user/validate-reset-token";
        public static final String RESET_PASSWORD = "/api/user/reset-password";

        public static final List<String> ALLOWED_ORIGIONS = List.of("http://securedoc.com",
                        "https://insurace-app.netlify.app",
                        "http://localhost:4200",
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
