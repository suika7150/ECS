package com.gjun.ecs.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "更新使用者資料請求")
public class UpdateUserReq {
  @NotBlank
  @Schema(description = "使用者帳號", example = "John_doe")
  private String username;

  @NotBlank
  @Schema(description = "使用者姓名", example = "John Doe")
  private String fullName;

  @NotBlank
  @Schema(description = "電話號碼", example = "0912345678")
  private String phone;

  @Email
  @NotBlank
  @Schema(description = "電子郵件", example = "john@example.com")
  private String email;

  @NotNull
  @Schema(description = "性別", example = "M")
  private String gender;

  @NotNull
  @Schema(description = "生日", example = "1990-01-01")
  private String birthday;
}
