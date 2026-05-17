package com.gjun.ecs.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.gjun.ecs.repository.EmailOtpRepository;

import jakarta.validation.constraints.Email;

@Component
public class OtpCleanupScheduler {

    private final EmailOtpRepository emailOtpRepository;

    public OtpCleanupScheduler(EmailOtpRepository emailOtpRepository) {

        this.emailOtpRepository = emailOtpRepository;

    }

    @Scheduled(cron = "0 0 3 * * ?") // 每天凌晨3點
    public void cleanOtp() {
        emailOtpRepository.deleteExpiredOrUsed();
    }
}
