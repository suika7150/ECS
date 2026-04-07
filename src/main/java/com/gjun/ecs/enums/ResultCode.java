/**
 * 根據代碼字串（code）回傳對應的 ResultCode 枚舉物件
 * * @param code 前端或資料庫傳入的代碼字串，例如 "0000" 或 "0104"
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
  USER_NOT_FOUND("0103", "使用者不存在"),
  PASSWORD_NOT_MATCH("0104", "密碼不正確"),
  USER_IS_EXIST("0105", "使用者已存在"),
  USER_IS_NOT_EXIST("0106", "使用者不存在"),
  USER_IS_NOT_ACTIVE("0107", "使用者未啟用"),
  USER_IS_NOT_AUTHORIZED("0108", "使用者未授權"),
  USER_IS_NOT_AUTHENTICATED("0109", "使用者未驗證");

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
