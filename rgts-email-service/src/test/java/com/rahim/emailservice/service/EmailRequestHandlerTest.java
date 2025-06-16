package com.rahim.emailservice.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.github.nylle.javafixture.Fixture;
import com.rahim.common.util.DateUtil;
import com.rahim.emailservice.TestEmailFactory;
import com.rahim.emailservice.dto.EmailVerificationData;
import com.rahim.emailservice.exception.EmailProcessingException;
import com.rahim.emailservice.service.impl.EmailRequestHandler;
import com.rahim.proto.protobuf.email.EmailRequest;
import com.rahim.proto.protobuf.email.EmailTemplate;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * @created 11/06/2025
 * @author Rahim Ahmed
 */
@ExtendWith(MockitoExtension.class)
public class EmailRequestHandlerTest {

  @InjectMocks private EmailRequestHandler emailRequestHandler;

  @Mock private IEmailSenderService emailSenderService;

  @Mock private IEmailTemplateService emailTemplateService;

  private final Fixture fixture = new Fixture();

  private EmailRequest generateRandomEmailRequest() {
    String email = fixture.create(String.class) + "@gmail.com";
    String firstName = fixture.create(String.class);
    String lastName = fixture.create(String.class);
    String username = fixture.create(String.class);
    String rawVerificationCode = fixture.create(String.class);
    String hashedVerificationCode = fixture.create(String.class);
    OffsetDateTime dateTime = fixture.create(OffsetDateTime.class);

    return TestEmailFactory.createVerificationEmailRequest(
        email,
        firstName,
        lastName,
        username,
        rawVerificationCode,
        hashedVerificationCode,
        dateTime);
  }

  @Test
  void shouldPassCorrectDataToEmailTemplateService() {
    EmailRequest emailRequest = generateRandomEmailRequest();
    String expectedEmailContent = "Generated email content";
    ArgumentCaptor<EmailVerificationData> captor =
        ArgumentCaptor.forClass(EmailVerificationData.class);

    when(emailTemplateService.generateVerificationEmail(any(EmailVerificationData.class)))
        .thenReturn(expectedEmailContent);

    emailRequestHandler.handleEmailRequest(emailRequest);

    verify(emailTemplateService).generateVerificationEmail(captor.capture());

    EmailVerificationData capturedData = captor.getValue();
    assertEquals(emailRequest.getFirstName(), capturedData.getFirstName());
    assertEquals(emailRequest.getLastName(), capturedData.getLastName());
    assertEquals(emailRequest.getUsername(), capturedData.getUsername());
    assertEquals(
        emailRequest.getVerificationData().getVerificationCode(),
        capturedData.getVerificationCode());
    assertEquals(
        emailRequest.getVerificationData().getVerificationId(), capturedData.getVerificationId());
    assertEquals(
        emailRequest.getVerificationData().getExpirationTime(), capturedData.getExpirationTime());
  }

  @Test
  void shouldThrowExceptionWhenEmailRequestIsNull() {
    EmailProcessingException exception =
        assertThrows(
            EmailProcessingException.class, () -> emailRequestHandler.handleEmailRequest(null));

    assertEquals("Email request cannot be null", exception.getMessage());

    verifyNoInteractions(emailTemplateService);
    verifyNoInteractions(emailSenderService);
  }

  @Test
  void shouldThrowExceptionForUnknownEmailTemplate() {
    EmailRequest emailRequest =
        EmailRequest.newBuilder().setTemplate(EmailTemplate.UNKNOWN).build();

    EmailProcessingException exception =
        assertThrows(
            EmailProcessingException.class,
            () -> emailRequestHandler.handleEmailRequest(emailRequest));

    assertTrue(exception.getMessage().contains("Unknown email request type"));

    verifyNoInteractions(emailTemplateService);
    verifyNoInteractions(emailSenderService);
  }

  @Test
  void shouldThrowExceptionWhenVerificationCodeIsBlank() {
    EmailRequest emailRequest =
        TestEmailFactory.createVerificationEmailRequest(
            "test@gmail.com", "John", "Doe", "johndoe", "", "abdefg", DateUtil.nowUtc());

    assertThatThrownBy(() -> emailRequestHandler.handleEmailRequest(emailRequest))
        .isInstanceOf(EmailProcessingException.class)
        .hasMessage("Missing required fields in email request: verificationCode");

    verifyNoInteractions(emailTemplateService);
    verifyNoInteractions(emailSenderService);
  }

  @Test
  void shouldThrowExceptionWhenVerificationIdIsBlank() {
    EmailRequest emailRequest =
        TestEmailFactory.createVerificationEmailRequest(
            "test@gmail.com", "John", "Doe", "johndoe", "abc", "", DateUtil.nowUtc());

    assertThatThrownBy(() -> emailRequestHandler.handleEmailRequest(emailRequest))
        .isInstanceOf(EmailProcessingException.class)
        .hasMessage("Missing required fields in email request: verificationId");

    verifyNoInteractions(emailTemplateService);
    verifyNoInteractions(emailSenderService);
  }

  @Test
  void shouldThrowExceptionWhenExpirationTimeIsBlank() {
    EmailRequest emailRequest =
        TestEmailFactory.createVerificationEmailRequest(
            "test@gmail.com", "John", "Doe", "johndoe", "123456", "78910", null);

    assertThatThrownBy(() -> emailRequestHandler.handleEmailRequest(emailRequest))
        .isInstanceOf(EmailProcessingException.class)
        .hasMessage("Missing required fields in email request: expirationTime");

    verifyNoInteractions(emailTemplateService);
    verifyNoInteractions(emailSenderService);
  }

  @Test
  void shouldThrowExceptionWhenFirstNameIsBlank() {
    EmailRequest emailRequest =
        TestEmailFactory.createVerificationEmailRequest(
            "test@gmail.com", null, "Doe", "johndoe", "123456", "78910", DateUtil.nowUtc());

    assertThatThrownBy(() -> emailRequestHandler.handleEmailRequest(emailRequest))
        .isInstanceOf(EmailProcessingException.class)
        .hasMessage("Missing required fields in email request: firstName");

    verifyNoInteractions(emailTemplateService);
    verifyNoInteractions(emailSenderService);
  }

  @Test
  void shouldThrowExceptionWhenLastNameIsBlank() {
    EmailRequest emailRequest =
        TestEmailFactory.createVerificationEmailRequest(
            "test@gmail.com", "John", "", "johndoe", "123456", "78910", DateUtil.nowUtc());

    assertThatThrownBy(() -> emailRequestHandler.handleEmailRequest(emailRequest))
        .isInstanceOf(EmailProcessingException.class)
        .hasMessage("Missing required fields in email request: lastName");

    verifyNoInteractions(emailTemplateService);
    verifyNoInteractions(emailSenderService);
  }

  @Test
  void shouldThrowExceptionWhenUsernameIsBlank() {
    EmailRequest emailRequest =
        TestEmailFactory.createVerificationEmailRequest(
            "test@gmail.com", "John", "Doe", null, "123456", "78910", DateUtil.nowUtc());

    assertThatThrownBy(() -> emailRequestHandler.handleEmailRequest(emailRequest))
        .isInstanceOf(EmailProcessingException.class)
        .hasMessage("Missing required fields in email request: username");

    verifyNoInteractions(emailTemplateService);
    verifyNoInteractions(emailSenderService);
  }

  @Test
  void shouldThrowExceptionWhenRecipientEmailIsBlank() {
    EmailRequest emailRequest =
        TestEmailFactory.createVerificationEmailRequest(
            null, "John", "Doe", "johndoe", "123456", "78910", DateUtil.nowUtc());

    assertThatThrownBy(() -> emailRequestHandler.handleEmailRequest(emailRequest))
        .isInstanceOf(EmailProcessingException.class)
        .hasMessage("Missing required fields in email request: recipientEmail");

    verifyNoInteractions(emailTemplateService);
    verifyNoInteractions(emailSenderService);
  }

  @Test
  void shouldHandleEmailRequestWithVerificationDataMissing() {
    EmailRequest emailRequest =
        EmailRequest.newBuilder()
            .setTemplate(EmailTemplate.VERIFICATION_REQUEST)
            .setRecipientEmail("test@gmail.com")
            .setFirstName("John")
            .setLastName("Doe")
            .setUsername("johndoe")
            .build();

    assertThatThrownBy(() -> emailRequestHandler.handleEmailRequest(emailRequest))
        .isInstanceOf(EmailProcessingException.class);

    verifyNoInteractions(emailTemplateService);
    verifyNoInteractions(emailSenderService);
  }

  @Test
  void shouldHandleExceptionFromEmailSenderServiceGracefully() {
    EmailRequest emailRequest = generateRandomEmailRequest();
    String expectedEmailContent = "Generated email content";

    when(emailTemplateService.generateVerificationEmail(any(EmailVerificationData.class)))
        .thenReturn(expectedEmailContent);
    doThrow(new RuntimeException("Email sending failed"))
        .when(emailSenderService)
        .sendEmail(anyString(), any(), anyString());

    assertThatThrownBy(() -> emailRequestHandler.handleEmailRequest(emailRequest))
        .isInstanceOf(EmailProcessingException.class)
        .hasMessageContaining("Unexpected error during email processing");

    verify(emailTemplateService).generateVerificationEmail(any(EmailVerificationData.class));
    verify(emailSenderService).sendEmail(anyString(), any(), anyString());
  }
}
