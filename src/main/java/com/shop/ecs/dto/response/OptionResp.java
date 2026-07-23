package com.shop.ecs.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "新增選項資料")
public class OptionResp {

  @Schema(description = "選項ID")
  private Integer id;

  @Schema(description = "分類選項名稱")
  private String listName;

  @Schema(description = "選項名稱")
  private String name;

  @Schema(description = "選項值")
  private String value;

  @Schema(description = "排序")
  private Integer sortOrder;

  @Schema(description = "是否啟用")
  private Boolean isActive;

  @Schema(description = "描述")
  private String description;
}
