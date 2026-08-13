package com.offerbase.backend.service;

import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class EmailClassifier {

    public ClassificationResult classify(
            String subject,
            String from,
            String snippet
    ) {

        String text = (
                subject + " " +
                        from + " " +
                        snippet
        ).toLowerCase(Locale.ROOT);


        // OFFER
        if (
                containsAny(
                        text,
                        "offer letter",
                        "pleased to offer",
                        "happy to offer",
                        "extend an offer",
                        "employment offer",
                        "job offer"
                )
        ) {
            return new ClassificationResult(
                    "OFFER",
                    0.95
            );
        }


        // REJECTION
        if (
                containsAny(
                        text,
                        "unfortunately",
                        "not moving forward",
                        "will not be moving forward",
                        "decided not to move forward",
                        "other candidates",
                        "not selected",
                        "unable to offer",
                        "we regret to inform"
                )
        ) {
            return new ClassificationResult(
                    "REJECTED",
                    0.92
            );
        }


        // INTERVIEW
        if (
                containsAny(
                        text,
                        "interview",
                        "schedule a call",
                        "schedule a conversation",
                        "phone screen",
                        "technical screen",
                        "technical interview",
                        "next round",
                        "next step",
                        "meet with",
                        "availability"
                )
        ) {
            return new ClassificationResult(
                    "INTERVIEW",
                    0.90
            );
        }


        // APPLICATION CONFIRMATION
        if (
                containsAny(
                        text,
                        "thank you for applying",
                        "thanks for applying",
                        "thank you for your application",
                        "application received",
                        "received your application",
                        "application has been received",
                        "application confirmation",
                        "thank you for your interest"
                )
        ) {
            return new ClassificationResult(
                    "APPLIED",
                    0.88
            );
        }


        // Recruiting-ish, but uncertain
        if (
                containsAny(
                        text,
                        "recruiter",
                        "recruiting",
                        "career",
                        "careers",
                        "candidate",
                        "application"
                )
        ) {
            return new ClassificationResult(
                    "OTHER",
                    0.55
            );
        }


        return new ClassificationResult(
                "OTHER",
                0.10
        );
    }


    private boolean containsAny(
            String text,
            String... phrases
    ) {

        for (String phrase : phrases) {

            if (text.contains(phrase)) {
                return true;
            }
        }

        return false;
    }


    public record ClassificationResult(
            String status,
            double confidence
    ) {
    }
}