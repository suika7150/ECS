package com.shop.ecs.dto.request;

import com.shop.ecs.constant.OtpTypeEnum;

import lombok.Data;

@Data
public class SendEmailCodeReq {
    private String email;
    private OtpTypeEnum type;
}
