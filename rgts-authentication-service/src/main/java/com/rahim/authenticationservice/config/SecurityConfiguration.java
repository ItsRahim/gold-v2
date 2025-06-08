package com.rahim.authenticationservice.config;

import com.rahim.authenticationservice.constants.CorsConstants;
import com.rahim.authenticationservice.constants.Endpoints;
import com.rahim.authenticationservice.constants.HttpHeaderConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

/**
 * @created 08/06/2025
 * @author Rahim Ahmed
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
  private final AuthenticationProvider authenticationProvider;

  @Value("${app.cors.allowed-origins:}")
  private List<String> allowedOrigins;

  @Value("${app.cors.allowed-methods:}")
  private List<String> allowedMethods;

  @Value("${app.cors.allowed-headers:}")
  private List<String> allowedHeaders;

  @Value("${app.cors.allow-credentials:true}")
  private boolean allowCredentials;

  private static final String[] ALLOWED_MATCHERS = {
    Endpoints.LOGIN,
    Endpoints.REGISTER,
    Endpoints.REFRESH_TOKEN,
    Endpoints.VALIDATE_TOKEN,
    Endpoints.API_DOCS,
    Endpoints.SWAGGER_UI,
    Endpoints.SWAGGER_UI_HTML,
    Endpoints.ACTUATOR_HEALTH,
    Endpoints.ACTUATOR_INFO
  };

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            auth -> auth.requestMatchers(ALLOWED_MATCHERS).permitAll().anyRequest().authenticated())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .headers(
            headers ->
                headers
                    .contentTypeOptions(contentTypeOptions -> {})
                    .httpStrictTransportSecurity(
                        hstsConfig -> hstsConfig.maxAgeInSeconds(31536000).includeSubDomains(true))
                    .referrerPolicy(
                        referrerPolicy ->
                            referrerPolicy.policy(
                                ReferrerPolicyHeaderWriter.ReferrerPolicy
                                    .STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                    .frameOptions(HeadersConfigurer.FrameOptionsConfig::deny)
                    .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'")))
        .authenticationProvider(authenticationProvider)
        .build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    configuration.setAllowedOrigins(
        allowedOrigins.isEmpty() ? CorsConstants.DEFAULT_ALLOWED_ORIGINS : allowedOrigins);
    configuration.setAllowedMethods(
        allowedMethods.isEmpty() ? CorsConstants.DEFAULT_ALLOWED_METHODS : allowedMethods);
    configuration.setAllowedHeaders(
        allowedHeaders.isEmpty() ? List.of(CorsConstants.ALL_HEADERS) : allowedHeaders);

    configuration.setAllowCredentials(allowCredentials);
    configuration.setMaxAge(Duration.ofHours(1));
    configuration.setExposedHeaders(
        Arrays.asList(
            HttpHeaderConstants.AUTHORIZATION,
            HttpHeaderConstants.CACHE_CONTROL,
            HttpHeaderConstants.CONTENT_TYPE,
            HttpHeaderConstants.X_TOTAL_COUNT,
            HttpHeaderConstants.X_RATE_LIMIT_REMAINING,
            HttpHeaderConstants.X_RATE_LIMIT_RESET));
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);

    return source;
  }
}
