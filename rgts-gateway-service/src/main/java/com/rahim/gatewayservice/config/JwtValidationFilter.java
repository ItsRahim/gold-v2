package com.rahim.gatewayservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * @created 23/07/2025
 * @author Rahim Ahmed
 */
@Component
@RequiredArgsConstructor
public class JwtValidationFilter implements GlobalFilter, Ordered {
  private final WebClient.Builder webClientBuilder;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
      return exchange.getResponse().setComplete();
    }

    return webClientBuilder
        .build()
        .post()
        .uri("lb://authentication-service/api/v2/auth/validate-token")
        .header(HttpHeaders.AUTHORIZATION, authHeader)
        .retrieve()
        .onStatus(
            HttpStatusCode::isError, response -> Mono.error(new RuntimeException("Invalid token")))
        .bodyToMono(Map.class)
        .flatMap(
            userData -> {
              @SuppressWarnings("unchecked")
              List<String> roles = (List<String>) userData.get("roles");
              String username = (String) userData.get("username");

              ServerHttpRequest mutatedRequest =
                  exchange
                      .getRequest()
                      .mutate()
                      .header("X-Auth-Username", username)
                      .header("X-Auth-Roles", String.join(",", roles))
                      .build();

              return chain.filter(exchange.mutate().request(mutatedRequest).build());
            })
        .onErrorResume(
            e -> {
              exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
              return exchange.getResponse().setComplete();
            });
  }

  @Override
  public int getOrder() {
    return -1;
  }
}
