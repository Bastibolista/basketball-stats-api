package com.portfolio.basketball_stats_api.shot;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ShotRepository extends JpaRepository<Shot, UUID> {

    List<Shot> findByPlayerIdOrderByTakenAtDesc(UUID playerId);

    @Query("""
            SELECT s.zone AS zone,
                   COUNT(s) AS attempts,
                   SUM(CASE WHEN s.made = true THEN 1 ELSE 0 END) AS madeShots,
                   SUM(CASE WHEN s.made = true THEN s.points ELSE 0 END) AS pointsScored,
                   (100.0 * SUM(CASE WHEN s.made = true THEN 1 ELSE 0 END) / COUNT(s)) AS fieldGoalPercentage
            FROM Shot s
            WHERE s.player.id = :playerId
            GROUP BY s.zone
            ORDER BY s.zone
            """)
    List<ZoneStatisticsProjection> findZoneStatisticsByPlayerId(@Param("playerId") UUID playerId);
}
