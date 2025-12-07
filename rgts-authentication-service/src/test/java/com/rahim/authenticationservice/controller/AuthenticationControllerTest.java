package com.rahim.authenticationservice.controller;

import com.rahim.authenticationservice.BaseTestConfiguration;
import com.rahim.authenticationservice.constants.Endpoints;
import com.rahim.authenticationservice.dto.request.RegisterRequest;
import com.rahim.authenticationservice.dto.response.RegisterResponse;
import com.rahim.authenticationservice.repository.UserRepository;
import com.rahim.common.handler.ApiExceptionHandler;
import com.rahim.common.response.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @created 12/06/2025
 * @author Rahim Ahmed
 */
class AuthenticationControllerTest extends BaseTestConfiguration {
  @Autowired private AuthenticationController authenticationController;
  @Autowired private UserRepository userRepository;
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
    validator.afterPropertiesSet();

    mockMvc =
        MockMvcBuilders.standaloneSetup(authenticationController)
            .setValidator(validator)
            .setControllerAdvice(new ApiExceptionHandler())
            .build();
  }

  @ParameterizedTest
  @MethodSource("validRegistrationRequestPayload")
  @DisplayName("Should create a new user for a valid request payload")
  void should_createNewUser(
      String email,
      String username,
      String password,
      String firstName,
      String lastName,
      String phoneNumber)
      throws Exception {

    RegisterRequest req =
        new RegisterRequest(email, username, password, firstName, lastName, phoneNumber);

    String responseBody =
        mockMvc
            .perform(
                post(Endpoints.REGISTER_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestToJson(req)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    RegisterResponse registerResponse = responseBodyTo(responseBody, RegisterResponse.class);
    boolean userExists = userRepository.existsByUsername(username);

    assertThat(registerResponse.getEmail()).isEqualTo(email);
    assertThat(registerResponse.getUsername()).isEqualTo(username);
    assertThat(userExists).isTrue();
  }

  @ParameterizedTest
  @MethodSource("invalidRegistrationRequestPayload")
  @DisplayName("Should throw bad request when request payload is not valid")
  void should_throw_bad_request_when_request_payload_is_not_valid(
      String email,
      String username,
      String password,
      String firstName,
      String lastName,
      String phoneNumber,
      String expectedResponseMessage)
      throws Exception {
    RegisterRequest request =
        new RegisterRequest(email, username, password, firstName, lastName, phoneNumber);

    String responseBody =
        mockMvc
            .perform(
                post(Endpoints.REGISTER_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestToJson(request)))
            .andExpect(status().isBadRequest())
            .andReturn()
            .getResponse()
            .getContentAsString();

    ErrorResponse errorResponse = responseBodyTo(responseBody, ErrorResponse.class);
    boolean userExists = userRepository.existsByUsername(username);

    assertThat(errorResponse.message()).isEqualTo(expectedResponseMessage);
    assertThat(errorResponse.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(userExists).isFalse();
  }

  private static Stream<Arguments> validRegistrationRequestPayload() {
    return Stream.of(
        Arguments.of("rahim1605@gmail.com", "rahmed", "password1", "Rahim", "Ahmed", "1234567890"),
        Arguments.of(
            "leo.bigglesworth@gmail.com",
            "MrLee123",
            "password1",
            "Leo",
            "Bigglesworth",
            "9876543210"));
  }

  private static Stream<Arguments> invalidRegistrationRequestPayload() {
    return Stream.of(
        Arguments.of(
            null, "rahmed", "password1", "Rahim", "Ahmed", "07123456789", "Email is required"),
        Arguments.of(
            "rahim1605@gmail.com",
            null,
            "password1",
            "Rahim",
            "Ahmed",
            "07123456789",
            "Username is required"),
        Arguments.of(
            "rahim1605@gmail.com",
            "rahmed",
            null,
            "Rahim",
            "Ahmed",
            "07123456789",
            "Password is required"),
        Arguments.of(
            "rahim1605@gmail.com",
            "rahmed",
            "password1",
            null,
            "Ahmed",
            "07123456789",
            "First name is required"),
        Arguments.of(
            "rahim1605@gmail.com",
            "rahmed",
            "password1",
            "Rahim",
            null,
            "07123456789",
            "Last name is required"),
        Arguments.of(
            "rahim1605@gmail.com",
            "rahmed",
            "password1",
            "Rahim",
            "Ahmed",
            null,
            "Phone number is required"));
  }
}
