package com.shop.ecs.repository;

import com.shop.ecs.constant.OtpTypeEnum;
import com.shop.ecs.entity.EmailOtpEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EmailOtpRepository extends JpaRepository<EmailOtpEntity, Long> {

    @Query("""
        SELECT e FROM EmailOtpEntity e
        WHERE e.email = :email
        AND e.type = :type
        AND e.used = false
        AND e.expireTime > :now
        ORDER BY e.id DESC
        LIMIT 1
        """)
    Optional<EmailOtpEntity> findLatestValidOtp(
            String email,
            OtpTypeEnum type,
            LocalDateTime now);

    @Modifying
    @Transactional
    @Query("""
                DELETE FROM EmailOtpEntity e
                WHERE e.used = true
                   OR e.expireTime < CURRENT_TIMESTAMP
            """)
    void deleteExpiredOrUsed();
}
