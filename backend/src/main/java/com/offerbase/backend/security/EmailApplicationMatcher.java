package com.offerbase.backend.service;

import com.offerbase.backend.entity.JobApplication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class EmailApplicationMatcher {

    public MatchResult match(
            String extractedCompany,
            String extractedPosition,
            List<JobApplication> applications
    ) {

        if (
                extractedCompany == null ||
                        extractedCompany.isBlank()
        ) {
            return MatchResult.noMatch();
        }

        String normalizedCompany =
                normalize(extractedCompany);

        String normalizedPosition =
                normalize(extractedPosition);

        JobApplication bestMatch = null;
        double bestScore = 0.0;

        for (JobApplication application : applications) {

            double companyScore =
                    similarity(
                            normalizedCompany,
                            normalize(
                                    application.getCompany()
                            )
                    );

            double positionScore = 0.0;

            if (
                    !normalizedPosition.isBlank() &&
                            application.getPosition() != null
            ) {
                positionScore =
                        similarity(
                                normalizedPosition,
                                normalize(
                                        application.getPosition()
                                )
                        );
            }

            double totalScore =
                    (companyScore * 0.75)
                            +
                            (positionScore * 0.25);

            if (totalScore > bestScore) {
                bestScore = totalScore;
                bestMatch = application;
            }
        }

        if (
                bestMatch == null ||
                        bestScore < 0.55
        ) {
            return MatchResult.noMatch();
        }

        return new MatchResult(
                true,
                bestMatch,
                bestScore
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
                .replaceAll("[^a-z0-9 ]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private double similarity(
            String first,
            String second
    ) {

        if (
                first.isBlank() ||
                        second.isBlank()
        ) {
            return 0.0;
        }

        if (first.equals(second)) {
            return 1.0;
        }

        if (
                first.contains(second) ||
                        second.contains(first)
        ) {
            return 0.85;
        }

        String[] firstWords =
                first.split(" ");

        String[] secondWords =
                second.split(" ");

        int matches = 0;

        for (String firstWord : firstWords) {

            for (String secondWord : secondWords) {

                if (
                        firstWord.equals(secondWord) &&
                                firstWord.length() > 2
                ) {
                    matches++;
                    break;
                }
            }
        }

        int largestWordCount =
                Math.max(
                        firstWords.length,
                        secondWords.length
                );

        if (largestWordCount == 0) {
            return 0.0;
        }

        return (double) matches /
                largestWordCount;
    }

    public record MatchResult(
            boolean matchFound,
            JobApplication application,
            double confidence
    ) {

        public static MatchResult noMatch() {
            return new MatchResult(
                    false,
                    null,
                    0.0
            );
        }
    }
}