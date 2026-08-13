package com.offerbase.backend.repository;

import com.offerbase.backend.entity.GmailConnection;
import com.offerbase.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GmailConnectionRepository
        extends JpaRepository<GmailConnection, UUID> {

    Optional<GmailConnection> findByUser(User user);
}
