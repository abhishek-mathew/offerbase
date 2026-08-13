package com.offerbase.backend.service;

import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class RecruitingEmailFilter {

    public FilterResult evaluate(
            String subject,
            String sender,
            String body
    ) {

        String subjectText =
                normalize(subject);

        String senderText =
                normalize(sender);

        String bodyText =
                normalize(body);

        String allText =
                subjectText
                        + " "
                        + senderText
                        + " "
                        + bodyText;


        /*
         * Strong signs that this is NOT a personal
         * application-process email.
         */
        if (
                containsAny(
                        allText,
                        "weekly job recommendations",
                        "new jobs that might interest you",
                        "jobs you may like",
                        "job alert",
                        "new job alert",
                        "explore careers",
                        "talent community",
                        "career insights",
                        "career fair",
                        "open opportunities",
                        "new engineering openings"
                )
        ) {
            return new FilterResult(
                    false,
                    "JOB_MARKETING"
            );
        }


        /*
         * Known non-recruiting contexts that can contain
         * dangerous words such as application,
         * offer, status, or next steps.
         */
        if (
                containsAny(
                        allText,
                        "parking receipt",
                        "package has shipped",
                        "password reset",
                        "oauth application",
                        "fafsa",
                        "student aid",
                        "community post"
                )
        ) {
            return new FilterResult(
                    false,
                    "NON_RECRUITING"
            );
        }


        /*
         * Strong evidence that the message concerns
         * the user's own hiring process.
         */
        if (
                containsAny(
                        allText,
                        "your application",
                        "your candidacy",
                        "your interview",
                        "your submission",
                        "your candidate profile",
                        "thank you for applying",
                        "we received your application",
                        "we have received your application",
                        "schedule an interview",
                        "schedule a phone screen",
                        "your availability",
                        "hiring manager would like",
                        "pleased to offer you",
                        "extend an offer",
                        "offer you the",
                        "not moving forward with your",
                        "not be moving forward with your",
                        "not selected for",
                        "closing your candidacy"
                )
        ) {
            return new FilterResult(
                    true,
                    "PERSONAL_RECRUITING"
            );
        }


        /*
         * Possibly recruiting-related.
         *
         * Allow the hybrid classifier to evaluate it,
         * but we have not positively identified it yet.
         */
        return new FilterResult(
                true,
                "UNCERTAIN"
        );
    }


    private String normalize(
            String value
    ) {

        if (value == null) {
            return "";
        }

        return value
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ")
                .trim();
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


    public record FilterResult(
            boolean candidate,
            String reason
    ) {
    }
}