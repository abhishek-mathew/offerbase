package com.offerbase.backend.repository;

import com.offerbase.backend.entity.EmailClassificationFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EmailClassificationFeedbackRepository
        extends JpaRepository<EmailClassificationFeedback, UUID> {

    boolean existsByUserIdAndGmailMessageId(
            UUID userId,
            String gmailMessageId
    );

    List<EmailClassificationFeedback> findAllByUserId(
            UUID userId
    );
}