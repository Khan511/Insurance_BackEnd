// package com.example.insurance.embeddable;

// import org.slf4j.MDC;
// import org.springframework.security.core.context.SecurityContextHolder;

// import jakarta.persistence.Embeddable;
// import lombok.Getter;
// import lombok.NoArgsConstructor;

// @Getter
// @Embeddable
// @NoArgsConstructor
// public class SystemContext {
// private String correlationId; // For distributed tracing
// private String serviceInstance; // K8s pod ID or hostname
// private String clientIp;

// public static SystemContext current() {
// SystemContext context = new SystemContext();
// context.correlationId = MDC.get("correlationId"); // From SLF4J MDC
// context.serviceInstance = System.getenv("HOSTNAME"); // K8s environment
// context.clientIp = SecurityContextHolder.getContext()
// .getAuthentication() instanceof JwtAuthenticationToken token
// ? token.getToken().getClaim("ip")
// : "SYSTEM";
// return context;
// }

// }
