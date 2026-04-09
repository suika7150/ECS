package com.gjun.ecs.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class EcpayParamsResp {
    
    @JsonProperty("MerchantID")
    private String merchantID;

    @JsonProperty("MerchantTradeNo")
    private String merchantTradeNo;

    @JsonProperty("RtnCode")
    private String rtnCode;    // 付款狀態 (0:未付, 1:已付, 2:失敗)
    
    @JsonProperty("RtnMsg")
    private String rtnMsg;

    @JsonProperty("TradeAmt")
    private Integer tradeAmt;

    @JsonProperty("PaymentDate")
    private String paymentDate;

    @JsonProperty("CheckMacValue")
    private String checkMacValue; // 校驗資料是否正確 
    
}
