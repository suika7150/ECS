/**
 * 根據代碼字串（code）回傳對應的 ResultCode 物件
 * @return 對應的 ResultCode 枚舉項
 * @throws IllegalArgumentException 當傳入的代碼不在定義清單中時拋出異常
 */
package com.gjun.ecs.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {
  SUCCESS("0000", "success"),
  FAIL("9999", "FAIL"),
  UNAUTHORIZED("0401", "UNAUTHORIZED"),
  FORBIDDEN("0403", "FORBIDDEN"),
  NOT_FOUND("0404", "NOT_FOUND"),
  VALIDATION_ERROR("0422", "VALIDATION_ERROR"),
  SERVER_ERROR("0500", "SERVER_ERROR"),
  BAD_GATEWAY("0501", "BAD_GATEWAY"),
  GATEWAY_TIMEOUT("0502", "GATEWAY_TIMEOUT"),
  SERVICE_UNAVAILABLE("0503", "SERVICE_UNAVAILABLE"),
  INTERNAL_SERVER_ERROR("0504", "INTERNAL_SERVER_ERROR"),
  ACCOUNT_IS_EXIST("0101", "帳號已存在"),
  EMAIL_IS_EXIST("0102", "Email 已存在"),
  EMAIL_EMPTY("0103", "請輸入 Email"),
  USER_NOT_FOUND("0104", "帳號不存在，請重新輸入"),
  PASSWORD_NOT_MATCH("0105", "帳號或密碼輸入錯誤"),
  USER_IS_EXIST("0106", "帳號已存在"),
  USER_IS_NOT_EXIST("0107", "帳號不存在，請重新輸入"),
  USER_IS_NOT_ACTIVE("0108", "帳號未啟用"),
  USER_IS_NOT_AUTHORIZED("0109", "帳號未授權"),
  USER_IS_NOT_AUTHENTICATED("0110", "帳號未驗證"),
  USER_STATUS_ERROR("0111", "使用者狀態異常，請聯繫客服"),
  OTP_NOT_FOUND("0112", "驗證碼不存在"),
  OTP_EXPIRED("0113", "驗證碼已過期"),
  OTP_ALREADY_USED("0114", "驗證碼已使用"),
  OTP_INVALID("0115", "驗證碼錯誤");

  private final String code;
  private final String msg;

  public static ResultCode fromCode(String code) {
    // values() 是 Enum 內建方法，會回傳此 Enum 的所有項目（SUCCESS, FAIL等）
    for (ResultCode value : values()) {

      // 取得目前跑到的 Enum 項目的 code（例如 "0000"）
      // 並與傳入的參數 code 進行字串內容比對
      if (value.getCode().equals(code)) {

        // 如果比對成功，就回傳這個 Enum 實例，包含 code 與 msg
        return value;
      }
    }

    // 若沒有找到匹配的代碼，則拋出異常
    throw new IllegalArgumentException("Unknown code: " + code);
  }
}
