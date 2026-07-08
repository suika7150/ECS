package com.gjun.ecs.entity;

import java.time.LocalDateTime;

import com.gjun.ecs.enums.OtpType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "email_otp")
@Data
public class EmailOtp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String code;

    @Enumerated(EnumType.STRING)
    private OtpType type;

    private LocalDateTime expireTime;

    private Boolean used = false;

    private LocalDateTime createAt;

    @PrePersist
    public void prePersist() {
        this.createAt = LocalDateTime.now();
        if (this.used == null) {
            this.used = false;
        }
    }
}
