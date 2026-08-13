package com.offerbase.backend.service;

import java.util.List;

import com.offerbase.backend.dto.EmailFeedbackRequest;
import com.offerbase.backend.entity.EmailClassificationFeedback;
import com.offerbase.backend.entity.User;
import com.offerbase.backend.repository.EmailClassificationFeedbackRepository;
import com.offerbase.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class EmailFeedbackService {

    private final EmailClassificationFeedbackRepository feedbackRepository;
    private final UserRepository userRepository;

    public EmailFeedbackService(
            EmailClassificationFeedbackRepository feedbackRepository,
            UserRepository userRepository
    ) {
        this.feedbackRepository = feedbackRepository;
        this.userRepository = userRepository;
    }

    public void saveFeedback(
            EmailFeedbackRequest request,
            String userEmail
    ) {

        User user =
                userRepository
                        .findByEmail(userEmail)
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "User not found"
                                )
                        );

        boolean alreadyExists =
                feedbackRepository
                        .existsByUserIdAndGmailMessageId(
                                user.getId(),
                                request.gmailMessageId()
                        );

        if (alreadyExists) {
            return;
        }

        boolean correctPrediction =
                request.predictedLabel()
                        .equals(
                                request.actualLabel()
                        );

        EmailClassificationFeedback feedback =
                new EmailClassificationFeedback(
                        user,
                        request.gmailMessageId(),
                        request.subject(),
                        request.sender(),
                        request.body(),
                        request.predictedLabel(),
                        request.actualLabel(),
                        request.confidence(),
                        correctPrediction
                );

        feedbackRepository.save(
                feedback
        );
    }

    public List<EmailClassificationFeedback> getFeedbackForUser(
            String userEmail
    ) {

        User user =
                userRepository
                        .findByEmail(userEmail)
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "User not found"
                                )
                        );

        return feedbackRepository
                .findAllByUserId(
                        user.getId()
                );
    }
}