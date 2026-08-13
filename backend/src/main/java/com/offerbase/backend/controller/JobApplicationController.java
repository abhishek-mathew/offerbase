package com.offerbase.backend.controller;

import com.offerbase.backend.dto.CreateJobApplicationRequest;
import com.offerbase.backend.dto.JobApplicationResponse;
import com.offerbase.backend.dto.UpdateJobApplicationRequest;
import com.offerbase.backend.service.JobApplicationService;
import com.offerbase.backend.dto.ApplicationEventResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/applications")
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    public JobApplicationController(
            JobApplicationService jobApplicationService
    ) {
        this.jobApplicationService = jobApplicationService;
    }

    @PostMapping
    public ResponseEntity<JobApplicationResponse> createApplication(
            @Valid @RequestBody CreateJobApplicationRequest request,
            Authentication authentication
    ) {
        JobApplicationResponse response =
                jobApplicationService.create(
                        request,
                        authentication.getName()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<JobApplicationResponse>> getApplications(
            Authentication authentication
    ) {
        List<JobApplicationResponse> applications =
                jobApplicationService.getAllForUser(
                        authentication.getName()
                );

        return ResponseEntity.ok(applications);
    }

    @GetMapping("/{applicationId}/events")
    public List<ApplicationEventResponse> getApplicationEvents(
            @PathVariable UUID applicationId,
            Authentication authentication
    ) {

        return jobApplicationService.getEvents(
                applicationId,
                authentication.getName()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobApplicationResponse> updateApplication(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateJobApplicationRequest request,
            Authentication authentication
    ) {
        JobApplicationResponse response =
                jobApplicationService.update(
                        id,
                        request,
                        authentication.getName()
                );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        jobApplicationService.delete(
                id,
                authentication.getName()
        );

        return ResponseEntity.noContent().build();
    }
}