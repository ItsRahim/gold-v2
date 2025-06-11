package com.rahim.authenticationservice.service.verification;

import com.rahim.authenticationservice.entity.VerificationCode;
import com.rahim.authenticationservice.repository.VerificationCodeRepository;
import com.rahim.common.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * @created 11/06/2025
 * @author Rahim Ahmed
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class VerificationCleanupService {
  private final VerificationCodeRepository verificationCodeRepository;

  private static final String CRON_EXPRESSION = "0 0/30 * * * *";
  private static final int RETENTION_DAYS = 7;

  @Scheduled(cron = CRON_EXPRESSION)
  public void cleanupExpiredVerificationCodes() {
    try {
      log.debug("Starting cleanup of expired verification codes");

      OffsetDateTime now = DateUtil.nowUtc();
      OffsetDateTime retentionCutoff = now.minusDays(RETENTION_DAYS);

      List<VerificationCode> expiredCodes =
          verificationCodeRepository.findCodesForCleanup(now, retentionCutoff);
      if (expiredCodes.isEmpty()) {
        log.debug("No expired verification codes found for cleanup");
        return;
      }

      for (VerificationCode verificationCode : expiredCodes) {
        verificationCodeRepository.deleteById(verificationCode.getId());
      }

      log.info("Deleted {} expired verification codes", expiredCodes.size());
    } catch (Exception e) {
      log.error("Error during verification code cleanup", e);
    }
  }
}
