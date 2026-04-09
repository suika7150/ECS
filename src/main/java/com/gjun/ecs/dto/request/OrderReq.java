package com.gjun.ecs.dto.request;

import java.util.List;

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

    @Schema(description="優惠券代碼")
    private String couponCode;

    @Schema(description ="優惠券折扣金額", defaultValue = "0" )
    private Integer discount;

    @Schema(description = "總金額")
    private Integer total;

    @Schema(description = "信用卡最後四碼" , example = "1111")
    private String cardLast4;
    
    @Schema(description = "付款狀態")
    private String paymentStatus;

    @Schema(description = "訂單明細")
    private List<Item> items;

    @Data
    @Builder
    public static class Item {
        @Schema(description = "商品ID")
        private Integer productId;
        @Schema(description = "數量")
        private Integer quantity;
    }
}
