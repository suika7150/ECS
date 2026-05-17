package com.gjun.ecs.repository;

import com.gjun.ecs.entity.EmailOtp;
import com.gjun.ecs.enums.OtpType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EmailOtpRepository extends JpaRepository<EmailOtp, Long> {

    Optional<EmailOtp> findTopByEmailAndTypeAndUsedFalseAndExpireTimeAfterOrderByIdDesc(
            String email,
            OtpType type,
            LocalDateTime now);

    @Modifying
    @Transactional
    @Query("""
                DELETE FROM EmailOtp e
                WHERE e.used = true
                   OR e.expireTime < CURRENT_TIMESTAMP
            """)
    void deleteExpiredOrUsed();
}
