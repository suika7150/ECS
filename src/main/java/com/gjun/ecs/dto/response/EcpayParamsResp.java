package com.gjun.ecs.dto.response;

import lombok.Data;

@Data
public class EcpayParamsResp {

  private String MerchantID;
  private String MerchantTradeNo;
  private String MerchantTradeDate;
  private String PaymentType;
  private String TotalAmount;
  private String TradeDesc;
  private String ItemName;
  private String ReturnURL;
  private String ChoosePayment;
  private String EncryptType;
  private String ClientBackURL;
  private String CheckMacValue;
}
