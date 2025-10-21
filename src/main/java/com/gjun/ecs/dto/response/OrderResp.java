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
@Schema(description = "新增訂單")
public class OrderResp {

    @Schema(description = "訂單ID")
    private Long id;
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
    @Schema(description = "信用卡最後四碼")
    private String cardLast4;
    @Schema(description = "付款狀態")
    private String paymentStatus;
}
