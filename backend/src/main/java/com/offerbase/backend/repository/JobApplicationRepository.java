package com.offerbase.backend.repository;

import com.offerbase.backend.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JobApplicationRepository
        extends JpaRepository<JobApplication, UUID> {

    List<JobApplication> findAllByUserId(UUID userId);
}
