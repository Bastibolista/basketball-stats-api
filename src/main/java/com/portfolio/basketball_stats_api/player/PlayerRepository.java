package com.portfolio.basketball_stats_api.player;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlayerRepository extends JpaRepository<Player, UUID> {

	Optional<Player> findByIdAndOwnerSubject(UUID id, String ownerSubject);

	List<Player> findAllByOwnerSubjectOrderByCreatedAtDesc(String ownerSubject);
}
