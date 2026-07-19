package com.shop.ecs.repository;

import com.shop.ecs.entity.EmailOtpEntity;
import com.shop.ecs.enums.OtpType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EmailOtpRepository extends JpaRepository<EmailOtpEntity, Long> {

    Optional<EmailOtpEntity> findTopByEmailAndTypeAndUsedFalseAndExpireTimeAfterOrderByIdDesc(
            String email,
            OtpType type,
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
