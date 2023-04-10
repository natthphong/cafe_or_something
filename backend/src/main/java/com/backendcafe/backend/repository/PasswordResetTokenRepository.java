package com.backendcafe.backend.repository;

import com.backendcafe.backend.entity.PasswordResetToken;
import com.backendcafe.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    PasswordResetToken findByToken(@Param("token") String token);
    PasswordResetToken findByUserId(@Param("user") User user);
}
