package com.portfolio.basketball_stats_api.shot;

import com.portfolio.basketball_stats_api.player.Player;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "shots")
public class Shot {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @Column(name = "pos_x", nullable = false, precision = 5, scale = 2)
    private BigDecimal posX;

    @Column(name = "pos_y", nullable = false, precision = 5, scale = 2)
    private BigDecimal posY;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ShotZone zone;

    @Column(nullable = false)
    private boolean made;

    @Column(nullable = false)
    private short points;

    @Column(name = "taken_at", nullable = false, updatable = false)
    private Instant takenAt;

    protected Shot() {
        // required by JPA
    }

    public Shot(Player player, BigDecimal posX, BigDecimal posY, ShotZone zone, boolean made) {
        this.player = player;
        this.posX = posX;
        this.posY = posY;
        this.zone = zone;
        this.made = made;
        this.points = zone.pointValue();
        this.takenAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public Player getPlayer() {
        return player;
    }

    public BigDecimal getPosX() {
        return posX;
    }

    public BigDecimal getPosY() {
        return posY;
    }

    public ShotZone getZone() {
        return zone;
    }

    public boolean isMade() {
        return made;
    }

    public short getPoints() {
        return points;
    }

    public Instant getTakenAt() {
        return takenAt;
    }
}
