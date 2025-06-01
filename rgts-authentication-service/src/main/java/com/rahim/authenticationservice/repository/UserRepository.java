package com.rahim.authenticationservice.repository;

import com.rahim.authenticationservice.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @created 01/06/2025
 * @author Rahim Ahmed
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);

  Optional<User> findByEmail(String email);

  Optional<User> findByVerificationCode(String verificationCode);

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);
}
