package com.offerbase.backend.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "email_classification_feedback",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "user_id",
                                "gmail_message_id"
                        }
                )
        }
)
public class EmailClassificationFeedback {

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
            name = "subject",
            columnDefinition = "TEXT"
    )
    private String subject;

    @Column(
            name = "sender",
            columnDefinition = "TEXT"
    )
    private String sender;

    @Column(
            name = "body",
            columnDefinition = "TEXT"
    )
    private String body;

    @Column(
            name = "predicted_label",
            nullable = false
    )
    private String predictedLabel;

    @Column(
            name = "actual_label",
            nullable = false
    )
    private String actualLabel;

    @Column(
            name = "confidence",
            nullable = false
    )
    private double confidence;

    @Column(
            name = "correct_prediction",
            nullable = false
    )
    private boolean correctPrediction;

    @Column(
            name = "created_at",
            nullable = false
    )
    private Instant createdAt;

    public EmailClassificationFeedback() {
    }

    public EmailClassificationFeedback(
            User user,
            String gmailMessageId,
            String subject,
            String sender,
            String body,
            String predictedLabel,
            String actualLabel,
            double confidence,
            boolean correctPrediction
    ) {
        this.user = user;
        this.gmailMessageId = gmailMessageId;
        this.subject = subject;
        this.sender = sender;
        this.body = body;
        this.predictedLabel = predictedLabel;
        this.actualLabel = actualLabel;
        this.confidence = confidence;
        this.correctPrediction = correctPrediction;
        this.createdAt = Instant.now();
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

    public String getSubject() {
        return subject;
    }

    public String getSender() {
        return sender;
    }

    public String getBody() {
        return body;
    }

    public String getPredictedLabel() {
        return predictedLabel;
    }

    public String getActualLabel() {
        return actualLabel;
    }

    public double getConfidence() {
        return confidence;
    }

    public boolean isCorrectPrediction() {
        return correctPrediction;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}