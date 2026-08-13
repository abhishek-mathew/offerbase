package com.offerbase.backend.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "application_events")
public class ApplicationEvent {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "application_id",
            nullable = false
    )
    private JobApplication application;

    @Column(
            name = "event_type",
            nullable = false
    )
    private String eventType;

    @Column(
            name = "status"
    )
    private String status;

    @Column(
            name = "description",
            columnDefinition = "TEXT"
    )
    private String description;

    @Column(
            name = "source",
            nullable = false
    )
    private String source;

    @Column(
            name = "created_at",
            nullable = false
    )
    private Instant createdAt;

    public ApplicationEvent() {
    }

    public ApplicationEvent(
            JobApplication application,
            String eventType,
            String status,
            String description,
            String source
    ) {
        this.application = application;
        this.eventType = eventType;
        this.status = status;
        this.description = description;
        this.source = source;
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public JobApplication getApplication() {
        return application;
    }

    public String getEventType() {
        return eventType;
    }

    public String getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public String getSource() {
        return source;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}