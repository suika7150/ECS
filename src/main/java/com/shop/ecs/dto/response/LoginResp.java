package com.shop.ecs.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResp {

  @Schema(description ="身分驗證身份標記")
  private String token;

  @Schema(description ="使用者權限角色", example = "USER")
  private String role;

  @Schema(description ="使用者登入帳號", example = "john_doe")
  private String username;

  @Schema(description ="使用者顯示名稱 (姓名)", example = "John Doe")
  private String fullName;

  @Schema(description ="保持登入")
  private boolean rememberMe;
}
