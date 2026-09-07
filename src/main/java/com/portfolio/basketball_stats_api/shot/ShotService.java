package com.portfolio.basketball_stats_api.shot;

import com.portfolio.basketball_stats_api.common.NotFoundException;
import com.portfolio.basketball_stats_api.player.Player;
import com.portfolio.basketball_stats_api.player.PlayerRepository;
import com.portfolio.basketball_stats_api.shot.dto.CreateShotRequest;
import com.portfolio.basketball_stats_api.shot.dto.ShotResponse;

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
}
