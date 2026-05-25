package com.gjun.ecs.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "驗證登入驗證碼請求")
public class VerifyLoginCodeReq {

    @NotBlank
    @Schema(description = "帳號", example = "Admin12")
    private String username;

    @NotBlank
    @Schema(description = "登入驗證碼", example = "123456")
    private String code;

    @Schema(description = "保持登入", example = "true")
    private boolean rememberMe;
}
