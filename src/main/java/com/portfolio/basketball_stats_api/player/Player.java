package com.portfolio.basketball_stats_api.player;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "players")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "owner_subject", nullable = false, length = 200, updatable = false)
    private String ownerSubject;

    @Enumerated(EnumType.STRING)
    @Column(name = "dominant_hand", nullable = false, length = 10)
    private DominantHand dominantHand;

    @Column(name = "height_cm")
    private Short heightCm;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Player() {
        // required by JPA
    }

    public Player(String name, DominantHand dominantHand, Short heightCm, String ownerSubject) {
        this.name = name;
        this.dominantHand = dominantHand;
        this.heightCm = heightCm;
        this.ownerSubject = ownerSubject;
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getOwnerSubject() {
        return ownerSubject;
    }

    public DominantHand getDominantHand() {
        return dominantHand;
    }

    public Short getHeightCm() {
        return heightCm;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
