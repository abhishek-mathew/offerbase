package com.offerbase.backend.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "gmail_connections")
public class GmailConnection {

    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne(optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            unique = true
    )
    private User user;

    @Column(
            name = "refresh_token",
            nullable = false,
            length = 2000
    )
    private String refreshToken;

    @Column(name = "scope")
    private String scope;

    @Column(
            name = "connected_at",
            nullable = false
    )
    private Instant connectedAt;

    @Column(
            name = "last_history_id"
    )
    private String lastHistoryId;

    public GmailConnection() {
    }

    public GmailConnection(
            User user,
            String refreshToken,
            String scope
    ) {
        this.user = user;
        this.refreshToken = refreshToken;
        this.scope = scope;
        this.connectedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(
            String refreshToken
    ) {
        this.refreshToken = refreshToken;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(
            String scope
    ) {
        this.scope = scope;
    }

    public String getLastHistoryId() {
        return lastHistoryId;
    }

    public void setLastHistoryId(
            String lastHistoryId
    ) {
        this.lastHistoryId = lastHistoryId;
    }

    public Instant getConnectedAt() {
        return connectedAt;
    }
}