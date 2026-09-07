package com.portfolio.basketball_stats_api.player;

import com.portfolio.basketball_stats_api.player.dto.CreatePlayerRequest;
import com.portfolio.basketball_stats_api.player.dto.PlayerResponse;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping
    public ResponseEntity<PlayerResponse> createPlayer(@Valid @RequestBody CreatePlayerRequest request) {
        PlayerResponse response = playerService.createPlayer(request);
        return ResponseEntity.created(URI.create("/api/players/" + response.id())).body(response);
    }

    @GetMapping("/{id}")
    public PlayerResponse getPlayer(@PathVariable UUID id) {
        return playerService.getPlayer(id);
    }

    @GetMapping
    public List<PlayerResponse> listPlayers() {
        return playerService.listPlayers();
    }
}
