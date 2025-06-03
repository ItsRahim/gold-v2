package com.rahim.authenticationservice.service;

import com.rahim.authenticationservice.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Enhanced JWT Service with improved security, caching, and token management
 *
 * @created 01/06/2025
 * @author Rahim Ahmed
 */
@Slf4j
@Service
public class JwtService {

  @Value("${security.jwt.secret}")
  private String jwtSecret;

  @Value("${security.jwt.expiration:86400000}") // 24 hours default
  private long jwtExpiration;

  @Value("${security.jwt.refresh-expiration:604800000}") // 7 days default
  private long refreshExpiration;

  @Value("${security.jwt.issuer:authentication-service}")
  private String issuer;

  @Value("${security.jwt.audience:api-gateway}")
  private String audience;

  @Value("${security.jwt.clock-skew:60000}") // 1 minute default
  private long clockSkew;

  // Token type constants
  private static final String TOKEN_TYPE_CLAIM = "token_type";
  private static final String ACCESS_TOKEN_TYPE = "access";
  private static final String REFRESH_TOKEN_TYPE = "refresh";
  private static final String ROLES_CLAIM = "roles";
  private static final String USER_ID_CLAIM = "userId";
  private static final String EMAIL_CLAIM = "email";
  private static final String TOKEN_ID_CLAIM = "jti"; // JWT ID for tracking

  // Blacklisted tokens (in production, use Redis or database)
  private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

  // Cached signing key
  private Key signingKey;

  @PostConstruct
  private void initializeSigningKey() {
    if (!StringUtils.hasText(jwtSecret)) {
      throw new IllegalStateException("JWT secret key cannot be empty");
    }
    try {
      byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
      if (keyBytes.length < 32) { // 256 bits minimum
        throw new IllegalStateException("JWT secret key must be at least 256 bits (32 bytes)");
      }
      this.signingKey = Keys.hmacShaKeyFor(keyBytes);
      log.info("JWT signing key initialized successfully");
    } catch (Exception e) {
      log.error("Failed to initialize JWT signing key", e);
      throw new IllegalStateException("Invalid JWT secret key configuration", e);
    }
  }

  /**
   * Extract username from JWT token
   */
  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  /**
   * Extract user ID from JWT token
   */
  public String extractUserId(String token) {
    return extractClaim(token, claims -> claims.get(USER_ID_CLAIM, String.class));
  }

  /**
   * Extract email from JWT token
   */
  public String extractEmail(String token) {
    return extractClaim(token, claims -> claims.get(EMAIL_CLAIM, String.class));
  }

  /**
   * Extract roles from JWT token
   */
  public String extractRoles(String token) {
    return extractClaim(token, claims -> claims.get(ROLES_CLAIM, String.class));
  }

  /**
   * Extract token type from JWT token
   */
  public String extractTokenType(String token) {
    return extractClaim(token, claims -> claims.get(TOKEN_TYPE_CLAIM, String.class));
  }

  /**
   * Extract token ID (JTI) from JWT token
   */
  public String extractTokenId(String token) {
    return extractClaim(token, claims -> claims.get(TOKEN_ID_CLAIM, String.class));
  }

  /**
   * Generate access token with default claims
   */
  public String generateToken(UserDetails userDetails) {
    return generateToken(new HashMap<>(), userDetails);
  }

  /**
   * Generate access token with extra claims
   */
  public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>(extraClaims);
    claims.put(ROLES_CLAIM, extractRolesAsString(userDetails));
    claims.put(TOKEN_TYPE_CLAIM, ACCESS_TOKEN_TYPE);

    // Add user-specific claims if User entity
    if (userDetails instanceof User user) {
      claims.put(USER_ID_CLAIM, user.getId().toString());
      claims.put(EMAIL_CLAIM, user.getEmail());
    }

    return buildToken(claims, userDetails, jwtExpiration);
  }

  /**
   * Generate refresh token
   */
  public String generateRefreshToken(UserDetails userDetails) {
    return generateRefreshToken(new HashMap<>(), userDetails);
  }

  /**
   * Generate refresh token with extra claims
   */
  public String generateRefreshToken(Map<String, Object> extraClaims, UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>(extraClaims);
    claims.put(TOKEN_TYPE_CLAIM, REFRESH_TOKEN_TYPE);

    // Add minimal claims for refresh token
    if (userDetails instanceof User user) {
      claims.put(USER_ID_CLAIM, user.getId().toString());
    }

    return buildToken(claims, userDetails, refreshExpiration);
  }

  /**
   * Validate JWT token and return claims
   */
  public Claims validateToken(String token) throws JwtException {
    if (!StringUtils.hasText(token)) {
      throw new IllegalArgumentException("Token cannot be empty");
    }

    if (isTokenBlacklisted(token)) {
      throw new SecurityException("Token has been revoked");
    }

    return extractAllClaims(token);
  }

  /**
   * Validate token against user details
   */
  public boolean isTokenValid(String token, UserDetails userDetails) {
    try {
      Claims claims = validateToken(token);
      final String username = claims.getSubject();
      return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    } catch (Exception e) {
      log.debug("Token validation failed: {}", e.getMessage());
      return false;
    }
  }

  /**
   * Check if token is a valid access token
   */
  public boolean isValidAccessToken(String token, UserDetails userDetails) {
    try {
      return isTokenValid(token, userDetails) && ACCESS_TOKEN_TYPE.equals(extractTokenType(token));
    } catch (Exception e) {
      log.debug("Access token validation failed: {}", e.getMessage());
      return false;
    }
  }

  /**
   * Check if token is a valid refresh token
   */
  public boolean isValidRefreshToken(String token, UserDetails userDetails) {
    try {
      return isTokenValid(token, userDetails) && REFRESH_TOKEN_TYPE.equals(extractTokenType(token));
    } catch (Exception e) {
      log.debug("Refresh token validation failed: {}", e.getMessage());
      return false;
    }
  }

  /**
   * Check if token is expired
   */
  public boolean isTokenExpired(String token) {
    try {
      return extractExpiration(token).before(new Date());
    } catch (Exception e) {
      log.debug("Token expiration check failed: {}", e.getMessage());
      return true; // Consider invalid tokens as expired
    }
  }

  /**
   * Get remaining time until token expiration
   */
  public Duration getTimeUntilExpiration(String token) {
    try {
      Date expiration = extractExpiration(token);
      long remainingMillis = expiration.getTime() - System.currentTimeMillis();
      return Duration.ofMillis(Math.max(0, remainingMillis));
    } catch (Exception e) {
      log.debug("Failed to get time until expiration: {}", e.getMessage());
      return Duration.ZERO;
    }
  }

  /**
   * Revoke/blacklist a token
   */
  public void revokeToken(String token) {
    try {
      String tokenId = extractTokenId(token);
      if (StringUtils.hasText(tokenId)) {
        blacklistedTokens.add(tokenId);
        log.info("Token with ID {} has been revoked", tokenId);
      }
    } catch (Exception e) {
      log.warn("Failed to revoke token: {}", e.getMessage());
    }
  }

  /**
   * Check if token is blacklisted
   */
  public boolean isTokenBlacklisted(String token) {
    try {
      String tokenId = extractTokenId(token);
      return StringUtils.hasText(tokenId) && blacklistedTokens.contains(tokenId);
    } catch (Exception e) {
      log.debug("Failed to check token blacklist status: {}", e.getMessage());
      return false;
    }
  }

  /**
   * Refresh an access token using a refresh token
   */
  public String refreshAccessToken(String refreshToken, UserDetails userDetails) {
    if (!isValidRefreshToken(refreshToken, userDetails)) {
      throw new SecurityException("Invalid refresh token");
    }

    // Generate new access token
    return generateToken(userDetails);
  }

  /**
   * Get access token expiration time in milliseconds
   */
  public long getExpirationTime() {
    return jwtExpiration;
  }

  /**
   * Get refresh token expiration time in milliseconds
   */
  public long getRefreshExpirationTime() {
    return refreshExpiration;
  }

  /**
   * Extract specific claim from token
   */
  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  /**
   * Extract expiration date from token
   */
  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  /**
   * Extract roles as comma-separated string
   */
  private String extractRolesAsString(UserDetails userDetails) {
    return userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));
  }

  /**
   * Build JWT token with specified claims and expiration
   */
  private String buildToken(Map<String, Object> claims, UserDetails userDetails, long expiration) {
    Instant now = Instant.now();
    String tokenId = generateTokenId();

    return Jwts.builder()
            .setClaims(claims)
            .setSubject(userDetails.getUsername())
            .setIssuer(issuer)
            .setAudience(audience)
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plusMillis(expiration)))
            .setId(tokenId)
            .signWith(signingKey, SignatureAlgorithm.HS256)
            .compact();
  }

  /**
   * Extract all claims from token with comprehensive error handling
   */
  @Cacheable(value = "jwt-claims", key = "#token", unless = "#result == null")
  private Claims extractAllClaims(String token) {
    try {
      return Jwts.parserBuilder()
              .setSigningKey(signingKey)
              .setAllowedClockSkewSeconds(clockSkew / 1000)
              .requireIssuer(issuer)
              .requireAudience(audience)
              .build()
              .parseClaimsJws(token)
              .getBody();
    } catch (SecurityException e) {
      log.debug("Invalid JWT signature: {}", e.getMessage());
      throw new SecurityException("Invalid JWT signature", e);
    } catch (MalformedJwtException e) {
      log.debug("Invalid JWT token: {}", e.getMessage());
      throw new MalformedJwtException("Invalid JWT token", e);
    } catch (ExpiredJwtException e) {
      log.debug("JWT token is expired: {}", e.getMessage());
      throw new ExpiredJwtException(e.getHeader(), e.getClaims(), "JWT token is expired", e);
    } catch (UnsupportedJwtException e) {
      log.debug("JWT token is unsupported: {}", e.getMessage());
      throw new UnsupportedJwtException("JWT token is unsupported", e);
    } catch (IllegalArgumentException e) {
      log.debug("JWT claims string is empty: {}", e.getMessage());
      throw new IllegalArgumentException("JWT claims string is empty", e);
    } catch (Exception e) {
      log.error("Unexpected error during JWT parsing", e);
      throw new JwtException("Token parsing failed", e);
    }
  }

  /**
   * Generate unique token ID
   */
  private String generateTokenId() {
    return UUID.randomUUID().toString();
  }

  /**
   * Get the signing key (cached after initialization)
   */
  private Key getSignInKey() {
    return this.signingKey;
  }

  /**
   * Clean up expired blacklisted tokens (should be called periodically)
   */
  public void cleanupExpiredTokens() {
    // In production, implement proper cleanup logic
    // This is a simplified version
    blacklistedTokens.removeIf(tokenId -> {
      // Logic to check if token with this ID is expired
      // For now, just log the cleanup attempt
      return false;
    });
    log.debug("Cleanup of expired blacklisted tokens completed");
  }
}