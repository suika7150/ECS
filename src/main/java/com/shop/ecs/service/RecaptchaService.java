package com.shop.ecs.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.shop.ecs.dto.response.RecaptchaVerifyResp;

@Service
public class RecaptchaService {

    @Value("${google.recaptcha.project-id}")
    private String projectId;

    @Value("${google.recaptcha.api-key}")
    private String apiKey;

    @Value("${google.recaptcha.site-key}")
    private String siteKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public boolean verifyToken(String token) {

        if (token == null || token.trim().isEmpty()) {
            return false;
        }

        try {

            String verifyUrl =
                "https://recaptchaenterprise.googleapis.com/v1/projects/"
                + projectId
                + "/assessments?key="
                + apiKey;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> event = new HashMap<>();
            event.put("token", token);
            event.put("siteKey", siteKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("event", event);

            HttpEntity<Map<String, Object>> request =
                    new HttpEntity<>(requestBody, headers);

            RecaptchaVerifyResp response = 
            restTemplate.postForObject(
            verifyUrl,
            request,
            RecaptchaVerifyResp.class
            );

            System.out.println("reCAPTCHA Google response: " + response);

            if (response == null) {
            // 驗證成功狀態與分數
            return false;
            }

            return response.getTokenProperties().isValid()
                    && response.getRiskAnalysis().getScore() >= 0.5;

        } catch (Exception e) {

            System.err.println("reCAPTCHA Enterprise 驗證錯誤: " + e.getMessage());
        }

        return false;
    }
}
