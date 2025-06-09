package com.rahim.authenticationservice.config;

import com.rahim.authenticationservice.exception.UserNotFoundException;
import com.rahim.authenticationservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @created 08/06/2025
 * @author Rahim Ahmed
 */
@Configuration
@RequiredArgsConstructor
public class ApplicationConfiguration {
  private final UserRepository userRepository;

  @Bean
  public UserDetailsService userDetailsService() {
    return username ->
            userRepository
            .findByUsername(username)
            .orElseThrow(
                () -> new UserNotFoundException("User not found with username: " + username));
  }

  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider daoAuthenticationProvider =
        new DaoAuthenticationProvider(userDetailsService());
    daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

    return daoAuthenticationProvider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
      throws Exception {
    return config.getAuthenticationManager();
  }
}
