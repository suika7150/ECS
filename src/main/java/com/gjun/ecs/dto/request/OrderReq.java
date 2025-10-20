package com.gjun.ecs.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "新增訂單")
public class OrderReq {

    @Schema(description = "姓名")
    private String name;
    @Schema(description = "電話")
    private String phone;
    @Schema(description = "地址")
    private String address;
    @Schema(description = "運送方式")
    private String shippingMethod;
    @Schema(description = "備註")
    private String notes;
    @Schema(description = "付款方式")
    private String paymentMethod;
    @Schema(description = "總金額")
    private Integer total;
}
