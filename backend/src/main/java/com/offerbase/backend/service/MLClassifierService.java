package com.offerbase.backend.service;

import com.offerbase.backend.dto.MLPredictionResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class MLClassifierService {

    private final WebClient webClient;

    public MLClassifierService() {
        this.webClient = WebClient.builder()
                .baseUrl("http://127.0.0.1:8001")
                .build();
    }

    public MLPredictionResponse classify(
            String subject,
            String sender,
            String body
    ) {

        Map<String, String> request = Map.of(
                "subject", subject == null ? "" : subject,
                "sender", sender == null ? "" : sender,
                "body", body == null ? "" : body
        );

        return webClient
                .post()
                .uri("/predict")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(MLPredictionResponse.class)
                .block();
    }
}