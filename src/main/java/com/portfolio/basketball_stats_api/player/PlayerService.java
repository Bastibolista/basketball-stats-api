package com.portfolio.basketball_stats_api.player;

import com.portfolio.basketball_stats_api.common.NotFoundException;
import com.portfolio.basketball_stats_api.player.dto.CreatePlayerRequest;
import com.portfolio.basketball_stats_api.player.dto.PlayerResponse;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Transactional
    public PlayerResponse createPlayer(CreatePlayerRequest request, String ownerSubject) {
        Player player = new Player(request.name(), request.dominantHand(), request.heightCm(), ownerSubject);
        return PlayerResponse.from(playerRepository.save(player));
    }

    @Transactional(readOnly = true)
    public PlayerResponse getPlayer(UUID id, String ownerSubject) {
        return playerRepository.findByIdAndOwnerSubject(id, ownerSubject)
                .map(PlayerResponse::from)
                .orElseThrow(() -> new NotFoundException("Player not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<PlayerResponse> listPlayers(String ownerSubject) {
        return playerRepository.findAllByOwnerSubjectOrderByCreatedAtDesc(ownerSubject).stream()
                .map(PlayerResponse::from)
                .toList();
    }
}
