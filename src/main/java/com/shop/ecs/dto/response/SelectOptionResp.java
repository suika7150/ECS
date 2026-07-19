package com.shop.ecs.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SelectOptionResp {

  private String label;
  private String value;
  private Integer sortOrder;
}
