package com.rahim.gatewayservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.rahim.gatewayservice.constants.UriConstants.*;

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
        .build();
  }

  private GatewayFilterSpec applyPricingServiceFilters(GatewayFilterSpec filters) {
    return filters.stripPrefix(3);
    // .filter(jwtAuthenticationFilter())
  }
}
