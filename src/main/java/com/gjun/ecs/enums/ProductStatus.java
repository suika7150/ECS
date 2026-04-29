package com.gjun.ecs.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductStatus {
  ON_SALE("ON_SALE", "銷售中"),
  OFF_SALE("OFF_SALE", "停售"),
  DELETED("DELETED", "刪除");

  private final String code;
  private final String desc;

  // 取得產品狀態描述
  public static String getDesc(String code) {
    for (ProductStatus productStatus : ProductStatus.values()) {
      if (productStatus.getCode().equals(code)) {
        return productStatus.getDesc();
      }
    }
    return code;
  }

}
