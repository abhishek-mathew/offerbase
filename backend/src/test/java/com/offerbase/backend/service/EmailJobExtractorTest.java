package com.offerbase.backend.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmailJobExtractorTest {

    private final EmailJobExtractor extractor =
            new EmailJobExtractor();

    @Test
    void shouldExtractCompanyFromSenderName() {

        var result =
                extractor.extract(
                        "Software Engineering Intern - Application Update",
                        "Capital One Careers <careers@capitalone.com>",
                        "Thank you for your interest."
                );

        assertEquals(
                "Capital One",
                result.company()
        );
    }

    @Test
    void shouldExtractCompanyFromRecruitingSender() {

        var result =
                extractor.extract(
                        "Interview Invitation",
                        "Amazon Recruiting <recruiting@amazon.com>",
                        "We would like to schedule an interview."
                );

        assertEquals(
                "Amazon",
                result.company()
        );
    }

    @Test
    void shouldExtractCompanyFromDomainFallback() {

        var result =
                extractor.extract(
                        "Application received",
                        "no-reply@stripe.com",
                        "Thanks for applying."
                );

        assertEquals(
                "stripe",
                result.company()
        );
    }

    @Test
    void shouldExtractCompanyFromSubjectFallback() {

        var result =
                extractor.extract(
                        "Palantir - Interview Invitation",
                        "",
                        "We would like to speak with you."
                );

        assertEquals(
                "Palantir",
                result.company()
        );
    }

    @Test
    void shouldExtractEngineeringPosition() {

        var result =
                extractor.extract(
                        "Software Engineering Intern - Application Update",
                        "Capital One Careers <careers@capitalone.com>",
                        "We received your application."
                );

        assertEquals(
                "Software Engineering Intern",
                result.position()
        );
    }

    @Test
    void shouldExtractAnalystPosition() {

        var result =
                extractor.extract(
                        "Data Analyst | Interview Invitation",
                        "Acme Recruiting <recruiting@acme.com>",
                        "Please send us your availability."
                );

        assertEquals(
                "Data Analyst",
                result.position()
        );
    }

    @Test
    void shouldReturnBlankPositionWhenUnknown() {

        var result =
                extractor.extract(
                        "Application Update",
                        "Google Recruiting <recruiting@google.com>",
                        "We have an update regarding your application."
                );

        assertEquals(
                "",
                result.position()
        );
    }

    @Test
    void shouldRemoveTalentAcquisitionFromCompanyName() {

        var result =
                extractor.extract(
                        "Software Engineer - Next Steps",
                        "Microsoft Talent Acquisition <jobs@microsoft.com>",
                        "We would like to continue the process."
                );

        assertEquals(
                "Microsoft",
                result.company()
        );
    }
}