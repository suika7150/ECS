package com.gjun.ecs.dto.request;

import com.gjun.ecs.enums.OtpType;
import lombok.Data;

@Data
public class SendEmailCodeReq {
    private String email;
    private OtpType type;
}
