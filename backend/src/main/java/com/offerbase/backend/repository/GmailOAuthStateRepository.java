package com.offerbase.backend.repository;

import com.offerbase.backend.entity.GmailOAuthState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GmailOAuthStateRepository
        extends JpaRepository<GmailOAuthState, String> {
}