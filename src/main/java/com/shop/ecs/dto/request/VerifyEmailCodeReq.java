package com.shop.ecs.dto.request;

import com.shop.ecs.constant.OtpTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "驗證信箱驗證碼請求")
public class VerifyEmailCodeReq {

    @NotBlank
    @Schema(description = "電子郵件", example = "john@example.com")
    private String email;

    @NotBlank
    @Schema(description = "驗證碼", example = "123456")
    private String code;

    @NotNull
    @Schema(description = "驗證碼類型", example = "REGISTER")
    private OtpTypeEnum type;
}
