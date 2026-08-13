package com.offerbase.backend.service;

import com.offerbase.backend.dto.MLPredictionResponse;
import org.springframework.stereotype.Service;

@Service
public class HybridEmailClassifier {

    private final EmailClassifier ruleClassifier;
    private final MLClassifierService mlClassifierService;

    public HybridEmailClassifier(
            EmailClassifier ruleClassifier,
            MLClassifierService mlClassifierService
    ) {
        this.ruleClassifier = ruleClassifier;
        this.mlClassifierService = mlClassifierService;
    }

    public HybridClassificationResult classify(
            String subject,
            String sender,
            String body
    ) {

        EmailClassifier.ClassificationResult ruleResult =
                ruleClassifier.classify(subject, sender, body);

        MLPredictionResponse mlResult = null;

        try {
            mlResult = mlClassifierService.classify(
                    subject,
                    sender,
                    body
            );
        } catch (Exception ignored) {
            // Fall back to rule-based classification
            // if the ML service is unavailable.
        }

        if (mlResult == null) {
            return new HybridClassificationResult(
                    ruleResult.status(),
                    ruleResult.confidence(),
                    "RULES",
                    ruleResult.status(),
                    ruleResult.confidence(),
                    null,
                    null
            );
        }

        String ruleLabel = ruleResult.status();
        double ruleConfidence = ruleResult.confidence();

        String mlLabel = mlResult.label();
        double mlConfidence = mlResult.confidence();

        // Rules and ML agree
        if (ruleLabel.equals(mlLabel)) {

            double combinedConfidence = Math.min(
                    0.99,
                    (ruleConfidence + mlConfidence) / 2.0 + 0.08
            );

            return new HybridClassificationResult(
                    ruleLabel,
                    combinedConfidence,
                    "AGREEMENT",
                    ruleLabel,
                    ruleConfidence,
                    mlLabel,
                    mlConfidence
            );
        }

        // Rules are uncertain, ML is confident
        if (
                ruleLabel.equals("OTHER")
                        && ruleConfidence <= 0.55
                        && mlConfidence >= 0.75
        ) {
            return new HybridClassificationResult(
                    mlLabel,
                    mlConfidence,
                    "ML",
                    ruleLabel,
                    ruleConfidence,
                    mlLabel,
                    mlConfidence
            );
        }

        // Rules are very confident, ML is weak
        if (
                ruleConfidence >= 0.90
                        && mlConfidence < 0.65
        ) {
            return new HybridClassificationResult(
                    ruleLabel,
                    ruleConfidence,
                    "RULES",
                    ruleLabel,
                    ruleConfidence,
                    mlLabel,
                    mlConfidence
            );
        }

        // ML is extremely confident
        if (
                mlConfidence >= 0.90
                        && ruleConfidence < 0.85
        ) {
            return new HybridClassificationResult(
                    mlLabel,
                    mlConfidence,
                    "ML",
                    ruleLabel,
                    ruleConfidence,
                    mlLabel,
                    mlConfidence
            );
        }

        // Neither classifier clearly wins
        return new HybridClassificationResult(
                "OTHER",
                Math.max(ruleConfidence, mlConfidence),
                "REVIEW",
                ruleLabel,
                ruleConfidence,
                mlLabel,
                mlConfidence
        );
    }

    public record HybridClassificationResult(
            String status,
            double confidence,
            String source,
            String ruleStatus,
            double ruleConfidence,
            String mlStatus,
            Double mlConfidence
    ) {
    }
}