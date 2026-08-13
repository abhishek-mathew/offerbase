package com.offerbase.backend.dto;

import com.offerbase.backend.entity.ApplicationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateJobApplicationRequest(
        @NotBlank String company,
        @NotBlank String position,
        String location,
        String jobUrl,
        @NotNull ApplicationStatus status,
        LocalDate dateApplied,
        String notes
) {
}