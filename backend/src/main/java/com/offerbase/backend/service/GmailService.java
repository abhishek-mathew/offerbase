package com.offerbase.backend.service;

import com.offerbase.backend.entity.GmailConnection;
import com.offerbase.backend.entity.User;
import com.offerbase.backend.repository.GmailConnectionRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.offerbase.backend.entity.JobApplication;
import com.offerbase.backend.repository.JobApplicationRepository;

import com.offerbase.backend.repository.GmailProcessedMessageRepository;

@Service
public class GmailService {

    @Value("${google.client.id}")
    private String clientId;

    @Value("${google.client.secret}")
    private String clientSecret;

    private final GmailConnectionRepository gmailConnectionRepository;
    private final HybridEmailClassifier hybridEmailClassifier;
    private final RecruitingEmailFilter recruitingEmailFilter;
    private final EmailJobExtractor emailJobExtractor;
    private final WebClient webClient;
    private final JobApplicationRepository jobApplicationRepository;
    private final EmailApplicationMatcher emailApplicationMatcher;
    private final GmailProcessedMessageRepository gmailProcessedMessageRepository;

    public GmailService(
            GmailConnectionRepository gmailConnectionRepository,
            HybridEmailClassifier hybridEmailClassifier,
            RecruitingEmailFilter recruitingEmailFilter,
            EmailJobExtractor emailJobExtractor,
            JobApplicationRepository jobApplicationRepository,
            EmailApplicationMatcher emailApplicationMatcher,
            GmailProcessedMessageRepository gmailProcessedMessageRepository
    ) {
        this.gmailConnectionRepository = gmailConnectionRepository;
        this.hybridEmailClassifier = hybridEmailClassifier;
        this.recruitingEmailFilter = recruitingEmailFilter;
        this.emailJobExtractor = emailJobExtractor;
        this.webClient = WebClient.builder().build();
        this.jobApplicationRepository = jobApplicationRepository;
        this.emailApplicationMatcher = emailApplicationMatcher;
        this.gmailProcessedMessageRepository = gmailProcessedMessageRepository;
    }

    public List<Map<String, Object>> getRecentEmails(User user) {

        GmailConnection connection =
                gmailConnectionRepository
                        .findByUser(user)
                        .orElseThrow(
                                () -> new IllegalStateException(
                                        "Gmail is not connected"
                                )
                        );

        List<JobApplication> userApplications =
                jobApplicationRepository
                        .findAllByUserId(
                                user.getId()
                        );

        String accessToken =
                refreshAccessToken(
                        connection.getRefreshToken()
                );

        boolean initialSync =
                connection.getLastHistoryId() == null;

        List<Map<?, ?>> messageSummaries =
                new ArrayList<>();

        if (initialSync) {

            final int MAX_CANDIDATES = 150;
            String pageToken = null;

            do {

                String currentPageToken =
                        pageToken;

                Map<?, ?> listResponse =
                        webClient
                                .get()
                                .uri(uriBuilder -> {

                                    var builder =
                                            uriBuilder
                                                    .scheme("https")
                                                    .host("gmail.googleapis.com")
                                                    .path(
                                                            "/gmail/v1/users/me/messages"
                                                    )
                                                    .queryParam(
                                                            "maxResults",
                                                            100
                                                    )
                                                    .queryParam(
                                                            "q",
                                                            "newer_than:1y (application OR interview OR recruiter OR recruiting OR offer OR \"thank you for applying\" OR \"thank you for your interest\" OR unfortunately OR careers)"
                                                    );

                                    if (currentPageToken != null) {
                                        builder.queryParam(
                                                "pageToken",
                                                currentPageToken
                                        );
                                    }

                                    return builder.build();
                                })
                                .headers(headers ->
                                        headers.setBearerAuth(
                                                accessToken
                                        )
                                )
                                .retrieve()
                                .bodyToMono(Map.class)
                                .block();

                if (
                        listResponse == null ||
                                listResponse.get("messages") == null
                ) {
                    break;
                }

                List<?> pageMessages =
                        (List<?>) listResponse.get(
                                "messages"
                        );

                for (Object item : pageMessages) {

                    if (
                            messageSummaries.size()
                                    >= MAX_CANDIDATES
                    ) {
                        break;
                    }

                    messageSummaries.add(
                            (Map<?, ?>) item
                    );
                }

                Object nextPageToken =
                        listResponse.get(
                                "nextPageToken"
                        );

                pageToken =
                        nextPageToken == null
                                ? null
                                : String.valueOf(
                                nextPageToken
                        );

            } while (
                    pageToken != null &&
                            messageSummaries.size()
                                    < MAX_CANDIDATES
            );

        } else {

            try {

                messageSummaries.addAll(
                        getNewMessageSummaries(
                                accessToken,
                                connection.getLastHistoryId()
                        )
                );

            } catch (Exception exception) {

                connection.setLastHistoryId(
                        null
                );

                gmailConnectionRepository.save(
                        connection
                );

                return getRecentEmails(
                        user
                );
            }
        }

        List<Map<String, Object>> results =
                new ArrayList<>();

        for (Map<?, ?> messageSummary : messageSummaries) {

            String messageId =
                    String.valueOf(
                            messageSummary.get("id")
                    );

            boolean alreadyProcessed =
                    gmailProcessedMessageRepository
                            .existsByUserIdAndGmailMessageId(
                                    user.getId(),
                                    messageId
                            );

            if (alreadyProcessed) {
                continue;
            }

            Map<?, ?> message =
                    webClient
                            .get()
                            .uri(uriBuilder ->
                                    uriBuilder
                                            .scheme("https")
                                            .host(
                                                    "gmail.googleapis.com"
                                            )
                                            .path(
                                                    "/gmail/v1/users/me/messages/{id}"
                                            )
                                            .queryParam(
                                                    "format",
                                                    "metadata"
                                            )
                                            .queryParam(
                                                    "metadataHeaders",
                                                    "Subject"
                                            )
                                            .queryParam(
                                                    "metadataHeaders",
                                                    "From"
                                            )
                                            .queryParam(
                                                    "metadataHeaders",
                                                    "Date"
                                            )
                                            .build(
                                                    messageId
                                            )
                            )
                            .headers(headers ->
                                    headers.setBearerAuth(
                                            accessToken
                                    )
                            )
                            .retrieve()
                            .bodyToMono(Map.class)
                            .block();

            if (message == null) {
                continue;
            }

            String subject = "";
            String from = "";
            String date = "";

            Map<?, ?> payload =
                    (Map<?, ?>) message.get(
                            "payload"
                    );

            if (payload != null) {

                List<?> headers =
                        (List<?>) payload.get(
                                "headers"
                        );

                if (headers != null) {

                    for (Object headerObject : headers) {

                        Map<?, ?> header =
                                (Map<?, ?>) headerObject;

                        String name =
                                String.valueOf(
                                        header.get("name")
                                );

                        String value =
                                String.valueOf(
                                        header.get("value")
                                );

                        if (
                                name.equalsIgnoreCase(
                                        "Subject"
                                )
                        ) {
                            subject = value;
                        }

                        if (
                                name.equalsIgnoreCase(
                                        "From"
                                )
                        ) {
                            from = value;
                        }

                        if (
                                name.equalsIgnoreCase(
                                        "Date"
                                )
                        ) {
                            date = value;
                        }
                    }
                }
            }

            String snippet =
                    message.get("snippet") == null
                            ? ""
                            : String.valueOf(
                            message.get("snippet")
                    );

            RecruitingEmailFilter.FilterResult filterResult =
                    recruitingEmailFilter.evaluate(
                            subject,
                            from,
                            snippet
                    );

            if (!filterResult.candidate()) {
                continue;
            }

            HybridEmailClassifier.HybridClassificationResult classification =
                    hybridEmailClassifier.classify(
                            subject,
                            from,
                            snippet
                    );

            if (
                    classification.status().equals("OTHER") &&
                            classification.confidence() < 0.50
            ) {
                continue;
            }

            EmailJobExtractor.ExtractionResult extraction =
                    emailJobExtractor.extract(
                            subject,
                            from,
                            snippet
                    );

            EmailApplicationMatcher.MatchResult match =
                    emailApplicationMatcher.match(
                            extraction.company(),
                            extraction.position(),
                            userApplications
                    );

            String suggestionType;

            if (match.matchFound()) {
                suggestionType = "UPDATE";
            } else if (
                    !extraction.company().isBlank() &&
                            !classification.status().equals("OTHER")
            ) {
                suggestionType = "CREATE";
            } else {
                suggestionType = "REVIEW";
            }

            Map<String, Object> result =
                    new java.util.HashMap<>();

            result.put(
                    "id",
                    messageId
            );

            result.put(
                    "subject",
                    subject
            );

            result.put(
                    "from",
                    from
            );

            result.put(
                    "date",
                    date
            );

            result.put(
                    "snippet",
                    snippet
            );

            result.put(
                    "classification",
                    classification.status()
            );

            result.put(
                    "confidence",
                    classification.confidence()
            );

            result.put(
                    "filterReason",
                    filterResult.reason()
            );

            result.put(
                    "classificationSource",
                    classification.source()
            );

            result.put(
                    "ruleClassification",
                    classification.ruleStatus()
            );

            result.put(
                    "ruleConfidence",
                    classification.ruleConfidence()
            );

            if (classification.mlStatus() != null) {

                result.put(
                        "mlClassification",
                        classification.mlStatus()
                );
            }

            if (classification.mlConfidence() != null) {

                result.put(
                        "mlConfidence",
                        classification.mlConfidence()
                );
            }

            result.put(
                    "company",
                    extraction.company()
            );

            result.put(
                    "position",
                    extraction.position()
            );

            result.put(
                    "matchFound",
                    match.matchFound()
            );

            result.put(
                    "matchConfidence",
                    match.confidence()
            );

            result.put(
                    "suggestionType",
                    suggestionType
            );

            if (
                    match.matchFound() &&
                            match.application() != null
            ) {

                result.put(
                        "matchedApplicationId",
                        match.application().getId()
                );

                result.put(
                        "currentStatus",
                        match.application()
                                .getStatus()
                                .toString()
                );

                result.put(
                        "suggestedStatus",
                        classification.status()
                );
            }

            results.add(result);
        }

        String currentHistoryId =
                getCurrentHistoryId(
                        accessToken
                );

        connection.setLastHistoryId(
                currentHistoryId
        );

        gmailConnectionRepository.save(
                connection
        );

        return results;
    }

    private String getCurrentHistoryId(
            String accessToken
    ) {

        Map<?, ?> profile =
                webClient
                        .get()
                        .uri(
                                "https://gmail.googleapis.com/gmail/v1/users/me/profile"
                        )
                        .headers(headers ->
                                headers.setBearerAuth(
                                        accessToken
                                )
                        )
                        .retrieve()
                        .bodyToMono(Map.class)
                        .block();

        if (
                profile == null ||
                        profile.get("historyId") == null
        ) {
            throw new IllegalStateException(
                    "Could not retrieve Gmail history ID"
            );
        }

        return String.valueOf(
                profile.get("historyId")
        );
    }

    private List<Map<?, ?>> getNewMessageSummaries(
            String accessToken,
            String startHistoryId
    ) {

        List<Map<?, ?>> messageSummaries =
                new ArrayList<>();

        String pageToken = null;

        do {

            String currentPageToken =
                    pageToken;

            Map<?, ?> response =
                    webClient
                            .get()
                            .uri(uriBuilder -> {

                                var builder =
                                        uriBuilder
                                                .scheme("https")
                                                .host(
                                                        "gmail.googleapis.com"
                                                )
                                                .path(
                                                        "/gmail/v1/users/me/history"
                                                )
                                                .queryParam(
                                                        "startHistoryId",
                                                        startHistoryId
                                                )
                                                .queryParam(
                                                        "historyTypes",
                                                        "messageAdded"
                                                )
                                                .queryParam(
                                                        "maxResults",
                                                        100
                                                );

                                if (
                                        currentPageToken != null
                                ) {
                                    builder.queryParam(
                                            "pageToken",
                                            currentPageToken
                                    );
                                }

                                return builder.build();
                            })
                            .headers(headers ->
                                    headers.setBearerAuth(
                                            accessToken
                                    )
                            )
                            .retrieve()
                            .bodyToMono(Map.class)
                            .block();

            if (response == null) {
                break;
            }

            Object historyObject =
                    response.get("history");

            if (historyObject instanceof List<?> historyRecords) {

                for (Object historyRecordObject : historyRecords) {

                    Map<?, ?> historyRecord =
                            (Map<?, ?>) historyRecordObject;

                    Object addedObject =
                            historyRecord.get(
                                    "messagesAdded"
                            );

                    if (!(addedObject instanceof List<?> addedMessages)) {
                        continue;
                    }

                    for (Object addedMessageObject : addedMessages) {

                        Map<?, ?> addedMessage =
                                (Map<?, ?>) addedMessageObject;

                        Object messageObject =
                                addedMessage.get(
                                        "message"
                                );

                        if (!(messageObject instanceof Map<?, ?> message)) {
                            continue;
                        }

                        messageSummaries.add(
                                message
                        );
                    }
                }
            }

            Object nextPageToken =
                    response.get(
                            "nextPageToken"
                    );

            pageToken =
                    nextPageToken == null
                            ? null
                            : String.valueOf(
                            nextPageToken
                    );

        } while (pageToken != null);

        return messageSummaries;
    }

    private String refreshAccessToken(
            String refreshToken
    ) {

        MultiValueMap<String, String> form =
                new LinkedMultiValueMap<>();

        form.add(
                "client_id",
                clientId
        );

        form.add(
                "client_secret",
                clientSecret
        );

        form.add(
                "refresh_token",
                refreshToken
        );

        form.add(
                "grant_type",
                "refresh_token"
        );

        Map<?, ?> response =
                webClient
                        .post()
                        .uri(
                                "https://oauth2.googleapis.com/token"
                        )
                        .contentType(
                                MediaType.APPLICATION_FORM_URLENCODED
                        )
                        .bodyValue(form)
                        .retrieve()
                        .bodyToMono(Map.class)
                        .block();

        if (
                response == null ||
                        response.get("access_token") == null
        ) {
            throw new IllegalStateException(
                    "Could not refresh Google access token"
            );
        }

        return String.valueOf(
                response.get("access_token")
        );
    }
}
