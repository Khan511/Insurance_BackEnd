package com.example.insurance.shared.context;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;

@Component
public class RequestContextInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response,
            Object handler) {
        // Generate or get existing trace ID
        String traceId = request.getHeader("X-Trace-Id");
        if (traceId == null || traceId.isEmpty()) {
            traceId = UUID.randomUUID().toString();
        }

        // Store in MDC for logging
        MDC.put("traceId", traceId);

        // Set in response headers
        response.setHeader("X-Trace-Id", traceId);

        // Store in request attribute for use in controllers
        request.setAttribute("traceId", traceId);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex) {
        // Clean up MDC
        MDC.clear();
    }
}