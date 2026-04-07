/**
 * 
 * 根據代碼字串（code）回傳對應的 ResultCode 物件
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
  USER_NOT_FOUND("0103", "帳號不存在，請重新輸入"),
  PASSWORD_NOT_MATCH("0104", "帳號或密碼輸入錯誤"),
  USER_IS_EXIST("0105", "帳號已存在"),
  USER_IS_NOT_EXIST("0106", "帳號不存在，請重新輸入"),
  USER_IS_NOT_ACTIVE("0107", "帳號未啟用"),
  USER_IS_NOT_AUTHORIZED("0108", "帳號未授權"),
  USER_IS_NOT_AUTHENTICATED("0109", "帳號未驗證"),
  SMS_CODE_ERROR("0110","驗證碼錯誤或已過期");

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

/**
 * 若 ResultCode 增加到上百個，可考慮使用靜態 Map 來優化查找效率，避免每次都要迴圈遍歷 Enum 項目。
private static final Map<String, ResultCode> CODE_MAP = new HashMap<>();

static {
    for (ResultCode value : values()) {
        CODE_MAP.put(value.getCode(), value);
    }
}

public static ResultCode fromCode(String code) {
    ResultCode result = CODE_MAP.get(code);
    if (result == null) {
        throw new IllegalArgumentException("Unknown code: " + code);
    }
    return result;
}
*/

/**
 * Java 8+ 的寫法(Stream API)
public static ResultCode fromCode(String code) {
    return Arrays.stream(ResultCode.values())
           .filter(v -> v.getCode().equals(code))
           .findFirst()
           .orElseThrow(() -> new IllegalArgumentException("Unknown code: " + code));
}
 */