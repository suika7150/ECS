package com.gjun.ecs.dto.request;

import com.gjun.ecs.enums.OtpType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "驗證信箱驗證碼請求")
public class VerifyEmailCodeReq {
    private String email;
    private String code;
    private OtpType type;
}
