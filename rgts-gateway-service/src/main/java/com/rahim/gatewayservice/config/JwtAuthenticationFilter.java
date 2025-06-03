package com.rahim.gatewayservice.config;

import com.rahim.gatewayservice.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * @created 01/06/2025
 * @author Rahim Ahmed
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter
    implements GatewayFilterFactory<JwtAuthenticationFilter.Config> {

  private final JwtUtil jwtUtil;

  public static class Config {}

  @Override
  public GatewayFilter apply(Config config) {
    return (exchange, chain) -> {
      ServerHttpRequest request = exchange.getRequest();
      String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        return onError(exchange, "Authorization header is missing or invalid");
      }
      try {
        String token = authHeader.substring(7);
        Claims claims = jwtUtil.validateToken(token);
        String username = claims.getSubject();

        ServerHttpRequest modifiedRequest =
            request
                .mutate()
                .header("X-User", username)
                .header("X-Roles", claims.get("roles", String.class))
                .build();

        return chain.filter(exchange.mutate().request(modifiedRequest).build());
      } catch (Exception e) {
        return onError(exchange, "Authentication failed: " + e.getMessage());
      }
    };
  }

  private Mono<Void> onError(ServerWebExchange exchange, String error) {
    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
    exchange
        .getResponse()
        .getHeaders()
        .set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

    String body = "{\"error\": \"" + error + "\"}";
    DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();
    DataBuffer buffer = bufferFactory.wrap(body.getBytes(StandardCharsets.UTF_8));

    return exchange.getResponse().writeWith(Mono.just(buffer));
  }
}
