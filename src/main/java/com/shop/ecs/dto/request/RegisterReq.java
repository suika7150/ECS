package com.shop.ecs.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
  @Size(min = 6, max = 20, message = "帳號長度應在 6-20 個字之間")
  @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "帳號只能包含字母、數字和底線")
  @Schema(description = "使用者帳號", example = "elon_musk")
  private String username;

  @NotBlank
  @Email
  @Schema(description = "電子郵件", example = "elon@spacex.com")
  private String email;

  @NotBlank
  @Size(min = 6, max = 6, message = "驗證碼長度應為 6 位數")
  @Schema(description = "信箱驗證碼", example = "123456")
  private String emailCode;

  @NotBlank
  @Size(min = 6, message = "密碼至少需要 6 個字")
  @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d).+$", message = "密碼必須包含字母和數字")
  @Schema(description = "使用者密碼", example = "Mars2026Pass")
  private String password;

  @NotBlank
  @Size(min = 2, message = "姓名至少需要 2 個字")
  @Schema(description = "使用者姓名", example = "Elon Musk")
  private String fullName;

  @NotBlank
  @Schema(description = "性別", example = "M")
  private String gender;

  @NotBlank
  @Schema(description = "生日", example = "1990-01-01")
  private String birthday;

  @NotBlank
  @Pattern(regexp = "^09\\d{8}$", message = "請輸入有效的手機號碼")
  @Schema(description = "手機號碼", example = "0987654321")
  private String phone;
}
