package com.offerbase.backend.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmailClassifierTest {

    private final EmailClassifier classifier =
            new EmailClassifier();

    @Test
    void shouldDetectApplicationConfirmation() {

        var result =
                classifier.classify(
                        "Thank you for applying",
                        "Capital One Careers <careers@capitalone.com>",
                        "We have received your application for the Software Engineering Intern position."
                );

        assertEquals(
                "APPLIED",
                result.status()
        );
    }

    @Test
    void shouldDetectInterview() {

        var result =
                classifier.classify(
                        "Next steps for your application",
                        "Amazon Recruiting <recruiting@amazon.com>",
                        "We would like to schedule an interview with our engineering team."
                );

        assertEquals(
                "INTERVIEW",
                result.status()
        );
    }

    @Test
    void shouldDetectTechnicalInterview() {

        var result =
                classifier.classify(
                        "Technical Interview Invitation",
                        "Microsoft University Recruiting <recruiting@microsoft.com>",
                        "Please select your availability for the technical interview."
                );

        assertEquals(
                "INTERVIEW",
                result.status()
        );
    }

    @Test
    void shouldDetectOffer() {

        var result =
                classifier.classify(
                        "Offer Letter",
                        "Stripe Recruiting <recruiting@stripe.com>",
                        "We are pleased to offer you the Software Engineer position."
                );

        assertEquals(
                "OFFER",
                result.status()
        );
    }

    @Test
    void shouldDetectRejection() {

        var result =
                classifier.classify(
                        "Application Update",
                        "Google Recruiting <recruiting@google.com>",
                        "Unfortunately, we have decided not to move forward with your application."
                );

        assertEquals(
                "REJECTED",
                result.status()
        );
    }

    @Test
    void shouldDetectAnotherRejectionFormat() {

        var result =
                classifier.classify(
                        "Regarding your application",
                        "Meta Careers <careers@meta.com>",
                        "After careful consideration, you were not selected for this role."
                );

        assertEquals(
                "REJECTED",
                result.status()
        );
    }

    @Test
    void shouldMarkGenericRecruitingEmailAsOther() {

        var result =
                classifier.classify(
                        "Explore careers at Palantir",
                        "Palantir Careers <careers@palantir.com>",
                        "Learn more about our open opportunities."
                );

        assertEquals(
                "OTHER",
                result.status()
        );
    }

    @Test
    void shouldIgnoreUnrelatedEmail() {

        var result =
                classifier.classify(
                        "Your Amazon order has shipped",
                        "Amazon <shipment-tracking@amazon.com>",
                        "Your package will arrive tomorrow."
                );

        assertEquals(
                "OTHER",
                result.status()
        );
    }
}