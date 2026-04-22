package com.gjun.ecs.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

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

    @Schema(description="優惠券代碼")
    private String couponCode;

    @Schema(description ="優惠券折扣金額" )
    private Integer discount;

    @Schema(description = "總金額")
    private Integer total;

    @Schema(description = "信用卡最後四碼")
    private String cardLast4;
    
    @Schema(description = "付款狀態")
    private String paymentStatus;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "訂單建立時間")
    private LocalDateTime createdAt;

    @Schema(description = "綠界金流專用參數包")
    private EcpayParamsResp ecpayParams;

    @Schema(description = "交易編號")
    private String merchantTradeNo;

    @Schema(description = "訂單商品清單")
    private List<OrderItemResp> items;
}
