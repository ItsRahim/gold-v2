package com.rahim.authenticationservice.config;

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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static com.rahim.authenticationservice.constants.Endpoints.*;

/**
 * @created 01/06/2025
 * @author Rahim Ahmed
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

  private final AuthenticationProvider authenticationProvider;
  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  @Value("${app.cors.allowed-origins:http://localhost:3000,http://localhost:8080}")
  private List<String> allowedOrigins;

  @Value("${app.cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
  private List<String> allowedMethods;

  @Value("${app.cors.allowed-headers:*}")
  private List<String> allowedHeaders;

  @Value("${app.cors.allow-credentials:true}")
  private boolean allowCredentials;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http.csrf(AbstractHttpConfigurer::disable)
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(
                        SIGNUP,
                        LOGIN,
                        RESEND,
                        FORGOT_PASSWORD,
                        RESET_PASSWORD,
                        "/swagger-ui/**",
                        "/swagger-ui.html")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .headers(
            headers ->
                headers
                    .frameOptions(HeadersConfigurer.FrameOptionsConfig::deny)
                    .contentTypeOptions(contentTypeOptions -> {})
                    .httpStrictTransportSecurity(
                        hstsConfig -> hstsConfig.maxAgeInSeconds(31536000).includeSubDomains(true))
                    .referrerPolicy(
                        referrerPolicy ->
                            referrerPolicy.policy(
                                ReferrerPolicyHeaderWriter.ReferrerPolicy
                                    .STRICT_ORIGIN_WHEN_CROSS_ORIGIN)))
        .authenticationProvider(authenticationProvider)
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(allowedOrigins);
    configuration.setAllowedMethods(allowedMethods);

    if (allowedHeaders.contains("*")) {
      configuration.setAllowedHeaders(List.of("*"));
    } else {
      configuration.setAllowedHeaders(allowedHeaders);
    }
    configuration.setAllowCredentials(allowCredentials);
    configuration.setMaxAge(Duration.ofHours(1));
    configuration.setExposedHeaders(
        Arrays.asList("Authorization", "Cache-Control", "Content-Type", "X-Total-Count"));
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);

    return source;
  }
}
