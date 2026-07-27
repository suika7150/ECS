package com.shop.ecs.dto.request;

import com.shop.ecs.constant.OtpTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SendEmailCodeReq {

    @NotBlank
    private String email;

    @NotNull
    private OtpTypeEnum type;
}
