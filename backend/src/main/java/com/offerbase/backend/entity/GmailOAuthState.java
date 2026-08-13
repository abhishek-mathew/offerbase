package com.offerbase.backend.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "gmail_oauth_states")
public class GmailOAuthState {

    @Id
    private String state;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    public GmailOAuthState() {
    }

    public GmailOAuthState(
            String state,
            User user,
            Instant expiresAt
    ) {
        this.state = state;
        this.user = user;
        this.expiresAt = expiresAt;
    }

    public String getState() {
        return state;
    }

    public User getUser() {
        return user;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }
}
