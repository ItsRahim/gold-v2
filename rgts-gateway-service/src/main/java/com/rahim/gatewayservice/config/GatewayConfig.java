package com.rahim.gatewayservice.config;

import static com.rahim.gatewayservice.constants.UriConstants.*;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @created 01/06/2025
 * @author Rahim Ahmed
 */
@Configuration
@RequiredArgsConstructor
public class GatewayConfig {
  @Bean
  public RouteLocator routeLocator(RouteLocatorBuilder builder) {
    return builder
        .routes()
        .route(
            PRICING_SERVICE,
            r ->
                r.path(PRICING_PATHS[0], PRICING_PATHS[1])
                    .filters(f -> f.rewritePath(PRICING_REWRITE_REGEX, PRICING_REWRITE_REPLACEMENT))
                    .uri(PRICING_SERVICE_URI))
        .route(
            AUTHENTICATION_SERVICE,
            r ->
                r.path(AUTHENTICATION_PATHS[0], AUTHENTICATION_PATHS[1])
                    .filters(
                        f ->
                            f.rewritePath(
                                AUTHENTICATION_REWRITE_REGEX, AUTHENTICATION_REWRITE_REPLACEMENT))
                    .uri(AUTHENTICATION_SERVICE_URI))
        .build();
  }
}
