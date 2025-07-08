package com.spaceoperators.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AIFormatterService {

    @Value("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")
    private String groqApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String formatQuestionWithGroqWithCustomPrompt(String question, String prompt) {
        String url = "https://api.groq.com/openai/v1/chat/completions";

        Map<String, Object> body = new HashMap<>();
        body.put("model", "llama-3.3-70b-versatile");
        body.put("messages", List.of(
                Map.of("role", "user", "content", question + "\n" + prompt)
        ));
        body.put("max_tokens", 64);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(groqApiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            var choices = (List<Map<String, Object>>) response.getBody().get("choices");
            if (choices != null && !choices.isEmpty()) {
                var message = (Map<String, Object>) choices.get(0).get("message");
                String content = (String) message.get("content");
                return content.trim();
            }
        }
        return null;
    }
}