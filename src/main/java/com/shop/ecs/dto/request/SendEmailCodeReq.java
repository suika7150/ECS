package com.shop.ecs.dto.request;

import com.shop.ecs.enums.OtpType;

import lombok.Data;

@Data
public class SendEmailCodeReq {
    private String email;
    private OtpType type;
}
