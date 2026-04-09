package com.gjun.ecs.dto.response;

import lombok.Data;

@Data
public class EcpayParamsResp {
    
    private String MerchantID;
    private String MerchantTradeNo;
    private String RtnCode;    // 付款狀態 (0:未付, 1:已付, 2:失敗)
    private String RtnMsg;
    private Integer TradeAmt;
    private String PaymentDate;
    private String CheckMacValue; // 校驗資料是否正確 
    
}
