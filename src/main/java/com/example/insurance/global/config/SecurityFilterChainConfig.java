
// package com.example.insurance.global.config;

// import static com.example.insurance.shared.constant.Constant.ALLOWED_METHODS;
// import static com.example.insurance.shared.constant.Constant.ALLOWED_ORIGIONS;
// import static com.example.insurance.shared.constant.Constant.EXPOSED_HEADERS;
// import static com.example.insurance.shared.constant.Constant.MAX_AGE;
// import java.time.Duration;
// import java.util.List;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.core.annotation.Order;
// import org.springframework.http.HttpMethod;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.authentication.AuthenticationProvider;
// import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
// import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
// import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
// import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.web.cors.CorsConfiguration;
// import org.springframework.web.cors.CorsConfigurationSource;
// import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
// import lombok.RequiredArgsConstructor;

// @Configuration
// @EnableWebSecurity
// @RequiredArgsConstructor
// @EnableMethodSecurity(prePostEnabled = true)
// public class SecurityFilterChainConfig {

//         @Value("${security.headers.csp}")
//         private String contentSecurityPolicy;

//         private final CustomUserDetailsService customUserDetailsService;
//         // private final JwtAuthenticationFilter jwtAuthenticationFilter;
//         // private final JwtAuthorizationFilter jwtAuthorizationFilter;
//         private final CustomSecurityConfigurer customSecurityConfigurer;
//         private final SecurityConfig securityConfig;

//         /**
//          * Primary security filter chain for API endpoints
//          */
//         @Bean
//         @Order(1) // Highest priority
//         public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager)
//                         throws Exception {
//                 // Create filters with
//                 http
//                                 .with(customSecurityConfigurer, c -> {
//                                 })
//                                 .securityMatcher("/api/**")
//                                 .cors(cors -> cors.configurationSource(configurationSource()))
//                                 .csrf(csrf -> csrf.disable()) // disable for stateless APIs
//                                 .headers(headers -> headers
//                                                 .contentSecurityPolicy(
//                                                                 csp -> csp.policyDirectives(contentSecurityPolicy))
//                                                 .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
//                                                 .httpStrictTransportSecurity(hsts -> hsts
//                                                                 .includeSubDomains(true)
//                                                                 .preload(true)
//                                                                 .maxAgeInSeconds(31536000)))
//                                 .authorizeHttpRequests(auth -> auth
//                                                 // Public endpoints (no authentication required)
//                                                 .requestMatchers(HttpMethod.OPTIONS, "/api/**").permitAll() // Allow all
//                                                                                                             // OPTIONS
//                                                                                                             // requests
//                                                 .requestMatchers(HttpMethod.POST,
//                                                                 "/api/auth/**",
//                                                                 "/api/v1/auth/resend-verification",
//                                                                 "/api/user/create-user")
//                                                 .permitAll()
//                                                 .requestMatchers(HttpMethod.GET,
//                                                                 "/api/v1/auth/**",
//                                                                 "/api/v1/auth/verify-email",
//                                                                 "/api/public/**",
//                                                                 "/api/doc/**",
//                                                                 "/api/product/all-products",
//                                                                 "/api/product/product-details/**")
//                                                 .permitAll()

//                                                 // ========== ROLE-BASED ACCESS ==========
//                                                 // ADMIN only endpoints
//                                                 .requestMatchers("/admin/**").hasRole("ADMIN")
//                                                 .requestMatchers("/api/system-settings/**").hasRole("ADMIN")
//                                                 .requestMatchers("/api/roles/**").hasRole("ADMIN")
//                                                 .requestMatchers("/api/users/**").hasRole("ADMIN")

//                                                 // AGENT endpoints
//                                                 .requestMatchers("/api/agent/**").hasRole("AGENT")

//                                                 // CLAIM_MANAGER endpoints
//                                                 .requestMatchers("/api/claims/manage/**").hasRole("CLAIM_MANAGER")

//                                                 // CUSTOMER endpoints
//                                                 .requestMatchers("/api/customer/**").hasRole("CUSTOMER")
//                                                 // Premium Calculation - Allow all authenticated users
//                                                 .requestMatchers(HttpMethod.POST, "/api/premium/calculate")
//                                                 .authenticated()

//                                                 // ========== PERMISSION-BASED ACCESS ==========
//                                                 // Policy Management
//                                                 .requestMatchers(HttpMethod.GET, "/api/policies/**")
//                                                 .hasAnyAuthority("VIEW_POLICY", "ROLE_ADMIN", "ROLE_AGENT",
//                                                                 "ROLE_CLAIM_MANAGER", "ROLE_CUSTOMER")

//                                                 .requestMatchers(HttpMethod.POST, "/api/policies/**")
//                                                 .hasAnyAuthority("CREATE_POLICY", "ROLE_ADMIN", "ROLE_AGENT")

//                                                 .requestMatchers(HttpMethod.PUT, "/api/policies/**")
//                                                 .hasAnyAuthority("EDIT_POLICY", "ROLE_ADMIN", "ROLE_AGENT")

//                                                 .requestMatchers(HttpMethod.DELETE, "/api/policies/**")
//                                                 .hasAnyAuthority("DELETE_POLICY", "ROLE_ADMIN")

//                                                 // Claim Management
//                                                 .requestMatchers(HttpMethod.GET, "/api/claims/**")
//                                                 .hasAnyAuthority("VIEW_CLAIM", "ROLE_ADMIN", "ROLE_AGENT",
//                                                                 "ROLE_CLAIM_MANAGER", "ROLE_CUSTOMER")

//                                                 .requestMatchers(HttpMethod.GET, "/api/admin/employees/**")
//                                                 .hasAnyAuthority("ROLE_ADMIN")

//                                                 .requestMatchers(HttpMethod.POST, "/api/claims/**")
//                                                 .hasAnyAuthority("FILE_CLAIM", "ROLE_ADMIN", "ROLE_AGENT",
//                                                                 "ROLE_CUSTOMER")
//                                                 .requestMatchers(HttpMethod.PUT, "/api/claims/**")
//                                                 .hasAnyAuthority("EDIT_CLAIM", "ROLE_ADMIN", "ROLE_CLAIM_MANAGER")

//                                                 .requestMatchers(HttpMethod.PATCH, "/api/claims/approve/**")
//                                                 .hasAnyAuthority("APPROVE_CLAIM", "ROLE_ADMIN", "ROLE_CLAIM_MANAGER")

//                                                 .requestMatchers(HttpMethod.PATCH, "/api/claims/reject/**")
//                                                 .hasAnyAuthority("REJECT_CLAIM", "ROLE_ADMIN", "ROLE_CLAIM_MANAGER")

//                                                 // Payment Management
//                                                 .requestMatchers("/api/payments/**")
//                                                 .hasAnyAuthority("MAKE_PAYMENT", "ROLE_ADMIN", "ROLE_AGENT",
//                                                                 "ROLE_CUSTOMER")

//                                                 .requestMatchers(HttpMethod.GET, "/api/payments/history")
//                                                 .hasAnyAuthority("VIEW_PAYMENT_HISTORY", "ROLE_ADMIN", "ROLE_AGENT",
//                                                                 "ROLE_CLAIM_MANAGER")

//                                                 .requestMatchers(HttpMethod.POST, "/api/payments/refund")
//                                                 .hasAnyAuthority("REFUND_PAYMENT", "ROLE_ADMIN")

//                                                 // Premium Calculation
//                                                 .requestMatchers("/api/premium/calculate")
//                                                 .hasAnyAuthority("CALCULATE_PREMIUM", "ROLE_ADMIN", "ROLE_AGENT",
//                                                                 "ROLE_CUSTOMER")

//                                                 // Policy Approval (for underwriters, now handled by ADMIN/AGENT)
//                                                 .requestMatchers("/api/policies/approve/**")
//                                                 .hasAnyAuthority("APPROVE_POLICY", "ROLE_ADMIN")

//                                                 // Audit Logs
//                                                 .requestMatchers("/api/audit-logs/**")
//                                                 .hasAnyAuthority("VIEW_AUDIT_LOGS", "ROLE_ADMIN", "ROLE_CLAIM_MANAGER")

//                                                 .requestMatchers(HttpMethod.POST, "/api/audit-logs/export")
//                                                 .hasAnyAuthority("EXPORT_LOGS", "ROLE_ADMIN")

//                                                 // Dashboard
//                                                 .requestMatchers("/api/dashboard/**")
//                                                 .hasAnyAuthority("VIEW_DASHBOARD", "ROLE_ADMIN")

//                                                 // S3 Operations
//                                                 .requestMatchers(HttpMethod.DELETE, "/api/s3/**")
//                                                 .hasAnyAuthority("DELETE_POLICY", "ROLE_ADMIN")

//                                                 // Default: all other requests require authentication
//                                                 .anyRequest().authenticated())

//                                 .sessionManagement(session -> session
//                                                 .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

//                                 .authenticationProvider(authenticationProvider());

//                 return http.build();
//         }

//         @Bean
//         public CorsConfigurationSource configurationSource() {
//                 CorsConfiguration config = new CorsConfiguration();
//                 config.setAllowedOriginPatterns(ALLOWED_ORIGIONS);
//                 config.setAllowedMethods(ALLOWED_METHODS);
//                 config.setAllowedHeaders(List.of("*"));
//                 config.setExposedHeaders(EXPOSED_HEADERS);
//                 config.setAllowCredentials(true);
//                 config.setMaxAge(Duration.ofSeconds(MAX_AGE));

//                 UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//                 source.registerCorsConfiguration("/**", config);
//                 return source;
//         }

//         @Bean
//         public AuthenticationProvider authenticationProvider() {
//                 // Create provider with UserDetailsService in constructor
//                 DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(this.customUserDetailsService);

//                 authProvider.setPasswordEncoder(securityConfig.passwordEncoder());
//                 // Setting false: Now the error message clearly states when a user is not found,
//                 // which:
//                 // Can help in debugging and giving more accurate error responses (e.g.,
//                 // "Invalid email" vs. "Invalid password").
//                 authProvider.setHideUserNotFoundExceptions(false);
//                 // Default: true → Returns String (e.g., just the username/email) as principal.
//                 // Setting false: Keeps the full UserDetails object (e.g., User or
//                 // CustomUserDetails) in the Authentication object.
//                 authProvider.setForcePrincipalAsString(false);
//                 return authProvider;
//         }

//         @Bean
//         public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
//                 return config.getAuthenticationManager();
//         }
// }

package com.example.insurance.global.config;

import static com.example.insurance.shared.constant.Constant.ALLOWED_METHODS;
import static com.example.insurance.shared.constant.Constant.ALLOWED_ORIGIONS;
import static com.example.insurance.shared.constant.Constant.EXPOSED_HEADERS;
import static com.example.insurance.shared.constant.Constant.MAX_AGE;
import java.time.Duration;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityFilterChainConfig {

        @Value("${security.headers.csp}")
        private String contentSecurityPolicy;

        private final CustomUserDetailsService customUserDetailsService;
        // private final JwtAuthenticationFilter jwtAuthenticationFilter;
        // private final JwtAuthorizationFilter jwtAuthorizationFilter;
        private final CustomSecurityConfigurer customSecurityConfigurer;
        private final SecurityConfig securityConfig;

        /**
         * Primary security filter chain for API endpoints
         */
        @Bean
        @Order(1) // Highest priority
        public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager)
                        throws Exception {
                // Create filters with
                http
                                .with(customSecurityConfigurer, c -> {
                                })
                                .securityMatcher("/api/**")
                                .cors(cors -> cors.configurationSource(configurationSource()))
                                .csrf(csrf -> csrf.disable()) // disable for stateless APIs
                                .headers(headers -> headers
                                                .contentSecurityPolicy(
                                                                csp -> csp.policyDirectives(contentSecurityPolicy))
                                                .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                                                .httpStrictTransportSecurity(hsts -> hsts
                                                                .includeSubDomains(true)
                                                                .preload(true)
                                                                .maxAgeInSeconds(31536000)))
                                .authorizeHttpRequests(auth -> auth
                                                // Public endpoints (no authentication required)
                                                .requestMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()
                                                .requestMatchers(HttpMethod.POST,
                                                                "/api/auth/**",
                                                                "/api/v1/auth/resend-verification",
                                                                "/api/user/create-user")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.GET,
                                                                "/api/v1/auth/**",
                                                                "/api/v1/auth/verify-email",
                                                                "/api/public/**",
                                                                "/api/doc/**",
                                                                "/api/product/all-products",
                                                                "/api/product/product-details/**")
                                                .permitAll()

                                                // ========== ROLE-BASED ACCESS (Using hasAuthority) ==========
                                                // ADMIN only endpoints
                                                .requestMatchers("/admin/**").hasAuthority("ADMIN")
                                                .requestMatchers("/api/system-settings/**").hasAuthority("ADMIN")
                                                .requestMatchers("/api/roles/**").hasAuthority("ADMIN")
                                                .requestMatchers("/api/users/**").hasAuthority("ADMIN")

                                                // AGENT endpoints
                                                .requestMatchers("/api/agent/**").hasAuthority("AGENT")

                                                // CLAIM_MANAGER endpoints
                                                .requestMatchers("/api/claims/manage/**").hasAuthority("CLAIM_MANAGER")

                                                // CUSTOMER endpoints
                                                .requestMatchers("/api/customer/**").hasAuthority("CUSTOMER")

                                                // Premium Calculation - Allow all authenticated users
                                                .requestMatchers(HttpMethod.POST, "/api/premium/calculate")
                                                .authenticated()

                                                // ========== PERMISSION-BASED ACCESS ==========
                                                // Policy Management
                                                .requestMatchers(HttpMethod.GET, "/api/policies/**")
                                                .hasAnyAuthority("VIEW_POLICY", "ADMIN", "AGENT", "CLAIM_MANAGER",
                                                                "CUSTOMER")

                                                .requestMatchers(HttpMethod.POST, "/api/policies/**")
                                                .hasAnyAuthority("CREATE_POLICY", "ADMIN", "AGENT")

                                                .requestMatchers(HttpMethod.PUT, "/api/policies/**")
                                                .hasAnyAuthority("EDIT_POLICY", "ADMIN", "AGENT")

                                                .requestMatchers(HttpMethod.DELETE, "/api/policies/**")
                                                .hasAnyAuthority("DELETE_POLICY", "ADMIN")

                                                // Claim Management
                                                .requestMatchers(HttpMethod.GET, "/api/claims/**")
                                                .hasAnyAuthority("VIEW_CLAIM", "ADMIN", "AGENT", "CLAIM_MANAGER",
                                                                "CUSTOMER")

                                                .requestMatchers(HttpMethod.GET, "/api/admin/employees/**")
                                                .hasAuthority("ADMIN")

                                                .requestMatchers(HttpMethod.POST, "/api/claims/**")
                                                .hasAnyAuthority("FILE_CLAIM", "ADMIN", "AGENT", "CUSTOMER")

                                                .requestMatchers(HttpMethod.PUT, "/api/claims/**")
                                                .hasAnyAuthority("EDIT_CLAIM", "ADMIN", "CLAIM_MANAGER")

                                                .requestMatchers(HttpMethod.PATCH, "/api/claims/approve/**")
                                                .hasAnyAuthority("APPROVE_CLAIM", "ADMIN", "CLAIM_MANAGER")

                                                .requestMatchers(HttpMethod.PATCH, "/api/claims/reject/**")
                                                .hasAnyAuthority("REJECT_CLAIM", "ADMIN", "CLAIM_MANAGER")

                                                // Payment Management
                                                .requestMatchers("/api/payments/**")
                                                .hasAnyAuthority("MAKE_PAYMENT", "ADMIN", "AGENT", "CUSTOMER")

                                                .requestMatchers(HttpMethod.GET, "/api/payments/history")
                                                .hasAnyAuthority("VIEW_PAYMENT_HISTORY", "ADMIN", "AGENT",
                                                                "CLAIM_MANAGER")

                                                .requestMatchers(HttpMethod.POST, "/api/payments/refund")
                                                .hasAnyAuthority("REFUND_PAYMENT", "ADMIN")

                                                // Premium Calculation
                                                .requestMatchers("/api/premium/calculate")
                                                .hasAnyAuthority("CALCULATE_PREMIUM", "ADMIN", "AGENT", "CUSTOMER")

                                                // Policy Approval (for underwriters, now handled by ADMIN/AGENT)
                                                .requestMatchers("/api/policies/approve/**")
                                                .hasAnyAuthority("APPROVE_POLICY", "ADMIN")

                                                // Audit Logs
                                                .requestMatchers("/api/audit-logs/**")
                                                .hasAnyAuthority("VIEW_AUDIT_LOGS", "ADMIN", "CLAIM_MANAGER")

                                                .requestMatchers(HttpMethod.POST, "/api/audit-logs/export")
                                                .hasAnyAuthority("EXPORT_LOGS", "ADMIN")

                                                // Dashboard
                                                .requestMatchers("/api/dashboard/**")
                                                .hasAnyAuthority("VIEW_DASHBOARD", "ADMIN")

                                                // S3 Operations
                                                .requestMatchers(HttpMethod.DELETE, "/api/s3/**")
                                                .hasAnyAuthority("DELETE_POLICY", "ADMIN")

                                                // User Management
                                                .requestMatchers(HttpMethod.GET, "/api/user/**")
                                                .hasAnyAuthority("VIEW_USER", "ADMIN", "AGENT", "CLAIM_MANAGER")

                                                .requestMatchers(HttpMethod.POST, "/api/user/**")
                                                .hasAnyAuthority("CREATE_USER", "ADMIN", "AGENT")

                                                .requestMatchers(HttpMethod.PUT, "/api/user/**")
                                                .hasAnyAuthority("EDIT_USER", "ADMIN", "AGENT")

                                                .requestMatchers(HttpMethod.DELETE, "/api/user/**")
                                                .hasAnyAuthority("DELETE_USER", "ADMIN")

                                                // Role Management
                                                .requestMatchers(HttpMethod.POST, "/api/roles/assign")
                                                .hasAnyAuthority("ASSIGN_ROLE", "ADMIN")

                                                // System Settings
                                                .requestMatchers("/api/system-settings/manage/**")
                                                .hasAnyAuthority("MANAGE_SYSTEM_SETTINGS", "ADMIN")

                                                // Admin Panel Access
                                                .requestMatchers("/api/admin-panel/**")
                                                .hasAnyAuthority("ACCESS_ADMIN_PANEL", "ADMIN")

                                                // Default: all other requests require authentication
                                                .anyRequest().authenticated())

                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                                .authenticationProvider(authenticationProvider());

                return http.build();
        }

        @Bean
        public CorsConfigurationSource configurationSource() {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOriginPatterns(ALLOWED_ORIGIONS);
                config.setAllowedMethods(ALLOWED_METHODS);
                config.setAllowedHeaders(List.of("*"));
                config.setExposedHeaders(EXPOSED_HEADERS);
                config.setAllowCredentials(true);
                config.setMaxAge(Duration.ofSeconds(MAX_AGE));

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", config);
                return source;
        }

        @Bean
        public AuthenticationProvider authenticationProvider() {
                // Create provider with UserDetailsService in constructor
                DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(this.customUserDetailsService);

                authProvider.setPasswordEncoder(securityConfig.passwordEncoder());
                authProvider.setHideUserNotFoundExceptions(false);
                authProvider.setForcePrincipalAsString(false);
                return authProvider;
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
                return config.getAuthenticationManager();
        }
}