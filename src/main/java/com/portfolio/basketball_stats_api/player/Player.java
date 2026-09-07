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

    public Player(String name, DominantHand dominantHand, Short heightCm) {
        this.name = name;
        this.dominantHand = dominantHand;
        this.heightCm = heightCm;
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
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
