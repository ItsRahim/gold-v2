package com.rahim.authenticationservice.repository;

import com.rahim.authenticationservice.entity.AuditLog;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @created 08/06/2025
 * @author Rahim Ahmed
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {}
