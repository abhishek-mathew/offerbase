package com.offerbase.backend.controller;

import com.offerbase.backend.dto.GoogleTokenResponse;
import com.offerbase.backend.entity.GmailConnection;
import com.offerbase.backend.entity.GmailOAuthState;
import com.offerbase.backend.entity.User;
import com.offerbase.backend.repository.GmailConnectionRepository;
import com.offerbase.backend.repository.GmailOAuthStateRepository;
import com.offerbase.backend.repository.UserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;

import com.offerbase.backend.service.GmailService;
import com.offerbase.backend.entity.GmailProcessedMessage;
import com.offerbase.backend.repository.GmailProcessedMessageRepository;

import com.offerbase.backend.dto.EmailFeedbackRequest;
import com.offerbase.backend.service.EmailFeedbackService;

@RestController
@RequestMapping("/api/gmail")
public class GmailController {

    @Value("${google.client.id}")
    private String clientId;

    @Value("${google.client.secret}")
    private String clientSecret;

    @Value("${google.redirect.uri}")
    private String redirectUri;

    private final WebClient webClient;

    private final UserRepository userRepository;
    private final GmailConnectionRepository gmailConnectionRepository;
    private final GmailOAuthStateRepository gmailOAuthStateRepository;

    private final GmailService gmailService;
    private final GmailProcessedMessageRepository gmailProcessedMessageRepository;

    private final EmailFeedbackService emailFeedbackService;

    public GmailController(
            UserRepository userRepository,
            GmailConnectionRepository gmailConnectionRepository,
            GmailOAuthStateRepository gmailOAuthStateRepository,
            GmailService gmailService,
            GmailProcessedMessageRepository gmailProcessedMessageRepository,
            EmailFeedbackService emailFeedbackService
    ) {
        this.userRepository = userRepository;
        this.gmailConnectionRepository = gmailConnectionRepository;
        this.gmailOAuthStateRepository = gmailOAuthStateRepository;

        this.webClient = WebClient.builder().build();

        this.gmailService = gmailService;
        this.gmailProcessedMessageRepository = gmailProcessedMessageRepository;

        this.emailFeedbackService = emailFeedbackService;
    }

    @GetMapping("/connect")
    public ResponseEntity<?> connect(
            Authentication authentication
    ) {

        String email = authentication.getName();

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> new IllegalStateException(
                                "OfferBase user not found"
                        )
                );

        String state =
                UUID.randomUUID().toString();

        GmailOAuthState oauthState =
                new GmailOAuthState(
                        state,
                        user,
                        Instant.now()
                                .plus(
                                        10,
                                        ChronoUnit.MINUTES
                                )
                );

        gmailOAuthStateRepository.save(
                oauthState
        );

        String scope =
                "https://www.googleapis.com/auth/gmail.readonly";

        String authorizationUrl =
                "https://accounts.google.com/o/oauth2/v2/auth"
                        + "?client_id="
                        + encode(clientId)

                        + "&redirect_uri="
                        + encode(redirectUri)

                        + "&response_type=code"

                        + "&scope="
                        + encode(scope)

                        + "&access_type=offline"

                        + "&prompt=consent"

                        + "&state="
                        + encode(state);

        return ResponseEntity.ok(
                Map.of(
                        "authorizationUrl",
                        authorizationUrl
                )
        );
    }

    @GetMapping("/oauth/callback")
    public ResponseEntity<?> callback(
            @RequestParam String code,
            @RequestParam String state
    ) {

        GmailOAuthState oauthState =
                gmailOAuthStateRepository
                        .findById(state)
                        .orElse(null);

        if (oauthState == null) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "error",
                                    "Invalid OAuth state"
                            )
                    );
        }

        /*
         * State values are one-time-use.
         * Delete it immediately after finding it.
         */
        gmailOAuthStateRepository.delete(
                oauthState
        );

        if (
                oauthState
                        .getExpiresAt()
                        .isBefore(Instant.now())
        ) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "error",
                                    "OAuth state expired"
                            )
                    );
        }

        MultiValueMap<String, String> formData =
                new LinkedMultiValueMap<>();

        formData.add(
                "client_id",
                clientId
        );

        formData.add(
                "client_secret",
                clientSecret
        );

        formData.add(
                "code",
                code
        );

        formData.add(
                "grant_type",
                "authorization_code"
        );

        formData.add(
                "redirect_uri",
                redirectUri
        );

        GoogleTokenResponse tokenResponse =
                webClient
                        .post()
                        .uri(
                                "https://oauth2.googleapis.com/token"
                        )
                        .contentType(
                                MediaType.APPLICATION_FORM_URLENCODED
                        )
                        .bodyValue(formData)
                        .retrieve()
                        .bodyToMono(
                                GoogleTokenResponse.class
                        )
                        .block();

        if (
                tokenResponse == null ||
                        tokenResponse.accessToken() == null
        ) {

            return ResponseEntity
                    .internalServerError()
                    .body(
                            Map.of(
                                    "error",
                                    "Could not exchange Google authorization code"
                            )
                    );
        }

        User user =
                oauthState.getUser();

        GmailConnection connection =
                gmailConnectionRepository
                        .findByUser(user)
                        .orElse(null);

        /*
         * First Gmail connection
         */
        if (connection == null) {

            if (
                    tokenResponse.refreshToken()
                            == null
            ) {

                return ResponseEntity
                        .internalServerError()
                        .body(
                                Map.of(
                                        "error",
                                        "Google did not return a refresh token"
                                )
                        );
            }

            connection =
                    new GmailConnection(
                            user,
                            tokenResponse.refreshToken(),
                            tokenResponse.scope()
                    );

        } else {

            /*
             * Google does not necessarily issue a new
             * refresh token every time.
             */
            if (
                    tokenResponse.refreshToken()
                            != null
            ) {

                connection.setRefreshToken(
                        tokenResponse.refreshToken()
                );
            }

            connection.setScope(
                    tokenResponse.scope()
            );
        }

        gmailConnectionRepository.save(
                connection
        );

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Gmail connected successfully",
                        "gmailStored",
                        true
                )
        );
    }

    @GetMapping("/emails")
    public ResponseEntity<?> getRecentEmails(
            Authentication authentication
    ) {

        User user =
                userRepository
                        .findByEmail(
                                authentication.getName()
                        )
                        .orElseThrow(
                                () -> new IllegalStateException(
                                        "OfferBase user not found"
                                )
                        );

        return ResponseEntity.ok(
                gmailService.getRecentEmails(
                        user
                )
        );
    }

    @PostMapping("/emails/feedback")
    public ResponseEntity<?> saveEmailFeedback(
            @RequestBody EmailFeedbackRequest request,
            Authentication authentication
    ) {

        emailFeedbackService.saveFeedback(
                request,
                authentication.getName()
        );

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Feedback saved"
                )
        );
    }

    @GetMapping("/emails/feedback")
    public ResponseEntity<?> getEmailFeedback(
            Authentication authentication
    ) {

        var feedback =
                emailFeedbackService
                        .getFeedbackForUser(
                                authentication.getName()
                        );

        var response =
                feedback
                        .stream()
                        .map(item ->
                                Map.of(
                                        "gmailMessageId",
                                        item.getGmailMessageId(),

                                        "subject",
                                        item.getSubject() == null
                                                ? ""
                                                : item.getSubject(),

                                        "sender",
                                        item.getSender() == null
                                                ? ""
                                                : item.getSender(),

                                        "body",
                                        item.getBody() == null
                                                ? ""
                                                : item.getBody(),

                                        "predictedLabel",
                                        item.getPredictedLabel(),

                                        "actualLabel",
                                        item.getActualLabel(),

                                        "confidence",
                                        item.getConfidence(),

                                        "correctPrediction",
                                        item.isCorrectPrediction()
                                )
                        )
                        .toList();

        return ResponseEntity.ok(
                response
        );
    }

    @PostMapping("/emails/{messageId}/processed")
    public ResponseEntity<?> markEmailProcessed(
            @PathVariable String messageId,
            @RequestParam String action,
            Authentication authentication
    ) {

        User user =
                userRepository
                        .findByEmail(
                                authentication.getName()
                        )
                        .orElseThrow(
                                () -> new IllegalStateException(
                                        "OfferBase user not found"
                                )
                        );

        if (
                !action.equals("APPROVED") &&
                        !action.equals("IGNORED")
        ) {
            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "error",
                                    "Invalid action"
                            )
                    );
        }

        boolean alreadyProcessed =
                gmailProcessedMessageRepository
                        .existsByUserIdAndGmailMessageId(
                                user.getId(),
                                messageId
                        );

        if (!alreadyProcessed) {

            GmailProcessedMessage processedMessage =
                    new GmailProcessedMessage(
                            user,
                            messageId,
                            action
                    );

            gmailProcessedMessageRepository.save(
                    processedMessage
            );
        }

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Email marked as processed"
                )
        );
    }

    private String encode(
            String value
    ) {
        return URLEncoder.encode(
                value,
                StandardCharsets.UTF_8
        );
    }
}