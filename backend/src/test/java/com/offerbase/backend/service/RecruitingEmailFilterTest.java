package com.offerbase.backend.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RecruitingEmailFilterTest {

    private final RecruitingEmailFilter filter =
            new RecruitingEmailFilter();

    @Test
    void shouldRejectParkingReceipt() {

        var result =
                filter.evaluate(
                        "Your Parking Receipt",
                        "donotreply@greenp.com",
                        "Thank you for purchasing your parking using the Green P application."
                );

        assertFalse(result.candidate());
        assertEquals(
                "NON_RECRUITING",
                result.reason()
        );
    }

    @Test
    void shouldRejectWellfoundJobRecommendations() {

        var result =
                filter.evaluate(
                        "New jobs: Software Engineer Intern at Paddox Technologies and 7 more jobs",
                        "Wellfound <team@hi.wellfound.com>",
                        "I've found 8 new jobs that might interest you. Ready to Interview Open to offers."
                );

        assertFalse(result.candidate());
        assertEquals(
                "JOB_MARKETING",
                result.reason()
        );
    }

    @Test
    void shouldRejectCareerNewsletter() {

        var result =
                filter.evaluate(
                        "Gallagher’s Global Impact & Career Insights",
                        "Gallagher Talent Acquisition <careers@ajg.com>",
                        "Thank you for being part of Gallagher Talent Community."
                );

        assertFalse(result.candidate());
        assertEquals(
                "JOB_MARKETING",
                result.reason()
        );
    }

    @Test
    void shouldRejectFafsaEmail() {

        var result =
                filter.evaluate(
                        "We Received Your 2026–27 FAFSA Form",
                        "U.S. Department of Education <donotreply@studentaid.gov>",
                        "Log in to your account to see your status and next steps."
                );

        assertFalse(result.candidate());
        assertEquals(
                "NON_RECRUITING",
                result.reason()
        );
    }

    @Test
    void shouldRejectGlassdoorCommunityPost() {

        var result =
                filter.evaluate(
                        "I countered a job offer today",
                        "Glassdoor Community <noreply@glassdoor.com>",
                        "Explore real talk from across Glassdoor community posts."
                );

        assertFalse(result.candidate());
        assertEquals(
                "NON_RECRUITING",
                result.reason()
        );
    }

    @Test
    void shouldAcceptApplicationConfirmation() {

        var result =
                filter.evaluate(
                        "Application received",
                        "Capital One Careers <careers@capitalone.com>",
                        "We have received your application for the Software Engineering Intern position."
                );

        assertTrue(result.candidate());
        assertEquals(
                "PERSONAL_RECRUITING",
                result.reason()
        );
    }

    @Test
    void shouldAcceptInterviewInvitation() {

        var result =
                filter.evaluate(
                        "Next steps",
                        "Microsoft Recruiting <recruiting@microsoft.com>",
                        "We would like to schedule an interview with our engineering team."
                );

        assertTrue(result.candidate());
        assertEquals(
                "PERSONAL_RECRUITING",
                result.reason()
        );
    }

    @Test
    void shouldAcceptOffer() {

        var result =
                filter.evaluate(
                        "Offer Letter",
                        "Stripe Recruiting <recruiting@stripe.com>",
                        "We are pleased to offer you the Software Engineer position."
                );

        assertTrue(result.candidate());
        assertEquals(
                "PERSONAL_RECRUITING",
                result.reason()
        );
    }

    @Test
    void shouldAcceptRejection() {

        var result =
                filter.evaluate(
                        "Application Update",
                        "Google Recruiting <recruiting@google.com>",
                        "Unfortunately, we have decided not to move forward with your application."
                );

        assertTrue(result.candidate());
        assertEquals(
                "PERSONAL_RECRUITING",
                result.reason()
        );
    }
}