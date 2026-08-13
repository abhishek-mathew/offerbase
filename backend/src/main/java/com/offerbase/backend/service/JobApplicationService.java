package com.offerbase.backend.service;

import java.util.List;
import com.offerbase.backend.dto.CreateJobApplicationRequest;
import com.offerbase.backend.dto.JobApplicationResponse;
import com.offerbase.backend.entity.JobApplication;
import com.offerbase.backend.entity.User;
import com.offerbase.backend.repository.JobApplicationRepository;
import com.offerbase.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import com.offerbase.backend.dto.UpdateJobApplicationRequest;
import java.util.UUID;
import com.offerbase.backend.entity.ApplicationEvent;
import com.offerbase.backend.repository.ApplicationEventRepository;
import com.offerbase.backend.dto.ApplicationEventResponse;

@Service
public class JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final UserRepository userRepository;
    private final ApplicationEventRepository applicationEventRepository;

    public JobApplicationService(
            JobApplicationRepository jobApplicationRepository,
            UserRepository userRepository,
            ApplicationEventRepository applicationEventRepository
    ) {
        this.jobApplicationRepository = jobApplicationRepository;
        this.userRepository = userRepository;
        this.applicationEventRepository = applicationEventRepository;
    }

    public JobApplicationResponse create(
            CreateJobApplicationRequest request,
            String userEmail
    ) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        JobApplication application = new JobApplication();
        application.setCompany(request.company());
        application.setPosition(request.position());
        application.setLocation(request.location());
        application.setJobUrl(request.jobUrl());
        application.setStatus(request.status());
        application.setDateApplied(request.dateApplied());
        application.setNotes(request.notes());
        application.setUser(user);

        JobApplication saved =
                jobApplicationRepository.save(application);

        ApplicationEvent createdEvent =
                new ApplicationEvent(
                        saved,
                        "CREATED",
                        saved.getStatus().toString(),
                        "Application added to OfferBase",
                        "MANUAL"
                );

        applicationEventRepository.save(
                createdEvent
        );

        return JobApplicationResponse.from(saved);
    }

    public List<ApplicationEventResponse> getEvents(
            UUID applicationId,
            String email
    ) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> new IllegalStateException(
                                "User not found"
                        )
                );

        JobApplication application =
                jobApplicationRepository
                        .findById(applicationId)
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "Application not found"
                                )
                        );

        if (!application
                .getUser()
                .getId()
                .equals(user.getId())) {

            throw new IllegalArgumentException(
                    "Application not found"
            );
        }

        return applicationEventRepository
                .findAllByApplicationIdOrderByCreatedAtDesc(
                        applicationId
                )
                .stream()
                .map(ApplicationEventResponse::from)
                .toList();
    }

    public List<JobApplicationResponse> getAllForUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return jobApplicationRepository.findAllByUserId(user.getId())
                .stream()
                .map(JobApplicationResponse::from)
                .toList();
    }

    public JobApplicationResponse update(
            UUID applicationId,
            UpdateJobApplicationRequest request,
            String userEmail
    ) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        JobApplication application = jobApplicationRepository
                .findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        if (!application.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Application not found");
        }

        String previousStatus =
                application
                        .getStatus()
                        .toString();

        application.setCompany(request.company());
        application.setPosition(request.position());
        application.setLocation(request.location());
        application.setJobUrl(request.jobUrl());
        application.setStatus(request.status());
        application.setDateApplied(request.dateApplied());
        application.setNotes(request.notes());

        JobApplication saved = jobApplicationRepository.save(application);

        String newStatus =
                saved
                        .getStatus()
                        .toString();

        if (!previousStatus.equals(newStatus)) {

            ApplicationEvent statusEvent =
                    new ApplicationEvent(
                            saved,
                            "STATUS_CHANGED",
                            newStatus,
                            previousStatus
                                    + " → "
                                    + newStatus,
                            "MANUAL"
                    );

            applicationEventRepository.save(
                    statusEvent
            );
        }

        return JobApplicationResponse.from(saved);
    }

    public void delete(
            UUID applicationId,
            String userEmail
    ) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        JobApplication application = jobApplicationRepository
                .findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        if (!application.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Application not found");
        }

        jobApplicationRepository.delete(application);
    }
}