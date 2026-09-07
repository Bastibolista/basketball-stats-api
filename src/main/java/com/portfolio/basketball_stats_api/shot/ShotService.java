package com.portfolio.basketball_stats_api.shot;

import com.portfolio.basketball_stats_api.common.NotFoundException;
import com.portfolio.basketball_stats_api.player.Player;
import com.portfolio.basketball_stats_api.player.PlayerRepository;
import com.portfolio.basketball_stats_api.shot.dto.CreateShotRequest;
import com.portfolio.basketball_stats_api.shot.dto.GlobalStatisticsResponse;
import com.portfolio.basketball_stats_api.shot.dto.ShotResponse;
import com.portfolio.basketball_stats_api.shot.dto.ZoneStatisticsResponse;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public ShotResponse createShot(CreateShotRequest request) {
        Player player = playerRepository.findById(request.playerId())
                .orElseThrow(() -> new NotFoundException("Player not found: " + request.playerId()));

        ShotZone zone = zoneClassifier.classify(request.posX(), request.posY());
        Shot shot = new Shot(player, request.posX(), request.posY(), zone, request.made());
        return ShotResponse.from(shotRepository.save(shot));
    }

    @Transactional(readOnly = true)
    public List<ShotResponse> getShotsForPlayer(UUID playerId) {
        if (!playerRepository.existsById(playerId)) {
            throw new NotFoundException("Player not found: " + playerId);
        }
        return shotRepository.findByPlayerIdOrderByTakenAtDesc(playerId).stream()
                .map(ShotResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ZoneStatisticsResponse> getZoneStatisticsForPlayer(UUID playerId) {
        if (!playerRepository.existsById(playerId)) {
            throw new NotFoundException("Player not found: " + playerId);
        }
        return shotRepository.findZoneStatisticsByPlayerId(playerId).stream()
                .map(ZoneStatisticsResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public GlobalStatisticsResponse getGlobalStatisticsForPlayer(UUID playerId) {
        if (!playerRepository.existsById(playerId)) {
            throw new NotFoundException("Player not found: " + playerId);
        }
        return GlobalStatisticsResponse.from(shotRepository.findGlobalStatisticsByPlayerId(playerId));
    }
}
