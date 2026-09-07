package com.portfolio.basketball_stats_api.shot;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ShotRepository extends JpaRepository<Shot, UUID> {

    List<Shot> findByPlayerIdOrderByTakenAtDesc(UUID playerId);
}
