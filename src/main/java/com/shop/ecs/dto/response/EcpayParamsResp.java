package com.shop.ecs.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EcpayParamsResp {

  @JsonProperty("MerchantID")
  private String merchantID;

  @JsonProperty("MerchantTradeNo")
  private String merchantTradeNo;

  @JsonProperty("MerchantTradeDate")
  private String merchantTradeDate;

  @JsonProperty("PaymentType")
  private String paymentType;

  @JsonProperty("TotalAmount")
  private String totalAmount;

  @JsonProperty("TradeDesc")
  private String tradeDesc;

  @JsonProperty("ItemName")
  private String itemName;

  @JsonProperty("ReturnURL")
  private String returnURL;

  @JsonProperty("ChoosePayment")
  private String choosePayment;

  @JsonProperty("EncryptType")
  private String encryptType;

  @JsonProperty("ClientBackURL")
  private String clientBackURL;

  @JsonProperty("CheckMacValue")
  private String checkMacValue;
}
