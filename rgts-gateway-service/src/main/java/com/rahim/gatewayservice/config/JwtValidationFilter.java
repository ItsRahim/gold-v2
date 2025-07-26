package com.rahim.gatewayservice.config;

import com.rahim.cachemanager.service.RedisService;
import com.rahim.jwtcore.constants.JwtConstants;
import com.rahim.jwtcore.response.TokenVerificationResponse;
import lombok.RequiredArgsConstructor;
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

import java.util.Date;
import java.util.Optional;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class JwtValidationFilter implements GlobalFilter, Ordered {

  private static final String AUTH_SERVICE_URL =
      "lb://authentication-service/api/v2/authentication-service/validate-token";
  private static final String TOKEN_CACHE_PREFIX = "jwt:token:";
  private static final long DEFAULT_TOKEN_EXPIRY = 3600;

  private final RedisService redisService;
  private final WebClient.Builder webClientBuilder;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    String path = exchange.getRequest().getURI().getPath();

    // Bypass authentication for public endpoints
    if (Stream.of("/auth/register", "/auth/login", "/auth/verify-email")
        .anyMatch(path::startsWith)) {
      return chain.filter(exchange);
    }

    // Handle logout separately
    if (path.startsWith("/auth/logout")) {
      Optional.ofNullable(extractToken(exchange.getRequest().getHeaders()))
          .ifPresent(token -> redisService.deleteKey(TOKEN_CACHE_PREFIX + token));
      exchange.getResponse().setStatusCode(HttpStatus.NO_CONTENT);
      return exchange.getResponse().setComplete();
    }

    // Extract and validate token
    String token = extractToken(exchange.getRequest().getHeaders());
    if (token == null) {
      return unauthorized(exchange);
    }

    String cacheKey = TOKEN_CACHE_PREFIX + token;
    TokenVerificationResponse cached =
        redisService.getValue(cacheKey, TokenVerificationResponse.class);

    if (cached != null && isTokenValid(cached, cacheKey)) {
      return enrichRequest(exchange, chain, cached);
    }

    return validateTokenWithAuthService(token)
        .flatMap(
            response -> {
              saveTokenToCache(cacheKey, response);
              return enrichRequest(exchange, chain, response);
            })
        .onErrorResume(e -> unauthorized(exchange));
  }

  private String extractToken(HttpHeaders headers) {
    String authHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);
    return (authHeader != null && authHeader.startsWith(JwtConstants.BEARER_PREFIX))
        ? authHeader
        : null;
  }

  private boolean isTokenValid(TokenVerificationResponse response, String cacheKey) {
    Date expiry = response.getExpiry();
    if (expiry == null || expiry.before(new Date())) {
      redisService.deleteKey(cacheKey);
      return false;
    }
    return true;
  }

  private Mono<TokenVerificationResponse> validateTokenWithAuthService(String authHeader) {
    return webClientBuilder
        .build()
        .post()
        .uri(AUTH_SERVICE_URL)
        .header(HttpHeaders.AUTHORIZATION, authHeader)
        .retrieve()
        .onStatus(
            HttpStatusCode::isError,
            res -> Mono.error(new RuntimeException("Token validation failed")))
        .bodyToMono(TokenVerificationResponse.class);
  }

  private Mono<Void> enrichRequest(
      ServerWebExchange exchange, GatewayFilterChain chain, TokenVerificationResponse userData) {
    ServerHttpRequest request =
        exchange
            .getRequest()
            .mutate()
            .header("X-Auth-Username", userData.getUsername())
            .header("X-Auth-Roles", String.join(",", userData.getRoles()))
            .build();

    return chain.filter(exchange.mutate().request(request).build());
  }

  private void saveTokenToCache(String cacheKey, TokenVerificationResponse response) {
    try {
      Date expiry = response.getExpiry();
      long ttlSeconds =
          expiry != null
              ? Math.max(1, (expiry.getTime() - System.currentTimeMillis()) / 1000)
              : DEFAULT_TOKEN_EXPIRY;
      redisService.setValue(cacheKey, response, ttlSeconds);
    } catch (Exception e) {
      redisService.setValue(cacheKey, response, DEFAULT_TOKEN_EXPIRY);
    }
  }

  private Mono<Void> unauthorized(ServerWebExchange exchange) {
    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
    return exchange.getResponse().setComplete();
  }

  @Override
  public int getOrder() {
    return -1;
  }
}
