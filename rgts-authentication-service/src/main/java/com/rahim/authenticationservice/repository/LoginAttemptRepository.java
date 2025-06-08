package com.rahim.authenticationservice.repository;

import com.rahim.authenticationservice.entity.LoginAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * @created 08/06/2025
 * @author Rahim Ahmed
 */
@Repository
public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, UUID> {}
