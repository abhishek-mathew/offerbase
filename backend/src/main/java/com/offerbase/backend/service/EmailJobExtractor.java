package com.offerbase.backend.service;

import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class EmailJobExtractor {

    public ExtractionResult extract(
            String subject,
            String from,
            String snippet
    ) {

        String company =
                extractCompany(
                        subject,
                        from,
                        snippet
                );

        String position =
                extractPosition(
                        subject,
                        snippet
                );

        return new ExtractionResult(
                cleanCompany(company),
                cleanPosition(position)
        );
    }

    private String extractCompany(
            String subject,
            String from,
            String snippet
    ) {

        /*
         * Best source: explicit language in the email body.
         *
         * Example:
         * "Software Engineering Intern position at The Nuclear Company"
         */

        String combined =
                ((subject == null ? "" : subject)
                        + " "
                        + (snippet == null ? "" : snippet));

        Pattern atCompanyPattern =
                Pattern.compile(
                        "(?i)\\bposition\\s+at\\s+(.+?)(?:[\\.,]|\\s+we\\b|\\s+and\\b|$)"
                );

        Matcher atCompanyMatcher =
                atCompanyPattern.matcher(combined);

        if (atCompanyMatcher.find()) {
            return atCompanyMatcher
                    .group(1)
                    .trim();
        }


        /*
         * Subject examples:
         *
         * "Thank you for applying to The Nuclear Company"
         */

        if (subject != null) {

            Pattern applyingToPattern =
                    Pattern.compile(
                            "(?i)(?:applying|application)\\s+(?:to|at)\\s+(.+)$"
                    );

            Matcher matcher =
                    applyingToPattern.matcher(subject);

            if (matcher.find()) {
                return matcher
                        .group(1)
                        .trim();
            }
        }


        /*
         * Readable sender name.
         *
         * "Capital One Recruiting Team <...>"
         */

        if (
                from != null &&
                        from.contains("<")
        ) {

            String senderName =
                    from.substring(
                            0,
                            from.indexOf("<")
                    ).trim();

            senderName =
                    senderName.replace(
                            "\"",
                            ""
                    );

            senderName =
                    cleanCompany(senderName);

            if (!senderName.isBlank()) {
                return senderName;
            }
        }


        /*
         * Fall back to domain.
         */

        if (
                from != null &&
                        from.contains("@")
        ) {

            String emailPart = from;

            if (
                    from.contains("<") &&
                            from.contains(">")
            ) {

                emailPart =
                        from.substring(
                                from.indexOf("<") + 1,
                                from.indexOf(">")
                        );
            }

            int atIndex =
                    emailPart.indexOf("@");

            if (atIndex >= 0) {

                String domain =
                        emailPart.substring(
                                atIndex + 1
                        );

                String[] pieces =
                        domain.split("\\.");

                if (pieces.length > 0) {
                    return pieces[0];
                }
            }
        }


        /*
         * Last resort: beginning of subject.
         */

        if (
                subject != null &&
                        subject.contains(" - ")
        ) {

            return subject
                    .substring(
                            0,
                            subject.indexOf(" - ")
                    )
                    .trim();
        }

        return "";
    }

    private String extractPosition(
            String subject,
            String snippet
    ) {

        String combined =
                ((subject == null ? "" : subject)
                        + " "
                        + (snippet == null ? "" : snippet));

        /*
         * Example:
         *
         * "Thank you for your interest in the Summer 2027
         * Software Engineering Intern position at The Nuclear Company"
         */

        Pattern interestPattern =
                Pattern.compile(
                        "(?i)interest\\s+in\\s+(?:the\\s+)?(.+?)\\s+position\\s+at\\b"
                );

        Matcher interestMatcher =
                interestPattern.matcher(combined);

        if (interestMatcher.find()) {
            return interestMatcher
                    .group(1)
                    .trim();
        }


        /*
         * "applied for the Software Engineer position"
         */

        Pattern appliedForPattern =
                Pattern.compile(
                        "(?i)applied\\s+for\\s+(?:the\\s+)?(.+?)\\s+position\\b"
                );

        Matcher appliedForMatcher =
                appliedForPattern.matcher(combined);

        if (appliedForMatcher.find()) {
            return appliedForMatcher
                    .group(1)
                    .trim();
        }


        /*
         * "application for Software Engineer"
         */

        Pattern applicationForPattern =
                Pattern.compile(
                        "(?i)application\\s+for\\s+(?:the\\s+)?(.+?)(?:[\\.,]|$)"
                );

        Matcher applicationForMatcher =
                applicationForPattern.matcher(combined);

        if (applicationForMatcher.find()) {
            return applicationForMatcher
                    .group(1)
                    .trim();
        }


        /*
         * Existing subject fallback.
         */

        if (subject != null) {

            String[] separators = {
                    " - ",
                    " | ",
                    ": "
            };

            for (String separator : separators) {

                if (subject.contains(separator)) {

                    String[] parts =
                            subject.split(
                                    Pattern.quote(
                                            separator
                                    )
                            );

                    for (String part : parts) {

                        String lower =
                                part.toLowerCase();

                        if (
                                lower.contains("engineer") ||
                                        lower.contains("developer") ||
                                        lower.contains("analyst") ||
                                        lower.contains("intern") ||
                                        lower.contains("scientist") ||
                                        lower.contains("manager") ||
                                        lower.contains("associate")
                        ) {

                            return part.trim();
                        }
                    }
                }
            }
        }

        return "";
    }

    private String cleanCompany(
            String company
    ) {

        if (company == null) {
            return "";
        }

        return company
                .replaceAll(
                        "(?i)\\b(hiring|recruiting|recruitment|talent acquisition|talent|careers)\\s+team\\b",
                        ""
                )
                .replaceAll(
                        "(?i)\\b(talent acquisition|hiring|recruiting|recruitment|careers)\\b",
                        ""
                )
                .replaceAll(
                        "(?i)[-_]?team$",
                        ""
                )
                .replaceAll(
                        "\\s{2,}",
                        " "
                )
                .replaceAll(
                        "^[\\s\\-_|]+|[\\s\\-_|]+$",
                        ""
                )
                .trim();
    }

    private String cleanPosition(
            String position
    ) {

        if (position == null) {
            return "";
        }

        return position
                .replaceAll(
                        "(?i)^the\\s+",
                        ""
                )
                .replaceAll(
                        "\\s{2,}",
                        " "
                )
                .trim();
    }

    public record ExtractionResult(
            String company,
            String position
    ) {
    }
}