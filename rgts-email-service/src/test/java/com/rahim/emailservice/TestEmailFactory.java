package com.rahim.emailservice;

import com.rahim.common.util.DateUtil;
import com.rahim.proto.protobuf.email.AccountVerificationData;
import com.rahim.proto.protobuf.email.EmailRequest;
import com.rahim.proto.protobuf.email.EmailTemplate;
import java.time.OffsetDateTime;

/**
 * @created 11/06/2025
 * @author Rahim Ahmed
 */
public class TestEmailFactory {
  public static EmailRequest createVerificationEmailRequest(
      String email,
      String firstName,
      String lastName,
      String username,
      String verificationCode,
      OffsetDateTime expirationTime) {
    AccountVerificationData.Builder verificationDataBuilder = AccountVerificationData.newBuilder();

    if (verificationCode != null) {
      verificationDataBuilder.setVerificationCode(verificationCode);
    }

    if (expirationTime != null) {
      verificationDataBuilder.setExpirationTime(DateUtil.formatOffsetDateTime(expirationTime));
    }

    AccountVerificationData accountVerificationData = verificationDataBuilder.build();

    EmailRequest.Builder emailRequestBuilder =
        EmailRequest.newBuilder()
            .setTemplate(EmailTemplate.VERIFICATION_REQUEST)
            .setVerificationData(accountVerificationData);

    if (email != null) {
      emailRequestBuilder.setRecipientEmail(email);
    }
    if (firstName != null) {
      emailRequestBuilder.setFirstName(firstName);
    }
    if (lastName != null) {
      emailRequestBuilder.setLastName(lastName);
    }
    if (username != null) {
      emailRequestBuilder.setUsername(username);
    }

    return emailRequestBuilder.build();
  }
}
