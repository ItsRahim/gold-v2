package com.rahim.gatewayservice.config;

import com.rahim.cachemanager.service.RedisService;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @created 23/07/2025
 * @author Rahim Ahmed
 */
@Component
@RequiredArgsConstructor
public class JwtValidationFilter implements GlobalFilter, Ordered {
  private final RedisService redisService;

  private static final String AUTH_SERVICE_URL =
      "lb://authentication-service/api/v2/authentication-service/validate-token";
  private static final String TOKEN_CACHE_PREFIX = "jwt:token:";
  private static final long TOKEN_EXPIRY = 3600;
  private final WebClient.Builder webClientBuilder;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    String path = exchange.getRequest().getURI().getPath();
    if (path.startsWith("/auth/register")
        || path.startsWith("/auth/login")
        || path.startsWith("/auth/verify-email")) {
      return chain.filter(exchange);
    }

    if (path.startsWith("/auth/logout")) {
      String token = extractToken(exchange.getRequest().getHeaders());

      if (token != null) {
        String cacheKey = TOKEN_CACHE_PREFIX + token;
        redisService.deleteKey(cacheKey);
      }

      return chain.filter(exchange);
    }

    String token = extractToken(exchange.getRequest().getHeaders());
    if (token == null) {
      return unauthorized(exchange);
    }

    String cacheKey = TOKEN_CACHE_PREFIX + token;
    Object cached = redisService.getValue(cacheKey);

    if (cached != null) {
      @SuppressWarnings("unchecked")
      Map<String, Object> userData = (Map<String, Object>) cached;
      return enrichRequest(exchange, chain, userData);
    }

    return validateTokenWithAuthService(token)
        .flatMap(
            userData -> {
              saveTokenToCache(cacheKey, userData);
              return enrichRequest(exchange, chain, userData);
            })
        .onErrorResume(e -> unauthorized(exchange));
  }

  private String extractToken(HttpHeaders headers) {
    String authHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);
    return (authHeader != null && authHeader.startsWith("Bearer "))
        ? authHeader
        : null;
  }

  private Mono<Map<String, Object>> validateTokenWithAuthService(String authHeader) {
    return webClientBuilder
        .build()
        .post()
        .uri(AUTH_SERVICE_URL)
        .header(HttpHeaders.AUTHORIZATION, authHeader)
        .retrieve()
        .onStatus(
            HttpStatusCode::isError,
            response -> Mono.error(new RuntimeException("Token validation failed")))
        .bodyToMono(new ParameterizedTypeReference<>() {});
  }

  private Mono<Void> enrichRequest(
      ServerWebExchange exchange, GatewayFilterChain chain, Map<String, Object> userData) {
    String username = (String) userData.get("username");
    @SuppressWarnings("unchecked")
    List<String> roles = (List<String>) userData.get("roles");

    ServerHttpRequest mutatedRequest =
        exchange
            .getRequest()
            .mutate()
            .header("X-Auth-Username", username)
            .header("X-Auth-Roles", String.join(",", roles))
            .build();

    return chain.filter(exchange.mutate().request(mutatedRequest).build());
  }

  private Mono<Void> unauthorized(ServerWebExchange exchange) {
    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
    return exchange.getResponse().setComplete();
  }

  @Override
  public int getOrder() {
    return -1;
  }

  private void saveTokenToCache(String cacheKey, Map<String, Object> userData) {
    try {
      String expiry = (String) userData.get("expiry");
      long expiryMillis = Instant.parse(expiry).toEpochMilli();
      long currentMillis = System.currentTimeMillis();
      long ttlSeconds = Math.max(1, (expiryMillis - currentMillis) / 1000);

      redisService.setValue(cacheKey, userData, ttlSeconds);
    } catch (Exception e) {
      redisService.setValue(cacheKey, userData, TOKEN_EXPIRY);
    }
  }
}
