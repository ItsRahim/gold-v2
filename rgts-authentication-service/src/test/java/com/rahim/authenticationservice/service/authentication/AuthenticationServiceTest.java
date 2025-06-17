package com.rahim.authenticationservice.service.authentication;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.nylle.javafixture.Fixture;
import com.rahim.authenticationservice.BaseTestConfiguration;
import com.rahim.authenticationservice.dto.enums.ResponseStatus;
import com.rahim.authenticationservice.dto.request.RegisterRequest;
import com.rahim.authenticationservice.dto.response.RegisterResponse;
import com.rahim.authenticationservice.entity.User;
import com.rahim.authenticationservice.enums.Role;
import com.rahim.authenticationservice.repository.UserRepository;
import com.rahim.authenticationservice.service.authentication.impl.AuthenticationService;
import com.rahim.authenticationservice.service.role.impl.RoleService;
import com.rahim.authenticationservice.service.verification.impl.VerificationService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * @created 12/06/2025
 * @author Rahim Ahmed
 */
@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest extends BaseTestConfiguration {
  @Autowired private AuthenticationService authenticationService;
  @Autowired private UserRepository userRepository;
  @MockitoBean private VerificationService verificationService;
  @MockitoBean private RoleService roleService;
  @Mock private HttpServletRequest request;

  private final Fixture fixture = new Fixture();
  private final String email = fixture.create(String.class).substring(0, 10) + "@example.com";
  private final String username = fixture.create(String.class).substring(0, 10);
  private final String password = fixture.create(String.class).substring(0, 10);
  private final String firstName = fixture.create(String.class).substring(0, 10);
  private final String lastName = fixture.create(String.class).substring(0, 10);
  private final String phoneNumber = fixture.create(String.class).substring(0, 11);

  @Test
  void shouldRegisterUserSuccessfully() {
    when(request.getHeader("Time-Zone")).thenReturn("UTC");
    when(request.getLocale()).thenReturn(Locale.ENGLISH);

    RegisterRequest registerRequest =
        new RegisterRequest(email, username, password, firstName, lastName, phoneNumber);

    RegisterResponse registerResponse = authenticationService.register(registerRequest, request);
    User createdUser =
        userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

    assertThat(registerResponse).isNotNull();
    assertThat(registerResponse.getStatus()).isEqualTo(ResponseStatus.PENDING);
    assertThat(registerResponse.getEmail()).isEqualTo(email);
    assertThat(registerResponse.getUsername()).isEqualTo(username);
    assertThat(registerResponse.getId()).isNotNull();

    assertThat(createdUser).isNotNull();
    assertThat(createdUser.getEmail()).isEqualTo(email);
    assertThat(createdUser.getUsername()).isEqualTo(username);
    assertThat(createdUser.isEmailVerified()).isFalse();
    assertThat(createdUser.isAccountLocked()).isTrue();
    assertThat(createdUser.getLocale()).isEqualTo("en");
    assertThat(createdUser.getTimezone()).isEqualTo("UTC");

    verify(verificationService).sendEmailVerification(createdUser);
    verify(roleService).assignRoleToUser(createdUser, Role.USER);
  }
}
