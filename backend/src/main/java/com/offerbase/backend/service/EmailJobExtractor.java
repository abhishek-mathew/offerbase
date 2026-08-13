package com.offerbase.backend.service;

import org.springframework.stereotype.Service;

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
                        from
                );

        String position =
                extractPosition(
                        subject,
                        snippet
                );

        return new ExtractionResult(
                company,
                position
        );
    }


    private String extractCompany(
            String subject,
            String from
    ) {

        /*
         * First try to get a readable sender name.
         *
         * Example:
         *
         * Capital One Careers <careers@capitalone.com>
         *
         * becomes:
         *
         * Capital One Careers
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
                    senderName
                            .replaceAll(
                                    "(?i)careers",
                                    ""
                            )
                            .replaceAll(
                                    "(?i)recruiting",
                                    ""
                            )
                            .replaceAll(
                                    "(?i)recruitment",
                                    ""
                            )
                            .replaceAll(
                                    "(?i)talent acquisition",
                                    ""
                            )
                            .trim();

            if (!senderName.isBlank()) {
                return senderName;
            }
        }


        /*
         * Fall back to the email domain.
         *
         * recruiting@capitalone.com
         *
         * becomes:
         *
         * capitalone
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
         * Last resort:
         * try the beginning of the subject.
         *
         * "Capital One - Interview Invitation"
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

        if (subject == null) {
            return "";
        }


        /*
         * Example:
         *
         * "Software Engineering Intern - Application Update"
         *
         * becomes:
         *
         * "Software Engineering Intern"
         */

        String[] separators = {
                " - ",
                " | ",
                ": "
        };


        for (String separator : separators) {

            if (subject.contains(separator)) {

                String[] parts =
                        subject.split(
                                java.util.regex.Pattern.quote(
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


        return "";
    }


    public record ExtractionResult(
            String company,
            String position
    ) {
    }
}