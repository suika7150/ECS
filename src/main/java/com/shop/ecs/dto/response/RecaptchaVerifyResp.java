package com.shop.ecs.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RecaptchaVerifyResp {

    @JsonProperty("tokenProperties")
    private TokenProperties tokenProperties;

    @JsonProperty("riskAnalysis")
    private RiskAnalysis riskAnalysis;


    @Data
    public static class TokenProperties {

        private boolean valid;

        private String action;
    }


    @Data
    public static class RiskAnalysis {

        private float score;
    }
}