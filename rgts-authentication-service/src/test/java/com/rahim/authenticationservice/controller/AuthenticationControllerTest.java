package com.rahim.authenticationservice.controller;

import com.rahim.authenticationservice.BaseTestConfiguration;
import com.rahim.authenticationservice.constants.Endpoints;
import com.rahim.authenticationservice.dto.request.RegisterRequest;
import com.rahim.authenticationservice.dto.response.RegisterResponse;
import com.rahim.authenticationservice.repository.UserRepository;
import com.rahim.authenticationservice.service.authentication.IAuthenticationService;
import com.rahim.common.exception.ServiceException;
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
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @created 12/06/2025
 * @author Rahim Ahmed
 */
class AuthenticationControllerTest extends BaseTestConfiguration {
  private final IAuthenticationService mockAuthService = mock(IAuthenticationService.class);
  @Autowired private AuthenticationController authenticationController;

  private final AuthenticationController mockAuthController =
      new AuthenticationController(mockAuthService);

  @Autowired private IAuthenticationService authenticationService;
  @Autowired private UserRepository userRepository;
  private MockMvc mockMvc;
  private MockMvc localMockMvc;

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
    localMockMvc =
        MockMvcBuilders.standaloneSetup(mockAuthController)
            .setControllerAdvice(new ApiExceptionHandler())
            .setValidator(validator)
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

  @ParameterizedTest
  @MethodSource("validRegistrationRequestPayload")
  @DisplayName("Should throw conflict when user already exists")
  void should_throw_conflict_when_user_already_exists(
      String email,
      String username,
      String password,
      String firstName,
      String lastName,
      String phoneNumber)
      throws Exception {
    RegisterRequest existingUser =
        new RegisterRequest(email, username, password, firstName, lastName, phoneNumber);
    MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
    httpServletRequest.setAttribute("locale", "en");
    httpServletRequest.setAttribute("timezone", "UTC");
    authenticationService.register(existingUser, httpServletRequest);

    String responseBody =
        mockMvc
            .perform(
                post(Endpoints.REGISTER_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestToJson(existingUser)))
            .andExpect(status().isConflict())
            .andReturn()
            .getResponse()
            .getContentAsString();

    ErrorResponse errorResponse = responseBodyTo(responseBody, ErrorResponse.class);

    assertThat(errorResponse.message())
        .isEqualTo("User with username " + username + " already exists.");
    assertThat(errorResponse.status()).isEqualTo(HttpStatus.CONFLICT.value());
  }

  @ParameterizedTest
  @MethodSource("validRegistrationRequestPayload")
  @DisplayName("Should return internal server error when an unexpected error occurs")
  void should_return_internal_server_error_on_unexpected_exception(
      String email,
      String username,
      String password,
      String firstName,
      String lastName,
      String phoneNumber)
      throws Exception {

    when(mockAuthService.register(any(), any()))
        .thenThrow(new ServiceException("An unexpected error occurred."));

    RegisterRequest request =
        new RegisterRequest(email, username, password, firstName, lastName, phoneNumber);

    String responseBody =
        localMockMvc
            .perform(
                post(Endpoints.REGISTER_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestToJson(request)))
            .andExpect(status().isInternalServerError())
            .andReturn()
            .getResponse()
            .getContentAsString();

    ErrorResponse errorResponse = responseBodyTo(responseBody, ErrorResponse.class);
    assertThat(errorResponse.message()).isEqualTo("An unexpected error occurred.");
    assertThat(errorResponse.status()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
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
