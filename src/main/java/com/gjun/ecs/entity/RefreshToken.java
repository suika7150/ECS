package com.gjun.ecs.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "refresh_token")
public class RefreshToken {
   
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Token 不可為空且必須唯一
    @Column(nullable = false, unique = true) 
    private String token;

    @Column(nullable = false)
    private LocalDateTime expiryDate;
    
    // 指定外鍵欄位名稱
    @OneToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id") 
    private UserInfo userInfo;

}
