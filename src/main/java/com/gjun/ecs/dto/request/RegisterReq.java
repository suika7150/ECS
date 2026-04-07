package com.gjun.ecs.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "使用者註冊請求資料")
public class RegisterReq {

  @NotBlank
  @Schema(description = "使用者帳號", example = "john_doe")
  public String username;

  @Email
  @NotBlank
  @Schema(description = "電子郵件", example = "john@example.com")
  public String email;

  @NotBlank
  @Schema(description = "信箱驗證碼",example = "123456")
  public String smsCode;

  @NotBlank
  @Schema(description = "使用者密碼", example = "P@ssw0rd123")
  public String password;

  @NotBlank
  @Schema(description = "使用者姓名", example = "John Doe")
  public String fullName;
  
  @NotBlank
  @Schema(description = "性別", example = "M")
  public String gender;

  @NotBlank
  @Schema(description = "生日", example = "1990-01-01")
  public String birthday;

  @NotBlank
  @Schema(description = "手機號碼", example = "0912345678")
  public String phone;

  }
