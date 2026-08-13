package com.offerbase.backend.dto;

import java.util.Map;

public record MLPredictionResponse(
        String label,
        double confidence,
        Map<String, Double> probabilities
) {
}