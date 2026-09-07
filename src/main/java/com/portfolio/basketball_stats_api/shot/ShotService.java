package com.portfolio.basketball_stats_api.shot;

import com.portfolio.basketball_stats_api.common.NotFoundException;
import com.portfolio.basketball_stats_api.player.Player;
import com.portfolio.basketball_stats_api.player.PlayerRepository;
import com.portfolio.basketball_stats_api.shot.dto.CreateShotRequest;
import com.portfolio.basketball_stats_api.shot.dto.GlobalStatisticsResponse;
import com.portfolio.basketball_stats_api.shot.dto.PagedShotsResponse;
import com.portfolio.basketball_stats_api.shot.dto.ShotResponse;
import com.portfolio.basketball_stats_api.shot.dto.ZoneStatisticsResponse;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ShotService {

    private final ShotRepository shotRepository;
    private final PlayerRepository playerRepository;
    private final ShotZoneClassifier zoneClassifier;

    public ShotService(ShotRepository shotRepository, PlayerRepository playerRepository,
                        ShotZoneClassifier zoneClassifier) {
        this.shotRepository = shotRepository;
        this.playerRepository = playerRepository;
        this.zoneClassifier = zoneClassifier;
    }

    @Transactional
    public ShotResponse createShot(CreateShotRequest request, String ownerSubject) {
        Player player = playerRepository.findByIdAndOwnerSubject(request.playerId(), ownerSubject)
                .orElseThrow(() -> new NotFoundException("Player not found: " + request.playerId()));

        ShotZone zone = zoneClassifier.classify(request.posX(), request.posY());
        Shot shot = new Shot(player, request.posX(), request.posY(), zone, request.made());
        return ShotResponse.from(shotRepository.save(shot));
    }

    @Transactional(readOnly = true)
    public PagedShotsResponse getShotsForPlayer(UUID playerId, String ownerSubject,
                                                 Instant from, Instant to, Pageable pageable) {
        if (playerRepository.findByIdAndOwnerSubject(playerId, ownerSubject).isEmpty()) {
            throw new NotFoundException("Player not found: " + playerId);
        }
        if (from != null && to != null && !from.isBefore(to)) {
            throw new IllegalArgumentException("from must be before to");
        }
        if (pageable.getPageSize() > 100) {
            throw new IllegalArgumentException("size cannot exceed 100");
        }
        Page<ShotResponse> result = shotRepository.findByPlayerIdAndDateRange(
                playerId, from, to, ownerSubject, pageable)
                .map(ShotResponse::from);
        return PagedShotsResponse.from(result);
    }

    @Transactional(readOnly = true)
    public List<ZoneStatisticsResponse> getZoneStatisticsForPlayer(UUID playerId, String ownerSubject) {
        if (playerRepository.findByIdAndOwnerSubject(playerId, ownerSubject).isEmpty()) {
            throw new NotFoundException("Player not found: " + playerId);
        }
        return shotRepository.findZoneStatisticsByPlayerId(playerId, ownerSubject).stream()
                .map(ZoneStatisticsResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public GlobalStatisticsResponse getGlobalStatisticsForPlayer(UUID playerId, String ownerSubject) {
        if (playerRepository.findByIdAndOwnerSubject(playerId, ownerSubject).isEmpty()) {
            throw new NotFoundException("Player not found: " + playerId);
        }
        return GlobalStatisticsResponse.from(shotRepository.findGlobalStatisticsByPlayerId(playerId, ownerSubject));
    }
}
