package com.rahim.authenticationservice.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * @created 01/06/2025
 * @author Rahim Ahmed
 */
@Slf4j
@Service
public class JwtService {

  @Value("${security.jwt.secret}")
  private String jwtSecret;

  @Value("${security.jwt.expiration}")
  private long jwtExpiration;

  @Value("${security.jwt.refresh-expiration}")
  private long refreshExpiration;

  private static final String TOKEN_TYPE_CLAIM = "token_type";
  private static final String ACCESS_TOKEN_TYPE = "access";
  private static final String REFRESH_TOKEN_TYPE = "refresh";

  /** Extract username from JWT token */
  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  /** Extract token type from JWT token */
  public String extractTokenType(String token) {
    return extractClaim(token, claims -> claims.get(TOKEN_TYPE_CLAIM, String.class));
  }

  /** Generate access token with default claims */
  public String generateToken(UserDetails userDetails) {
    return generateToken(new HashMap<>(), userDetails);
  }

  /** Generate access token with extra claims */
  public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>(extraClaims);
    claims.put(TOKEN_TYPE_CLAIM, ACCESS_TOKEN_TYPE);
    return buildToken(claims, userDetails, jwtExpiration);
  }

  /** Generate refresh token */
  public String generateRefreshToken(UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();
    claims.put(TOKEN_TYPE_CLAIM, REFRESH_TOKEN_TYPE);
    return buildToken(claims, userDetails, refreshExpiration);
  }

  /** Generate refresh token with extra claims */
  public String generateRefreshToken(Map<String, Object> extraClaims, UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>(extraClaims);
    claims.put(TOKEN_TYPE_CLAIM, REFRESH_TOKEN_TYPE);
    return buildToken(claims, userDetails, refreshExpiration);
  }

  /** Get access token expiration time */
  public long getExpirationTime() {
    return jwtExpiration;
  }

  /** Get refresh token expiration time */
  public long getRefreshExpirationTime() {
    return refreshExpiration;
  }

  /** Validate token against user details */
  public boolean isTokenValid(String token, UserDetails userDetails) {
    try {
      final String username = extractUsername(token);
      return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    } catch (Exception e) {
      log.warn("Token validation failed: {}", e.getMessage());
      return false;
    }
  }

  /** Check if token is a valid access token */
  public boolean isValidAccessToken(String token, UserDetails userDetails) {
    try {
      return isTokenValid(token, userDetails) && ACCESS_TOKEN_TYPE.equals(extractTokenType(token));
    } catch (Exception e) {
      log.warn("Access token validation failed: {}", e.getMessage());
      return false;
    }
  }

  /** Check if token is a valid refresh token */
  public boolean isValidRefreshToken(String token, UserDetails userDetails) {
    try {
      return isTokenValid(token, userDetails) && REFRESH_TOKEN_TYPE.equals(extractTokenType(token));
    } catch (Exception e) {
      log.warn("Refresh token validation failed: {}", e.getMessage());
      return false;
    }
  }

  /** Check if token is expired */
  public boolean isTokenExpired(String token) {
    try {
      return extractExpiration(token).before(new Date());
    } catch (Exception e) {
      log.warn("Token expiration check failed: {}", e.getMessage());
      return true; // Consider invalid tokens as expired
    }
  }

  /** Get remaining time until token expiration in milliseconds */
  public long getTimeUntilExpiration(String token) {
    try {
      Date expiration = extractExpiration(token);
      return expiration.getTime() - System.currentTimeMillis();
    } catch (Exception e) {
      log.warn("Failed to get time until expiration: {}", e.getMessage());
      return 0;
    }
  }

  /** Extract specific claim from token */
  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  /** Extract expiration date from token */
  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  /** Build JWT token with specified claims and expiration */
  private String buildToken(Map<String, Object> claims, UserDetails userDetails, long expiration) {
    long now = System.currentTimeMillis();
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date(now))
        .setExpiration(new Date(now + expiration))
        .signWith(getSignInKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  /** Extract all claims from token with comprehensive error handling */
  private Claims extractAllClaims(String token) {
    try {
      return Jwts.parserBuilder()
          .setSigningKey(getSignInKey())
          .build()
          .parseClaimsJws(token)
          .getBody();
    } catch (SecurityException e) {
      log.error("Invalid JWT signature: {}", e.getMessage());
      throw new SecurityException("Invalid JWT signature");
    } catch (MalformedJwtException e) {
      log.error("Invalid JWT token: {}", e.getMessage());
      throw new MalformedJwtException("Invalid JWT token");
    } catch (ExpiredJwtException e) {
      log.error("JWT token is expired: {}", e.getMessage());
      throw new ExpiredJwtException(e.getHeader(), e.getClaims(), "JWT token is expired");
    } catch (UnsupportedJwtException e) {
      log.error("JWT token is unsupported: {}", e.getMessage());
      throw new UnsupportedJwtException("JWT token is unsupported");
    } catch (IllegalArgumentException e) {
      log.error("JWT claims string is empty: {}", e.getMessage());
      throw new IllegalArgumentException("JWT claims string is empty");
    }
  }

  /** Get signing key for JWT operations */
  private Key getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
