package com.example.app.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class GeminiService {

    private final RestTemplate restTemplate;



    public GeminiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String summarize(String content) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=AIzaSyDtjmB_YLGEh_vcK93I7Dd3DJwYUI76ydg";

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", "Briefly explain what this text is about and insights:\n" + content)
                        ))
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        try {
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
            Map<String, Object> firstCandidate = candidates.get(0);
            Map<String, Object> contentMap = (Map<String, Object>) firstCandidate.get("content");
            List<Map<String, String>> parts = (List<Map<String, String>>) contentMap.get("parts");
            return parts.get(0).get("text");
        } catch (Exception e) {
            return "Failed to get summary: " + e.getMessage();
        }
    }
}
