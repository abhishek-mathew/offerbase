package com.offerbase.backend.service;

import com.offerbase.backend.entity.JobApplication;
import com.offerbase.backend.entity.ApplicationStatus;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmailPipelineTest {

    private final EmailClassifier classifier =
            new EmailClassifier();

    private final EmailJobExtractor extractor =
            new EmailJobExtractor();

    private final EmailApplicationMatcher matcher =
            new EmailApplicationMatcher();

    @Test
    void shouldSuggestInterviewForMatchingApplication() {

        JobApplication application =
                new JobApplication();

        application.setCompany("Capital One");
        application.setPosition("Software Engineering Intern");
        application.setStatus(ApplicationStatus.APPLIED);

        String subject =
                "Software Engineering Intern - Interview Invitation";

        String from =
                "Capital One Recruiting <recruiting@capitalone.com>";

        String snippet =
                "We would like to schedule an interview with you.";

        var classification =
                classifier.classify(
                        subject,
                        from,
                        snippet
                );

        var extraction =
                extractor.extract(
                        subject,
                        from,
                        snippet
                );

        var match =
                matcher.match(
                        extraction.company(),
                        extraction.position(),
                        List.of(application)
                );

        assertEquals(
                "INTERVIEW",
                classification.status()
        );

        assertEquals(
                "Capital One",
                extraction.company()
        );

        assertEquals(
                "Software Engineering Intern",
                extraction.position()
        );

        assertTrue(
                match.matchFound()
        );

        assertEquals(
                application,
                match.application()
        );
    }

    @Test
    void shouldSuggestRejectionForMatchingApplication() {

        JobApplication application =
                new JobApplication();

        application.setCompany("Google");
        application.setPosition("Software Engineer");
        application.setStatus(ApplicationStatus.APPLIED);

        String subject =
                "Software Engineer - Application Update";

        String from =
                "Google Recruiting <recruiting@google.com>";

        String snippet =
                "Unfortunately, we have decided not to move forward with your application.";

        var classification =
                classifier.classify(
                        subject,
                        from,
                        snippet
                );

        var extraction =
                extractor.extract(
                        subject,
                        from,
                        snippet
                );

        var match =
                matcher.match(
                        extraction.company(),
                        extraction.position(),
                        List.of(application)
                );

        assertEquals(
                "REJECTED",
                classification.status()
        );

        assertTrue(
                match.matchFound()
        );
    }

    @Test
    void shouldNotMatchUnrelatedApplication() {

        JobApplication application =
                new JobApplication();

        application.setCompany("Microsoft");
        application.setPosition("Software Engineer");
        application.setStatus(ApplicationStatus.APPLIED);

        String subject =
                "Data Analyst - Interview Invitation";

        String from =
                "Capital One Recruiting <recruiting@capitalone.com>";

        String snippet =
                "We would like to schedule an interview.";

        var classification =
                classifier.classify(
                        subject,
                        from,
                        snippet
                );

        var extraction =
                extractor.extract(
                        subject,
                        from,
                        snippet
                );

        var match =
                matcher.match(
                        extraction.company(),
                        extraction.position(),
                        List.of(application)
                );

        assertEquals(
                "INTERVIEW",
                classification.status()
        );

        assertFalse(
                match.matchFound()
        );
    }

    @Test
    void shouldHandleMissingPositionButMatchingCompany() {

        JobApplication application =
                new JobApplication();

        application.setCompany("Amazon");
        application.setPosition("Software Development Engineer");
        application.setStatus(ApplicationStatus.APPLIED);

        String subject =
                "Next steps for your application";

        String from =
                "Amazon Recruiting <recruiting@amazon.com>";

        String snippet =
                "We would like to schedule a conversation with you.";

        var classification =
                classifier.classify(
                        subject,
                        from,
                        snippet
                );

        var extraction =
                extractor.extract(
                        subject,
                        from,
                        snippet
                );

        var match =
                matcher.match(
                        extraction.company(),
                        extraction.position(),
                        List.of(application)
                );

        assertEquals(
                "INTERVIEW",
                classification.status()
        );

        assertTrue(
                match.matchFound()
        );
    }
}