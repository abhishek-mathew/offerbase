package com.offerbase.backend.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "gmail_processed_messages",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "user_id",
                                "gmail_message_id"
                        }
                )
        }
)
public class GmailProcessedMessage {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    @Column(
            name = "gmail_message_id",
            nullable = false
    )
    private String gmailMessageId;

    @Column(
            name = "action",
            nullable = false
    )
    private String action;

    @Column(
            name = "processed_at",
            nullable = false
    )
    private Instant processedAt;

    public GmailProcessedMessage() {
    }

    public GmailProcessedMessage(
            User user,
            String gmailMessageId,
            String action
    ) {
        this.user = user;
        this.gmailMessageId = gmailMessageId;
        this.action = action;
        this.processedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getGmailMessageId() {
        return gmailMessageId;
    }

    public String getAction() {
        return action;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }
}