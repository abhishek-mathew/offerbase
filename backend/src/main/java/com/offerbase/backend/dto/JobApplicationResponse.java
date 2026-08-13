package com.offerbase.backend.dto;

import com.offerbase.backend.entity.ApplicationStatus;
import com.offerbase.backend.entity.JobApplication;

import java.time.LocalDate;
import java.util.UUID;

public record JobApplicationResponse(
        UUID id,
        String company,
        String position,
        String location,
        String jobUrl,
        ApplicationStatus status,
        LocalDate dateApplied,
        String notes
) {
    public static JobApplicationResponse from(JobApplication application) {
        return new JobApplicationResponse(
                application.getId(),
                application.getCompany(),
                application.getPosition(),
                application.getLocation(),
                application.getJobUrl(),
                application.getStatus(),
                application.getDateApplied(),
                application.getNotes()
        );
    }
}