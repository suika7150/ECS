/* 被定義在 OrderResp */
package com.gjun.ecs.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "訂單商品細項")
public class OrderItemResp {

    @Schema(description = "商品名稱")
    private String name;

    @Schema(description = "單價")
    private Integer price;

    @Schema(description = "數量")
    private Integer quantity;

    @Schema(description = "商品圖片路徑")
    private String productImage;
}