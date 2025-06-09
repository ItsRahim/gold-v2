package com.rahim.authenticationservice.service.authentication;

import com.rahim.authenticationservice.dto.request.RegisterRequest;
import com.rahim.authenticationservice.dto.response.RegisterResponse;
import com.rahim.authenticationservice.entity.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

/**
 * @created 09/06/2025
 * @author Rahim Ahmed
 */
public interface IAuthenticationService {
  RegisterResponse register(RegisterRequest registerRequest, HttpServletRequest request);

  Optional<User> findByUsername(String username);
}
