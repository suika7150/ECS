package com.shop.ecs.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.shop.ecs.dto.response.RecaptchaVerifyResp;

@Service
public class RecaptchaService {

    @Value("${google.recaptcha.secret-key}")
    private String recaptchaSecret;

    private static final String GOOGLE_RECAPTCHA_VERIFY_URL = 
    "https://www.google.com/recaptcha/api/siteverify";

    private final RestTemplate restTemplate = new RestTemplate();

    public boolean verifyToken(String token) {

        if (token == null || token.trim().isEmpty()) {
            return false;
        }

        try {

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("secret", recaptchaSecret);
            body.add("response", token);

            HttpEntity<MultiValueMap<String, String>> request =
                    new HttpEntity<>(body, headers);

            RecaptchaVerifyResp response = restTemplate.postForObject(
            GOOGLE_RECAPTCHA_VERIFY_URL,
            request,
            RecaptchaVerifyResp.class
            );

            System.out.println("reCAPTCHA Google response: " + response);

            if (response != null) {
            System.out.println("reCAPTCHA Google response: " + response);
            // 驗證成功狀態與分數
            return response.isSuccess() && response.getScore() >= 0.5;
            }
        } catch (Exception e) {

            System.err.println("reCAPTCHA 驗證發生異常: " + e.getMessage());
        }

        return false;
    }
}
