package com.rahim.authenticationservice.constants;

import java.util.List;
import org.springframework.http.HttpMethod;

public final class CorsConstants {
  private CorsConstants() {}

  public static final List<String> DEFAULT_ALLOWED_METHODS =
      List.of(
          HttpMethod.GET.name(),
          HttpMethod.POST.name(),
          HttpMethod.PUT.name(),
          HttpMethod.DELETE.name(),
          HttpMethod.OPTIONS.name());

  public static final List<String> DEFAULT_ALLOWED_ORIGINS =
      List.of("http://localhost:3000", "http://localhost:8080");

  public static final String ALL_HEADERS = "*";
}
