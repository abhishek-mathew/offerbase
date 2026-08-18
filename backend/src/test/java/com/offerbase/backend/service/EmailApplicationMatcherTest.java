package com.offerbase.backend.service;
import com.offerbase.backend.security.EmailApplicationMatcher;

import com.offerbase.backend.entity.JobApplication;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmailApplicationMatcherTest {

    private final EmailApplicationMatcher matcher =
            new EmailApplicationMatcher();

    @Test
    void shouldMatchExactCompanyAndPosition() {

        JobApplication application =
                new JobApplication();

        application.setCompany("Capital One");
        application.setPosition("Software Engineering Intern");

        var result =
                matcher.match(
                        "Capital One",
                        "Software Engineering Intern",
                        List.of(application)
                );

        assertTrue(result.matchFound());
        assertEquals(application, result.application());
    }

    @Test
    void shouldMatchCompanyWhenPositionIsMissing() {

        JobApplication application =
                new JobApplication();

        application.setCompany("Amazon");
        application.setPosition("Software Development Engineer");

        var result =
                matcher.match(
                        "Amazon",
                        "",
                        List.of(application)
                );

        assertTrue(result.matchFound());
        assertEquals(application, result.application());
    }

    @Test
    void shouldMatchMinorCompanyVariation() {

        JobApplication application =
                new JobApplication();

        application.setCompany("Microsoft");
        application.setPosition("Software Engineer");

        var result =
                matcher.match(
                        "Microsoft Recruiting",
                        "Software Engineer",
                        List.of(application)
                );

        assertTrue(result.matchFound());
    }

    @Test
    void shouldPreferBestMatchingApplication() {

        JobApplication first =
                new JobApplication();

        first.setCompany("Capital One");
        first.setPosition("Data Analyst");

        JobApplication second =
                new JobApplication();

        second.setCompany("Capital One");
        second.setPosition("Software Engineering Intern");

        var result =
                matcher.match(
                        "Capital One",
                        "Software Engineering Intern",
                        List.of(first, second)
                );

        assertTrue(result.matchFound());
        assertEquals(second, result.application());
    }

    @Test
    void shouldNotMatchDifferentCompany() {

        JobApplication application =
                new JobApplication();

        application.setCompany("Google");
        application.setPosition("Software Engineer");

        var result =
                matcher.match(
                        "Amazon",
                        "Software Engineer",
                        List.of(application)
                );

        assertFalse(result.matchFound());
    }

    @Test
    void shouldNotMatchEmptyCompany() {

        JobApplication application =
                new JobApplication();

        application.setCompany("Stripe");
        application.setPosition("Backend Engineer");

        var result =
                matcher.match(
                        "",
                        "Backend Engineer",
                        List.of(application)
                );

        assertFalse(result.matchFound());
    }

    @Test
    void shouldHandleNoApplications() {

        var result =
                matcher.match(
                        "Meta",
                        "Software Engineer",
                        List.of()
                );

        assertFalse(result.matchFound());
    }
}