package com.shop.ecs.dto.request;

import com.shop.ecs.constant.OtpTypeEnum;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "驗證信箱驗證碼請求")
public class VerifyEmailCodeReq {
    private String email;
    private String code;
    private OtpTypeEnum type;
}
