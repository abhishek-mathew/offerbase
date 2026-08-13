package com.offerbase.backend.dto;

import com.offerbase.backend.entity.ApplicationEvent;

import java.time.Instant;
import java.util.UUID;

public record ApplicationEventResponse(
        UUID id,
        String eventType,
        String status,
        String description,
        String source,
        Instant createdAt
) {

    public static ApplicationEventResponse from(
            ApplicationEvent event
    ) {
        return new ApplicationEventResponse(
                event.getId(),
                event.getEventType(),
                event.getStatus(),
                event.getDescription(),
                event.getSource(),
                event.getCreatedAt()
        );
    }
}