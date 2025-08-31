
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
                                                .requestMatchers(HttpMethod.OPTIONS, "/api/s3/**").permitAll()
                                                .requestMatchers(HttpMethod.POST, "/api/auth/**",
                                                                "/api/user/create-user", "/api/s3/**")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/public/**", "/api/doc/**",
                                                                "/api/policy/all-policies",
                                                                "/api/policy/policy-details/**")
                                                .permitAll()
                                                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                                                .requestMatchers(HttpMethod.DELETE, "/api/**")
                                                .hasAnyAuthority("SCOPE_DELETE")
                                                .anyRequest().authenticated())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                // .exceptionHandling(exception -> exception
                                // .authenticationEntryPoint(null)
                                // .accessDeniedHandler(null))
                                .authenticationProvider(authenticationProvider());
                // .addFilterBefore(jwtAuthorizationFilter,
                // UsernamePasswordAuthenticationFilter.class)
                // .addFilterBefore(jwtAuthenticationFilter, jwtAuthorizationFilter.getClass());

                return http.build();
        }

        /**
         * Default security filter chain for all other requests
         */

        @Bean
        @Order(2)
        public SecurityFilterChain defauSecurityFilterChain(HttpSecurity http) throws Exception {
                http
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/", "/error", "/webjars/**").permitAll()
                                                .anyRequest().denyAll()// Deny all other requests by default
                                )
                                .formLogin(login -> login.disable())
                                .httpBasic(basic -> basic.disable());
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
                // Setting false: Now the error message clearly states when a user is not found,
                // which:
                // Can help in debugging and giving more accurate error responses (e.g.,
                // “Invalid email” vs. “Invalid password”).
                authProvider.setHideUserNotFoundExceptions(false);
                // Default: true → Returns String (e.g., just the username/email) as principal.
                // Setting false: Keeps the full UserDetails object (e.g., User or
                // CustomUserDetails) in the Authentication object.
                authProvider.setForcePrincipalAsString(false);
                return authProvider;
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
                return config.getAuthenticationManager();
        }

}
