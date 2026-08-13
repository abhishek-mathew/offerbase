package com.offerbase.backend.repository;

import com.offerbase.backend.entity.GmailProcessedMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GmailProcessedMessageRepository
        extends JpaRepository<GmailProcessedMessage, UUID> {

    boolean existsByUserIdAndGmailMessageId(
            UUID userId,
            String gmailMessageId
    );

    List<GmailProcessedMessage> findAllByUserId(
            UUID userId
    );
}