package com.offerbase.backend.dto;

public record EmailFeedbackRequest(
        String gmailMessageId,
        String subject,
        String sender,
        String body,
        String predictedLabel,
        String actualLabel,
        double confidence
) {
}